package me.srgantmoomoo.postman.mixins;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import me.srgantmoomoo.postman.module.modules.render.RenderSupport;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.memory.ObjectAllocator;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldRenderer.class)
public class MixinWorldProjection {
    @Inject(method = "render", at = @At("HEAD"))
    private void postman$capture(ObjectAllocator allocator, RenderTickCounter tickCounter,
            boolean outline, Camera camera, Matrix4f view, Matrix4f projection,
            Matrix4f cullingProjection, GpuBufferSlice fog, Vector4f fogColor,
            boolean renderSky, CallbackInfo ci) {
        RenderSupport.capture(camera, view, projection, tickCounter.getTickProgress(false));
    }
}
