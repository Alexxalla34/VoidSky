package com.voidsky.listeners;

import com.voidsky.VoidSkyPlugin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import org.bukkit.event.player.PlayerQuitEvent;

public class DamageManager implements Listener {

    // Set of player UUIDs that are immune to fall damage for their first landing.
    private final Set<UUID> immunePlayers = new HashSet<>();

    public DamageManager(VoidSkyPlugin plugin) {
    }

    public void grantImmunity(UUID uuid) {
        immunePlayers.add(uuid);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        if (event.getCause() != DamageCause.FALL) {
            return;
        }

        Player player = (Player) event.getEntity();
        UUID uuid = player.getUniqueId();

        if (immunePlayers.contains(uuid)) {
            // Immune to the first fall damage after being teleported
            event.setCancelled(true);
            immunePlayers.remove(uuid);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        immunePlayers.remove(event.getPlayer().getUniqueId());
    }
}
