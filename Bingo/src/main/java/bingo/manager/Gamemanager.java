package bingo.manager;

import bingo.Main;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

import java.util.*;
import java.util.stream.Collectors;

public class Gamemanager {
    private static Map<UUID, Integer> scorePlayer = new HashMap<>();
    private static Map<UUID, List<Material>> missingMaterialsPlayer = new HashMap<>();

    private static boolean ASC = true;
    private static boolean DESC = false;

    /////////////////////////////////////// TEAMS ///////////////////////////////////////
    private static Map<String, Integer> scoreTeams = new HashMap<>();
    private static Map<String, ArrayList<Material>> missingMaterialsTeam = new HashMap<>();

    ScoreboardManager manager = Bukkit.getScoreboardManager();
    Scoreboard board = manager.getNewScoreboard();
    Team team1 = board.registerNewTeam("team1");
    Team team2 = board.registerNewTeam("team2");
    Team team3 = board.registerNewTeam("team3");
    Team team4 = board.registerNewTeam("team4");
    Team team5 = board.registerNewTeam("team5");
    Team team6 = board.registerNewTeam("team6");
    Team team7 = board.registerNewTeam("team7");
    Team team8 = board.registerNewTeam("team8");
    Team team9 = board.registerNewTeam("team9");

    public Gamemanager() {
        team1.setDisplayName("[Team1]");
        team1.setPrefix(ChatColor.WHITE + "Team1 | " + ChatColor.WHITE);
        //team1.setColor(ChatColor.WHITE);
        ///////////////////////////////////////////////
        team2.setDisplayName("[Team2]");
        team2.setPrefix(ChatColor.DARK_GRAY + "Team2 | " + ChatColor.WHITE);
        //team2.setColor(ChatColor.DARK_GRAY);
        ///////////////////////////////////////////////
        team3.setDisplayName("[Team3]");
        team3.setPrefix(ChatColor.RED + "Team3 | " + ChatColor.WHITE);
        //team3.setColor(ChatColor.RED);
        ///////////////////////////////////////////////
        team4.setDisplayName("[Team4]");
        team4.setPrefix(ChatColor.YELLOW + "Team4 | " + ChatColor.WHITE);
        //team4.setColor(ChatColor.YELLOW);
        ///////////////////////////////////////////////
        team5.setDisplayName("[Team5]");
        team5.setPrefix(ChatColor.GREEN + "Team5 | " + ChatColor.WHITE);
        //team5.setColor(ChatColor.GREEN);
        ///////////////////////////////////////////////
        team6.setDisplayName("[Team6]");
        team6.setPrefix(ChatColor.AQUA + "Team6 | " + ChatColor.WHITE);
        //team6.setColor(ChatColor.AQUA);
        ///////////////////////////////////////////////
        team7.setDisplayName("[Team7]");
        team7.setPrefix(ChatColor.DARK_BLUE + "Team7 | " + ChatColor.WHITE);
        //team7.setColor(ChatColor.DARK_BLUE);
        ///////////////////////////////////////////////
        team8.setDisplayName("[Team8]");
        team8.setPrefix(ChatColor.DARK_PURPLE + "Team8 | " + ChatColor.WHITE);
        //team8.setColor(ChatColor.DARK_PURPLE);
        ///////////////////////////////////////////////
        team9.setDisplayName("[Team9]");
        team9.setPrefix(ChatColor.LIGHT_PURPLE + "Team9 | " + ChatColor.WHITE);
        //team9.setColor(ChatColor.LIGHT_PURPLE);
    }

    public boolean isPlayerInMaps(Player player) {
        boolean value = true;
        if (Main.getInstance().getConfig().getBoolean("settings.isTeamGame")) {
            /////////////////////////////////////// TEAMS ///////////////////////////////////////
            if (getPlayerTeam(player) == null) value = false;
        } else {
            if (!scorePlayer.containsKey(player.getUniqueId())) value = false;
            if (!missingMaterialsPlayer.containsKey(player.getUniqueId())) value = false;
        }
        return value;
    }

