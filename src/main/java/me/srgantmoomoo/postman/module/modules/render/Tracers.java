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
public class Tracers extends Module {
    public final BooleanSetting players=new BooleanSetting("players",this,true),hostiles=new BooleanSetting("hostiles",this,false),passives=new BooleanSetting("passives",this,false);
    public final ColorSetting playerColor=new ColorSetting("playerColor",this,Color.WHITE,false),hostileColor=new ColorSetting("hostileMobColor",this,Color.RED,false),passiveColor=new ColorSetting("passiveMobColor",this,Color.GREEN,false);
    public final NumberSetting range=new NumberSetting("range",this,100,10,160,1),width=new NumberSetting("lineWidth",this,1,1,4,1);
    public Tracers(){super("tracers","Draw lines toward nearby entities.",Category.RENDER,0);addSettings(players,hostiles,passives,playerColor,hostileColor,passiveColor,range,width);}
    @Override public void onEvent(Event e){if(!(e instanceof EventRender2d ev)||!RenderSupport.ready())return;var pr=new RenderSupport.Projection();int count=0;
        for(Entity ent:RenderSupport.mc().world.getEntities()){if(!RenderSupport.near(ent,range.getValue()))continue;
            Color c=ent instanceof PlayerEntity&&players.isEnabled()?playerColor.getValue():ent instanceof HostileEntity&&hostiles.isEnabled()?hostileColor.getValue():ent instanceof MobEntity&&!(ent instanceof HostileEntity)&&passives.isEnabled()?passiveColor.getValue():null;
            if(c==null)continue;Vec3d pos=ent.getLerpedPos(RenderSupport.tick);var pt=pr.point(pos.x,pos.y+ent.getHeight()/2,pos.z);if(pt!=null)RenderSupport.line(ev.context,new RenderSupport.Point(pr.w/2f,pr.h/2f),pt,RenderSupport.opaque(c),(int)width.getValue());if(++count>=128)break;
        }
    }
}
