package bingo.commands;

import bingo.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandInvsee implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!(sender instanceof Player)) return false;
        if (args.length == 1) {
            Player p = Bukkit.getPlayer(sender.getName());
            if (Bukkit.getPlayer(args[0]) != null) {
                p.openInventory(Bukkit.getPlayer(args[0]).getInventory());
                sendMessageToAllOPs(p, args[0]);
            } else if (Main.getBackpack().isInMap(args[0])){
                p.openInventory(Main.getBackpack().getBp(args[0].toLowerCase()));
                sendMessageToAllOPs(p, args[0]);
            } else sender.sendMessage(ChatColor.RED + "This player is not online");
            return true;
        } else {
            sender.sendMessage(ChatColor.RED + "Usage: /invsee <player_name/team[nr]>");
            return false;
        }
    }

    public void sendMessageToAllOPs(Player player, String inventory) {
        Bukkit.getOnlinePlayers().forEach(player1 -> {
            if (player1.isOp()) player1.sendMessage(ChatColor.RED + player.getName() + " used invsee on " + inventory);
        });
        Main.getInstance().logToFile("[" + Main.getTimer().getTime() + "] | " + player.getName() + " used invsee on " + inventory);
    }
}
