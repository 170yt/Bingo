package bingo.manager;

import bingo.Main;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Listeners implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (Main.getTimer().isRunning()) {
            if (!Main.getGamemanager().isPlayerInMaps(event.getPlayer())) {
                event.getPlayer().getInventory().clear();
                event.getPlayer().setLevel(0);
                event.getPlayer().setExp(0);
                event.getPlayer().setGameMode(GameMode.SPECTATOR);
            } else {
                event.getPlayer().setWalkSpeed(Main.getInvSettings().getWalkSpeed());
                event.getPlayer().setAllowFlight(Main.getInstance().getConfig().getBoolean("settings.fly"));
                event.getPlayer().setFlySpeed(Main.getInvSettings().getFlySpeed());
                if (Main.getInstance().getConfig().getBoolean("settings.haste")) {
                    event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 999999999, Main.getInvSettings().getHasteLevel(), false, false, false));
                } else event.getPlayer().removePotionEffect(PotionEffectType.FAST_DIGGING);
                if (Main.getInstance().getConfig().getBoolean("settings.jumpBoost")) {
                    event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 999999999, Main.getInvSettings().getJumpBoostLevel(), false, false, false));
                } else event.getPlayer().removePotionEffect(PotionEffectType.JUMP);
            }
        } else {
            event.getPlayer().getInventory().clear();
            event.getPlayer().setLevel(0);
            event.getPlayer().setExp(0);
            event.getPlayer().setGameMode(GameMode.SURVIVAL);
            event.getPlayer().getInventory().setItem(4, Main.getInvSettings().createGuiItem(Material.COMPASS, ChatColor.GREEN + "Teleporter", "right click to use"));
            if (Main.getInstance().getConfig().getBoolean("settings.isTeamGame"))
                event.getPlayer().getInventory().setItem(0, Main.getInvSettings().createGuiItem(Material.PAPER, ChatColor.GREEN + "Teams", "right click to choose your team"));
            if (event.getPlayer().isOp()) {
                event.getPlayer().getInventory().setItem(7, Main.getInvSettings().createGuiItem(Material.COMMAND_BLOCK_MINECART, ChatColor.YELLOW + "Settings", "right click to edit"));
                event.getPlayer().getInventory().setItem(8, Main.getInvSettings().createGuiItem(Material.LIME_DYE, ChatColor.GREEN + "Start", "right click to start"));
            }
            event.getPlayer().setWalkSpeed(Main.getInvSettings().getWalkSpeed());
            event.getPlayer().setAllowFlight(Main.getInstance().getConfig().getBoolean("settings.fly"));
            event.getPlayer().setFlySpeed(Main.getInvSettings().getFlySpeed());
            if (Main.getInstance().getConfig().getBoolean("settings.haste")) {
                event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 999999999, Main.getInvSettings().getHasteLevel(), false, false, false));
            } else event.getPlayer().removePotionEffect(PotionEffectType.FAST_DIGGING);
            if (Main.getInstance().getConfig().getBoolean("settings.jumpBoost")) {
                event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 999999999, Main.getInvSettings().getJumpBoostLevel(), false, false, false));
            } else event.getPlayer().removePotionEffect(PotionEffectType.JUMP);
        }
        event.getPlayer().setScoreboard(Main.getGamemanager().getBoard());
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (Main.getTimer().isRunning()) {
            if (!(event.getEntity() instanceof Player)) return;
            if (Main.getInstance().getConfig().getBoolean("settings.damage")) return;
            if (event.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK ||
                    event.getCause() == EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK ||
                    event.getCause() == EntityDamageEvent.DamageCause.PROJECTILE ||
                    event.getCause() == EntityDamageEvent.DamageCause.THORNS) return;
            event.setCancelled(true);
        } else event.setCancelled(true);
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!Main.getTimer().isRunning()) {
            event.setCancelled(true);
        } else if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) { // pvp
            event.setCancelled(!Main.getInstance().getConfig().getBoolean("settings.pvp"));
        } else if (event.getEntity() instanceof Player && !(event.getDamager() instanceof Player)) { // damage on player by mob
            event.setCancelled(!Main.getInstance().getConfig().getBoolean("settings.damage"));
        }
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        if (Main.getInstance().getConfig().getBoolean("settings.damage")) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (Main.getTimer().isRunning()) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (Main.getTimer().isRunning()) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (Main.getTimer().isRunning()) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (Main.getTimer().isRunning()) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        if (Main.getTimer().isRunning()) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onEntityPickupItem(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        if (Main.getTimer().isRunning()) return;
        event.setCancelled(true);
    }

    @EventHandler
    public void onEntityTargetLivingEntity(EntityTargetLivingEntityEvent event) {
        if (Main.getTimer().isRunning() && Main.getInstance().getConfig().getBoolean("settings.damage")) return;
        if (event.getTarget() == null) return;
        if (event.getTarget().getType() != EntityType.PLAYER) return;
        event.setTarget(null);
        event.setCancelled(true);
    }
}
