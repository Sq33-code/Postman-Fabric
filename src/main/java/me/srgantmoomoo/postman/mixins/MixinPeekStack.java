package me.srgantmoomoo.postman.mixins;
import me.srgantmoomoo.postman.module.modules.render.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipData;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.item.BlockItem;
import java.util.Optional;
@Mixin(ItemStack.class)
public class MixinPeekStack {
    @Inject(method="getTooltipData",at=@At("RETURN"),cancellable=true,require=0)
    private void postman$peek(CallbackInfoReturnable<Optional<TooltipData>> cir){
        if(RenderSupport.enabled("peek",Peek.class)==null||cir.getReturnValue().isPresent())return;
        ItemStack stack=(ItemStack)(Object)this;
        if(!(stack.getItem() instanceof BlockItem b)||!(b.getBlock() instanceof ShulkerBoxBlock))return;
        var contents=stack.get(DataComponentTypes.CONTAINER);
        if (contents != null) {
            var slots = net.minecraft.util.collection.DefaultedList.ofSize(27, ItemStack.EMPTY);
            contents.copyTo(slots);
            cir.setReturnValue(Optional.of(new PeekData(java.util.List.copyOf(slots))));
        }
    }
}