    public void checkItem(Player player, Material material) {
        if (!isPlayerInMaps(player)) return;
        if (Main.getInstance().getConfig().getBoolean("settings.isTeamGame")) {
            /////////////////////////////////////// TEAMS ///////////////////////////////////////
            if (missingMaterialsTeam.get(getPlayerTeamSTRING(player)).contains(material)) {
                scoreTeams.put(getPlayerTeamSTRING(player), scoreTeams.get(getPlayerTeamSTRING(player)) + 1);
                missingMaterialsTeam.get(getPlayerTeamSTRING(player)).remove(material);

                board.getTeam(getPlayerTeamSTRING(player)).getEntries().forEach(e -> {
                    Bukkit.getPlayer(e).playSound(Bukkit.getPlayer(e).getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 1, 1);
                });
                Main.getInstance().logToFile("[" + Main.getTimer().getTime() + "] | (" + getPlayerTeamSTRING(player) + ") " + player.getName() + " got item: " + material.toString() + " | new points: " + getTeamScoreFromPlayer(player));

                Bukkit.broadcastMessage(ChatColor.GOLD + getPlayerTeamSTRING(player) + " got " + material);

                if (missingMaterialsTeam.get(getPlayerTeamSTRING(player)).isEmpty()) {
                    Bukkit.broadcastMessage(ChatColor.GOLD + getPlayerTeamSTRING(player) + " finished!");
                    Main.getInstance().logToFile(getPlayerTeamSTRING(player) + " finished!");
                    finishGame();
                }
            }
        } else {
            if (missingMaterialsPlayer.get(player.getUniqueId()).contains(material)) {
                scorePlayer.put(player.getUniqueId(), scorePlayer.get(player.getUniqueId()) + 1);
                missingMaterialsPlayer.get(player.getUniqueId()).remove(material);
                player.playSound(player.getLocation(), Sound.ENTITY_ARROW_HIT_PLAYER, 1, 1);
                Main.getInstance().logToFile("[" + Main.getTimer().getTime() + "] | " + player.getName() + " got item: " + material.toString() + " | new points: " + scorePlayer.get(player.getUniqueId()));
            }
        }
    }

    public void checkPlayerInv(Player player) {
            if (Main.getInstance().getConfig().getBoolean("settings.isTeamGame")) {
                /////////////////////////////////////// TEAMS ///////////////////////////////////////
                /*
                for (Material material : getMissingItemsTeamsFromPlayer(player)) { // Fehlermeldung -> ConcurrentModificationException: null
                    if (player.getInventory().contains(material)) {
                        checkItem(player, material);
                    }
                }
                 */

                for (Iterator<Material> it = getMissingItemsTeamsFromPlayer(player).iterator(); it.hasNext();) {
                    Material material = it.next();
                    if (player.getInventory().contains(material)) {
                        checkItem(player, material);
                    }
                }
            } else {
                for (Material material : getMissingItemsPlayer(player)) { // Fehlermeldung -> ConcurrentModificationException: null
                    if (player.getInventory().contains(material)) {
                        checkItem(player, material);
                    }
                }
            }
    }

    public Material generateMaterial() {
        Material item = Material.BARRIER;

        if (Main.getInstance().getConfig().getBoolean("difficulty.easy")) {
            item = Main.getItemDifficultiesManager().getEasyMaterial();
        } else if (Main.getInstance().getConfig().getBoolean("difficulty.medium")) {
            item = Main.getItemDifficultiesManager().getMediumMaterial();
        } else if (Main.getInstance().getConfig().getBoolean("difficulty.hard")) {
            item = Main.getItemDifficultiesManager().getHardMaterial();
        }
        return item;
    }

    public int getScorePlayer(Player player) {
        return scorePlayer.get(player.getUniqueId());
    }

    public List<Material> getMissingItemsPlayer(Player player) {
        return missingMaterialsPlayer.get(player.getUniqueId());
    }

    public void initializeMaps() {
        ArrayList<Material> list = new ArrayList<>();
        if (Main.getInstance().getConfig().getInt("settings.itemCount") > 54) {
            Bukkit.broadcastMessage(ChatColor.RED + "ERROR: The maximum amount of items is 54");
        }

        for (int i=1; i<= Main.getInstance().getConfig().getInt("settings.itemCount"); i++) {
            Material mat = generateMaterial();
            while (list.contains(mat)) {
                mat = generateMaterial();
            }
            list.add(mat);
        }
        Main.getInstance().logToFile(list.toString());

        if (Main.getInstance().getConfig().getBoolean("settings.isTeamGame")) {
            /////////////////////////////////////// TEAMS ///////////////////////////////////////
            for (int i = 1; i <= 9; i++) {
                scoreTeams.put("team" + i, 0);
                missingMaterialsTeam.put("team" + i, (ArrayList<Material>) list.clone());
            }
            Main.getBackpack().clearAllBp();
        } else {
            Bukkit.getOnlinePlayers().forEach(player -> {
                scorePlayer.put(player.getUniqueId(), 0);
                missingMaterialsPlayer.put(player.getUniqueId(), (List<Material>) list.clone());
            });
        }

        Main.getInvBingo().createInventories();
    }

