package com.stormai.plugin;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class FireMazePlugin extends JavaPlugin {

    private double damageRate = 1.0;
    private final Set<Location> heatZones = new HashSet<>();
    private final Set<Location> fireMobZones = new HashSet<>();
    private final Set<Location> triggerBlocks = new HashSet<>();
    private final Set<Location> flameWalls = new HashSet<>();

    @Override
    public void onEnable() {
        saveDefaultConfig();
        loadMazeData();
        getLogger().info("FireMaze Plugin has been enabled!");
        getCommand("firemaze").setExecutor(new CommandHandler(this));
        getServer().getPluginManager().registerEvents(new MazeListener(this), this);
    }

    @Override
    public void onDisable() {
        saveMazeData();
        getLogger().info("FireMaze Plugin has been disabled.");
    }

    public void createMaze(Player player) {
        World world = player.getWorld();
        Location center = player.getLocation();

        // Create a simple maze structure
        int size = 20;
        for (int x = -size; x <= size; x++) {
            for (int z = -size; z <= size; z++) {
                if (x == -size || x == size || z == -size || z == size) {
                    world.getBlockAt(center.getBlockX() + x, center.getBlockY(), center.getBlockZ() + z).setType(Material.NETHERRACK);
                }
            }
        }

        // Add heat zones
        for (int x = -size + 2; x < size - 2; x += 4) {
            for (int z = -size + 2; z < size - 2; z += 4) {
                Block b = world.getBlockAt(center.getBlockX() + x, center.getBlockY() - 1, center.getBlockZ() + z);
                b.setType(Material.MAGMA_BLOCK);
                heatZones.add(b.getLocation());
            }
        }

        // Add lava traps
        for (int i = 0; i < 5; i++) {
            int x = (int) (Math.random() * (size - 4) - size / 2);
            int z = (int) (Math.random() * (size - 4) - size / 2);
            Block b = world.getBlockAt(center.getBlockX() + x, center.getBlockY() - 1, center.getBlockZ() + z);
            b.setType(Material.LAVA);
        }

        // Add fire mob spawn zones
        for (int i = 0; i < 10; i++) {
            int x = (int) (Math.random() * (size - 6) - size / 2);
            int z = (int) (Math.random() * (size - 6) - size / 2);
            Block b = world.getBlockAt(center.getBlockX() + x, center.getBlockY(), center.getBlockZ() + z);
            fireMobZones.add(b.getLocation());
        }

        // Add trigger blocks
        Block trigger = world.getBlockAt(center.getBlockX() + size/2, center.getBlockY(), center.getBlockZ());
        trigger.setType(Material.REDSTONE_BLOCK);
        triggerBlocks.add(trigger.getLocation());

        // Add flame walls (timed)
        for (int x = -size + 2; x <= size - 2; x += 2) {
            Block b = world.getBlockAt(center.getBlockX() + x, center.getBlockY() + 1, center.getBlockZ() + size - 2);
            b.setType(Material.CAMPFIRE);
            flameWalls.add(b.getLocation());
        }

        getLogger().info("Maze created at " + center.getBlockX() + ", " + center.getBlockZ());
    }

    public void showMazeInfo(Player player) {
        player.sendMessage("FireMaze Info:");
        player.sendMessage("Heat Zones: " + heatZones.size());
        player.sendMessage("Fire Mob Zones: " + fireMobZones.size());
        player.sendMessage("Trigger Blocks: " + triggerBlocks.size());
        player.sendMessage("Flame Walls: " + flameWalls.size());
        player.sendMessage("Damage Rate: " + damageRate);
    }

    public void setDamageRate(Player player, double rate) {
        damageRate = rate;
        player.sendMessage("Damage rate set to: " + rate);
    }

    public boolean isHeatZone(Block block) {
        return heatZones.contains(block.getLocation());
    }

    public boolean isFireMobZone(Location loc) {
        return fireMobZones.contains(loc);
    }

    public boolean isTriggerBlock(Block block) {
        return triggerBlocks.contains(block.getLocation());
    }

    public boolean isNearFlameWall(Location loc) {
        for (Location flame : flameWalls) {
            if (flame.distance(loc) < 3) {
                return true;
            }
        }
        return false;
    }

    public void activateTrigger(Player player, Block block) {
        player.sendMessage("Trigger activated! Flame walls ignited.");
        for (Location flame : flameWalls) {
            Block fBlock = flame.getBlock();
            fBlock.setType(Material.FIRE);
            // Remove fire after 10 seconds
            new BukkitRunnable() {
                public void run() {
                    if (fBlock.getType() == Material.FIRE) {
                        fBlock.setType(Material.AIR);
                    }
                }
            }.runTaskLater(this, 200);
        }
    }

    public double getDamageRate() {
        return damageRate;
    }

    private void loadMazeData() {
        FileConfiguration config = getConfig();
        damageRate = config.getDouble("damage-rate", 1.0);
    }

    private void saveMazeData() {
        FileConfiguration config = getConfig();
        config.set("damage-rate", damageRate);
        saveConfig();
    }
}