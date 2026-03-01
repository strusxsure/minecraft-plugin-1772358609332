package com.stormai.plugin;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class MazeListener implements Listener {

    private final FireMazePlugin plugin;
    private final Set<UUID> inMazePlayers = new HashSet<>();

    public MazeListener(FireMazePlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (!inMazePlayers.contains(player.getUniqueId())) {
            return;
        }

        Location loc = player.getLocation();
        Block blockBelow = loc.getBlock().getRelative(0, -1, 0);

        // Heat damage over time
        if (plugin.isHeatZone(blockBelow)) {
            double damage = plugin.getDamageRate();
            player.damage(damage);
        }

        // Lava trap triggers
        if (blockBelow.getType() == Material.LAVA) {
            player.damage(4.0);
            player.setFireTicks(100);
        }

        // Fire mob spawn zones
        if (plugin.isFireMobZone(loc)) {
            if (Math.random() < 0.05) { // 5% chance per tick
                loc.getWorld().spawnEntity(loc, EntityType.BLAZE);
            }
        }

        // Timed flame wall sections - check if player is near flame wall
        if (plugin.isNearFlameWall(loc)) {
            player.setFireTicks(60);
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        Player player = event.getPlayer();
        Block block = event.getClickedBlock();

        // Trigger blocks activation
        if (plugin.isTriggerBlock(block)) {
            plugin.activateTrigger(player, block);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (inMazePlayers.contains(event.getPlayer().getUniqueId())) {
            if (!event.getPlayer().isOp()) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (inMazePlayers.contains(event.getPlayer().getUniqueId())) {
            if (!event.getPlayer().isOp()) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if (inMazePlayers.contains(player.getUniqueId())) {
                // Apply resistance effect to balance maze difficulty
                player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 200, 0));
            }
        }
    }

    public void addPlayerToMaze(Player player) {
        inMazePlayers.add(player.getUniqueId());
    }

    public void removePlayerFromMaze(Player player) {
        inMazePlayers.remove(player.getUniqueId());
    }
}