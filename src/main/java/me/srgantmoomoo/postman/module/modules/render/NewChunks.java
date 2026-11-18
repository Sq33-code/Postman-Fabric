package me.srgantmoomoo.postman.module.modules.render;
import me.srgantmoomoo.postman.module.Module;
import me.srgantmoomoo.postman.module.Category;
import me.srgantmoomoo.postman.module.setting.settings.*;
import me.srgantmoomoo.postman.event.Event;
import me.srgantmoomoo.postman.event.events.*;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.util.math.*;
import net.minecraft.block.*;
import java.awt.Color;
import java.util.*;
public class NewChunks extends Module {
    private final LinkedHashSet<Long> chunks=new LinkedHashSet<>();private ClientWorld world;
    public final ColorSetting color=new ColorSetting("color",this,new Color(0,200,255,60),false);
    public NewChunks(){super("newChunks","Show chunks received while enabled (not generation detection).",Category.RENDER,0);addSettings(color);}
    public void received(ClientWorld w,int x,int z){if(world!=w){world=w;chunks.clear();}chunks.add(((long)x<<32)|(z&0xffffffffL));if(chunks.size()>2048)chunks.remove(chunks.iterator().next());}
    @Override public void onDisable(){chunks.clear();world=null;}
    @Override public void onEvent(Event e){if(!(e instanceof EventRender2d ev)||!RenderSupport.ready()||world!=RenderSupport.mc().world)return;var pr=new RenderSupport.Projection();var p=RenderSupport.mc().player;int n=0;for(long k:chunks){int x=(int)(k>>32),z=(int)k;if(Math.abs(x-(p.getBlockX()>>4))>6||Math.abs(z-(p.getBlockZ()>>4))>6||!world.isChunkLoaded(x,z))continue;double y=p.getY()-0.1;RenderSupport.box(ev.context,pr,new Box(x*16,y,z*16,x*16+16,y+.03,z*16+16),color.getValue(),"outline",1);if(++n>=80)break;}}
}
