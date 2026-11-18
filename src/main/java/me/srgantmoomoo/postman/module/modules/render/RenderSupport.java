package me.srgantmoomoo.postman.module.modules.render;

import me.srgantmoomoo.postman.Main;
import me.srgantmoomoo.postman.module.Module;
import me.srgantmoomoo.postman.mixins.AccessorDrawContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.render.state.SimpleGuiElementRenderState;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.texture.TextureSetup;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.*;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import java.awt.Color;
import java.util.*;

/** World-space projection captured from the actual render pass, with GUI-space clipping. */
public final class RenderSupport {
    private RenderSupport() {}
    public static <T extends Module> T enabled(String name, Class<T> type) {
        var main = Main.INSTANCE;
        if (main == null || main.moduleManager == null) return null;
        Module mod = main.moduleManager.getModuleByName(name);
        return type.isInstance(mod) && mod.isModuleEnabled() ? type.cast(mod) : null;
    }
    static MinecraftClient mc() { return MinecraftClient.getInstance(); }
    static boolean ready() { return mc().player != null && mc().world != null; }
    static boolean near(Entity e, double range) { return e != mc().player && !e.isRemoved() && e.squaredDistanceTo(mc().player) <= range * range; }
    public static float tick = 1;
    private static Matrix4f viewProjection;
    private static Vec3d origin;
    private static ClientWorld capturedWorld;
    public static void capture(Camera camera, Matrix4f view, Matrix4f projection, float delta) {
        origin = camera.getCameraPos();
        viewProjection = new Matrix4f(projection).mul(view);
        capturedWorld = mc().world;
        tick = delta;
    }
    public record Point(float x,float y) {}
    static final class Projection {
        final int w = mc().getWindow().getScaledWidth(), h = mc().getWindow().getScaledHeight();
        final Matrix4f matrix = capturedWorld == mc().world ? viewProjection : null;
        final Vec3d cam = origin;
        Point point(double x,double y,double z) {
            if (matrix == null || cam == null) return null;
            var clip = matrix.transform(new Vector4f((float)(x-cam.x),(float)(y-cam.y),(float)(z-cam.z),1));
            if (!Float.isFinite(clip.w) || clip.w <= .05f || clip.z < -clip.w || clip.z > clip.w) return null;
            float px=(clip.x/clip.w+1)*w*.5f, py=(1-clip.y/clip.w)*h*.5f;
            return Float.isFinite(px) && Float.isFinite(py) ? new Point(px,py) : null;
        }
        boolean visible(Point p) { return p!=null && p.x>=0 && p.x<=w && p.y>=0 && p.y<=h; }
    }
    static Box box(Entity e) {
        Vec3d pos=e.getLerpedPos(tick);
        return e.getBoundingBox().offset(pos.x-e.getX(),pos.y-e.getY(),pos.z-e.getZ());
    }
    static int opaque(Color color) { return color.getRGB()|0xff000000; }
    static void line(DrawContext ctx, Point a, Point b, int color, int width) {
        if(a==null||b==null) return;
        // Clip to the viewport before transforming the line so close targets do not create enormous quads.
        float dx=b.x-a.x,dy=b.y-a.y;
        float[] t={0,1};
        int w=mc().getWindow().getScaledWidth(), h=mc().getWindow().getScaledHeight();
        if(!clipTest(-dx,a.x,t)||!clipTest(dx,w-a.x,t)||!clipTest(-dy,a.y,t)||!clipTest(dy,h-a.y,t)) return;
        float x=a.x+dx*t[0], y=a.y+dy*t[0];
        float len=(float)Math.hypot(dx,dy)*(t[1]-t[0]);
        if(len<.5f) return;
        var mat=ctx.getMatrices(); mat.pushMatrix();
        try {
            mat.translate(x,y); mat.rotate((float)Math.atan2(dy,dx));
            ctx.fill(0,-width/2,(int)Math.ceil(len),Math.max(1,width-width/2),color);
        } finally { mat.popMatrix(); }
    }
    private static boolean clipTest(float p,float q,float[] t) {
        if(p==0) return q>=0;
        float r=q/p;
        if(p<0) { if(r>t[1]) return false; t[0]=Math.max(t[0],r); }
        else { if(r<t[0]) return false; t[1]=Math.min(t[1],r); }
        return true;
    }
    static void box(DrawContext ctx,Projection pr,Box b,Color c,String mode,int width) {
        if(mode.equals("off") || pr.cam==null || b.contains(pr.cam)) return;
        Point[] pts=new Point[8];
        float minX=Float.MAX_VALUE,minY=Float.MAX_VALUE,maxX=-Float.MAX_VALUE,maxY=-Float.MAX_VALUE;
        for(int i=0;i<8;i++) {
            Point p=pr.point((i&1)==0?b.minX:b.maxX,(i&2)==0?b.minY:b.maxY,(i&4)==0?b.minZ:b.maxZ);
            // Avoid near-plane artifacts when a corner is behind the camera.
            if(p==null) return;
            pts[i]=p; minX=Math.min(minX,p.x); minY=Math.min(minY,p.y); maxX=Math.max(maxX,p.x); maxY=Math.max(maxY,p.y);
        }
        if(maxX<0||maxY<0||minX>pr.w||minY>pr.h) return;
        if(mode.contains("fill") && c.getAlpha()>0) {
            List<Point> polygon=hull(pts);
            for(int axis=0;axis<4;axis++) polygon=clipPolygon(polygon,axis,pr.w,pr.h);
            if(polygon.size()>=3) {
                var state=((AccessorDrawContext)(Object)ctx).postman$getState();
                for(int i=1;i+1<polygon.size();i++) {
                    Point a=polygon.get(0), p=polygon.get(i), q=polygon.get(i+1);
                    if(Math.abs(cross(a,p,q))>.01f) state.addSimpleElement(new Triangle(a,p,q,c.getRGB()));
                }
            }
        }
        if(mode.equals("fake2d")||mode.equals("glow")) {
            Point a=new Point(minX,minY), d=new Point(maxX,maxY), b1=new Point(maxX,minY),c1=new Point(minX,maxY);
            line(ctx,a,b1,opaque(c),width);line(ctx,b1,d,opaque(c),width);line(ctx,d,c1,opaque(c),width);line(ctx,c1,a,opaque(c),width);
        } else if(mode.equals("trace")) line(ctx,new Point(pr.w/2f,pr.h/2f),pr.point((b.minX+b.maxX)/2,b.minY,(b.minZ+b.maxZ)/2),opaque(c),width);
        else if(!mode.equals("fill")) for(int i=0;i<8;i++) for(int bit=1;bit<=4;bit*=2) if((i&bit)==0) line(ctx,pts[i],pts[i|bit],opaque(c),width);
    }
    private static float cross(Point a,Point b,Point c) { return (b.x-a.x)*(c.y-a.y)-(b.y-a.y)*(c.x-a.x); }
    private static List<Point> hull(Point[] pts) {
        Point[] sorted=pts.clone(); Arrays.sort(sorted,Comparator.comparingDouble(Point::x).thenComparingDouble(Point::y));
        List<Point> h=new ArrayList<>();
        for(Point p:sorted) { while(h.size()>=2&&cross(h.get(h.size()-2),h.getLast(),p)<=0) h.removeLast(); h.add(p); }
        int lower=h.size();
        for(int i=sorted.length-2;i>=0;i--) { Point p=sorted[i]; while(h.size()>lower&&cross(h.get(h.size()-2),h.getLast(),p)<=0) h.removeLast(); h.add(p); }
        if(h.size()>1)h.removeLast(); return h;
    }
    private static float distance(Point p,int axis,int w,int h) { return switch(axis){case 0->p.x;case 1->w-p.x;case 2->p.y;default->h-p.y;}; }
    private static List<Point> clipPolygon(List<Point> in,int axis,int w,int h) {
        List<Point> out=new ArrayList<>(); if(in.isEmpty())return out;
        Point prev=in.getLast(); float dp=distance(prev,axis,w,h);
        for(Point p:in) { float d=distance(p,axis,w,h); if((d>=0)!=(dp>=0)) {float t=dp/(dp-d);out.add(new Point(prev.x+t*(p.x-prev.x),prev.y+t*(p.y-prev.y)));} if(d>=0)out.add(p);prev=p;dp=d; }
        return out;
    }
    /** GUI uses QUADS: repeating the final vertex emits one nondegenerate triangle. */
    private record Triangle(Point a,Point b,Point c,int color) implements SimpleGuiElementRenderState {
        @Override public RenderPipeline pipeline(){return RenderPipelines.GUI;}
        @Override public TextureSetup textureSetup(){return TextureSetup.empty();}
        @Override public ScreenRect scissorArea(){return null;}
        @Override public ScreenRect bounds(){
            int x=(int)Math.floor(Math.min(a.x,Math.min(b.x,c.x))), y=(int)Math.floor(Math.min(a.y,Math.min(b.y,c.y)));
            int right=(int)Math.ceil(Math.max(a.x,Math.max(b.x,c.x))), bottom=(int)Math.ceil(Math.max(a.y,Math.max(b.y,c.y)));
            return new ScreenRect(x,y,Math.max(1,right-x),Math.max(1,bottom-y));
        }
        @Override public void setupVertices(VertexConsumer v){
            // Same winding as vanilla GUI quads (screen Y increases downwards).
            vertex(v,a);vertex(v,c);vertex(v,b);vertex(v,b);
        }
        private void vertex(VertexConsumer v,Point p){v.vertex(p.x,p.y,0).color(color);}
    }
    static boolean loaded(ClientWorld w,BlockPos pos) {return w.isInBuildLimit(pos)&&w.isChunkLoaded(pos.getX()>>4,pos.getZ()>>4);}
}
