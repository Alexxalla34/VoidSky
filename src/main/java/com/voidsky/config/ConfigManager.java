package com.voidsky.config;

import com.voidsky.VoidSkyPlugin;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.Map;

public class ConfigManager {

    private final VoidSkyPlugin plugin;
    private final Map<String, WorldSettings> cache = new HashMap<>();

    public class WorldSettings {
        public boolean enabled;
        public double voidHeight;
        public double teleportHeight;
        public String teleportWorld;
        public boolean disableFallDamage;
    }

    public ConfigManager(VoidSkyPlugin plugin) {
        this.plugin = plugin;
    }

    public void loadConfig() {
        plugin.saveDefaultConfig();
        plugin.reloadConfig();
        updateWorlds();
    }

    public void updateWorlds() {
        FileConfiguration config = plugin.getConfig();
        boolean changed = false;
        
        cache.clear();

        // Add any missing worlds
        for (World world : Bukkit.getWorlds()) {
            String path = "Worlds." + world.getName();
            if (!config.contains(path)) {
                setupDefaultWorldConfig(path);
                changed = true;
                plugin.getLogger().info("Added new world '" + world.getName() + "' to config.yml");
            }
        }

        // Load all worlds present in the config into cache
        if (config.getConfigurationSection("Worlds") != null) {
            for (String worldName : config.getConfigurationSection("Worlds").getKeys(false)) {
                loadWorldIntoCache(worldName);
            }
        }

        if (changed) {
            plugin.saveConfig();
        }
    }

    private void setupDefaultWorldConfig(String path) {
        FileConfiguration config = plugin.getConfig();
        config.set(path + ".Enabled", false);
        config.set(path + ".VoidHeight", -64.0);
        config.set(path + ".TeleportHeight", 300.0);
        config.set(path + ".TeleportWorld", "world_the_end");
        config.set(path + ".DisableFallDamage", true);
    }

    private void loadWorldIntoCache(String worldName) {
        FileConfiguration config = plugin.getConfig();
        String path = "Worlds." + worldName;
        WorldSettings settings = new WorldSettings();
        settings.enabled = config.getBoolean(path + ".Enabled", false);
        settings.voidHeight = config.getDouble(path + ".VoidHeight", -64.0);
        settings.teleportHeight = config.getDouble(path + ".TeleportHeight", 300.0);
        settings.teleportWorld = config.getString(path + ".TeleportWorld", "world_the_end");
        settings.disableFallDamage = config.getBoolean(path + ".DisableFallDamage", true);
        cache.put(worldName, settings);
    }

    public void handleNewWorld(String worldName) {
        FileConfiguration config = plugin.getConfig();
        String path = "Worlds." + worldName;
        if (!config.contains(path)) {
            setupDefaultWorldConfig(path);
            loadWorldIntoCache(worldName);
            plugin.saveConfig();
            plugin.getLogger().info("Detected newly created world '" + worldName + "', automatically added to config.yml!");
        } else if (!cache.containsKey(worldName)) {
            loadWorldIntoCache(worldName);
        }
    }

    public void renameWorldConfig(String oldName, String newName) {
        FileConfiguration config = plugin.getConfig();
        String oldPath = "Worlds." + oldName;
        String newPath = "Worlds." + newName;

        if (config.contains(oldPath)) {
            // Copy settings
            config.set(newPath + ".Enabled", config.get(oldPath + ".Enabled"));
            config.set(newPath + ".VoidHeight", config.get(oldPath + ".VoidHeight"));
            config.set(newPath + ".TeleportHeight", config.get(oldPath + ".TeleportHeight"));
            config.set(newPath + ".TeleportWorld", config.get(oldPath + ".TeleportWorld"));
            config.set(newPath + ".DisableFallDamage", config.get(oldPath + ".DisableFallDamage"));

            // Remove old
            config.set(oldPath, null);
            
            // Update cache
            cache.remove(oldName);
            loadWorldIntoCache(newName);

            plugin.saveConfig();
            plugin.getLogger().info("Successfully migrated VoidSky configuration from '" + oldName + "' to '" + newName + "' due to Multiverse rename.");
        }
    }

    public boolean isWorldEnabled(String worldName) {
        WorldSettings settings = cache.get(worldName);
        if (settings != null) return settings.enabled;
        return plugin.getConfig().getBoolean("Worlds." + worldName + ".Enabled", false);
    }
    
    public void setWorldEnabled(String worldName, boolean enabled) {
        plugin.getConfig().set("Worlds." + worldName + ".Enabled", enabled);
        if (cache.containsKey(worldName)) cache.get(worldName).enabled = enabled;
        plugin.saveConfig();
    }

    public double getVoidHeight(String worldName) {
        WorldSettings settings = cache.get(worldName);
        if (settings != null) return settings.voidHeight;
        return plugin.getConfig().getDouble("Worlds." + worldName + ".VoidHeight", -64.0);
    }
    
    public void setVoidHeight(String worldName, double height) {
        plugin.getConfig().set("Worlds." + worldName + ".VoidHeight", height);
        if (cache.containsKey(worldName)) cache.get(worldName).voidHeight = height;
        plugin.saveConfig();
    }

    public double getTeleportHeight(String worldName) {
        WorldSettings settings = cache.get(worldName);
        if (settings != null) return settings.teleportHeight;
        return plugin.getConfig().getDouble("Worlds." + worldName + ".TeleportHeight", 300.0);
    }
    
    public void setTeleportHeight(String worldName, double height) {
        plugin.getConfig().set("Worlds." + worldName + ".TeleportHeight", height);
        if (cache.containsKey(worldName)) cache.get(worldName).teleportHeight = height;
        plugin.saveConfig();
    }

    public String getTeleportWorld(String worldName) {
        WorldSettings settings = cache.get(worldName);
        if (settings != null) return settings.teleportWorld;
        return plugin.getConfig().getString("Worlds." + worldName + ".TeleportWorld", "world_the_end");
    }
    
    public void setTeleportWorld(String worldName, String targetWorld) {
        plugin.getConfig().set("Worlds." + worldName + ".TeleportWorld", targetWorld);
        if (cache.containsKey(worldName)) cache.get(worldName).teleportWorld = targetWorld;
        plugin.saveConfig();
    }

    public boolean isDisableFallDamage(String worldName) {
        WorldSettings settings = cache.get(worldName);
        if (settings != null) return settings.disableFallDamage;
        return plugin.getConfig().getBoolean("Worlds." + worldName + ".DisableFallDamage", true);
    }
    
    public void setDisableFallDamage(String worldName, boolean disable) {
        plugin.getConfig().set("Worlds." + worldName + ".DisableFallDamage", disable);
        if (cache.containsKey(worldName)) cache.get(worldName).disableFallDamage = disable;
        plugin.saveConfig();
    }
}
