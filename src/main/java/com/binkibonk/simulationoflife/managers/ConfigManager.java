package com.binkibonk.simulationoflife.managers;

import com.binkibonk.simulationoflife.SimulationOfLife;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ConfigManager {
    // Plugin instance
    private final SimulationOfLife plugin;
    // General settings
    private boolean enabled;
    private boolean debug;
    private boolean playerNotifications;
    private boolean adminNotifications;
    // Exhaustion settings
    private float placeBlockExhaustionAmount;
    private float miningExhaustionAmount;
    private float farmingExhaustionAmount;
    private float hitEntityExhaustionAmount;
    private float defaultExhaustionAmount;
    private float minimumFoodLevel;
    private long exhaustionCooldown;
    // Specialization settings
    private int buildingPoints;
    private int fightingPoints;
    private int athleticsPoints;
    private int miningPoints;
    private int farmingPoints;
    private int maxPoints;
    private int autoSaveInterval;
    // Run speed settings
    private float baseRunSpeed;
    private float maxRunSpeed;
    // Block exemptions
    private Set<Material> exemptBlocksFromPlacement;
    private Set<Material> exemptBlocksFromDestruction;
    // Admin notifications
    private String prefix;
    private String exhaustionNotification;
    private String pluginReloadedMessage;
    private String pluginStatusMessage;
    private String pluginDisabledMessage;
    
    public ConfigManager(SimulationOfLife plugin) {
        this.plugin = plugin;
        this.exemptBlocksFromPlacement = new HashSet<>();
        this.exemptBlocksFromDestruction = new HashSet<>();
    }
    
    public void loadConfig() {
        plugin.saveDefaultConfig();
        plugin.reloadConfig();
        // Load general settings
        ConfigurationSection general = plugin.getConfig().getConfigurationSection("general");
        if (general != null) {
            this.enabled = general.getBoolean("enabled", true);
            this.debug = general.getBoolean("debug", false);
            this.playerNotifications = general.getBoolean("player-notifications", false);
            this.adminNotifications = general.getBoolean("admin-notifications", true);
        }
        // Load exhaustion settings
        ConfigurationSection exhaustion = plugin.getConfig().getConfigurationSection("exhaustion");
        if (exhaustion != null) {
            this.placeBlockExhaustionAmount = (float) exhaustion.getDouble("place-block", 1.0);
            this.miningExhaustionAmount = (float) exhaustion.getDouble("mining-block", 1.0);
            this.farmingExhaustionAmount = (float) exhaustion.getDouble("farming-block", 1.0);
            this.hitEntityExhaustionAmount = (float) exhaustion.getDouble("hit-entity", 1.0);
            this.defaultExhaustionAmount = (float) exhaustion.getDouble("default", 1.0);
            this.minimumFoodLevel = (float) exhaustion.getDouble("minimum-food-level", 0.0);
            this.exhaustionCooldown = exhaustion.getLong("cooldown", 100);
        }
        // Load specialization settings
        ConfigurationSection specialization = plugin.getConfig().getConfigurationSection("specialization");
        if (specialization != null) {
            this.buildingPoints = specialization.getInt("building", 1);
            this.fightingPoints = specialization.getInt("fighting", 1);
            this.athleticsPoints = specialization.getInt("athletics", 1);
            this.miningPoints = specialization.getInt("mining", 1);
            this.farmingPoints = specialization.getInt("farming", 1);
            this.maxPoints = specialization.getInt("max-points", 100);
            this.autoSaveInterval = specialization.getInt("auto-save-interval", 5);
        }
        // Load run speed settings
        ConfigurationSection runSpeed = plugin.getConfig().getConfigurationSection("run-speed");
        if (runSpeed != null) {
            this.baseRunSpeed = (float) runSpeed.getDouble("base-speed", 0.2);
            this.maxRunSpeed = (float) runSpeed.getDouble("max-speed", 1.0);
        }
        // Load block exemptions
        ConfigurationSection blocks = plugin.getConfig().getConfigurationSection("blocks");
        if (blocks != null) {
            loadBlockExemptions("place-exempt", exemptBlocksFromPlacement);
            loadBlockExemptions("destroy-exempt", exemptBlocksFromDestruction);
        }
        // Load admin messages
        ConfigurationSection adminMessages = plugin.getConfig().getConfigurationSection("admin-notification-messages");
        if (adminMessages != null) {
            this.prefix = adminMessages.getString("prefix", "<dark_gray>[<red>Simulation of Life</red>]</dark_gray> ");
            this.exhaustionNotification = adminMessages.getString("exhaustion-reduced", "<yellow>Exhaustion: <red>{exhaustion}</red>/<green>4</green></yellow>");
            this.pluginReloadedMessage = adminMessages.getString("plugin-reloaded", "<green>Plugin configuration reloaded!</green>");
            this.pluginStatusMessage = adminMessages.getString("plugin-status", "<green>Plugin is <dark_green>enabled</dark_green></green>");
            this.pluginDisabledMessage = adminMessages.getString("plugin-disabled", "<red>Plugin is <dark_red>disabled</dark_red></red>");
        }
        if (debug) {
            plugin.getLogger().info("Configuration loaded successfully!");
            plugin.getLogger().info("Place block exhaustion amount: " + placeBlockExhaustionAmount);
            plugin.getLogger().info("Mining block exhaustion amount: " + miningExhaustionAmount);
            plugin.getLogger().info("Hit entity exhaustion amount: " + hitEntityExhaustionAmount);
            plugin.getLogger().info("Exhaustion cooldown: " + exhaustionCooldown + "ms");
            plugin.getLogger().info("Exempt blocks from placing: " + exemptBlocksFromPlacement.size());
            plugin.getLogger().info("Exempt blocks from destroying: " + exemptBlocksFromDestruction.size());
        }
    }
    
    private void loadBlockExemptions(String path, Set<Material> exemptSet) {
        List<String> exemptList = plugin.getConfig().getStringList("blocks." + path);
        exemptSet.clear();
        for (String blockName : exemptList) {
            try {
                Material material = Material.valueOf(blockName.toUpperCase());
                exemptSet.add(material);
            } catch (IllegalArgumentException e) {
                plugin.getLogger().warning("Invalid material name in config: " + blockName);
            }
        }
    }

    public boolean getPlayerNotifications() {
        return playerNotifications;
    }
    
    public boolean getAdminNotifications() {
        return adminNotifications;
    }
    
    public boolean isEnabled() {
        return enabled;
    }
    
    public boolean isDebug() {
        return debug;
    }
    
    public float getPlaceBlockExhaustionAmount() {
        return placeBlockExhaustionAmount;
    }
    
    public float getMiningExhaustionAmount() {
        return miningExhaustionAmount;
    }
    
    public float getFarmingExhaustionAmount() {
        return farmingExhaustionAmount;
    }
    
    public float getHitEntityExhaustionAmount() {
        return hitEntityExhaustionAmount;
    }
    
    public float getDefaultExhaustionAmount() {
        return defaultExhaustionAmount;
    }
    
    public float getMinimumFoodLevel() {
        return minimumFoodLevel;
    }
    
    public long getExhaustionCooldown() {
        return exhaustionCooldown;
    }

    public int getBuildingPoints() {
        return buildingPoints;
    }
    
    public int getFightingPoints() {
        return fightingPoints;
    }

    public int getAthleticsPoints() {
        return athleticsPoints;
    }

    public int getMiningPoints() {
        return miningPoints;
    }

    public int getFarmingPoints() {
        return farmingPoints;
    }

    public int getMaxPoints() {
        return maxPoints;
    }
    
    public int getAutoSaveInterval() {
        return autoSaveInterval;
    }
    
    public float getBaseRunSpeed() {
        return baseRunSpeed;
    }
    
    public float getMaxRunSpeed() {
        return maxRunSpeed;
    }
    
    public Set<Material> getExemptBlocksFromPlacement() {
        return exemptBlocksFromPlacement;
    }
    
    public Set<Material> getExemptBlocksFromDestruction() {
        return exemptBlocksFromDestruction;
    }
    
    public String getPrefix() {
        return prefix;
    }
    
    public String getExhaustionNotification() {
        return exhaustionNotification;
    }
    
    public String getPluginReloadedMessage() {
        return pluginReloadedMessage;
    }
    
    public String getPluginStatusMessage() {
        return pluginStatusMessage;
    }
    
    public String getPluginDisabledMessage() {
        return pluginDisabledMessage;
    }
} 