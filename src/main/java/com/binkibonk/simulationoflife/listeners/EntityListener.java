package com.binkibonk.simulationoflife.listeners;

import com.binkibonk.simulationoflife.SimulationOfLife;
import com.binkibonk.simulationoflife.managers.SpecializationManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class EntityListener implements Listener {
    
    private final SimulationOfLife plugin;
    
    public EntityListener(SimulationOfLife plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        // Check if the damager is a player
        if (!(event.getDamager() instanceof Player)) {
            return;
        }
        Player player = (Player) event.getDamager();
        boolean playerExhausted = plugin.getExhaustionManager().exhaustPlayerForHittingEntities(player);
        if (playerExhausted) {
            plugin.getPerformanceMonitor().recordPlayerExhaustion();
        }
        // Log
        if (plugin.getConfigManager().isDebug()) {
            plugin.getLogger().info("Player " + player.getName() + " HIT an entity and got exhausted");
        }
        plugin.getSpecializationManager().increaseSpecialization(player, SpecializationManager.SpecializationType.FIGHTING, plugin.getConfigManager().getFightingPoints());
        plugin.getSpecializationManager().increaseSpecialization(player, SpecializationManager.SpecializationType.ATHLETICS, plugin.getConfigManager().getAthleticsPoints());
        plugin.getPerformanceMonitor().recordEntityHitEvent();
    }
} 