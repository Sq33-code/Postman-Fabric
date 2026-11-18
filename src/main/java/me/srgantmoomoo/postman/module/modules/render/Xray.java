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
public class Xray extends Module {
    public final NumberSetting range=new NumberSetting("range",this,12,4,24,1);
    public final BooleanSetting diamond=new BooleanSetting("diamond",this,true),ancientDebris=new BooleanSetting("ancientDebris",this,true),otherOres=new BooleanSetting("otherOres",this,false);
    private ClientWorld world;private BlockPos center;private int index;private int radius;private final Map<BlockPos,Color> found=new HashMap<>();
    public Xray(){super("xray","Ore overlay in loaded blocks (not transparent terrain).",Category.RENDER,0);addSettings(range,diamond,ancientDebris,otherOres);}
    @Override public void onDisable(){world=null;center=null;found.clear();index=0;}
    private Color ore(Block b){if(diamond.isEnabled()&&(b==Blocks.DIAMOND_ORE||b==Blocks.DEEPSLATE_DIAMOND_ORE))return Color.CYAN;if(ancientDebris.isEnabled()&&b==Blocks.ANCIENT_DEBRIS)return new Color(210,130,65);if(otherOres.isEnabled()){if(b==Blocks.GOLD_ORE||b==Blocks.DEEPSLATE_GOLD_ORE||b==Blocks.NETHER_GOLD_ORE)return Color.YELLOW;if(b==Blocks.IRON_ORE||b==Blocks.DEEPSLATE_IRON_ORE)return Color.LIGHT_GRAY;if(b==Blocks.EMERALD_ORE||b==Blocks.DEEPSLATE_EMERALD_ORE)return Color.GREEN;if(b==Blocks.REDSTONE_ORE||b==Blocks.DEEPSLATE_REDSTONE_ORE)return Color.RED;if(b==Blocks.LAPIS_ORE||b==Blocks.DEEPSLATE_LAPIS_ORE)return Color.BLUE;}return null;}
    @Override public void onEvent(Event e){if(!RenderSupport.ready()){onDisable();return;}var mc=RenderSupport.mc();int rr=(int)range.getValue();var pos=mc.player.getBlockPos();if(world!=mc.world||center==null||!center.isWithinDistance(pos,4)||radius!=rr){world=mc.world;center=pos.toImmutable();radius=rr;index=0;found.clear();}
        if(e instanceof EventTick){int side=2*rr+1,total=side*side*side;for(int i=0;i<3072;i++){if(index>=total)index=0;int a=index++;BlockPos p=center.add(a%side-rr,(a/side)%side-rr,a/(side*side)-rr);if(!RenderSupport.loaded(world,p)){found.remove(p);continue;}Color c=ore(world.getBlockState(p).getBlock());if(c==null)found.remove(p);else found.put(p,c);}}
        if(e instanceof EventRender2d ev){var pr=new RenderSupport.Projection();int n=0;for(var p:found.keySet()){if(!RenderSupport.loaded(world,p))continue;Color c=ore(world.getBlockState(p).getBlock());if(c==null)continue;RenderSupport.box(ev.context,pr,new Box(p),c,"outline",1);if(++n>=200)break;}}
    }
}
