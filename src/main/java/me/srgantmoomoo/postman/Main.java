package me.srgantmoomoo.postman;

import me.srgantmoomoo.postman.command.CommandManager;
import me.srgantmoomoo.postman.config.Load;
import me.srgantmoomoo.postman.config.Save;
import me.srgantmoomoo.postman.module.hud.HudManager;
import me.srgantmoomoo.postman.module.ModuleManager;
import me.srgantmoomoo.postman.module.setting.SettingManager;
import net.fabricmc.api.ClientModInitializer;

//ocnarf
public class Main implements ClientModInitializer {
    int strong;
    int postman = strong;

    public final String MODID = "postman";
    public final String NAME = "postman";
    public final String VERSION = net.fabricmc.loader.api.FabricLoader.getInstance()
            .getModContainer(MODID)
            .map(container -> container.getMetadata().getVersion().getFriendlyString())
            .orElse("dev");

    public static Main INSTANCE;

    public Main() {
        INSTANCE = this;
    }

    public HudManager hudManager;
    public ModuleManager moduleManager;
    public SettingManager settingManager;
    public CommandManager commandManager;
    public Save save;
    public Load load;

    @Override
    public void onInitializeClient() {
        hudManager = new HudManager();
        moduleManager = new ModuleManager();
        settingManager = new SettingManager();
        commandManager = new CommandManager();

        load = new Load();
        save = new Save();
        net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientChunkEvents.CHUNK_LOAD.register((world, chunk) -> {
            var mod = me.srgantmoomoo.postman.module.modules.render.RenderSupport.enabled("newChunks", me.srgantmoomoo.postman.module.modules.render.NewChunks.class);
            if (mod != null) mod.received(world, chunk.getPos().x, chunk.getPos().z);
        });
    }

    public void save() {
        if(this.save != null) {
            try {
                this.save.save();
            } catch (Exception ignored) {}
        }
    }
}
