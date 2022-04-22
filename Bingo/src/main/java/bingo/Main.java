package bingo;

import bingo.commands.*;
import bingo.manager.*;
import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Comparator;

public final class Main extends JavaPlugin {

    private static Main instance;
    private static Gamemanager gamemanager;
    private static Timer timer;
    private static Backpack backpack;
    private static InvTeamVote invTeamVote;
    private static InvSettings invSettings;
    private static ItemDifficultiesManager itemDifficultiesManager;
    private static InvTeleport invTeleport;
    private static InvBingo invBingo;

    @Override
    public void onLoad() {
        instance = this;

        saveConfig();
        if (!getConfig().contains("timer.time")) { getConfig().set("timer.time", 0); }
        if (!getConfig().contains("settings.isTeamGame")) { getConfig().set("settings.isTeamGame", false); }
        if (!getConfig().contains("settings.keepinventory")) { getConfig().set("settings.keepinventory", true); }
        if (!getConfig().contains("settings.pvp")) { getConfig().set("settings.pvp", false); }
        if (!getConfig().contains("settings.damage")) { getConfig().set("settings.damage", false); }
        if (!getConfig().contains("settings.commandBp")) { getConfig().set("settings.commandBp", true); }
        if (!getConfig().contains("settings.commandTop")) { getConfig().set("settings.commandTop", true); }
        if (!getConfig().contains("settings.itemCount")) { getConfig().set("settings.itemCount", 18); }
        getConfig().set("settings.changeWalkingSpeed", false);
        getConfig().set("settings.fly", false);
        getConfig().set("settings.haste", false);
        getConfig().set("settings.jumpBoost", false);
        if (!getConfig().contains("standard.backpackSize")) { getConfig().set("standard.backpackSize", 27); }
        if (!getConfig().contains("standard.showInfoOnGameStart")) { getConfig().set("standard.showInfoOnGameStart", true); }
        if (!getConfig().contains("difficulty.easy")) { getConfig().set("difficulty.easy", true); }
        if (!getConfig().contains("difficulty.medium")) { getConfig().set("difficulty.medium", false); }
        if (!getConfig().contains("difficulty.hard")) { getConfig().set("difficulty.hard", false); }


        saveConfig();
        if (!getConfig().contains("isReset")){
            getConfig().set("isReset" , false);
            saveConfig();
            return;
        }
        if (getConfig().getBoolean("isReset")){

            try {
                //////////////////////////////////////////////////////////////////////////////
                //Files.deleteIfExists(getDataFolder().toPath());
                //////////////////////////////////////////////////////////////////////////////

                File world = new File(Bukkit.getWorldContainer() , "world");
                File nether = new File(Bukkit.getWorldContainer() , "world_nether");
                File end = new File(Bukkit.getWorldContainer() , "world_the_end");

                Files.walk(world.toPath())
                        .sorted(Comparator.reverseOrder())
                        . map(Path::toFile)
                        . forEach(File::delete);
                Files.walk(nether.toPath())
                        .sorted(Comparator.reverseOrder())
                        . map(Path::toFile)
                        . forEach(File::delete);
                Files.walk(end.toPath())
                        .sorted(Comparator.reverseOrder())
                        . map(Path::toFile)
                        . forEach(File::delete);

                //////////////////////////////////////////////////////////////////////////////

                world.mkdirs();
                nether.mkdirs();
                end.mkdirs();

                new File(world , "data").mkdirs();
                new File(world , "datapacks").mkdirs();
                new File(world , "playerdata").mkdirs();
                new File(world , "poi").mkdirs();
                new File(world , "region").mkdirs();

                new File(nether , "data").mkdirs();
                new File(nether , "datapacks").mkdirs();
                new File(nether , "playerdata").mkdirs();
                new File(nether , "poi").mkdirs();
                new File(nether , "region").mkdirs();

                new File(end , "data").mkdirs();
                new File(end , "datapacks").mkdirs();
                new File(end , "playerdata").mkdirs();
                new File(end , "poi").mkdirs();
                new File(end , "region").mkdirs();
            } catch (IOException e) {
                e.printStackTrace();
            }

            getConfig().set("isReset" , false);
            saveConfig();

        }
    }

    @Override
    public void onEnable() {
        gamemanager = new Gamemanager();
        timer = new Timer();
        backpack = new Backpack();
        invTeamVote = new InvTeamVote();
        invSettings = new InvSettings();
        itemDifficultiesManager = new ItemDifficultiesManager();
        invTeleport = new InvTeleport();
        invBingo = new InvBingo();

        PluginManager manager = Bukkit.getPluginManager();
        manager.registerEvents(new Listeners(), this);
        manager.registerEvents(invSettings, this);
        manager.registerEvents(invTeamVote, this);
        manager.registerEvents(invTeleport, this);
        manager.registerEvents(invBingo, this);

        getCommand("reset").setExecutor(new CommandReset());
        getCommand("start").setExecutor(new CommandStart());
        getCommand("stoptimer").setExecutor(new CommandStopTimer());
        getCommand("bingo").setExecutor(new CommandBingo());
        getCommand("invsee").setExecutor(new CommandInvsee());
        getCommand("top").setExecutor(new CommandTop());
        getCommand("bp").setExecutor(new CommandBp());
        getCommand("settings").setExecutor(new CommandSettings());
        getCommand("teleporter").setExecutor(new CommandTeleporter());

        Bukkit.getWorlds().forEach(world -> world.setGameRule(GameRule.KEEP_INVENTORY, getConfig().getBoolean("settings.keepinventory")));
    }

    @Override
    public void onDisable() {
        if (getConfig().getBoolean("isReset")) {
            getConfig().set("timer.time", 0);
        } else timer.save();
        getConfig().set("settings.changeWalkingSpeed", false);
        getConfig().set("settings.fly", false);
        getConfig().set("settings.haste", false);
        getConfig().set("settings.jumpBoost", false);
        saveConfig();
    }

    public void logToFile(String message) {
        try {
            File dataFolder = getDataFolder();
            if(!dataFolder.exists()) {
                dataFolder.mkdir();
            }

            File saveTo = new File(getDataFolder(), "logs_plugin.txt");
            if (!saveTo.exists()) {
                saveTo.createNewFile();
            }

            FileWriter fw = new FileWriter(saveTo, true);
            PrintWriter pw = new PrintWriter(fw);

            pw.println("[" + getTime() + "] | " + message);
            pw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getTime(){
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(cal.getTime());
    }


    public static Main getInstance() {
        return instance;
    }

    public static Gamemanager getGamemanager() {
        return gamemanager;
    }

    public static Timer getTimer() {
        return timer;
    }

    public static Backpack getBackpack() {
        return backpack;
    }

    public static InvTeamVote getInvTeamVote() {
        return invTeamVote;
    }

    public static InvSettings getInvSettings() {
        return invSettings;
    }

    public static ItemDifficultiesManager getItemDifficultiesManager() {
        return itemDifficultiesManager;
    }

    public static InvTeleport getInvTeleport() {
        return invTeleport;
    }

    public static InvBingo getInvBingo() {
        return invBingo;
    }
}
