package me.srgantmoomoo.postman.mixins;
import me.srgantmoomoo.postman.module.modules.render.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.entity.PlayerLikeEntity;
@Mixin(PlayerEntityRenderer.class)
public class MixinRenderNames {
    @Inject(method="hasLabel(Lnet/minecraft/entity/PlayerLikeEntity;D)Z",at=@At("HEAD"),cancellable=true,require=0)
    private void postman$labels(PlayerLikeEntity entity,double distance,CallbackInfoReturnable<Boolean> cir){
        var n=RenderSupport.enabled("nametags",Nametags.class);
        if(n!=null&&n.replacesLabel(entity))cir.setReturnValue(false);
    }
}
