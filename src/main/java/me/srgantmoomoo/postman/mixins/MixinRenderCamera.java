package me.srgantmoomoo.postman.mixins;
import me.srgantmoomoo.postman.module.modules.render.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;
import net.minecraft.client.render.Camera;
@Mixin(Camera.class)
public class MixinRenderCamera {
    @Inject(method="clipToSpace",at=@At("HEAD"),cancellable=true,require=0)
    private void postman$clip(float distance, CallbackInfoReturnable<Float> cir) {
        if(RenderSupport.enabled("cameraClip",CameraClip.class)!=null) cir.setReturnValue(distance);
    }
}
