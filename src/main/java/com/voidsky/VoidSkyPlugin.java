package com.voidsky;

import com.voidsky.commands.VoidSkyCommand;
import com.voidsky.config.ConfigManager;
import com.voidsky.listeners.DamageManager;
import com.voidsky.listeners.MultiverseRenameListener;
import com.voidsky.listeners.PlayerFallListener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class VoidSkyPlugin extends JavaPlugin implements Listener {

    private ConfigManager configManager;
    private DamageManager damageManager;

    @Override
    public void onEnable() {
        getLogger().info("Initializing VoidSky...");

        // Setup managers
        configManager = new ConfigManager(this);
        configManager.loadConfig(); // also updates new worlds natively

        damageManager = new DamageManager(this);

        // Register Listeners
        getServer().getPluginManager().registerEvents(this, this); // for WorldLoadEvent
        getServer().getPluginManager().registerEvents(new PlayerFallListener(this), this);
        getServer().getPluginManager().registerEvents(new MultiverseRenameListener(this), this);
        getServer().getPluginManager().registerEvents(damageManager, this);

        // Register Command
        VoidSkyCommand commandExecutor = new VoidSkyCommand(this);
        getCommand("voidsky").setExecutor(commandExecutor);
        getCommand("voidsky").setTabCompleter(commandExecutor);

        getLogger().info("VoidSky successfully enabled! Config loaded perfectly.");
    }

    @Override
    public void onDisable() {
        getLogger().info("VoidSky gracefully disabled.");
    }

    @EventHandler
    public void onWorldLoad(WorldLoadEvent event) {
        // Automatically append new worlds securely right as Multiverse loads them, and load into cache!
        configManager.handleNewWorld(event.getWorld().getName());
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public DamageManager getDamageManager() {
        return damageManager;
    }
}
