package com.voidsky.listeners;

import com.voidsky.VoidSkyPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.Bukkit;

public class MultiverseRenameListener implements Listener {

    private final VoidSkyPlugin plugin;

    public MultiverseRenameListener(VoidSkyPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        handleCommand(event.getMessage());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onConsoleCommand(ServerCommandEvent event) {
        handleCommand("/" + event.getCommand());
    }

    private void handleCommand(String commandLine) {
        // e.g. "/mv rename old new" or "/mvr old new"
        String[] args = commandLine.trim().split("\\s+");
        
        if (args.length >= 4 && args[0].equalsIgnoreCase("/mv") && args[1].equalsIgnoreCase("rename")) {
            String oldName = args[2];
            String newName = args[3];
            scheduleRename(oldName, newName);
        } else if (args.length >= 3 && args[0].equalsIgnoreCase("/mvr")) {
            String oldName = args[1];
            String newName = args[2];
            scheduleRename(oldName, newName);
        }
    }

    private void scheduleRename(final String oldName, final String newName) {
        // Schedule slightly delayed to allow Multiverse to finish its rename and potentially load the world
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            plugin.getConfigManager().renameWorldConfig(oldName, newName);
        }, 10L); // half a second delay
    }
}
