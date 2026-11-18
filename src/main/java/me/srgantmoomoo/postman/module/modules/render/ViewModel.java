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
public class ViewModel extends Module {
    public final BooleanSetting noEat=new BooleanSetting("noEat",this,false);
    public final NumberSetting leftX=new NumberSetting("LeftX",this,0,-2,2,.1),leftY=new NumberSetting("LeftY",this,0,-2,2,.1),leftZ=new NumberSetting("LeftZ",this,0,-2,2,.1),rightX=new NumberSetting("RightX",this,0,-2,2,.1),rightY=new NumberSetting("RightY",this,0,-2,2,.1),rightZ=new NumberSetting("RightZ",this,0,-2,2,.1);
    public ViewModel(){super("viewModel","Change first-person held item position.",Category.RENDER,0);addSettings(noEat,leftX,leftY,leftZ,rightX,rightY,rightZ);}
}
