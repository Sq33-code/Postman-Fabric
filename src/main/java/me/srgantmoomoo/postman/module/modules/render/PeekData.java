package me.srgantmoomoo.postman.module.modules.render;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipData;
import java.util.List;
public record PeekData(List<ItemStack> stacks) implements TooltipData {}
