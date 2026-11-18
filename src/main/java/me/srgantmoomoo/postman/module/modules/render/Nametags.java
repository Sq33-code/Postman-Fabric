package me.srgantmoomoo.postman.module.modules.render;

import me.srgantmoomoo.postman.module.Module;
import me.srgantmoomoo.postman.module.Category;
import me.srgantmoomoo.postman.module.setting.settings.*;
import me.srgantmoomoo.postman.event.Event;
import me.srgantmoomoo.postman.event.events.EventRender2d;
import me.srgantmoomoo.postman.module.modules.player.PlayerClone;
import me.srgantmoomoo.postman.module.modules.player.Freecam;
import me.srgantmoomoo.postman.util.FakePlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.util.math.Vec3d;
import java.util.*;

public class Nametags extends Module {
    public final BooleanSetting self = b("self", true), fakePlayers = b("fakePlayers", true),
            items = b("items", true), durability = b("durability", true), protType = b("protType", true),
            health = b("health", true), ping = b("ping", true);
    public final NumberSetting range = new NumberSetting("Range", this, 100, 10, 260, 1);
    private BooleanSetting b(String name, boolean value) { return new BooleanSetting(name, this, value); }

    public Nametags() {
        super("nametags", "Custom player and fake player names with equipment.", Category.RENDER, 0);
        addSettings(self, fakePlayers, range, items, durability, protType, health, ping);
    }

    public boolean includes(Entity entity) {
        if (!(entity instanceof PlayerEntity p) || !RenderSupport.ready() || p.isRemoved() || !p.isAlive()) return false;
        var mc = RenderSupport.mc();
        if (p instanceof FakePlayerEntity && !fakePlayers.isEnabled()) return false;
        if (p == mc.player && (!self.isEnabled() || !mc.gameRenderer.getCamera().isThirdPerson())) return false;
        return p.squaredDistanceTo(mc.player) <= range.getValue() * range.getValue();
    }

    private RenderSupport.Point screenPoint(PlayerEntity p, RenderSupport.Projection projection) {
        Vec3d pos = p.getLerpedPos(RenderSupport.tick);
        return projection.point(pos.x, pos.y + p.getHeight() + .35, pos.z);
    }

    public boolean replacesLabel(Entity e) {
        if (!includes(e) || RenderSupport.mc().options.hudHidden) return false;
        var pr = new RenderSupport.Projection();
        return pr.visible(screenPoint((PlayerEntity)e, pr));
    }

    @Override public void onEvent(Event e) {
        if (!(e instanceof EventRender2d ev) || !RenderSupport.ready()) return;
        var mc = RenderSupport.mc();
        if (mc.options.hudHidden) return;
        var pr = new RenderSupport.Projection();
        // Explicitly include clones even when absent from the normal world player list.
        Set<PlayerEntity> targets = new LinkedHashSet<>();
        var clone = RenderSupport.enabled("playerClone", PlayerClone.class);
        var freecam = RenderSupport.enabled("freecam", Freecam.class);
        if (clone != null && clone.getClone() != null) targets.add(clone.getClone());
        if (freecam != null && freecam.getClone() != null) targets.add(freecam.getClone());
        targets.addAll(mc.world.getPlayers());
        for (PlayerEntity p : targets) {
            if (!includes(p)) continue;
            var point = screenPoint(p, pr);
            if (!pr.visible(point)) continue;
            drawTag(ev.context, p, (int)point.x(), (int)point.y());
        }
    }

    private void drawTag(DrawContext ctx, PlayerEntity p, int x, int y) {
        var mc = RenderSupport.mc();
        boolean fake = p instanceof FakePlayerEntity;
        String text = p.getName().getString();
        if (health.isEnabled()) text += " | " + Math.round(p.getHealth() + p.getAbsorptionAmount()) + " HP";
        if (ping.isEnabled() && !fake && mc.getNetworkHandler() != null) {
            var entry = mc.getNetworkHandler().getPlayerListEntry(p.getUuid());
            if (entry != null) text += " | " + entry.getLatency() + "ms";
        }
        if (fake) text += " | LOCAL";

        int fontHeight = mc.textRenderer.fontHeight;
        int tw = mc.textRenderer.getWidth(text);
        int bgTop = y - 3;
        int bgBottom = y + fontHeight + 1;
        ctx.fill(x - tw / 2 - 3, bgTop, x + (tw + 1) / 2 + 3, bgBottom, 0xc0000000);
        ctx.drawTextWithShadow(mc.textRenderer, text, x - tw / 2, y, fake ? 0xff75e4ff : 0xffffffff);
        if (!items.isEnabled()) return;

        List<ItemStack> stacks = new ArrayList<>();
        for (var slot : new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS,
                EquipmentSlot.FEET, EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND}) {
            var stack = p.getEquippedStack(slot);
            if (!stack.isEmpty()) stacks.add(stack);
        }
        if (stacks.isEmpty()) return;

        int totalWidth = stacks.size() * 20 - 4;
        int startX = x - totalWidth / 2;
        int itemY = y - 22;
        for (int i = 0; i < stacks.size(); i++) {
            ItemStack stack = stacks.get(i);
            int itemX = startX + i * 20;
            ctx.drawItem(stack, itemX, itemY);
            ctx.drawStackOverlay(mc.textRenderer, stack, itemX, itemY);

            int auxY = itemY - 8;
            if (durability.isEnabled() && stack.isDamageable()) {
                int pct = Math.round(100f * (stack.getMaxDamage() - stack.getDamage()) / Math.max(1, stack.getMaxDamage()));
                String d = Math.max(0, Math.min(100, pct)) + "%";
                int color = pct > 60 ? 0xffaaffaa : pct > 25 ? 0xffffd166 : 0xffff6666;
                smallText(ctx, d, itemX + 8, auxY, color);
                auxY -= 7;
            }
            if (protType.isEnabled()) {
                for (var entry : stack.getEnchantments().getEnchantmentEntries()) {
                    var key = entry.getKey().getKey();
                    if (key.isEmpty()) continue;
                    String label = switch (key.get().getValue().getPath()) {
                        case "protection" -> "P";
                        case "blast_protection" -> "B";
                        case "fire_protection" -> "F";
                        case "projectile_protection" -> "Pr";
                        default -> "";
                    };
                    if (label.isEmpty()) continue;
                    label += entry.getIntValue();
                    smallText(ctx, label, itemX + 8, auxY, 0xffffffff);
                    auxY -= 7;
                }
            }
        }
    }
    private void smallText(DrawContext ctx, String text, int centerX, int y, int color) {
        var font = RenderSupport.mc().textRenderer;
        var mat = ctx.getMatrices();
        mat.pushMatrix();
        try {
            mat.translate((float)centerX, (float)y);
            mat.scale(0.65f, 0.65f);
            ctx.drawTextWithShadow(font, text, -font.getWidth(text) / 2, 0, color);
        } finally { mat.popMatrix(); }
    }
}
