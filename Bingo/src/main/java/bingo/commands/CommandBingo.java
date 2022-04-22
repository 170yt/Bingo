package bingo.commands;

import bingo.Main;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandBingo implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!Main.getTimer().isRunning()) return false;
        if (!(commandSender instanceof Player)) return false;
        Player p = Bukkit.getPlayer(commandSender.getName());
        Main.getInvBingo().openBingoInv(p);
        return false;
    }
}
