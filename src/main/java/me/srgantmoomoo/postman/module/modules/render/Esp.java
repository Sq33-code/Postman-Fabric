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
public class Esp extends Module {
    public final ModeSetting entityMode=new ModeSetting("entity",this,"outline","outline","fill","outline & fill","trace","fake2d","off"),storage=new ModeSetting("storage",this,"outline","outline","fill","outline & fill","off"),crystalMode=new ModeSetting("crystal",this,"pretty","pretty","off");
    public final BooleanSetting walls=new BooleanSetting("walls",this,false),mobs=new BooleanSetting("mobs",this,false),items=new BooleanSetting("items",this,true);
    public final NumberSetting range=new NumberSetting("range",this,100,10,160,1),lineWidth=new NumberSetting("lineWidth",this,2,1,5,1);
    public final ColorSetting playerColor=c("player",0,121,194),hostileColor=c("hostileMob",255,0,0),passiveColor=c("passiveMob",0,255,0),itemColor=c("item",0,121,194),chestColor=c("chest",255,255,0),enderChestColor=c("enderChest",255,70,200),shulkerColor=c("shulkerBox",255,182,193),otherColor=c("otherContainers",150,150,150),crystalColor=c("crystalColor",180,105,255);
    private ColorSetting c(String n,int r,int g,int b){return new ColorSetting(n,this,new Color(r,g,b,100),false);}
    private final Map<BlockPos,ColorSetting> containers=new HashMap<>();private ClientWorld lastWorld;private int ticks;
    public Esp(){super("esp's","Projected entity and storage ESP.",Category.RENDER,0);addSettings(entityMode,storage,crystalMode,mobs,items,walls,range,lineWidth,playerColor,passiveColor,hostileColor,itemColor,chestColor,enderChestColor,shulkerColor,otherColor,crystalColor);}
    @Override public void onDisable(){containers.clear();lastWorld=null;ticks=0;}
    private void scan(){var mc=RenderSupport.mc();var w=mc.world;containers.clear();int cx=mc.player.getBlockX()>>4,cz=mc.player.getBlockZ()>>4, cr=Math.min(6,(int)Math.ceil(range.getValue()/16));
        for(int x=cx-cr;x<=cx+cr;x++)for(int z=cz-cr;z<=cz+cr;z++){if(!w.isChunkLoaded(x,z))continue;var chunk=w.getChunk(x,z);for(var be:chunk.getBlockEntities().values()){BlockPos pos=be.getPos();if(!pos.isWithinDistance(mc.player.getBlockPos(),range.getValue()))continue;var b=w.getBlockState(pos).getBlock();ColorSetting color=b==Blocks.CHEST||b==Blocks.TRAPPED_CHEST?chestColor:b==Blocks.ENDER_CHEST?enderChestColor:b instanceof ShulkerBoxBlock?shulkerColor:b==Blocks.BARREL||b==Blocks.HOPPER||b==Blocks.FURNACE||b==Blocks.DROPPER||b==Blocks.DISPENSER||b==Blocks.SMOKER||b==Blocks.BLAST_FURNACE?otherColor:null;if(color!=null)containers.put(pos.toImmutable(),color);}}
    }
    @Override public void onEvent(Event e){if(!RenderSupport.ready()){onDisable();return;}var mc=RenderSupport.mc();if(lastWorld!=mc.world){lastWorld=mc.world;containers.clear();ticks=0;}
        if(e instanceof EventTick){if(!storage.is("off")&&ticks++%20==0)scan();return;}if(!(e instanceof EventRender2d ev))return;var pr=new RenderSupport.Projection();int n=0,w=(int)lineWidth.getValue();
        for(Entity ent:mc.world.getEntities()){if(!RenderSupport.near(ent,range.getValue()))continue;if(!walls.isEnabled()&&!mc.player.canSee(ent))continue;String mode=entityMode.getMode();ColorSetting c=ent instanceof PlayerEntity?playerColor:ent instanceof ItemEntity&&items.isEnabled()?itemColor:ent instanceof HostileEntity&&mobs.isEnabled()?hostileColor:ent instanceof MobEntity&&mobs.isEnabled()?passiveColor:null;
            if(ent instanceof EndCrystalEntity){mode=crystalMode.is("off")?"off":"outline & fill";c=crystalColor;}if(c!=null){RenderSupport.box(ev.context,pr,RenderSupport.box(ent),c.getValue(),mode,w);if(++n>=128)break;}}
        if(!storage.is("off")){n=0;for(var it:containers.entrySet()){if(!RenderSupport.loaded(mc.world,it.getKey()) || !it.getKey().isWithinDistance(mc.player.getBlockPos(),range.getValue()))continue;
                var pos=it.getKey(); var state=mc.world.getBlockState(pos);
                if(mc.world.getBlockEntity(pos)==null)continue;
                var shape=state.getOutlineShape(mc.world,pos);
                if(shape.isEmpty())continue;
                RenderSupport.box(ev.context,pr,shape.getBoundingBox().offset(pos.getX(),pos.getY(),pos.getZ()),it.getValue().getValue(),storage.getMode(),w);if(++n>=128)break;}}
    }
}
