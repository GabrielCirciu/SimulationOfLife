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
    
    @Override
    public void onEnable() {
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
        getLogger().info("Simulation of Life has been enabled!");
    }
    
    @Override
    public void onDisable() {
        // Save specializations
        if (specializationManager != null) {
            specializationManager.saveAllSpecializations();
        }
        // Clean up resources
        if (exhaustionManager != null) {
            exhaustionManager.clearAllExhaustionCooldownsForAllPlayers();
        }
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
} 