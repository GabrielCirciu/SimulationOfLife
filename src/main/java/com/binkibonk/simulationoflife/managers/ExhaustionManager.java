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
    
    // Reduces player food level when building blocks
    // This comment is here for future Builder specialization
    public boolean exhaustPlayerForBuildingBlocks(Player player) {
        if (!plugin.getConfigManager().isEnabled() || 
            player.hasPermission("simulationoflife.bypass") || 
            !canGetExhausted(player)) {
            return false;
        }
        float specializationLevel = plugin.getSpecializationManager().getSpecializationLevelOfPlayer(player, SpecializationManager.SpecializationType.BUILDING);
        float exhaustionDivisor = Math.max(1, specializationLevel / 10);
        float exhaustionAmount = plugin.getConfigManager().getPlaceBlockExhaustionAmount() / exhaustionDivisor;
        return exhaustPlayer(player, exhaustionAmount, "building");
    }
    
    // Reduces player food level when demolishing a block
    // This comment is here for future Builder specialization
    // This is going to be split up in the future to different blocks
    public boolean exhaustPlayerForMiningBlocks(Player player) {
        if (!plugin.getConfigManager().isEnabled() || 
            player.hasPermission("simulationoflife.bypass") || 
            !canGetExhausted(player)) {
            return false;
        }        
        float specializationLevel = plugin.getSpecializationManager().getSpecializationLevelOfPlayer(player, SpecializationManager.SpecializationType.MINING);
        float exhaustionDivisor = Math.max(1, specializationLevel / 10);
        float exhaustionAmount = plugin.getConfigManager().getMiningExhaustionAmount() / exhaustionDivisor;
        return exhaustPlayer(player, exhaustionAmount, "mining");
    }
    
    public boolean exhaustPlayerForFarmingBlocks(Player player) {
        if (!plugin.getConfigManager().isEnabled() || 
            player.hasPermission("simulationoflife.bypass") || 
            !canGetExhausted(player)) {
            return false;
        }
        float specializationLevel = plugin.getSpecializationManager().getSpecializationLevelOfPlayer(player, SpecializationManager.SpecializationType.FARMING);
        float exhaustionDivisor = Math.max(1, specializationLevel / 10);
        float exhaustionAmount = plugin.getConfigManager().getFarmingExhaustionAmount() / exhaustionDivisor;
        return exhaustPlayer(player, exhaustionAmount, "farming");
    }
    
    // Reduces player food level when hitting entities
    // This comment is here for future Fighter specialization
    public boolean exhaustPlayerForHittingEntities(Player player) {
        if (!plugin.getConfigManager().isEnabled() || 
            player.hasPermission("simulationoflife.bypass") || 
            !canGetExhausted(player)) {
            return false;
        }
        float specializationLevel = plugin.getSpecializationManager().getSpecializationLevelOfPlayer(player, SpecializationManager.SpecializationType.FIGHTING);
        float exhaustionDivisor = Math.max(1, specializationLevel / 10);
        float exhaustionAmount = plugin.getConfigManager().getHitEntityExhaustionAmount() / exhaustionDivisor;
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
        if (plugin.getConfigManager().getPlayerMessages()) {
            sendExhaustionMessageToPlayer(player);
        }
        // Log
        if (plugin.getConfigManager().isDebug()) {
            plugin.getLogger().info("Exhausted " + player.getName() + " by " + exhaustionAmount + " due to " + action);
        }
        return true;
    }
    
    private void sendExhaustionMessageToPlayer(Player player) {
        String message = plugin.getConfigManager().getExhaustionMessage();
        message = message.replace("{exhaustion}", String.format("%.1f", player.getExhaustion()));
        message = plugin.getConfigManager().getPrefix() + message;
        Component component = MiniMessage.miniMessage().deserialize(message);
        player.sendMessage(component);
    }
    
    public void clearExhaustionCooldownForPlayer(Player player) {
        UUID playerId = player.getUniqueId();
        lastExhaustion.remove(playerId);
    }

    public void clearAllExhaustionCooldownsForAllPlayers() {
        lastExhaustion.clear();
    }
    
    public int getActiveExhaustionCooldownsForAllPlayers() {
        return lastExhaustion.size();
    }
} 