package me.srgantmoomoo.postman.mixins;

import me.srgantmoomoo.postman.Main;
import me.srgantmoomoo.postman.module.modules.render.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import me.srgantmoomoo.postman.event.Type;
import me.srgantmoomoo.postman.event.events.EventRender3d;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.util.math.MatrixStack;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class MixinGameRenderer {
    @Inject(method = "renderWorld", at = @At("TAIL"), cancellable = true)
    private void renderWorld(RenderTickCounter renderTickCounter, CallbackInfo ci) {
        EventRender3d e = new EventRender3d();
        e.setType(Type.PRE);
        Main.INSTANCE.moduleManager.onEvent(e);
        if (e.isCancelled()) ci.cancel();
    }

    @Inject(method="tiltViewWhenHurt",at=@At("HEAD"),cancellable=true,require=0)
    private void postman$hurt(MatrixStack matrices,float delta,CallbackInfo ci){
        var n=RenderSupport.enabled("noRender",NoRender.class);
        if(n!=null&&n.hurtCam.is("noHurtCam"))ci.cancel();
    }
    @ModifyExpressionValue(method="tiltViewWhenHurt",at=@At(value="INVOKE",target="Lnet/minecraft/client/option/SimpleOption;getValue()Ljava/lang/Object;"),require=0)
    private Object postman$damageTilt(Object original){
        return original instanceof Double && RenderSupport.enabled("damageTilt",DamageTiltCorrection.class)!=null ? 1.0 : original;
    }
    @Inject(method="showFloatingItem",at=@At("HEAD"),cancellable=true,require=0)
    private void postman$totem(net.minecraft.item.ItemStack stack,CallbackInfo ci){
        var n=RenderSupport.enabled("noRender",NoRender.class);
        if(n!=null&&n.totemAnimation.isEnabled()&&stack.isOf(net.minecraft.item.Items.TOTEM_OF_UNDYING))ci.cancel();
    }
}
