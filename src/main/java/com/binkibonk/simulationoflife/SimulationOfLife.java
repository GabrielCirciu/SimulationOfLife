package com.binkibonk.simulationoflife;

import com.binkibonk.simulationoflife.commands.SimulationOfLifeCommand;
import com.binkibonk.simulationoflife.listeners.BlockListener;
import com.binkibonk.simulationoflife.listeners.EntityListener;
import com.binkibonk.simulationoflife.listeners.RunListener;
import com.binkibonk.simulationoflife.managers.ConfigManager;
import com.binkibonk.simulationoflife.managers.ExhaustionManager;
import com.binkibonk.simulationoflife.managers.SpecializationManager;
import com.binkibonk.simulationoflife.utils.PerformanceMonitor;
import org.bukkit.plugin.java.JavaPlugin;

public class SimulationOfLife extends JavaPlugin {
    
    private static SimulationOfLife instance;
    private ConfigManager configManager;
    private ExhaustionManager exhaustionManager;
    private SpecializationManager specializationManager;
    private PerformanceMonitor performanceMonitor;
    private Object autoSaveTask;
    
    @Override
    public void onEnable() {
        try {
            instance = this;
            // Initialize managers
            this.configManager = new ConfigManager(this);
            this.exhaustionManager = new ExhaustionManager(this);
            this.specializationManager = new SpecializationManager(this);
            this.performanceMonitor = new PerformanceMonitor();
            configManager.loadConfig();
            // Load specializations
            specializationManager.loadAllSpecializations();
            // Register listeners
            getServer().getPluginManager().registerEvents(new BlockListener(this), this);
            getServer().getPluginManager().registerEvents(new EntityListener(this), this);
            getServer().getPluginManager().registerEvents(new RunListener(this), this);
            // Set commands
            SimulationOfLifeCommand commandExecutor = new SimulationOfLifeCommand(this);
            getCommand("simulationoflife").setExecutor(commandExecutor);
            getCommand("simulationoflife").setTabCompleter(commandExecutor);
            // Saving
            startAutoSaveTask();
            // Log
            getLogger().info("Simulation of Life has been enabled!");
        } catch (Exception e) {
            getLogger().severe("Failed to enable Simulation of Life plugin: " + e.getMessage());
            e.printStackTrace();
            // Disable the plugin
            getServer().getPluginManager().disablePlugin(this);
        }
    }
    
    @Override
    public void onDisable() {
        stopAutoSaveTask();
        // Save specializations
        if (specializationManager != null) {
            specializationManager.saveAllSpecializations();
        }
        // Clean up resources
        if (exhaustionManager != null) {
            exhaustionManager.clearAllExhaustionCooldownsForAllPlayers();
        }
        // Log
        getLogger().info("Simulation of Life has been disabled!");
    }
    
    public static SimulationOfLife getInstance() {
        return instance;
    }
    
    public ConfigManager getConfigManager() {
        return configManager;
    }
    
    public ExhaustionManager getExhaustionManager() {
        return exhaustionManager;
    }
    
    public SpecializationManager getSpecializationManager() {
        return specializationManager;
    }
    
    public PerformanceMonitor getPerformanceMonitor() {
        return performanceMonitor;
    }
    
    private void startAutoSaveTask() {
        int interval = configManager.getAutoSaveInterval();
        if (interval <= 0) {
            // Log
            if (configManager.isDebug()) {
                getLogger().info("Auto-save disabled (interval set to 0)");
            }
            return;
        }
        // Convert minutes to ticks (20 ticks per second, 60 seconds per minute)
        long ticks = interval * 20L * 60L;
        // Use GlobalRegionScheduler for Folia compatibility
        autoSaveTask = getServer().getGlobalRegionScheduler().runAtFixedRate(this, (task) -> {
            try {
                if (specializationManager != null) {
                    specializationManager.saveAllSpecializations();
                    // Log
                    if (configManager.isDebug()) {
                        getLogger().info("Auto-saved specializations for players");
                    }
                }
            } catch (Exception e) {
                getLogger().severe("Error during auto-save: " + e.getMessage());
            }
        }, ticks, ticks);
        // Log
        if (configManager.isDebug()) {
            getLogger().info("Auto-save task started with " + interval + " minute interval");
        }
    }
    
    private void stopAutoSaveTask() {
        if (autoSaveTask != null) {
            // Cancel the task using reflection to handle different scheduler types
            try {
                autoSaveTask.getClass().getMethod("cancel").invoke(autoSaveTask);
            } catch (Exception e) {
                getLogger().warning("Could not cancel auto-save task: " + e.getMessage());
            }
            autoSaveTask = null;
            // Log
            if (configManager.isDebug()) {
                getLogger().info("Auto-save task stopped");
            }
        }
    }
} 