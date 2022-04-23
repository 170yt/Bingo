package bingo.commands;

import bingo.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandTop implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!Main.getInstance().getConfig().getBoolean("settings.commandTop")) {
            sender.sendMessage(ChatColor.RED + "This command is disabled!");
            return false;
        }
        if (!(sender instanceof Player)) return false;
        Player p = Bukkit.getPlayer(sender.getName());
        Location loc;
        if (p.getWorld().equals(Bukkit.getWorld("world_nether"))) {
            loc = Bukkit.getWorld("world").getSpawnLocation();
        } else {
            loc = new Location(p.getWorld(), p.getLocation().getX(), p.getWorld().getHighestBlockYAt(p.getLocation().getBlockX(), p.getLocation().getBlockZ()) +1, p.getLocation().getZ());
        }
        p.playSound(p.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
        p.teleport(loc);
        return false;
    }
}
