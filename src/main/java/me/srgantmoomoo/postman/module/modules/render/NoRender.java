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
public class NoRender extends Module {
    public final ModeSetting hurtCam=new ModeSetting("hurtCam",this,"yesHurtCam","yesHurtCam","noHurtCam");
    public final BooleanSetting rain=b("rain"),fire=b("fire"),portalEffect=b("portalEffect"),potionIndicators=b("potionIndicators"),crystals=b("crystals"),totemAnimation=b("totemAnimation"),armor=b("armor"),tnt=b("tnt"),items=b("items"),withers=b("withers"),skulls=b("skulls"),fireworks=b("fireworks");
    private BooleanSetting b(String name){return new BooleanSetting(name,this,false);}
    public NoRender(){super("noRender","Hide selected visual effects and entities.",Category.RENDER,0);addSettings(rain,hurtCam,fire,portalEffect,potionIndicators,crystals,totemAnimation,armor,tnt,items,withers,skulls,fireworks);}
    public boolean hides(Entity e){var t=e.getType();return (crystals.isEnabled()&&t==net.minecraft.entity.EntityType.END_CRYSTAL)||(tnt.isEnabled()&&t==net.minecraft.entity.EntityType.TNT)||(items.isEnabled()&&t==net.minecraft.entity.EntityType.ITEM)||(withers.isEnabled()&&t==net.minecraft.entity.EntityType.WITHER)||(skulls.isEnabled()&&t==net.minecraft.entity.EntityType.WITHER_SKULL)||(fireworks.isEnabled()&&t==net.minecraft.entity.EntityType.FIREWORK_ROCKET);}
}
