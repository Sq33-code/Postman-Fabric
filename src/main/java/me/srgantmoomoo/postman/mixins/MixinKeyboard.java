package me.srgantmoomoo.postman.mixins;

import me.srgantmoomoo.postman.Main;
import me.srgantmoomoo.postman.event.Type;
import me.srgantmoomoo.postman.event.events.EventGuiKeyPress;
import me.srgantmoomoo.postman.event.events.EventKeyPress;
import net.minecraft.client.Keyboard;
import net.minecraft.client.input.KeyInput;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Keyboard.class)
public class MixinKeyboard {

    @Inject(method = "onKey", at = @At("HEAD"), cancellable = true)
    public void onKey(long window, int action, KeyInput input, CallbackInfo info) {
        int key = input.key(), scancode = input.scancode();
        if (action == GLFW.GLFW_PRESS) {
            EventKeyPress e = new EventKeyPress(key, scancode);
            Main.INSTANCE.moduleManager.onKeyPress(e, key, scancode);
            Main.INSTANCE.commandManager.onKeyPress();

            e.setType(Type.PRE);
            Main.INSTANCE.moduleManager.onEvent(e);
            if (e.isCancelled()) info.cancel();
        }
    }

    @Inject(method = "onKey", at = @At("HEAD"), cancellable = true)
    private void onGuiKeyPressed(long windowPointer, int action, KeyInput input, CallbackInfo info) {
        int key = input.key(), scanCode = input.scancode();
        // for key listeners in gui screens
        EventGuiKeyPress e = new EventGuiKeyPress(key, scanCode);
        e.setType(Type.PRE);
        Main.INSTANCE.moduleManager.onEvent(e);
        if(e.isCancelled()) info.cancel();
    }
}
