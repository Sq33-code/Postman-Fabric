package me.srgantmoomoo.postman.module.modules.render;

import net.minecraft.client.gui.tooltip.TooltipComponent;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;

public final class PeekComponent implements TooltipComponent {
    private final PeekData data;
    public PeekComponent(PeekData data) { this.data = data; }
    @Override public int getWidth(TextRenderer renderer) { return 166; }
    @Override public int getHeight(TextRenderer renderer) { return 58; }
    @Override public void drawItems(TextRenderer renderer, int x, int y, int width, int height, DrawContext ctx) {
        ctx.fill(x, y, x + 166, y + 58, 0xff23232b);
        for (int i = 0; i < 27; i++) {
            int a = x + 2 + (i % 9) * 18, b = y + 2 + (i / 9) * 18;
            ctx.fill(a, b, a + 18, b + 18, 0xff17171c);
            ctx.fill(a + 1, b + 1, a + 17, b + 17, 0xff484852);
            if (i >= data.stacks().size()) continue;
            var stack = data.stacks().get(i);
            if (stack.isEmpty()) continue;
            ctx.drawItem(stack, a + 1, b + 1);
            ctx.drawStackOverlay(renderer, stack, a + 1, b + 1);
        }
    }
}
