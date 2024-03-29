package bingo.commands;

import bingo.Main;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandStopTimer implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player)) return false;
        if (!Main.getTimer().isRunning()) {
            commandSender.sendMessage(ChatColor.RED + "The game is not running. Start it first with /start");
            return false;
        }
        Main.getGamemanager().finishGame();
        return false;
    }
}
