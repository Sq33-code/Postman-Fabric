package me.srgantmoomoo.postman.module.modules.player;

import me.srgantmoomoo.postman.module.Category;
import me.srgantmoomoo.postman.module.Module;
import me.srgantmoomoo.postman.event.Event;
import me.srgantmoomoo.postman.event.events.EventTick;
import me.srgantmoomoo.postman.util.FakePlayerEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;

public class PlayerClone extends Module {
    private FakePlayerEntity clonedPlayer;
    private ClientWorld cloneWorld;

    public PlayerClone() { super("playerClone", "Clones your player locally.", Category.PLAYER, 0); }

    public FakePlayerEntity getClone() {
        return cloneWorld == MinecraftClient.getInstance().world && clonedPlayer != null && !clonedPlayer.isRemoved() ? clonedPlayer : null;
    }

    @Override public void onEnable() { spawn(); }

    private void spawn() {
        var mc = MinecraftClient.getInstance();
        if (mc.player == null || mc.world == null || getClone() != null) return;
        cloneWorld = mc.world;
        clonedPlayer = new FakePlayerEntity(cloneWorld, mc.player, "FakePlayer");
        cloneWorld.addEntity(clonedPlayer);
    }

    @Override public void onEvent(Event e) {
        if (e instanceof EventTick) {
            if (cloneWorld != MinecraftClient.getInstance().world) clear();
            spawn();
        }
    }

    private void clear() {
        if (cloneWorld != null && clonedPlayer != null && !clonedPlayer.isRemoved())
            cloneWorld.removeEntity(clonedPlayer.getId(), Entity.RemovalReason.DISCARDED);
        cloneWorld = null;
        clonedPlayer = null;
    }

    @Override public void onDisable() { clear(); }
}
