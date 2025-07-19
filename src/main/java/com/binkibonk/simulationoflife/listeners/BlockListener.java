package com.binkibonk.simulationoflife.listeners;

import com.binkibonk.simulationoflife.SimulationOfLife;
import com.binkibonk.simulationoflife.managers.SpecializationManager;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public class BlockListener implements Listener {
    
    private final SimulationOfLife plugin;
    
    public BlockListener(SimulationOfLife plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockPlaced(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Material blockType = event.getBlock().getType();
        // Check if the block type is exempt from exhausting the player
        if (plugin.getConfigManager().getExemptBlocksFromPlacement().contains(blockType)) {
            return;
        }
        // Check if this block benefits from a axe/hoe/shovel (is a farming block)
        if (isFarmingBlockPlacement(event.getBlock())) {
            plugin.getSpecializationManager().increaseSpecialization(player, SpecializationManager.SpecializationType.FARMING, plugin.getConfigManager().getFarmingPoints());
            boolean playerExhausted = plugin.getExhaustionManager().exhaustPlayerForFarmingBlocks(player);
            if (playerExhausted) {
                plugin.getPerformanceMonitor().recordPlayerExhaustion();
            }
            // Log
            if (plugin.getConfigManager().isDebug()) {
                plugin.getLogger().info("Player " + player.getName() + " placed a FARM block of type " + blockType.name() + " and got exhausted");
            }
        // If it's not a farming block, it's a default building block
        } else {
            plugin.getSpecializationManager().increaseSpecialization(player, SpecializationManager.SpecializationType.BUILDING, plugin.getConfigManager().getBuildingPoints());
            boolean playerExhausted = plugin.getExhaustionManager().exhaustPlayerForBuildingBlocks(player);
            if (playerExhausted) {
                plugin.getPerformanceMonitor().recordPlayerExhaustion();
            }
            // Log
            if (plugin.getConfigManager().isDebug()) {
                plugin.getLogger().info("Player " + player.getName() + " placed a BUILDING block of type " + blockType.name() + " and got exhausted");
            }
        }
        plugin.getSpecializationManager().increaseSpecialization(player, SpecializationManager.SpecializationType.ATHLETICS, plugin.getConfigManager().getAthleticsPoints());
        plugin.getPerformanceMonitor().recordBlockPlacedEvent();
    }
    
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockDestroyed(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Material blockType = event.getBlock().getType();
        // Check if the block type is exempt from exhausting the player
        if (plugin.getConfigManager().getExemptBlocksFromDestruction().contains(blockType)) {
            return;
        }
        // Check if this block benefits from a pickaxe (is a mining block)
        if (isMiningBlock(event.getBlock())) {
            plugin.getSpecializationManager().increaseSpecialization(player, SpecializationManager.SpecializationType.MINING, plugin.getConfigManager().getMiningPoints());
            boolean playerExhausted = plugin.getExhaustionManager().exhaustPlayerForMiningBlocks(player);
            if (playerExhausted) {
                plugin.getPerformanceMonitor().recordPlayerExhaustion();
            }
            // Log
            if (plugin.getConfigManager().isDebug()) {
                plugin.getLogger().info("Player " + player.getName() + " MINED a " + blockType.name() + " block and got exhausted");
            }
        // Check if this block benefits from a axe/hoe/shovel (is a farming block)
        } else if (isFarmingBlock(event.getBlock())) {
            plugin.getSpecializationManager().increaseSpecialization(player, SpecializationManager.SpecializationType.FARMING, plugin.getConfigManager().getFarmingPoints());
            boolean playerExhausted = plugin.getExhaustionManager().exhaustPlayerForFarmingBlocks(player);
            if (playerExhausted) {
                plugin.getPerformanceMonitor().recordPlayerExhaustion();
            }
            // Log
            if (plugin.getConfigManager().isDebug()) {
                plugin.getLogger().info("Player " + player.getName() + " FARMED a " + blockType.name() + " block and got exhausted");
            }
        // If it's not a mining or farming block, it's a default block
        } else {
            boolean playerExhausted = plugin.getExhaustionManager().exhaustPlayerDefault(player);
            if (playerExhausted) {
                plugin.getPerformanceMonitor().recordPlayerExhaustion();
            }
            // Log
            if (plugin.getConfigManager().isDebug()) {
                plugin.getLogger().info("Player " + player.getName() + " DESTROYED a " + blockType.name() + " block and got exhausted");
            }
        }
        plugin.getSpecializationManager().increaseSpecialization(player, SpecializationManager.SpecializationType.ATHLETICS, plugin.getConfigManager().getAthleticsPoints());
        plugin.getPerformanceMonitor().recordBlockDestroyedEvent();
    }
    
    private boolean isMiningBlock(org.bukkit.block.Block block) {
        Material material = block.getType();
        if (Tag.MINEABLE_PICKAXE.isTagged(material)) {
            return true;
        }
        return false;
    }

    private boolean isFarmingBlock(org.bukkit.block.Block block) {
        Material material = block.getType();
        if (Tag.MINEABLE_AXE.isTagged(material) || Tag.MINEABLE_HOE.isTagged(material) || Tag.MINEABLE_SHOVEL.isTagged(material)
        || Tag.CROPS.isTagged(material) || Tag.LEAVES.isTagged(material) || Tag.SAPLINGS.isTagged(material) || Tag.FLOWERS.isTagged(material)
        || Tag.SMALL_FLOWERS.isTagged(material)) {
            return true;
        }
        return false;
    }

    private boolean isFarmingBlockPlacement(org.bukkit.block.Block block) {
        Material material = block.getType();
        if (Tag.MINEABLE_HOE.isTagged(material) || Tag.MINEABLE_SHOVEL.isTagged(material)
        || Tag.CROPS.isTagged(material) || Tag.LEAVES.isTagged(material) || Tag.SAPLINGS.isTagged(material) || Tag.FLOWERS.isTagged(material)
        || Tag.SMALL_FLOWERS.isTagged(material)) {
            return true;
        }
        return false;
    }
} 