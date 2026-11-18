package me.srgantmoomoo.postman.module.modules.movement;

import me.srgantmoomoo.postman.event.Event;
import me.srgantmoomoo.postman.event.events.EventTick;
import me.srgantmoomoo.postman.module.Category;
import me.srgantmoomoo.postman.module.Module;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.c2s.play.PlayerInputC2SPacket;
import net.minecraft.util.PlayerInput;

public class Sneak extends Module {
    public Sneak() {
        super("sneak", "pretends you're sneaking when you're not", Category.MOVEMENT, 0);
    }

    @Override
    public void onDisable() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null || mc.getNetworkHandler() == null) return;
        // Restore the actual input, including a physically held sneak key.
        mc.getNetworkHandler().sendPacket(new PlayerInputC2SPacket(mc.player.input.playerInput));
    }

    @Override
    public void onEvent(Event e) {
        if (!(e instanceof EventTick)) return;
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null || mc.getNetworkHandler() == null) return;

        // Since 1.21.11 the legacy shift commands are absent. Preserve all other inputs.
        PlayerInput input = mc.player.input.playerInput;
        mc.getNetworkHandler().sendPacket(new PlayerInputC2SPacket(new PlayerInput(
                input.forward(), input.backward(), input.left(), input.right(),
                input.jump(), true, input.sprint())));
    }
}
