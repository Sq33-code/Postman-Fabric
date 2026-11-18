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
public class HoleEsp extends Module {
    public final NumberSetting size=new NumberSetting("size",this,.1,0,1,.1),range=new NumberSetting("range",this,8,2,12,1);
    public final BooleanSetting outline=new BooleanSetting("outline",this,true);
    public final ColorSetting obbyColor=new ColorSetting("obbyColor",this,new Color(0,121,194,35),false),bedrockColor=new ColorSetting("bedrockColor",this,new Color(0,200,255,35),false);
    private final Map<BlockPos,Boolean> holes=new HashMap<>();private ClientWorld world;private int ticks;
    public HoleEsp(){super("holeEsp","Highlight single-block blast resistant holes.",Category.RENDER,0);addSettings(size,range,outline,bedrockColor,obbyColor);}
    @Override public void onDisable(){holes.clear();world=null;ticks=0;}
    private boolean safe(Block b){return b==Blocks.BEDROCK||b==Blocks.OBSIDIAN||b==Blocks.CRYING_OBSIDIAN||b==Blocks.ENDER_CHEST||b==Blocks.ANVIL;}
    @Override public void onEvent(Event e){if(!RenderSupport.ready()){onDisable();return;}var mc=RenderSupport.mc();if(world!=mc.world){world=mc.world;holes.clear();ticks=0;}
        if(e instanceof EventTick&&ticks++%10==0){holes.clear();var center=mc.player.getBlockPos();int rr=(int)range.getValue();for(BlockPos p:BlockPos.iterate(center.add(-rr,-rr,-rr),center.add(rr,rr,rr))){if(!p.isWithinDistance(center,rr)||!RenderSupport.loaded(world,p)||!world.getBlockState(p).isAir()||!world.getBlockState(p.up()).isAir()||!world.getBlockState(p.up(2)).isAir())continue;
            boolean valid=true,bedrock=true;for(Direction d:new Direction[]{Direction.DOWN,Direction.NORTH,Direction.SOUTH,Direction.EAST,Direction.WEST}){var q=p.offset(d);if(!RenderSupport.loaded(world,q)){valid=false;break;}Block b=world.getBlockState(q).getBlock();if(!safe(b)){valid=false;break;}bedrock&=b==Blocks.BEDROCK;}if(valid)holes.put(p.toImmutable(),bedrock);}}
        if(e instanceof EventRender2d ev){var pr=new RenderSupport.Projection();for(var hole:holes.entrySet()){var p=hole.getKey();RenderSupport.box(ev.context,pr,new Box(p.getX(),p.getY(),p.getZ(),p.getX()+1,p.getY()+Math.max(.02,size.getValue()),p.getZ()+1),(hole.getValue()?bedrockColor:obbyColor).getValue(), outline.isEnabled()?"outline & fill":"fill", 1);}}
    }
}
