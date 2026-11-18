package me.srgantmoomoo.postman.clickgui;

import me.srgantmoomoo.postman.Main;
import me.srgantmoomoo.postman.clickgui.component.ModuleComponent;
import me.srgantmoomoo.postman.module.Category;
import me.srgantmoomoo.postman.module.setting.Setting;
import me.srgantmoomoo.postman.module.setting.settings.BooleanSetting;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Click;
import net.minecraft.client.input.KeyInput;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;

public class ClickGuiScreen extends Screen {
    public static ArrayList<CategoryRect> categoryRects;
    Setting background = Main.INSTANCE.moduleManager.getModuleByName("clickGui").getSettingByName("background");
    Setting pauseGame = Main.INSTANCE.moduleManager.getModuleByName("clickGui").getSettingByName("pauseGame");

    public ClickGuiScreen() {
        super(Text.literal("clickGui"));
        categoryRects = new ArrayList<>();
        int rectX = 10;
        int rectY = 15;
        int rectWidth = 100; // 88, 12
        int rectHeight = 12;

        for(Category category : Category.values()) {
            categoryRects.add(new CategoryRect(category, rectX, rectY, rectWidth, rectHeight, true, false, 0, 0));
            rectX += rectWidth + 1;
        }
    }

    public static ArrayList<CategoryRect> getCategoryRects() {
        return categoryRects;
    }

    public static CategoryRect getCategoryRectByName(String name) {
        CategoryRect cat = null;
        for (CategoryRect c : getCategoryRects()) {
            if (c.getCategory().getName().equalsIgnoreCase(name)) {
                cat = c;
            }
        }
        return cat;
    }

    public static void closeAllSettingComponents() {
        for(CategoryRect categoryRect : categoryRects) {
            categoryRect.getModuleComponents().forEach(compo -> compo.setOpen(false));
        }
    }

    private final Identifier postmanLogo = Identifier.of(Main.INSTANCE.MODID, "textures/postman-logo-transparent.png");
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

        // postman logo
        context.drawTexture(RenderPipelines.GUI_TEXTURED, postmanLogo, 0, MinecraftClient.getInstance().getWindow().getScaledHeight() - 80,
                0, 0, 80, 80, 80, 80);

        // categories & modules
        for(CategoryRect categoryRect : categoryRects) {
            categoryRect.updatePosition(mouseX, mouseY);
            categoryRect.draw(context);
            for(ModuleComponent compo : categoryRect.getModuleComponents()) { //TODO moving this into catRect would probably work better
                compo.updateComponent(mouseX, mouseY);
            }
        }
    }

    @Override
    public boolean mouseClicked(Click click, boolean doubled) {
        double mouseX = click.x(), mouseY = click.y();
        int button = click.button();
        for(CategoryRect rect : categoryRects) {
            if(rect.isWithinRect(mouseX, mouseY) && button == 0) {
                rect.setDragging(true);
                rect.setDragX(mouseX - rect.getX());
                rect.setDragY(mouseY - rect.getY());
            }else if(rect.isWithinRect(mouseX, mouseY) && button == 1) {
                rect.setOpen(!rect.isOpen());
            }/*else if(rect.isOpen()) { // module interactions need to be put in here?? mouse clicked
                for(ModuleComponent compo : rect.getModuleComponents()) {
                    compo.updateComponent(mouseX, mouseY);
                }
            }*/

            if(rect.isOpen()) {
                for(ModuleComponent compo : rect.getModuleComponents()) {
                    compo.mouseClicked(mouseX, mouseY, button);
                }
            }
        }
        return false;
    }

    @Override
    public boolean mouseReleased(Click click) {
        double mouseX = click.x(), mouseY = click.y();
        int button = click.button();
        for(CategoryRect rect : categoryRects) {
            if(button == 0) {
                rect.setDragging(false);
            }

            if(rect.isOpen()) {
                for(ModuleComponent compo : rect.getModuleComponents()) {
                    compo.mouseReleased(mouseX, mouseY, button);
                }
            }
        }
        return false;
    }

    @Override
    public boolean keyPressed(KeyInput input) {
        int keyCode = input.key(), scanCode = input.scancode(), modifiers = input.modifiers();
        for(CategoryRect rect : categoryRects) {
            if(rect.isOpen()) {
                rect.getModuleComponents().forEach(compo -> compo.keyPressed(keyCode, scanCode, modifiers));
            }
        }

        if (keyCode == GLFW.GLFW_KEY_ESCAPE) { //TODO clickgui esc close option
            Main.INSTANCE.moduleManager.getModuleByName("clickGui").disable();
            return true;
        }
        return false;
    }

    /*
    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        return false;
    }
    */

    @Override
    public boolean shouldPause() {
        return ((BooleanSetting) this.pauseGame).isEnabled();
    }
}
