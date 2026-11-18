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
public class World extends Module {
    public final ModeSetting weather=new ModeSetting("weather",this,"clear","clear","rain","thunderStorm");
    public final NumberSetting time=new NumberSetting("time",this,0,0,24000,1);
    public World(){super("world","Client-side time and weather.",Category.RENDER,0);addSettings(weather,time);}
    // ClientWorld#getTimeOfDay is inherited from World; use a return hook, no packet cancellation.
}
