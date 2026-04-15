package com.voidsky.listeners;

import com.voidsky.VoidSkyPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

public class PlayerFallListener implements Listener {

    private final VoidSkyPlugin plugin;

    public PlayerFallListener(VoidSkyPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerMove(PlayerMoveEvent event) {
        Location from = event.getFrom();
        Location to = event.getTo();

        // High performance check: only evaluate if Y-axis block changes
        if (to == null || from.getBlockY() == to.getBlockY()) {
            return;
        }

        // We only care about falling down
        if (to.getY() > from.getY()) {
            return;
        }

        Player player = event.getPlayer();
        String worldName = player.getWorld().getName();

        // Check if enabled in this world
        if (!plugin.getConfigManager().isWorldEnabled(worldName)) {
            return;
        }

        double voidHeight = plugin.getConfigManager().getVoidHeight(worldName);
        if (to.getY() <= voidHeight) {
            String targetWorldName = plugin.getConfigManager().getTeleportWorld(worldName);
            World targetWorld = Bukkit.getWorld(targetWorldName);

            if (targetWorld == null) {
                // Failsafe if target world doesn't exist
                return;
            }

            double teleportHeight = plugin.getConfigManager().getTeleportHeight(worldName);
            Location targetLocation = new Location(
                    targetWorld,
                    to.getX(),
                    teleportHeight,
                    to.getZ(),
                    to.getYaw(),
                    to.getPitch()
            );

            // Store current velocity to preserve the "falling" momentum
            Vector velocity = player.getVelocity();

            // Teleport
            player.teleport(targetLocation);
            
            // Re-apply velocity
            player.setVelocity(velocity);

            // Give fall damage immunity if enabled
            if (plugin.getConfigManager().isDisableFallDamage(worldName)) {
                plugin.getDamageManager().grantImmunity(player.getUniqueId());
            }
        }
    }
}
