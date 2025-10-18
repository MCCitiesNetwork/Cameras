package main.java.water.of.cup.cameras.core;

import main.java.water.of.cup.cameras.listeners.CameraClick;
import main.java.water.of.cup.cameras.listeners.CameraPlace;
import main.java.water.of.cup.cameras.utils.Utils;
import main.java.water.of.cup.cameras.utils.ResourcePackManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class Camera extends JavaPlugin {

    private static Camera instance;
    ResourcePackManager resourcePackManager = new ResourcePackManager();
    private File configFile;
    private FileConfiguration config;

    @Override
    public void onEnable() {
        instance = this;

        loadConfig();

        this.resourcePackManager.initialize();

        // Resource pack manager test
        File grassFile = this.resourcePackManager.getTextureByMaterial(Material.GRASS);

        // Note: We no longer scan for text file maps since we use direct map data storage
        // Maps are now stored directly in Minecraft's map system and don't need file scanning

        main.java.water.of.cup.cameras.utils.Utils.loadColors();
        registerListeners(new CameraClick(), new CameraPlace());

        Bukkit.getLogger().info("[Cameras] Plugin version: " + getDescription().getVersion());
    }

    @Override
    public void onDisable() {
        /* Disable all current async tasks */
        Bukkit.getScheduler().cancelTasks(this);
    }

    private void registerListeners(Listener... listeners) {
        Arrays.stream(listeners).forEach(listener -> getServer().getPluginManager().registerEvents(listener, this));
    }

    public static Camera getInstance() {
        return instance;
    }


    private void loadConfig() {
        if (!getDataFolder().exists()) {
            getDataFolder().mkdir();
        }

        configFile = new File(getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            try {
                configFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        config = YamlConfiguration.loadConfiguration(configFile);

        HashMap<String, Object> defaultConfig = new HashMap<>();

        defaultConfig.put("settings.messages.notready", "&cCameras is still loading, please wait.");
        defaultConfig.put("settings.messages.delay", "&cPlease wait before taking another picture.");
        defaultConfig.put("settings.messages.invfull", "&cYou can not take a picture with a full inventory.");
        defaultConfig.put("settings.messages.nopaper", "&cYou must have paper in order to take a picture.");
        defaultConfig.put("settings.delay.amount", 1000);
        defaultConfig.put("settings.delay.enabled", true);
        defaultConfig.put("settings.camera.transparentWater", true);
        defaultConfig.put("settings.camera.renderAsync", false);
        defaultConfig.put("settings.camera.shadows", true);
        defaultConfig.put("settings.camera.permissions", true);

        for (String key : defaultConfig.keySet()) {
            if (!config.contains(key)) {
                config.set(key, defaultConfig.get(key));
            }
        }

        // Note: No longer creating maps folder since we use direct map data storage

        this.saveConfig();
    }

    public ResourcePackManager getResourcePackManager() {
        return this.resourcePackManager;
    }

    @Override
    public void saveConfig() {
        try {
            config.save(configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public FileConfiguration getConfig() {
        return config;
    }
}