    public void startGame(CommandSender sender) {
        try {
            Main.getTimer().setTime(0);
            initializeMaps();

            String dif = "";
            if (Main.getInstance().getConfig().getBoolean("difficulty.easy")) {
                dif = "easy";
            } else if (Main.getInstance().getConfig().getBoolean("difficulty.medium")) {
                dif = "medium";
            } else if (Main.getInstance().getConfig().getBoolean("difficulty.hard")) {
                dif = "hard";
            }

            Main.getInstance().logToFile("The game was started with " + Main.getInstance().getConfig().getInt("settings.itemCount") + " items. ");
            Main.getInstance().logToFile("Difficulty: " + dif +
                    " | Damage: " + Main.getInstance().getConfig().getBoolean("settings.damage") +
                    " | PVP: " + Main.getInstance().getConfig().getBoolean("settings.pvp") +
                    " | keepInventory: " + Main.getInstance().getConfig().getBoolean("settings.keepinventory"));

            Bukkit.getOnlinePlayers().forEach(player -> {
                player.setHealth(20);
                player.setSaturation(20);
                player.getInventory().clear();
                player.setLevel(0);
                player.setExp(0);
                player.teleport(Bukkit.getWorld("world").getSpawnLocation());
                player.setScoreboard(Main.getGamemanager().getBoard());
                player.setAllowFlight(Main.getInstance().getConfig().getBoolean("settings.fly"));
                player.setFlySpeed(Main.getInvSettings().getFlySpeed());
                player.playSound(player, Sound.BLOCK_END_PORTAL_SPAWN, 1, 1);
                if (Main.getInstance().getConfig().getBoolean("standard.showBingoItemsOnGameStart")) Main.getInvBingo().openBingoInv(player);
                if (Main.getGamemanager().isPlayerInMaps(player)) {
                    player.setGameMode(GameMode.SURVIVAL);
                    if (!Main.getInstance().getConfig().getBoolean("settings.isTeamGame")) {
                        Main.getInstance().logToFile(player.getName());
                    } else Main.getInstance().logToFile(player.getName() + " -> " + getPlayerTeamSTRING(player));
                } else {
                    player.setGameMode(GameMode.SPECTATOR);
                    Main.getInstance().logToFile(player.getName() + " -> Spectator");
                }
            });
            Bukkit.getWorld("world").setTime(0);
            Main.getTimer().setRunning(true);

            Bukkit.broadcastMessage(ChatColor.GOLD + "The game was started with " + Main.getInstance().getConfig().getInt("settings.itemCount") + " items.");
            Bukkit.broadcastMessage(ChatColor.GOLD + "Difficulty: " + dif +
                    " | Damage: " + Main.getInstance().getConfig().getBoolean("settings.damage") +
                    " | PVP: " + Main.getInstance().getConfig().getBoolean("settings.pvp") +
                    " | keepInventory: " + Main.getInstance().getConfig().getBoolean("settings.keepinventory"));
            if (Main.getInstance().getConfig().getBoolean("standard.showInfoOnGameStart")) {
                Bukkit.broadcastMessage(ChatColor.DARK_GREEN + "----------------------- " + ChatColor.GREEN + "Info" + ChatColor.DARK_GREEN + " -----------------------");
                Bukkit.broadcastMessage(ChatColor.GREEN + "TODO: ADD NEW DESCRIPTION\n" +
                        "Useful commands:\n" +
                        "- " + ChatColor.GOLD + "/bingo " + ChatColor.GREEN + "-> shows your missing items\n" +
                        "- " + ChatColor.GOLD + "/top " + ChatColor.GREEN + "-> teleports you to the surface\n" +
                        "- " + ChatColor.GOLD + "/bp " + ChatColor.GREEN + "-> opens your team-backpack if you play in a team");
                Bukkit.broadcastMessage(ChatColor.DARK_GREEN + "----------------------------------------------------");
            }
        } catch (NumberFormatException e) {
            sender.sendMessage(ChatColor.RED + "<settings.itemCount> in config has to be a number");
        }
    }

