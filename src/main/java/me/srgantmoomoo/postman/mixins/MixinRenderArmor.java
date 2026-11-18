package me.srgantmoomoo.postman.mixins;
import me.srgantmoomoo.postman.module.modules.render.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
@Mixin(ArmorFeatureRenderer.class)
public class MixinRenderArmor {
    @Inject(method="render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;ILnet/minecraft/client/render/entity/state/BipedEntityRenderState;FF)V",at=@At("HEAD"),cancellable=true,require=0)
    private void postman$armor(CallbackInfo ci){var n=RenderSupport.enabled("noRender",NoRender.class);if(n!=null&&n.armor.isEnabled())ci.cancel();}
}
