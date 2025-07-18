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
        int athleticsLevel = plugin.getSpecializationManager().getSpecializationLevelOfPlayer(
            player, 
            com.binkibonk.simulationoflife.managers.SpecializationManager.SpecializationType.ATHLETICS
        );
        float speedBoost = athleticsLevel * plugin.getConfigManager().getRunSpeedIncreasePerLevel();
        float newSpeed = plugin.getConfigManager().getBaseRunSpeed() + speedBoost;
        // Preventing speed from exceeding Minecraft's limits (-1.0 to 1.0)
        newSpeed = Math.max(-1.0f, Math.min(1.0f, newSpeed));
        player.setWalkSpeed(newSpeed);
    }
    
    private void resetRunSpeed(Player player) {
        player.setWalkSpeed(0.2f);
    }
} 