    public void finishGame() {
        Main.getTimer().setRunning(false);
        //scoreboard in chat
        if (Main.getInstance().getConfig().getBoolean("settings.isTeamGame")) {
            /////////////////////////////////////// TEAMS ///////////////////////////////////////
            Map<String, Integer> sortedMapDesc;
            sortedMapDesc = sortByValueTeams(scoreTeams, DESC);
            printMapTeams(sortedMapDesc);

            Main.getBackpack().clearAllBp();
            Bukkit.getOnlinePlayers().forEach(player -> {
                player.setHealth(20);
                player.setSaturation(20);
                player.getInventory().clear();
                player.setLevel(0);
                player.setExp(0);
                for (PotionEffect effect : player.getActivePotionEffects()) {
                    player.removePotionEffect(effect.getType());
                }
                player.setGameMode(GameMode.SURVIVAL);
                player.setAllowFlight(true);
                player.setFlySpeed(0.1f);
                player.playSound(player, Sound.BLOCK_END_PORTAL_SPAWN, 1, 1);
                player.getInventory().setItem(0, Main.getInvSettings().createGuiItem(Material.PAPER, ChatColor.GREEN + "Teams", "right click to choose your team"));
                player.getInventory().setItem(4, Main.getInvSettings().createGuiItem(Material.COMPASS, ChatColor.GREEN + "Teleporter", "right click to use"));
                if (player.isOp()) {
                    player.getInventory().setItem(7, Main.getInvSettings().createGuiItem(Material.COMMAND_BLOCK_MINECART, ChatColor.YELLOW + "Settings", "right click to edit"));
                    player.getInventory().setItem(8, Main.getInvSettings().createGuiItem(Material.LIME_DYE, ChatColor.GREEN + "Start", "right click to start"));
                    player.sendMessage(ChatColor.RED + "Use /reset to reset the world or use /start to start a new round.");
                }
            });
        } else {
            Map<UUID, Integer> sortedMapDesc;
            sortedMapDesc = sortByValue(scorePlayer, DESC);
            printMap(sortedMapDesc);

            Bukkit.getOnlinePlayers().forEach(player -> {
                player.setHealth(20);
                player.setSaturation(20);
                player.getInventory().clear();
                player.setLevel(0);
                player.setExp(0);
                for (PotionEffect effect : player.getActivePotionEffects()) {
                    player.removePotionEffect(effect.getType());
                }
                player.setGameMode(GameMode.SURVIVAL);
                player.setAllowFlight(true);
                player.setFlySpeed(0.1f);
                player.getInventory().setItem(4, Main.getInvSettings().createGuiItem(Material.COMPASS, ChatColor.GREEN + "Teleporter", "right click to use"));
                if (player.isOp()) {
                    player.getInventory().setItem(7, Main.getInvSettings().createGuiItem(Material.COMMAND_BLOCK_MINECART, ChatColor.YELLOW + "Settings", "right click to edit"));
                    player.getInventory().setItem(8, Main.getInvSettings().createGuiItem(Material.LIME_DYE, ChatColor.GREEN + "Start", "right click to start"));
                    player.sendMessage(ChatColor.RED + "Use /reset to reset the world or use /start to start a new round.");
                }
            });
        }
        Main.getInvBingo().deleteAllInvBingo();
    }

