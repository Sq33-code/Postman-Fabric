package me.srgantmoomoo.postman.mixins;
import me.srgantmoomoo.postman.module.modules.render.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;
import net.minecraft.client.render.entity.EntityRenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.client.render.Frustum;
@Mixin(EntityRenderManager.class)
public class MixinRenderEntities {
    @Inject(method="shouldRender",at=@At("HEAD"),cancellable=true,require=0)
    private void postman$hide(Entity entity,Frustum frustum,double x,double y,double z,CallbackInfoReturnable<Boolean> cir){
        var nr=RenderSupport.enabled("noRender",NoRender.class);
        if(nr!=null&&nr.hides(entity))cir.setReturnValue(false);
    }
}
