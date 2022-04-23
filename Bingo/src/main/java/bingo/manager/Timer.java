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
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public class Timer {
    private boolean running;
    private int time;

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
                } else {
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(ChatColor.GOLD.toString() +
                            ChatColor.BOLD + formatSeconds(getTime()) + " | " + Main.getGamemanager().getScorePlayer(player)));
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

                for (Iterator<Player> it = (Iterator<Player>) Bukkit.getOnlinePlayers().iterator(); it.hasNext();) {
                    Player player = it.next();
                    if (Main.getGamemanager().isPlayerInMaps(player)) {
                        Main.getGamemanager().checkPlayerInv(player);
                    }
                }
            }
        }.runTaskTimer(Main.getInstance(), 20, 20);
    }

}
