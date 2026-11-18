package me.srgantmoomoo.postman.mixins;

import com.mojang.authlib.GameProfile;
import me.srgantmoomoo.postman.Main;
import me.srgantmoomoo.postman.module.modules.movement.NoSlow;
import me.srgantmoomoo.postman.module.modules.movement.SafeWalk;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.input.Input;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public abstract class MixinClientPlayerEntity extends AbstractClientPlayerEntity {
    @Shadow
    public Input input;

    private MixinClientPlayerEntity(ClientWorld world, GameProfile profile) {
        super(world, profile);
    }

    @Override
    protected boolean clipAtLedge() {
        SafeWalk safeWalk = (SafeWalk) Main.INSTANCE.moduleManager.getModuleByName("safeWalk");
        if(safeWalk != null && safeWalk.isModuleEnabled())
            return true;
        return super.clipAtLedge();
    }

    @Inject(method = "shouldSlowDown", at = @At("HEAD"), cancellable = true)
    private void noSlow(org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable<Boolean> info) {
        NoSlow noSlow = (NoSlow) Main.INSTANCE.moduleManager.getModuleByName("noSlow");
        ClientPlayerEntity player = (ClientPlayerEntity)(Object)this;
        if(noSlow != null && noSlow.isModuleEnabled() && noSlow.food.isEnabled()
                && player.isUsingItem() && !player.hasVehicle()) info.setReturnValue(false);
    }

    @Inject(method = "shouldStopSprinting", at = @At("HEAD"), cancellable = true)
    private void noSlowShouldStopSprinting(org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable<Boolean> info) {
        ClientPlayerEntity player = (ClientPlayerEntity)(Object)this;
        NoSlow noSlow = (NoSlow) Main.INSTANCE.moduleManager.getModuleByName("noSlow");
        if(noSlow != null && noSlow.isModuleEnabled() && noSlow.food.isEnabled() && player.isUsingItem() && !player.hasVehicle())
            info.setReturnValue(false);
    }
}