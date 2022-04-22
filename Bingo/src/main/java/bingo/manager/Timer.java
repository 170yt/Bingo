package bingo.manager;

import bingo.Main;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Timer {
    private boolean running;
    private int time;
    private Map<UUID, BossBar> bossBar = new HashMap<UUID, BossBar>();

    public Timer() {
        this.running = false;

        if (Main.getInstance().getConfig().contains("timer.time")) {
            this.time = Main.getInstance().getConfig().getInt("timer.time");
        } else {
            this.time = 0;
        }

        run();
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public String formatSeconds(int inputSeconds) {
        int seconds = inputSeconds % 60;
        int minutes = (inputSeconds / 60) % 60;
        int hours = inputSeconds / 60 / 60;
        return hours + "h " + minutes + "m " + seconds + "s";
    }

    public void sendActionBar() {
        for (Player player : Bukkit.getOnlinePlayers()) {

            if (Main.getInstance().getConfig().getBoolean("settings.haste")) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 999999999, Main.getInvSettings().getHasteLevel()));
            } else player.removePotionEffect(PotionEffectType.FAST_DIGGING);
            if (Main.getInstance().getConfig().getBoolean("settings.jumpBoost")) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 999999999, Main.getInvSettings().getJumpBoostLevel(), false, false, false));
            } else player.removePotionEffect(PotionEffectType.JUMP);

            if (!isRunning()) {
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.RED +
                        "Timer paused"));
                continue;
            }

            if (Main.getGamemanager().isPlayerInMaps(player)) {
                if (Main.getInstance().getConfig().getBoolean("settings.isTeamGame")) {
                    /////////////////////////////////////// TEAMS ///////////////////////////////////////
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.GOLD.toString() +
                            ChatColor.BOLD + formatSeconds(getTime()) + " | " + Main.getGamemanager().getTeamScoreFromPlayer(player)));
                    try {
                        if (!bossBar.get(player.getUniqueId()).getTitle().equalsIgnoreCase(Main.getGamemanager().getMaterialTeamsFromPlayer(player).toString())) {
                            bossBar.get(player.getUniqueId()).setTitle(Main.getGamemanager().getMaterialTeamsFromPlayer(player).toString());
                            bossBar.get(player.getUniqueId()).addPlayer(player);
                        }
                    } catch (NullPointerException e) {
                        BossBar bar = Bukkit.createBossBar(Main.getGamemanager().getMaterialTeamsFromPlayer(player).toString(), BarColor.WHITE, BarStyle.SOLID);
                        bossBar.put(player.getUniqueId(), bar);
                        bossBar.get(player.getUniqueId()).addPlayer(player);
                    }
                } else {
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.GOLD.toString() +
                            ChatColor.BOLD + formatSeconds(getTime()) + " | " + Main.getGamemanager().getScore(player)));
                    try {
                        if (!bossBar.get(player.getUniqueId()).getTitle().equalsIgnoreCase(Main.getGamemanager().getCurrentMaterial(player).toString())) {
                            bossBar.get(player.getUniqueId()).setTitle(Main.getGamemanager().getCurrentMaterial(player).toString());
                            bossBar.get(player.getUniqueId()).addPlayer(player);
                        }
                    } catch (NullPointerException e) {
                        BossBar bar = Bukkit.createBossBar(Main.getGamemanager().getCurrentMaterial(player).toString(), BarColor.WHITE, BarStyle.SOLID);
                        bossBar.put(player.getUniqueId(), bar);
                        bossBar.get(player.getUniqueId()).addPlayer(player);
                    }
                }
            } else {
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.GOLD.toString() +
                        ChatColor.BOLD + formatSeconds(getTime()) + " | SPEC"));
            }
        }
    }

    public void save() {
        Main.getInstance().getConfig().set("timer.time", time);
    }

    private void run() {
        new BukkitRunnable() {
            @Override
            public void run() {

                sendActionBar();
                if (!isRunning()) {
                    return;
                }
                setTime(getTime() + 1);

                Bukkit.getOnlinePlayers().forEach(player -> {
                    if (Main.getGamemanager().isPlayerInMaps(player)) {
                        if (Main.getInstance().getConfig().getBoolean("settings.isTeamGame")) {
                            /////////////////////////////////////// TEAMS ///////////////////////////////////////
                            if (player.getInventory().contains(Main.getGamemanager().getMaterialTeamsFromPlayer(player))) {
                                Main.getGamemanager().checkItem(player, Main.getGamemanager().getMaterialTeamsFromPlayer(player));
                            }
                        } else {
                            if (player.getInventory().contains(Main.getGamemanager().getCurrentMaterial(player))) {
                                Main.getGamemanager().checkItem(player, Main.getGamemanager().getCurrentMaterial(player));
                            }
                        }
                    }
                });

                switch (getTime()) {
                    case 300: {
                        Bukkit.broadcastMessage(ChatColor.RED + "<< 5 minutes left >>");
                        Bukkit.getOnlinePlayers().forEach(player -> player.playSound(player, Sound.BLOCK_ANVIL_USE, 1, 1));
                        break;
                    }
                    case 60: {
                        Bukkit.broadcastMessage(ChatColor.RED + "<< 1 minute left >>");
                        Bukkit.getOnlinePlayers().forEach(player -> player.playSound(player, Sound.BLOCK_ANVIL_USE, 1, 1));
                        break;
                    }
                    case 30: {
                        Bukkit.broadcastMessage(ChatColor.RED + "<< 30 seconds left >>");
                        Bukkit.getOnlinePlayers().forEach(player -> player.playSound(player, Sound.BLOCK_ANVIL_USE, 1, 1));
                        break;
                    }
                    case 10: {
                        Bukkit.broadcastMessage(ChatColor.RED + "<< 10 seconds left >>");
                        Bukkit.getOnlinePlayers().forEach(player -> player.playSound(player, Sound.BLOCK_ANVIL_USE, 1, 1));
                        break;
                    }
                    case 5: {
                        Bukkit.broadcastMessage(ChatColor.RED + "<< 5 seconds left >>");
                        Bukkit.getOnlinePlayers().forEach(player -> player.playSound(player, Sound.BLOCK_ANVIL_USE, 1, 1));
                        break;
                    }
                    case 4: {
                        Bukkit.broadcastMessage(ChatColor.RED + "<< 4 seconds left >>");
                        Bukkit.getOnlinePlayers().forEach(player -> player.playSound(player, Sound.BLOCK_ANVIL_USE, 1, 1));
                        break;
                    }
                    case 3: {
                        Bukkit.broadcastMessage(ChatColor.RED + "<< 3 seconds left >>");
                        Bukkit.getOnlinePlayers().forEach(player -> player.playSound(player, Sound.BLOCK_ANVIL_USE, 1, 1));
                        break;
                    }
                    case 2: {
                        Bukkit.broadcastMessage(ChatColor.RED + "<< 2 seconds left >>");
                        Bukkit.getOnlinePlayers().forEach(player -> player.playSound(player, Sound.BLOCK_ANVIL_USE, 1, 1));
                        break;
                    }
                    case 1: {
                        Bukkit.broadcastMessage(ChatColor.RED + "<< 1 second left >>");
                        Bukkit.getOnlinePlayers().forEach(player -> player.playSound(player, Sound.BLOCK_ANVIL_USE, 1, 1));
                        break;
                    }
                    default:
                        break;
                }
                if (getTime()<=0) {
                    Bukkit.broadcastMessage(ChatColor.GOLD + "<< Force Item Battle is over >>");
                    Bukkit.getOnlinePlayers().forEach(player -> player.playSound(player, Sound.BLOCK_END_PORTAL_SPAWN, 1, 1));
                    setRunning(false);
                    Main.getGamemanager().finishGame();
                    Main.getInstance().logToFile("<< Force Item Battle is over >>");
                }
            }
        }.runTaskTimer(Main.getInstance(), 20, 20);
    }

    public Map<UUID, BossBar> getBossBar() {
        return bossBar;
    }
}
