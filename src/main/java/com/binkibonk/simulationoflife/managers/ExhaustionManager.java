package com.binkibonk.simulationoflife.managers;

import com.binkibonk.simulationoflife.SimulationOfLife;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.UUID;

public class ExhaustionManager {
    
    private final SimulationOfLife plugin;
    private final Map<UUID, Long> lastExhaustion;
    
    public ExhaustionManager(SimulationOfLife plugin) {
        this.plugin = plugin;
        this.lastExhaustion = new ConcurrentHashMap<>();
    }
    
    public boolean exhaustPlayerForBuildingBlocks(Player player) {
        if (!plugin.getConfigManager().isEnabled() || 
            player.hasPermission("simulationoflife.bypass") || 
            !canGetExhausted(player)) {
            return false;
        }
        float specializationLevel = plugin.getSpecializationManager().getSpecializationLevelOfPlayer(player, SpecializationManager.SpecializationType.BUILDING);
        float maxSpecializationLevel = plugin.getConfigManager().getMaxPoints();
        float exhaustionAmount = plugin.getConfigManager().getPlaceBlockExhaustionAmount() * (1 - specializationLevel / maxSpecializationLevel);
        return exhaustPlayer(player, exhaustionAmount, "building");
    }
    
    public boolean exhaustPlayerForMiningBlocks(Player player) {
        if (!plugin.getConfigManager().isEnabled() || 
            player.hasPermission("simulationoflife.bypass") || 
            !canGetExhausted(player)) {
            return false;
        }        
        float specializationLevel = plugin.getSpecializationManager().getSpecializationLevelOfPlayer(player, SpecializationManager.SpecializationType.MINING);
        float maxSpecializationLevel = plugin.getConfigManager().getMaxPoints();
        float exhaustionAmount = plugin.getConfigManager().getMiningExhaustionAmount() * (1 - specializationLevel / maxSpecializationLevel);
        return exhaustPlayer(player, exhaustionAmount, "mining");
    }
    
    public boolean exhaustPlayerForFarmingBlocks(Player player) {
        if (!plugin.getConfigManager().isEnabled() || 
            player.hasPermission("simulationoflife.bypass") || 
            !canGetExhausted(player)) {
            return false;
        }
        float specializationLevel = plugin.getSpecializationManager().getSpecializationLevelOfPlayer(player, SpecializationManager.SpecializationType.FARMING);
        float maxSpecializationLevel = plugin.getConfigManager().getMaxPoints();
        float exhaustionAmount = plugin.getConfigManager().getFarmingExhaustionAmount() * (1 - specializationLevel / maxSpecializationLevel);
        return exhaustPlayer(player, exhaustionAmount, "farming");
    }
    
    public boolean exhaustPlayerForHittingEntities(Player player) {
        if (!plugin.getConfigManager().isEnabled() || player.hasPermission("simulationoflife.bypass") || !canGetExhausted(player)) {
            return false;
        }
        float specializationLevel = plugin.getSpecializationManager().getSpecializationLevelOfPlayer(player, SpecializationManager.SpecializationType.FIGHTING);
        float maxSpecializationLevel = plugin.getConfigManager().getMaxPoints();
        float exhaustionAmount = plugin.getConfigManager().getHitEntityExhaustionAmount() * (1 - specializationLevel / maxSpecializationLevel);
        return exhaustPlayer(player, exhaustionAmount, "hitting");
    }

    public boolean exhaustPlayerDefault(Player player) {
        if (!plugin.getConfigManager().isEnabled() || 
            player.hasPermission("simulationoflife.bypass") || 
            !canGetExhausted(player)) {
            return false;
        }
        float exhaustionAmount = plugin.getConfigManager().getDefaultExhaustionAmount();
        return exhaustPlayer(player, exhaustionAmount, "default");
    }
    
    // Checks if in Survival, Cooldown, and Hunger > Minimum
    private boolean canGetExhausted(Player player) {
        if (player.getGameMode() != org.bukkit.GameMode.SURVIVAL) {
            return false;
        }
        long exhaustionCooldown = plugin.getConfigManager().getExhaustionCooldown();
        if (exhaustionCooldown > 0) {
            UUID playerId = player.getUniqueId();
            long lastExhaustionTime = lastExhaustion.getOrDefault(playerId, 0L);
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastExhaustionTime < exhaustionCooldown) {
                return false;
            }
        }
        float currentFoodLevel = player.getFoodLevel();
        float minimumFoodLevel = plugin.getConfigManager().getMinimumFoodLevel();
        return currentFoodLevel > minimumFoodLevel;
    }
    
    private boolean exhaustPlayer(Player player, float exhaustionAmount, String action) {
        UUID playerId = player.getUniqueId();
        float currentExhaustion = player.getExhaustion();
        player.setExhaustion(currentExhaustion + exhaustionAmount);
        // Update cooldown
        if (plugin.getConfigManager().getExhaustionCooldown() > 0) {
            lastExhaustion.put(playerId, System.currentTimeMillis());
        }
        if (plugin.getConfigManager().getPlayerNotifications()) {
            sendExhaustionNotificationToPlayer(player);
        }
        // Log
        if (plugin.getConfigManager().isDebug()) {
            plugin.getLogger().info("Exhausted " + player.getName() + " by " + exhaustionAmount + " due to " + action);
        }
        return true;
    }
    
    private void sendExhaustionNotificationToPlayer(Player player) {
        String message = plugin.getConfigManager().getExhaustionNotification();
        message = message.replace("{exhaustion}", String.format("%.1f", player.getExhaustion()));
        message = plugin.getConfigManager().getPrefix() + message;
        Component component = MiniMessage.miniMessage().deserialize(message);
        player.sendMessage(component);
    }

    public int getActiveExhaustionCooldownsForAllPlayers() {
        return lastExhaustion.size();
    }
    
    // To be implemented in the future
    public void clearExhaustionCooldownForPlayer(Player player) {
        UUID playerId = player.getUniqueId();
        lastExhaustion.remove(playerId);
    }

    // To be implemented in the future
    public void clearAllExhaustionCooldownsForAllPlayers() {
        lastExhaustion.clear();
    }
} 