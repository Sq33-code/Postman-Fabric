package me.srgantmoomoo.postman.mixins;
import me.srgantmoomoo.postman.module.modules.render.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.util.Hand;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Arm;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.player.PlayerEntity;
@Mixin(HeldItemRenderer.class)
public class MixinRenderHands {
    @Inject(method="renderFirstPersonItem", at=@At(value="INVOKE",
        target="Lnet/minecraft/client/util/math/MatrixStack;push()V", ordinal=0, shift=At.Shift.AFTER), require=0)
    private void postman$offset(AbstractClientPlayerEntity player, float delta, float pitch, Hand hand,
            float swing, ItemStack item, float equip, MatrixStack matrices,
            OrderedRenderCommandQueue queue, int light, CallbackInfo ci) {
        var vm = RenderSupport.enabled("viewModel", ViewModel.class);
        var low = RenderSupport.enabled("lowOffHand", LowOffHand.class);
        boolean off = hand == Hand.OFF_HAND;
        Arm arm = off ? player.getMainArm().getOpposite() : player.getMainArm();
        if (vm != null) {
            boolean left = arm == Arm.LEFT;
            matrices.translate((float)(left ? vm.leftX : vm.rightX).getValue(),
                    (float)(left ? vm.leftY : vm.rightY).getValue(),
                    (float)(left ? vm.leftZ : vm.rightZ).getValue());
        }
        if (low != null && off) matrices.translate(0f, (float)-low.lowness.getValue(), 0f);
    }
    @Inject(method="applyEatOrDrinkTransformation",at=@At("HEAD"),cancellable=true,require=0)
    private void postman$eat(MatrixStack matrices,float delta,Arm arm,ItemStack stack,PlayerEntity player,CallbackInfo ci) {
        var vm=RenderSupport.enabled("viewModel",ViewModel.class);
        if(vm!=null&&vm.noEat.isEnabled())ci.cancel();
    }
}
