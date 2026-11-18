package me.srgantmoomoo.postman.mixins;
import me.srgantmoomoo.postman.module.modules.render.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;
import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.item.tooltip.TooltipData;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
@Mixin(TooltipComponent.class)
public interface MixinPeekTooltip {
    @Inject(method="of(Lnet/minecraft/item/tooltip/TooltipData;)Lnet/minecraft/client/gui/tooltip/TooltipComponent;",at=@At("HEAD"),cancellable=true,require=0)
    private static void postman$tooltip(TooltipData data,CallbackInfoReturnable<TooltipComponent> cir){
        if(data instanceof PeekData peek)cir.setReturnValue(new me.srgantmoomoo.postman.module.modules.render.PeekComponent(peek));
    }
}
