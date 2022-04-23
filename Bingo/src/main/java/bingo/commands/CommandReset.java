package bingo.commands;

import bingo.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandReset implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Bukkit.getOnlinePlayers().forEach(player -> player.kickPlayer(ChatColor.DARK_RED + "Server Reset"));

        Main.getInstance().getConfig().set("isReset" , true);
        Main.getInstance().saveConfig();
        Bukkit.spigot().restart();
        return false;
    }
}
