package bingo.commands;

import bingo.Main;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandStart implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return false;
        if (Main.getTimer().isRunning()) {
            sender.sendMessage(ChatColor.RED + "The game has already started!");
            return false;
        }
        Main.getGamemanager().startGame(sender);
        return false;
    }
}
