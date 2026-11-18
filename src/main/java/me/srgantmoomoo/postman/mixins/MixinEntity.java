package me.srgantmoomoo.postman.mixins;

import me.srgantmoomoo.postman.Main;
import me.srgantmoomoo.postman.module.modules.movement.NoSlow;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public class MixinEntity {
    @Inject(method = "slowMovement", at = @At("HEAD"), cancellable = true)
    private void noSlowMovement(BlockState state, Vec3d multiplier, CallbackInfo info) {
        NoSlow noSlow = (NoSlow) Main.INSTANCE.moduleManager.getModuleByName("noSlow");
        if(noSlow == null || !noSlow.isModuleEnabled()) return;
        if(!((Object)this instanceof ClientPlayerEntity)) return;
        if(state.isOf(Blocks.COBWEB) && noSlow.web.isEnabled()) {
            info.cancel();
        }
    }
}
