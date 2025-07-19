package com.binkibonk.simulationoflife.listeners;

import com.binkibonk.simulationoflife.SimulationOfLife;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSprintEvent;

public class RunListener implements Listener {
    
    private final SimulationOfLife plugin;
    
    public RunListener(SimulationOfLife plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerToggleSprint(PlayerToggleSprintEvent event) {
        Player player = event.getPlayer();
        boolean isSprinting = event.isSprinting();
        if (isSprinting) {
            applyRunSpeedBoost(player);
        } else {
            resetRunSpeed(player);
        }
    }
    
    private void applyRunSpeedBoost(Player player) {
        float baseSpeed = plugin.getConfigManager().getBaseRunSpeed();
        float maxSpecializationLevel = plugin.getConfigManager().getMaxPoints();
        float maxSpeed = plugin.getConfigManager().getMaxRunSpeed();
        int athleticsLevel = plugin.getSpecializationManager().getSpecializationLevelOfPlayer(
            player,
            com.binkibonk.simulationoflife.managers.SpecializationManager.SpecializationType.ATHLETICS
        );
        float newSpeed = baseSpeed + (athleticsLevel / maxSpecializationLevel) * (maxSpeed - baseSpeed);
        player.setWalkSpeed(newSpeed);
        // Log
        if (plugin.getConfigManager().isDebug()) {
            plugin.getLogger().info("Player " + player.getName() + " is sprinting and got a speed of " + newSpeed +
            " with a base speed of " + baseSpeed + " and a max speed of " + maxSpeed +
            " with a max level of " + maxSpecializationLevel + " and a current level of " + athleticsLevel);
        }
    }
    
    private void resetRunSpeed(Player player) {
        float baseSpeed = plugin.getConfigManager().getBaseRunSpeed();
        player.setWalkSpeed(baseSpeed);
        // Log
        if (plugin.getConfigManager().isDebug()) {
            plugin.getLogger().info("Player " + player.getName() + " is not sprinting and got a speed of " + baseSpeed);
        }
    }
} 