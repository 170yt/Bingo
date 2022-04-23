package bingo.manager;

import bingo.Main;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public class InvBingo implements Listener {
    private static Map<String, Inventory> inv_team = new HashMap<>();
    private static Map<UUID, Inventory> inv_player = new HashMap<>();

    public void createInventories() {
        int count = Main.getInstance().getConfig().getInt("settings.itemCount");
        int size = 9;
        if (count > 9 && count <= 18) size = 18;
        else if (count > 18 && count <= 27) size = 27;
        else if (count > 27 && count <= 36) size = 36;
        else if (count > 36 && count <= 45) size = 45;
        else if (count > 45 && count <= 54) size = 54;
        int finalSize = size;

        if (Main.getInstance().getConfig().getBoolean("settings.isTeamGame")) {
            /////////////////////////////////////// TEAMS ///////////////////////////////////////
            for (int i=1; i<=9; i++) {
                inv_team.put("team"+i, Bukkit.createInventory(null, finalSize, "Team " + i + " | Bingo"));
            }
        } else {
            Bukkit.getOnlinePlayers().forEach(player -> {
                inv_player.put(player.getUniqueId(), Bukkit.createInventory(null, finalSize, "Bingo"));
            });
        }

        initializeItems();
    }

    public void initializeItems() {
        if (Main.getInstance().getConfig().getBoolean("settings.isTeamGame")) {
            /////////////////////////////////////// TEAMS ///////////////////////////////////////
            for (int i=1; i<=9; i++) {
                String team = "team"+i;
                inv_team.get(team).clear();
                for (Iterator<Material> it = Main.getGamemanager().getMissingItemsTeams(team).iterator(); it.hasNext();) {
                    Material mat = it.next();
                    inv_team.get(team).addItem(new ItemStack(mat));
                }
                //Main.getGamemanager().getMissingItemsTeams(team).forEach(mat -> inv_team.get(team).addItem(new ItemStack(mat)));
            }
        } else {
            Bukkit.getOnlinePlayers().forEach(player -> {
                if (Main.getGamemanager().isPlayerInMaps(player)) {
                    inv_player.get(player.getUniqueId()).clear();
                    Main.getGamemanager().getMissingItemsPlayer(player).forEach(mat -> inv_player.get(player.getUniqueId()).addItem(new ItemStack(mat)));
                }
            });
        }
    }

    public void openBingoInv(Player player) {
        initializeItems();
        if (Main.getInstance().getConfig().getBoolean("settings.isTeamGame")) {
            /////////////////////////////////////// TEAMS ///////////////////////////////////////
            if (Main.getGamemanager().getPlayerTeamSTRING(player) == null) return;
            player.openInventory(inv_team.get(Main.getGamemanager().getPlayerTeamSTRING(player)));
        } else {
            if (!inv_player.containsKey(player.getUniqueId())) return;
            player.openInventory(inv_player.get(player.getUniqueId()));
        }
    }

    // Check for clicks on items
    @EventHandler
    public void onInventoryClick(final InventoryClickEvent e) {
        if (Main.getInstance().getConfig().getBoolean("settings.isTeamGame")) {
            /////////////////////////////////////// TEAMS ///////////////////////////////////////
            if (inv_team.containsValue(e.getInventory())) e.setCancelled(true);
        } else {
            if (inv_player.containsValue(e.getInventory())) e.setCancelled(true);
        }
    }

    // Cancel dragging in our inventory
    @EventHandler
    public void onInventoryClick(final InventoryDragEvent e) {
        if (Main.getInstance().getConfig().getBoolean("settings.isTeamGame")) {
            /////////////////////////////////////// TEAMS ///////////////////////////////////////
            if (inv_team.containsValue(e.getInventory())) e.setCancelled(true);
        } else {
            if (inv_player.containsValue(e.getInventory())) e.setCancelled(true);
        }
    }

    public void addToAllInvBingo(ItemStack itemStack) {
        if (Main.getInstance().getConfig().getBoolean("settings.isTeamGame")) {
            /////////////////////////////////////// TEAMS ///////////////////////////////////////
            inv_team.forEach((key, value) -> {
                value.addItem(itemStack);
            });
        } else {
            inv_player.forEach((key, value) -> {
                value.addItem(itemStack);
            });
        }
    }

    public void clearAllInvBingo() {
        if (Main.getInstance().getConfig().getBoolean("settings.isTeamGame")) {
            /////////////////////////////////////// TEAMS ///////////////////////////////////////
            inv_team.forEach((key, value) -> {
                value.clear();
            });
        } else {
            inv_player.forEach((key, value) -> {
                value.clear();
            });
        }
    }

    public void deleteAllInvBingo() {
        if (Main.getInstance().getConfig().getBoolean("settings.isTeamGame")) {
            /////////////////////////////////////// TEAMS ///////////////////////////////////////
            inv_team.clear();
        } else {
            inv_player.clear();
        }
    }

}
