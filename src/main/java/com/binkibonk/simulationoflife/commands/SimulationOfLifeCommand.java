package com.binkibonk.simulationoflife.commands;

import com.binkibonk.simulationoflife.SimulationOfLife;
import com.binkibonk.simulationoflife.managers.SpecializationManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SimulationOfLifeCommand implements CommandExecutor, TabCompleter {
    
    private final SimulationOfLife plugin;
    
    public SimulationOfLifeCommand(SimulationOfLife plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender.hasPermission("simulationoflife.admin")) {
            if (args.length == 0) {
                sendAdminHelpMessage(sender);
                return true;
            }
            String subcommand = args[0].toLowerCase();
            switch (subcommand) {
                case "reload":
                    handleReload(sender);
                    break;
                case "status":
                    handleStatus(sender);
                    break;
                case "perf":
                    handlePerformanceStats(sender);
                    break;
                case "debug":
                    handleDebug(sender);
                    break;
                case "specs":
                    handleSpecs(sender);
                    break;
                case "stats":
                    handlePlayerStats(sender);
                    break;
                default:
                    sendAdminHelpMessage(sender);
                    break;
                }
            
        } else if (sender.hasPermission("simulationoflife.player")) {
        if (args.length == 0) {
            sendPlayerHelpMessage(sender);
            return true;
        }
        String subcommand = args[0].toLowerCase();
        switch (subcommand) {
            case "stats":
                handlePlayerStats(sender);
                break;
            default:
                sendAdminHelpMessage(sender);
                break;
        }
        } else {
            sender.sendMessage(Component.text("You don't have permission to use this command!", NamedTextColor.RED));
        }
        return true;
    }
    
    // Not very optimal, could be a for loop instead since we know the names of the specs to display
    private void handlePlayerStats(CommandSender sender) {
        sender.sendMessage(Component.text("=== Your Specialization Levels ===", NamedTextColor.GOLD));
        int[] specs = plugin.getSpecializationManager().getEachSpecializationLevelOfPlayer((Player) sender);
        sender.sendMessage(Component.text("|   Building: ", NamedTextColor.GRAY)
                .append(Component.text(specs[0], NamedTextColor.BLUE)));
        sender.sendMessage(Component.text("|   Fighting: ", NamedTextColor.GRAY)
                .append(Component.text(specs[1], NamedTextColor.DARK_RED)));
        sender.sendMessage(Component.text("|   Farming: ", NamedTextColor.GRAY)
                .append(Component.text(specs[2], NamedTextColor.GREEN)));
        sender.sendMessage(Component.text("|   Mining: ", NamedTextColor.GRAY)
                .append(Component.text(specs[3], NamedTextColor.GRAY)));
        sender.sendMessage(Component.text("|   Total: ", NamedTextColor.GRAY)
                .append(Component.text(plugin.getSpecializationManager().getTotalSpecializationPointsOfPlayer((Player) sender), NamedTextColor.WHITE)));
        sender.sendMessage(Component.text("-----------------------------", NamedTextColor.GOLD));
        sender.sendMessage(Component.text("|   Athletics: ", NamedTextColor.GRAY)
                .append(Component.text(specs[4], NamedTextColor.AQUA)));
        sender.sendMessage(Component.text("=============================", NamedTextColor.GOLD));
    }
    
    private void handleReload(CommandSender sender) {
        try {
            plugin.getConfigManager().loadConfig();
            plugin.getExhaustionManager().clearAllExhaustionCooldownsForAllPlayers();
            plugin.getPerformanceMonitor().resetCounters();
            String message = plugin.getConfigManager().getPluginReloadedMessage();
            message = plugin.getConfigManager().getPrefix() + message;
            sender.sendMessage(MiniMessage.miniMessage().deserialize(message));
            plugin.getLogger().info("Configuration reloaded by " + sender.getName());
        } catch (Exception e) {
            sender.sendMessage(Component.text("Error reloading configuration: " + e.getMessage(), NamedTextColor.RED));
            plugin.getLogger().severe("Error reloading configuration: " + e.getMessage());
        }
    }
    
    private void handleStatus(CommandSender sender) {
        String statusMessage;
        if (plugin.getConfigManager().isEnabled()) {
            statusMessage = plugin.getConfigManager().getPrefix() + plugin.getConfigManager().getPluginStatusMessage();
            sender.sendMessage(MiniMessage.miniMessage().deserialize(statusMessage));
        } else {
            statusMessage = plugin.getConfigManager().getPrefix() + plugin.getConfigManager().getPluginDisabledMessage();
            sender.sendMessage(MiniMessage.miniMessage().deserialize(statusMessage));
        }   
        sender.sendMessage(Component.text("Minimum Food Level: ", NamedTextColor.GRAY)
                .append(Component.text(plugin.getConfigManager().getMinimumFoodLevel(), NamedTextColor.YELLOW)));
        sender.sendMessage(Component.text("ExhaustionCooldown: ", NamedTextColor.GRAY)
                .append(Component.text(plugin.getConfigManager().getExhaustionCooldown() + "ms", NamedTextColor.YELLOW)));
        sender.sendMessage(Component.text("Block placed Exhaustion Amount: ", NamedTextColor.GRAY)
                .append(Component.text(plugin.getConfigManager().getPlaceBlockExhaustionAmount(), NamedTextColor.YELLOW)));
        sender.sendMessage(Component.text("Block mined Exhaustion Amount: ", NamedTextColor.GRAY)
                .append(Component.text(plugin.getConfigManager().getMiningExhaustionAmount(), NamedTextColor.YELLOW)));
        sender.sendMessage(Component.text("Entity hit Exhaustion Amount: ", NamedTextColor.GRAY)
                .append(Component.text(plugin.getConfigManager().getHitEntityExhaustionAmount(), NamedTextColor.YELLOW)));
        sender.sendMessage(Component.text("Building Points: ", NamedTextColor.GRAY)
                .append(Component.text(plugin.getConfigManager().getBuildingPoints(), NamedTextColor.BLUE)));
        sender.sendMessage(Component.text("Fighting Points: ", NamedTextColor.GRAY)
                .append(Component.text(plugin.getConfigManager().getFightingPoints(), NamedTextColor.DARK_RED)));
        sender.sendMessage(Component.text("Max Points: ", NamedTextColor.GRAY)
                .append(Component.text(plugin.getConfigManager().getMaxPoints(), NamedTextColor.GREEN)));
    }
    
    private void handlePerformanceStats(CommandSender sender) {
        sender.sendMessage(Component.text("=== Performance Statistics ===", NamedTextColor.GOLD));
        long uptime = (System.currentTimeMillis() - plugin.getPerformanceMonitor().getStartTime()) / 1000;
        long playerExhaustions = plugin.getPerformanceMonitor().getTotalPlayerExhaustions();
        long blockEvents = plugin.getPerformanceMonitor().getTotalBlockEvents();
        long entityHits = plugin.getPerformanceMonitor().getTotalEntityHits();
        double exhaustionPerSecond = uptime > 0 ? (playerExhaustions * 1.0) / uptime : 0;
        double blockEventsPerSecond = uptime > 0 ? (blockEvents * 1.0) / uptime : 0;
        double entityHitsPerSecond = uptime > 0 ? (entityHits * 1.0) / uptime : 0;
        sender.sendMessage(Component.text("Uptime: ", NamedTextColor.GRAY)
                .append(Component.text(uptime + " seconds", NamedTextColor.YELLOW)));
        sender.sendMessage(Component.text("Block Events: ", NamedTextColor.GRAY)
                .append(Component.text(blockEvents + " (", NamedTextColor.YELLOW))
                .append(Component.text(String.format("%.2f", blockEventsPerSecond), NamedTextColor.GREEN))
                .append(Component.text("/s)", NamedTextColor.YELLOW)));
        sender.sendMessage(Component.text("Player Exhaustions: ", NamedTextColor.GRAY)
                .append(Component.text(playerExhaustions + " (", NamedTextColor.YELLOW))
                .append(Component.text(String.format("%.2f", exhaustionPerSecond), NamedTextColor.GREEN))
                .append(Component.text("/s)", NamedTextColor.YELLOW)));
        sender.sendMessage(Component.text("Entity Hits: ", NamedTextColor.GRAY)
                .append(Component.text(entityHits + " (", NamedTextColor.YELLOW))
                .append(Component.text(String.format("%.2f", entityHitsPerSecond), NamedTextColor.GREEN))
                .append(Component.text("/s)", NamedTextColor.YELLOW)));
    }
    
    private void handleDebug(CommandSender sender) {
        sender.sendMessage(Component.text("=== Debug Information ===", NamedTextColor.GOLD));
        sender.sendMessage(Component.text("Current Exhaustion by Player:", NamedTextColor.YELLOW));
        for (org.bukkit.entity.Player player : plugin.getServer().getOnlinePlayers()) {
            double exhaustion = player.getExhaustion();
            sender.sendMessage(Component.text("  " + player.getName() + ": ", NamedTextColor.GRAY)
                    .append(Component.text(String.format("%.2f", exhaustion), NamedTextColor.RED)));
        }
        sender.sendMessage(Component.text("Debug Mode: ", NamedTextColor.GRAY)
                .append(Component.text(plugin.getConfigManager().isDebug() ? "enabled" : "disabled", 
                        plugin.getConfigManager().isDebug() ? NamedTextColor.GREEN : NamedTextColor.RED)));
    }
    
    private void handleSpecs(CommandSender sender) {
        sender.sendMessage(Component.text("=== Specialization Statistics ===", NamedTextColor.GOLD));
        int totalBuilding = plugin.getSpecializationManager().getAllPlayersTotalSpecializationPoints(SpecializationManager.SpecializationType.BUILDING);
        int totalFighting = plugin.getSpecializationManager().getAllPlayersTotalSpecializationPoints(SpecializationManager.SpecializationType.FIGHTING);
        int totalFarming = plugin.getSpecializationManager().getAllPlayersTotalSpecializationPoints(SpecializationManager.SpecializationType.FARMING);
        int totalMining = plugin.getSpecializationManager().getAllPlayersTotalSpecializationPoints(SpecializationManager.SpecializationType.MINING);
        int totalAthletics = plugin.getSpecializationManager().getAllPlayersTotalSpecializationPoints(SpecializationManager.SpecializationType.ATHLETICS);
        int playersWithBuilding = plugin.getSpecializationManager().getCountOfPlayersWithSpecificSpecialization(SpecializationManager.SpecializationType.BUILDING);
        int playersWithFighting = plugin.getSpecializationManager().getCountOfPlayersWithSpecificSpecialization(SpecializationManager.SpecializationType.FIGHTING);
        int playersWithFarming = plugin.getSpecializationManager().getCountOfPlayersWithSpecificSpecialization(SpecializationManager.SpecializationType.FARMING);
        int playersWithMining = plugin.getSpecializationManager().getCountOfPlayersWithSpecificSpecialization(SpecializationManager.SpecializationType.MINING);
        int playersWithAthletics = plugin.getSpecializationManager().getCountOfPlayersWithSpecificSpecialization(SpecializationManager.SpecializationType.ATHLETICS);
        int playersWithNoSpecializations = plugin.getSpecializationManager().getCountOfPlayersWithNoSpecializations();
        int totalPlayersWithSpecs = plugin.getSpecializationManager().getCountOfPlayersWithAnySpecializations();
        sender.sendMessage(Component.text("Total Building Points: ", NamedTextColor.GRAY)
                .append(Component.text(totalBuilding, NamedTextColor.BLUE)));
        sender.sendMessage(Component.text("Total Fighting Points: ", NamedTextColor.GRAY)
                .append(Component.text(totalFighting, NamedTextColor.DARK_RED)));
        sender.sendMessage(Component.text("Total Farming Points: ", NamedTextColor.GRAY)
                .append(Component.text(totalFarming, NamedTextColor.GREEN)));
        sender.sendMessage(Component.text("Total Mining Points: ", NamedTextColor.GRAY)
                .append(Component.text(totalMining, NamedTextColor.GRAY)));
        sender.sendMessage(Component.text("Total Athletics Points: ", NamedTextColor.GRAY)
                .append(Component.text(totalAthletics, NamedTextColor.AQUA)));
        sender.sendMessage(Component.text("Players with Building: ", NamedTextColor.GRAY)
                .append(Component.text(playersWithBuilding, NamedTextColor.BLUE)));
        sender.sendMessage(Component.text("Players with Fighting: ", NamedTextColor.GRAY)
                .append(Component.text(playersWithFighting, NamedTextColor.DARK_RED)));
        sender.sendMessage(Component.text("Players with Farming: ", NamedTextColor.GRAY)
                .append(Component.text(playersWithFarming, NamedTextColor.GREEN)));
        sender.sendMessage(Component.text("Players with Mining: ", NamedTextColor.GRAY)
                .append(Component.text(playersWithMining, NamedTextColor.GRAY)));
        sender.sendMessage(Component.text("Players with Athletics: ", NamedTextColor.GRAY)
                .append(Component.text(playersWithAthletics, NamedTextColor.AQUA)));
        sender.sendMessage(Component.text("Players with No Specializations: ", NamedTextColor.GRAY)
                .append(Component.text(playersWithNoSpecializations, NamedTextColor.YELLOW)));
        sender.sendMessage(Component.text("Total Players with Specializations: ", NamedTextColor.GRAY)
                .append(Component.text(totalPlayersWithSpecs, NamedTextColor.GREEN)));
        sender.sendMessage(Component.text("Individual Player Specializations:", NamedTextColor.YELLOW));
        for (org.bukkit.entity.Player player : plugin.getServer().getOnlinePlayers()) {
            int[] specs = plugin.getSpecializationManager().getEachSpecializationLevelOfPlayer(player);
            int total = plugin.getSpecializationManager().getTotalSpecializationPointsOfPlayer(player);
            if (total > 0) {
                sender.sendMessage(Component.text("  " + player.getName() + ": ", NamedTextColor.GRAY)
                        .append(Component.text("Building: " + specs[0], NamedTextColor.BLUE))
                        .append(Component.text(", Fighting: " + specs[1], NamedTextColor.DARK_RED))
                        .append(Component.text(" (Total: " + total + ")", NamedTextColor.GREEN)));
            } else {
                sender.sendMessage(Component.text("  " + player.getName() + ": ", NamedTextColor.GRAY)
                        .append(Component.text("Unspecialized", NamedTextColor.YELLOW)));
            }
        }
    }
    
    private void sendPlayerHelpMessage(CommandSender sender) {
        sender.sendMessage(Component.text("=== Simulation of Life Commands ===", NamedTextColor.GOLD));
        sender.sendMessage(Component.text("/simulationoflife stats ", NamedTextColor.YELLOW)
                .append(Component.text("- Show your specialization statistics", NamedTextColor.GRAY)));
    }
    
    private void sendAdminHelpMessage(CommandSender sender) {
        sender.sendMessage(Component.text("=== Simulation of Life Commands ===", NamedTextColor.GOLD));
        sender.sendMessage(Component.text("/simulationoflife reload ", NamedTextColor.YELLOW)
                .append(Component.text("- Reload the plugin configuration", NamedTextColor.GRAY)));
        sender.sendMessage(Component.text("/simulationoflife status ", NamedTextColor.YELLOW)
                .append(Component.text("- Show plugin status and settings", NamedTextColor.GRAY)));
        sender.sendMessage(Component.text("/simulationoflife perf ", NamedTextColor.YELLOW)
                .append(Component.text("- Show performance statistics", NamedTextColor.GRAY)));
        sender.sendMessage(Component.text("/simulationoflife debug ", NamedTextColor.YELLOW)
                .append(Component.text("- Show debug information", NamedTextColor.GRAY)));
        sender.sendMessage(Component.text("/simulationoflife specs ", NamedTextColor.YELLOW)
                .append(Component.text("- Show specialization statistics", NamedTextColor.GRAY)));
        sender.sendMessage(Component.text("/simulationoflife stats ", NamedTextColor.YELLOW)
                .append(Component.text("- Show your specialization statistics", NamedTextColor.GRAY)));
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            String partial = args[0].toLowerCase();
            if (sender.hasPermission("simulationoflife.admin")) {
                for (String subcommand : Arrays.asList("reload", "status", "perf", "debug", "specs", "stats")) {
                    if (subcommand.startsWith(partial)) {
                        completions.add(subcommand);
                    }
                }
            }
            if (sender.hasPermission("simulationoflife.player")) {
                for (String subcommand : Arrays.asList("stats")) {
                    if (subcommand.startsWith(partial)) {
                        completions.add(subcommand);
                    }
                }
            }
        }        
        return completions;
    }
} 