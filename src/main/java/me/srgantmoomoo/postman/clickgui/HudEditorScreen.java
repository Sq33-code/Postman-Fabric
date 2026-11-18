package me.srgantmoomoo.postman.clickgui;

import me.srgantmoomoo.postman.Main;
import me.srgantmoomoo.postman.module.hud.HudModule;
import me.srgantmoomoo.postman.module.setting.Setting;
import me.srgantmoomoo.postman.module.setting.settings.BooleanSetting;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Click;
import net.minecraft.client.input.KeyInput;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public class HudEditorScreen extends Screen {
    Setting background = Main.INSTANCE.moduleManager.getModuleByName("hudEditor").getSettingByName("background");
    Setting pauseGame = Main.INSTANCE.moduleManager.getModuleByName("hudEditor").getSettingByName("pauseGame");

    public HudEditorScreen() {
        super(Text.literal("hudEditor"));
    }

    // Minecraft 1.21.11 renders the background before render().
    // Use a lightweight translucent overlay instead of a blur pass.
    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
        if (((BooleanSetting) background).isEnabled()) {
            context.fill(0, 0, this.width, this.height, 0x80000000);
        }
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {

        // pulls the rendering for each individual hud module
        for(HudModule m : Main.INSTANCE.hudManager.hudModules) {
            m.drawDraggable(context, mouseX, mouseY);
        }

        // handles hud module dragging
        for(HudModule m : Main.INSTANCE.hudManager.hudModules) {
            m.getDraggableComponent().draw(context, mouseX, mouseY);
        }
    }

    @Override
    public boolean mouseClicked(Click click, boolean doubled) {
        double mouseX = click.x(), mouseY = click.y();
        int button = click.button();
        for(HudModule m : Main.INSTANCE.hudManager.hudModules) {
            m.getDraggableComponent().mouseClicked(mouseX, mouseY, button);
        }
        return false;
    }

    @Override
    public boolean mouseReleased(Click click) {
        double mouseX = click.x(), mouseY = click.y();
        int button = click.button();
        for (HudModule m : Main.INSTANCE.hudManager.hudModules) {
            m.getDraggableComponent().mouseReleased(mouseX, mouseY, button);
        }
        return false;
    }

    @Override
    public boolean keyPressed(KeyInput input) {
        int keyCode = input.key(), scanCode = input.scancode(), modifiers = input.modifiers();
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            Main.INSTANCE.moduleManager.getModuleByName("hudEditor").disable();
            return true;
        }
        return false;
    }

    @Override
    public boolean shouldPause() {
        return ((BooleanSetting) this.pauseGame).isEnabled();
    }
}
