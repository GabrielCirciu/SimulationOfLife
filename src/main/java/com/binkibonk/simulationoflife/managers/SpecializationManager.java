package com.binkibonk.simulationoflife.managers;

import com.binkibonk.simulationoflife.SimulationOfLife;
import org.bukkit.entity.Player;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.UUID;

public class SpecializationManager {
    
    public enum SpecializationType {        
        BUILDING(0, "Building"),
        FIGHTING(1, "Fighting"),
        FARMING(2, "Farming"),
        MINING(3, "Mining"),
        ATHLETICS(4, "Athletics"); // TBD, this would modify running and jumping exhaustion
        // Other ideas: Crafter, Chef, Fisher, Lumberjack, Artificer / Thaumaturge
        private final int index;
        private final String displayName;
        SpecializationType(int index, String displayName) {
            this.index = index;
            this.displayName = displayName;
        }
        public int getIndex() {
            return index;
        }
        public String getDisplayName() {
            return displayName;
        }
        public static int getTotalTypes() {
            return values().length;
        }
    }
    
    private final SimulationOfLife plugin;
    private final Map<UUID, int[]> playerSpecializations;
    private final File dataFile;
    private final FileConfiguration dataConfig;
    
    public SpecializationManager(SimulationOfLife plugin) {
        this.plugin = plugin;
        this.playerSpecializations = new ConcurrentHashMap<>();
        this.dataFile = new File(plugin.getDataFolder(), "specializations.yml");
        this.dataConfig = YamlConfiguration.loadConfiguration(dataFile);
    }
    
    private int[] getOrCreateSpecializationsForPlayer(UUID playerId) {
        return playerSpecializations.computeIfAbsent(playerId, k -> new int[SpecializationType.getTotalTypes()]);
    }
    
    public int increaseSpecialization(Player player, SpecializationType type, int amount) {
        UUID playerId = player.getUniqueId();
        int[] specializations = getOrCreateSpecializationsForPlayer(playerId);
        int currentLevel = specializations[type.getIndex()];
        int newLevel = currentLevel + amount;
        int maxLevel = plugin.getConfigManager().getMaxPoints();
        if (newLevel > maxLevel) {
            return maxLevel;
        }
        // Redistributing points if player has more points that max level overall
        // Redistribution happens by lowering the lowest larger than 0specialization level by the amount
        int totalPoints = getTotalSpecializationPointsOfPlayer(player);
        if (totalPoints + amount > maxLevel) {
            int lowestLevel = maxLevel;
            int lowestLevelIndex = 0;
            for (int i = 0; i < specializations.length - 1; i++) {
                if (specializations[i] < lowestLevel && specializations[i] > 0 && i != type.getIndex()) {
                    lowestLevel = specializations[i];
                    lowestLevelIndex = i;
                }
            }
            specializations[lowestLevelIndex] = lowestLevel - amount;
        }
        specializations[type.getIndex()] = newLevel;
        // Log
        if (plugin.getConfigManager().isDebug()) {
            plugin.getLogger().info("Increased " + type.getDisplayName() + " specialization for " + player.getName() + 
                " by " + amount + " (from " + currentLevel + " to " + newLevel + ")");
        }
        return newLevel;
    }

    public int getSpecializationLevelOfPlayer(Player player, SpecializationType type) {
        UUID playerId = player.getUniqueId();
        int[] specializations = playerSpecializations.get(playerId);
        if (getTotalSpecializationPointsOfPlayer(player) < 10 || specializations == null) {
            return 0;
        }
        return specializations[type.getIndex()];
    }
    
    public int[] getEachSpecializationLevelOfPlayer(Player player) {
        UUID playerId = player.getUniqueId();
        int[] specializations = playerSpecializations.get(playerId);
        if (specializations == null) {
            return new int[SpecializationType.getTotalTypes()];
        }
        return specializations.clone(); // Return a copy to prevent external modification
    }
    
    public int getTotalSpecializationPointsOfPlayer(Player player) {
        int[] specializations = getEachSpecializationLevelOfPlayer(player);
        int total = 0;
        for (int level : specializations) {
            total += level;
        }
        // Reduce total by the athleticism stat, as it wont count towards the total
        total -= specializations[SpecializationType.ATHLETICS.getIndex()];
        return total;
    }
    
    public int getCountOfPlayersWithAnySpecializations() {
        return playerSpecializations.size();
    }
    
    public int getCountOfPlayersWithSpecificSpecialization(SpecializationType type) {
        int count = 0;
        for (int[] specializations : playerSpecializations.values()) {
            if (specializations[type.getIndex()] > 0) {
                count++;
            }
        }
        return count;
    }
    
    public int getAllPlayersTotalSpecializationPoints(SpecializationType type) {
        int total = 0;
        for (int[] specializations : playerSpecializations.values()) {
            total += specializations[type.getIndex()];
        }
        return total;
    }
    
    public int getCountOfPlayersWithNoSpecializations() {
        int count = 0;
        for (int[] specializations : playerSpecializations.values()) {
            int total = 0;
            for (int level : specializations) {
                total += level;
            }
            if (total == 0) {
                count++;
            }
        }
        return count;
    }
    
    public void clearSpecializationsForPlayer(Player player) {
        playerSpecializations.remove(player.getUniqueId());
    }
    
    public void clearAllSpecializations() {
        playerSpecializations.clear();
    }
    
    public void saveAllSpecializations() {
        try {
            dataConfig.set("specializations", null);
            for (Map.Entry<UUID, int[]> entry : playerSpecializations.entrySet()) {
                UUID playerId = entry.getKey();
                int[] specializations = entry.getValue();
                String playerSection = "specializations." + playerId.toString();
                for (int i = 0; i < specializations.length; i++) {
                    dataConfig.set(playerSection + "." + i, specializations[i]);
                }
            }
            dataConfig.save(dataFile);
            // Log
            if (plugin.getConfigManager().isDebug()) {
                plugin.getLogger().info("Saved specializations for " + playerSpecializations.size() + " players");
            }
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to save specializations: " + e.getMessage());
        }
    }
    
    public void loadAllSpecializations() {
        try {
            if (!dataFile.exists()) {
                if (plugin.getConfigManager().isDebug()) {
                    plugin.getLogger().info("No specializations data file found, starting fresh");
                }
                return;
            }
            ConfigurationSection specializationsSection = dataConfig.getConfigurationSection("specializations");
            if (specializationsSection == null) {
                if (plugin.getConfigManager().isDebug()) {
                    plugin.getLogger().info("No specializations data found in file");
                }
                return;
            }
            int loadedCount = 0;
            for (String playerIdString : specializationsSection.getKeys(false)) {
                try {
                    UUID playerId = UUID.fromString(playerIdString);
                    ConfigurationSection playerSection = specializationsSection.getConfigurationSection(playerIdString);
                    if (playerSection != null) {
                        int[] specializations = new int[SpecializationType.getTotalTypes()];
                        for (int i = 0; i < specializations.length; i++) {
                            specializations[i] = playerSection.getInt(String.valueOf(i), 0);
                        }
                        playerSpecializations.put(playerId, specializations);
                        loadedCount++;
                    }
                } catch (IllegalArgumentException e) {
                    plugin.getLogger().warning("Invalid UUID in specializations file: " + playerIdString);
                }
            }
            // Log
            if (plugin.getConfigManager().isDebug()) {
                plugin.getLogger().info("Loaded specializations for " + loadedCount + " players");
            }
        } catch (Exception e) {
            plugin.getLogger().severe("Failed to load specializations: " + e.getMessage());
        }
    }
} 