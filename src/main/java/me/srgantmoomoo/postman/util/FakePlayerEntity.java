package me.srgantmoomoo.postman.util;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.SkinTextures;
import java.util.UUID;

/** A local-only player snapshot with independent identity and equipment. */
public final class FakePlayerEntity extends OtherClientPlayerEntity {
    private final SkinTextures skin;

    public FakePlayerEntity(ClientWorld world, ClientPlayerEntity source, String name) {
        super(world, new GameProfile(UUID.randomUUID(), name));
        skin = source.getSkin();
        copyPositionAndRotation(source);
        setLastPositionAndAngles(source.getEntityPos(), source.getYaw(), source.getPitch());
        headYaw = source.headYaw;
        bodyYaw = source.bodyYaw;
        setHealth(Math.min(source.getHealth(), getMaxHealth()));
        setAbsorptionAmount(source.getAbsorptionAmount());
        getInventory().clone(source.getInventory());
        getInventory().setSelectedSlot(source.getInventory().getSelectedSlot());
        for (EquipmentSlot slot : new EquipmentSlot[]{EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND,
                EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET}) {
            equipStack(slot, source.getEquippedStack(slot).copy());
        }
        setNoGravity(true);
        setInvulnerable(true);
        setVelocity(0, 0, 0);
    }

    @Override public SkinTextures getSkin() {
        return skin != null ? skin : super.getSkin();
    }
}
