package com.binkibonk.simulationoflife.utils;

import java.util.concurrent.atomic.AtomicLong;

public class PerformanceMonitor {
    
    private final AtomicLong totalPlayerExhaustions;
    private final AtomicLong totalBlockEvents;
    private final AtomicLong totalEntityHits;
    private long startTime;
    
    public PerformanceMonitor() {
        this.totalPlayerExhaustions = new AtomicLong(0);
        this.totalBlockEvents = new AtomicLong(0);
        this.totalEntityHits = new AtomicLong(0);
        this.startTime = System.currentTimeMillis();
    }
    
    public String getPerformanceStats() {
        long uptime = System.currentTimeMillis() - startTime;
        long playerExhaustions = totalPlayerExhaustions.get();
        long blockEvents = totalBlockEvents.get();
        long entityHits = totalEntityHits.get();
        double playerExhaustionsPerSecond = uptime > 0 ? (playerExhaustions * 1000.0) / uptime : 0;
        double blockEventsPerSecond = uptime > 0 ? (blockEvents * 1000.0) / uptime : 0;
        double entityHitsPerSecond = uptime > 0 ? (entityHits * 1000.0) / uptime : 0;
        return String.format(
            "Uptime: %d seconds | Player Exhaustions: %d (%.2f/sec) | Block Events: %d (%.2f/sec) | Entity Hits: %d (%.2f/sec)",
            uptime / 1000, playerExhaustions, playerExhaustionsPerSecond, blockEvents, blockEventsPerSecond, entityHits, entityHitsPerSecond
        );
    }

    public void resetCounters() {
        totalPlayerExhaustions.set(0);
        totalBlockEvents.set(0);
        totalEntityHits.set(0);
        startTime = System.currentTimeMillis();
    }

    public void recordPlayerExhaustion() {
        totalPlayerExhaustions.incrementAndGet();
    }

    public long getTotalPlayerExhaustions() {
        return totalPlayerExhaustions.get();
    }
   
    public void recordBlockPlacedEvent() {
        totalBlockEvents.incrementAndGet();
    }

    public long getTotalBlockEvents() {
        return totalBlockEvents.get();
    }
    
    public void recordBlockDestroyedEvent() {
        totalBlockEvents.incrementAndGet();
    }

    public void recordEntityHitEvent() {
        totalEntityHits.incrementAndGet();
    }

    public long getTotalEntityHits() {
        return totalEntityHits.get();
    }

    public long getStartTime() {
        return startTime;
    }
} 