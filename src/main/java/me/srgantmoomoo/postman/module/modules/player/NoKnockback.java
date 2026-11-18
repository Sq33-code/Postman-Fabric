package me.srgantmoomoo.postman.module.modules.player;

import me.srgantmoomoo.postman.event.Event;
import me.srgantmoomoo.postman.event.events.EventPacket;
import me.srgantmoomoo.postman.module.Category;
import me.srgantmoomoo.postman.module.Module;
import me.srgantmoomoo.postman.module.setting.settings.NumberSetting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket;
import net.minecraft.util.math.Vec3d;

import java.util.Optional;

public class NoKnockback extends Module {
    public NumberSetting playerKnockback = new NumberSetting("player", this, 0, 0, 100, 1);
    public NumberSetting explosionKnockback = new NumberSetting("explosion", this, 0, 0, 100, 1);

    public NoKnockback() {
        super("noKnockback", "take no knockback when taking damage.", Category.PLAYER, 0);
        this.addSettings(playerKnockback, explosionKnockback);
    }

    @Override
    public void onEvent(Event e) {
        if(MinecraftClient.getInstance().player == null)
            return;

        if(e instanceof EventPacket.Receive) {
            if(((EventPacket.Receive) e).getPacket() instanceof EntityVelocityUpdateS2CPacket p) {
                if(p.getEntityId() == MinecraftClient.getInstance().player.getId()) {
                    double velocity = playerKnockback.getValue() / 100;
                    Vec3d current = MinecraftClient.getInstance().player.getVelocity();
                    p.velocity = current.lerp(p.getVelocity(), velocity);
                }
            }
            if(((EventPacket) e).getPacket() instanceof ExplosionS2CPacket p1) {
                p1.playerKnockback().ifPresent(knockback -> {
                    double velocity = explosionKnockback.getValue() / 100;
                    p1.playerKnockback = Optional.of(new Vec3d(
                            knockback.x * velocity,
                            knockback.y * velocity,
                            knockback.z * velocity
                    ));
                });
            }
        }
    }

}
