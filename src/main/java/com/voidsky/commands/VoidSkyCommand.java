package com.voidsky.commands;

import com.voidsky.VoidSkyPlugin;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.Bukkit;
import org.bukkit.World;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class VoidSkyCommand implements CommandExecutor, TabCompleter {

    private final VoidSkyPlugin plugin;

    public VoidSkyCommand(VoidSkyPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
            sendHelp(sender);
            return true;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            plugin.getConfigManager().loadConfig();
            sender.sendMessage(ChatColor.GREEN + "VoidSky configuration reloaded!");
            return true;
        }

        if (args[0].equalsIgnoreCase("edit")) {
            if (args.length < 4) {
                sender.sendMessage(ChatColor.RED + "Usage: /voidsky edit <world> <Enabled|VoidHeight|TeleportHeight|TeleportWorld|DisableFallDamage> <value>");
                return true;
            }

            String worldName = args[1];
            String setting = args[2];
            String value = args[3];

            try {
                if (setting.equalsIgnoreCase("Enabled")) {
                    boolean val = Boolean.parseBoolean(value);
                    plugin.getConfigManager().setWorldEnabled(worldName, val);
                } else if (setting.equalsIgnoreCase("VoidHeight")) {
                    double val = Double.parseDouble(value);
                    plugin.getConfigManager().setVoidHeight(worldName, val);
                } else if (setting.equalsIgnoreCase("TeleportHeight")) {
                    double val = Double.parseDouble(value);
                    plugin.getConfigManager().setTeleportHeight(worldName, val);
                } else if (setting.equalsIgnoreCase("TeleportWorld")) {
                    plugin.getConfigManager().setTeleportWorld(worldName, value);
                } else if (setting.equalsIgnoreCase("DisableFallDamage")) {
                    boolean val = Boolean.parseBoolean(value);
                    plugin.getConfigManager().setDisableFallDamage(worldName, val);
                } else {
                    sender.sendMessage(ChatColor.RED + "Unknown setting. Expected: Enabled, VoidHeight, TeleportHeight, TeleportWorld, DisableFallDamage");
                    return true;
                }

                sender.sendMessage(ChatColor.GREEN + "Set " + setting + " to " + value + " for world " + worldName);
                
            } catch (NumberFormatException e) {
                sender.sendMessage(ChatColor.RED + "Invalid number format for " + setting);
            }
            return true;
        }

        sendHelp(sender);
        return true;
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage(ChatColor.AQUA + "=== VoidSky Commands ===");
        sender.sendMessage(ChatColor.YELLOW + "/voidsky help" + ChatColor.WHITE + " - View this page");
        sender.sendMessage(ChatColor.YELLOW + "/voidsky reload" + ChatColor.WHITE + " - Reload the config files and update world lists");
        sender.sendMessage(ChatColor.YELLOW + "/voidsky edit <world> <Setting> <Value>" + ChatColor.WHITE + " - Dynamically edit config values in memory/files");
        sender.sendMessage(ChatColor.GRAY + "Settings: Enabled, VoidHeight, TeleportHeight, TeleportWorld, DisableFallDamage");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            List<String> subCommands = Arrays.asList("help", "reload", "edit");
            for (String subCommand : subCommands) {
                if (subCommand.toLowerCase().startsWith(args[0].toLowerCase())) {
                    completions.add(subCommand);
                }
            }
        } else if (args.length == 2 && args[0].equalsIgnoreCase("edit")) {
            for (World world : Bukkit.getWorlds()) {
                if (world.getName().toLowerCase().startsWith(args[1].toLowerCase())) {
                    completions.add(world.getName());
                }
            }
        } else if (args.length == 3 && args[0].equalsIgnoreCase("edit")) {
            List<String> settings = Arrays.asList("Enabled", "VoidHeight", "TeleportHeight", "TeleportWorld", "DisableFallDamage");
            for (String setting : settings) {
                if (setting.toLowerCase().startsWith(args[2].toLowerCase())) {
                    completions.add(setting);
                }
            }
        } else if (args.length == 4 && args[0].equalsIgnoreCase("edit")) {
            String setting = args[2];
            if (setting.equalsIgnoreCase("Enabled") || setting.equalsIgnoreCase("DisableFallDamage")) {
                List<String> booleans = Arrays.asList("true", "false");
                for (String bool : booleans) {
                    if (bool.toLowerCase().startsWith(args[3].toLowerCase())) {
                        completions.add(bool);
                    }
                }
            } else if (setting.equalsIgnoreCase("TeleportWorld")) {
                for (World world : Bukkit.getWorlds()) {
                    if (world.getName().toLowerCase().startsWith(args[3].toLowerCase())) {
                        completions.add(world.getName());
                    }
                }
            }
        }
        return completions;
    }
}
