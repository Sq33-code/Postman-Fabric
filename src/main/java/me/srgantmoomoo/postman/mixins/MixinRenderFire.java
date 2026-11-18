package me.srgantmoomoo.postman.mixins;
import me.srgantmoomoo.postman.module.modules.render.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;
import net.minecraft.client.gui.hud.InGameOverlayRenderer;
@Mixin(InGameOverlayRenderer.class)
public class MixinRenderFire {
    @Inject(method="renderFireOverlay",at=@At("HEAD"),cancellable=true,require=0)
    private static void postman$fire(CallbackInfo ci){var n=RenderSupport.enabled("noRender",NoRender.class);if(n!=null&&n.fire.isEnabled())ci.cancel();}
}