    private static Map<UUID, Integer> sortByValue(Map<UUID, Integer> unsortMap, final boolean order) {
        List<Map.Entry<UUID, Integer>> list = new LinkedList<>(unsortMap.entrySet());

        // Sorting the list based on values
        list.sort((o1, o2) -> order ? o1.getValue().compareTo(o2.getValue()) == 0
                ? o1.getKey().compareTo(o2.getKey())
                : o1.getValue().compareTo(o2.getValue()) : o2.getValue().compareTo(o1.getValue()) == 0
                ? o2.getKey().compareTo(o1.getKey())
                : o2.getValue().compareTo(o1.getValue()));
        return list.stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> b, LinkedHashMap::new));

    }

    private static Map<String, Integer> sortByValueTeams(Map<String, Integer> unsortMap, final boolean order) {
        List<Map.Entry<String, Integer>> list = new LinkedList<>(unsortMap.entrySet());

        // Sorting the list based on values
        list.sort((o1, o2) -> order ? o1.getValue().compareTo(o2.getValue()) == 0
                ? o1.getKey().compareTo(o2.getKey())
                : o1.getValue().compareTo(o2.getValue()) : o2.getValue().compareTo(o1.getValue()) == 0
                ? o2.getKey().compareTo(o1.getKey())
                : o2.getValue().compareTo(o1.getValue()));
        return list.stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> b, LinkedHashMap::new));

    }

    private static void printMap(Map<UUID, Integer> map) {
        map.forEach((key, value) -> {
            Bukkit.broadcastMessage(ChatColor.GOLD.toString() + Bukkit.getPlayer(key).getName() + " | " + value);
            Main.getInstance().logToFile(Bukkit.getPlayer(key).getName() + " | " + value);
        });
    }

    private void printMapTeams(Map<String, Integer> map) {
        map.forEach((key, value) -> {
            switch (key) {
                case "team1": {
                    if (value > 0) {
                        Bukkit.broadcastMessage(ChatColor.WHITE + "Team 1" + ChatColor.GOLD + " | " + value + " | " + board.getTeam(key).getEntries());
                    }
                    break;
                }
                case "team2": {
                    if (value > 0) {
                        Bukkit.broadcastMessage(ChatColor.DARK_GRAY + "Team 2" + ChatColor.GOLD + " | " + value + " | " + board.getTeam(key).getEntries());
                    }
                    break;
                }
                case "team3": {
                    if (value > 0) {
                        Bukkit.broadcastMessage(ChatColor.RED + "Team 3" + ChatColor.GOLD + " | " + value + " | " + board.getTeam(key).getEntries());
                    }
                    break;
                }
                case "team4": {
                    if (value > 0) {
                        Bukkit.broadcastMessage(ChatColor.YELLOW + "Team 4" + ChatColor.GOLD + " | " + value + " | " + board.getTeam(key).getEntries());
                    }
                    break;
                }
                case "team5": {
                    if (value > 0) {
                        Bukkit.broadcastMessage(ChatColor.GREEN + "Team 5" + ChatColor.GOLD + " | " + value + " | " + board.getTeam(key).getEntries());
                    }
                    break;
                }
                case "team6": {
                    if (value > 0) {
                        Bukkit.broadcastMessage(ChatColor.AQUA + "Team 6" + ChatColor.GOLD + " | " + value + " | " + board.getTeam(key).getEntries());
                    }
                    break;
                }
                case "team7": {
                    if (value > 0) {
                        Bukkit.broadcastMessage(ChatColor.DARK_BLUE + "Team 7" + ChatColor.GOLD + " | " + value + " | " + board.getTeam(key).getEntries());
                    }
                    break;
                }
                case "team8": {
                    if (value > 0) {
                        Bukkit.broadcastMessage(ChatColor.DARK_PURPLE + "Team 8" + ChatColor.GOLD + " | " + value + " | " + board.getTeam(key).getEntries());
                    }
                    break;
                }
                case "team9": {
                    if (value > 0) {
                        Bukkit.broadcastMessage(ChatColor.LIGHT_PURPLE + "Team 9" + ChatColor.GOLD + " | " + value + " | " + board.getTeam(key).getEntries());
                    }
                    break;
                }
                default:
                    break;
            }
            Main.getInstance().logToFile(key + " | " + value + " | " + board.getTeam(key).getEntries());
        });
    }

    public Scoreboard getBoard() {
        return board;
    }

    /////////////////////////////////////// TEAMS ///////////////////////////////////////

    public boolean isTeam(Player player, String teamName) {
        if (board.getEntryTeam(player.getName()) == null) return false;
        return board.getEntryTeam(player.getName()).getName().equalsIgnoreCase(teamName);
    }

    public void addPlayerToTeam(Player player, String team) {
        board.getTeam(team).addEntry(player.getDisplayName());
    }

    public void removePlayerFromTeam(Player player) {
        if (board.getEntryTeam(player.getName()) == null) return;
        board.getEntryTeam(player.getName()).removeEntry(player.getName());
    }

    public List<String> getPlayersInTeam(String team) {
        List<String> l = new ArrayList<String>();
        l.add("<< left click to choose >>");
        l.add("<< right click to remove >>");
        l.add("-- players ----------------");
        l.addAll(board.getTeam(team).getEntries());
        return l;
    }

    public Team getPlayerTeam(Player player) {
        return board.getEntryTeam(player.getName());
    }

    public String getPlayerTeamSTRING(Player player) {
        if (board.getEntryTeam(player.getName()) == null) return null;
        return board.getEntryTeam(player.getName()).getName();
    }

    public int getScoreTeams(String team) {
        return scoreTeams.get(team);
    }

    public int getTeamScoreFromPlayer(Player player) {
        return scoreTeams.get(getPlayerTeamSTRING(player));
    }

    public List<Material> getMissingItemsTeamsFromPlayer(Player player) {
        return missingMaterialsTeam.get(getPlayerTeamSTRING(player));
    }

    public List<Material> getMissingItemsTeams(String team) {
        return missingMaterialsTeam.get(team);
    }
}
