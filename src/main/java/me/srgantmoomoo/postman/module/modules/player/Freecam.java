package me.srgantmoomoo.postman.module.modules.player;

import me.srgantmoomoo.postman.event.Event;
import me.srgantmoomoo.postman.event.events.EventPacket;
import me.srgantmoomoo.postman.module.Category;
import me.srgantmoomoo.postman.module.Module;
import me.srgantmoomoo.postman.module.setting.settings.NumberSetting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;

import me.srgantmoomoo.postman.util.FakePlayerEntity;
import net.minecraft.client.world.ClientWorld;

public class Freecam extends Module {
    public NumberSetting speed = new NumberSetting("speed", this, 5, 0, 20, 1);

    private double posX, posY, posZ;
    private float pitch, yaw;
    private FakePlayerEntity clonedPlayer;
    private ClientWorld cloneWorld;

    public FakePlayerEntity getClone() {
        return cloneWorld == MinecraftClient.getInstance().world && clonedPlayer != null && !clonedPlayer.isRemoved() ? clonedPlayer : null;
    }
    private boolean wasFlying;
    private float prevFlySpeed;

    public Freecam() {
        super("freecam", "out of body experience 0_0", Category.RENDER, 0);
        this.addSettings(speed);
    }

    @Override
    public void onEnable() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if(mc.player == null || mc.world == null) return;

        posX = mc.player.getX();
        posY = mc.player.getY();
        posZ = mc.player.getZ();
        pitch = mc.player.getPitch();
        yaw = mc.player.getYaw();

        wasFlying = mc.player.getAbilities().flying;
        prevFlySpeed = mc.player.getAbilities().getFlySpeed();

        cloneWorld = mc.world;
        clonedPlayer = new FakePlayerEntity(cloneWorld, mc.player, "Freecam");
        cloneWorld.addEntity(clonedPlayer);

        mc.player.getAbilities().flying = true;
        mc.player.getAbilities().setFlySpeed((float) (speed.getValue() / 100f));
        mc.player.noClip = true;
        mc.player.setVelocity(0, 0, 0);
    }

    @Override
    public void onDisable() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (cloneWorld != mc.world || mc.player == null || mc.world == null || clonedPlayer == null) {
            if (cloneWorld != null && clonedPlayer != null && !clonedPlayer.isRemoved())
                cloneWorld.removeEntity(clonedPlayer.getId(), Entity.RemovalReason.DISCARDED);
            cloneWorld = null; clonedPlayer = null;
            return;
        }

        mc.player.setPosition(posX, posY, posZ);
        mc.player.setYaw(yaw);
        mc.player.setPitch(pitch);

        if(clonedPlayer != null) {
            mc.world.removeEntity(clonedPlayer.getId(), Entity.RemovalReason.DISCARDED);
            clonedPlayer = null;
            cloneWorld = null;
        }

        mc.player.getAbilities().flying = wasFlying;
        mc.player.getAbilities().setFlySpeed(prevFlySpeed);
        mc.player.noClip = false;
        mc.player.setVelocity(0, 0, 0);

        posX = posY = posZ = 0;
        pitch = yaw = 0;
    }

    @Override
    public void onEvent(Event e) {
        MinecraftClient mc = MinecraftClient.getInstance();

        if (getClone() == null) return;

        if(e instanceof EventPacket.Send sendEvent) {
            if(sendEvent.getPacket() instanceof PlayerMoveC2SPacket) {
                sendEvent.setCancelled(true);
            }
        }

        if(mc.player != null) {
            mc.player.noClip = true;
            mc.player.getAbilities().flying = true;
            mc.player.getAbilities().setFlySpeed((float) (speed.getValue() / 100f));
            mc.player.fallDistance = 0;
        }
    }
}