package bingo.manager;

import bingo.Main;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;
import java.util.Collections;

public class InvSettings implements Listener {
    private int slot_start = 8;
    private int slot_difficulty = 4;
    private int slot_teams = 0;
    private int slot_command_top = 1;
    private int slot_command_bp = 2;
    private int slot_damage = 9;
    private int slot_pvp = 10;
    private int slot_keepinv = 11;
    private int slot_changeWalkingSpeed = 14;
    private int slot_walkingSpeed = 23;
    private int slot_fly = 15;
    private int slot_flySpeed = 24;
    private int slot_haste = 16;
    private int slot_hasteLevel = 25;
    private int slot_jumpBoost = 17;
    private int slot_jumpBoostLevel = 26;

    private final Inventory inv;
    private int walkSpeedArrayIndex = 2;
    private float[] walkSpeedArrayPresetValues = new float[]{0.02f, 0.1f, 0.2f, 0.24f, 0.3f, 0.4f, 0.8f};
    private String[] walkSpeedArrayName = new String[]{"10%", "50%", "100%", "120%", "150%", "200%", "400%"};

    private int flySpeedArrayIndex = 2;
    private float[] flySpeedArrayPresetValues = new float[]{0.01f, 0.05f, 0.1f, 0.2f, 0.4f};
    private String[] flySpeedArrayName = new String[]{"10%", "50%", "100%", "200%", "400%"};

    private int hasteArrayIndex = 1;
    private int[] hasteArrayPresetValues = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
    private String[] hasteArrayName = new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};

    private int jumpBoostArrayIndex = 1;
    private int[] jumpBoostArrayPresetValues = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
    private String[] jumpBoostArrayName = new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};

    public InvSettings() {
        // Create a new inventory, with no owner (as this isn't a real inventory), a size of nine, called example
        inv = Bukkit.createInventory(null, 27, "Settings");

        // Put the items into the inventory
        initializeItems();
    }

    // You can call this whenever you want to put the items in
    public void initializeItems() {
        inv.clear();

        String dif = "";
        Material mat = Material.BARRIER;
        if (Main.getInstance().getConfig().getBoolean("difficulty.easy")) {
            dif = "easy";
            mat = Material.GRASS_BLOCK;
        } else if (Main.getInstance().getConfig().getBoolean("difficulty.medium")) {
            dif = "medium";
            mat = Material.NETHERRACK;
        } else if (Main.getInstance().getConfig().getBoolean("difficulty.hard")) {
            dif = "hard";
            mat = Material.END_STONE;
        }
        inv.setItem(slot_difficulty, createGuiItem(mat, ChatColor.YELLOW + "Difficulty", dif));
        if (!Main.getTimer().isRunning()) {
            inv.setItem(slot_start, createGuiItem(Material.LIME_DYE, ChatColor.GREEN + "Start",
                    "time: " + Main.getInstance().getConfig().getInt("standard.countdown"),
                    "jokers: " + Main.getInstance().getConfig().getInt("standard.jokers"),
                    "damage: " + Main.getInstance().getConfig().getBoolean("settings.damage"),
                    "pvp: " + Main.getInstance().getConfig().getBoolean("settings.pvp"),
                    "keepInventory: " + Main.getInstance().getConfig().getBoolean("settings.keepinventory"),
                    "difficulty: " + dif,
                    "teams: " + Main.getInstance().getConfig().getBoolean("settings.isTeamGame")));
            addItem(slot_teams, Material.WOODEN_SWORD, "Teams", "settings.isTeamGame");
        }
        addItem(slot_command_top, Material.ENDER_PEARL, "Command: /top", "settings.commandTop");
        addItem(slot_command_bp, Material.ENDER_CHEST, "Command: /bp", "settings.commandBp");
        addItem(slot_damage, Material.GOLDEN_APPLE, "Damage", "settings.damage");
        addItem(slot_pvp, Material.DIAMOND_SWORD, "PVP", "settings.pvp");
        addItem(slot_keepinv, Material.TOTEM_OF_UNDYING, "KeepInventory", "settings.keepinventory");
        addItem(slot_changeWalkingSpeed, Material.LEATHER_BOOTS, "Change walking speed", "settings.changeWalkingSpeed");
        if (Main.getInstance().getConfig().getBoolean("settings.changeWalkingSpeed"))
            inv.setItem(slot_walkingSpeed, createGuiItem(Material.LIME_STAINED_GLASS_PANE, ChatColor.YELLOW + "WalkingSpeed", walkSpeedArrayName[walkSpeedArrayIndex]));
        addItem(slot_fly, Material.ELYTRA, "Fly", "settings.fly");
        if (Main.getInstance().getConfig().getBoolean("settings.fly"))
            inv.setItem(slot_flySpeed, createGuiItem(Material.LIME_STAINED_GLASS_PANE, ChatColor.YELLOW + "FlySpeed", flySpeedArrayName[flySpeedArrayIndex]));
        addItem(slot_haste, Material.GOLDEN_PICKAXE, "Give players haste effect", "settings.haste");
        if (Main.getInstance().getConfig().getBoolean("settings.haste"))
            inv.setItem(slot_hasteLevel, createGuiItem(Material.LIME_STAINED_GLASS_PANE, ChatColor.YELLOW + "HasteLevel", hasteArrayName[hasteArrayIndex]));
        addItem(slot_jumpBoost, Material.FEATHER, "Give players jumpBoost effect", "settings.jumpBoost");
        if (Main.getInstance().getConfig().getBoolean("settings.jumpBoost"))
            inv.setItem(slot_jumpBoostLevel, createGuiItem(Material.LIME_STAINED_GLASS_PANE, ChatColor.YELLOW + "jumpBoostLevel", jumpBoostArrayName[jumpBoostArrayIndex]));
    }

    public void addItem(int slot, Material material, String name, String configBoolean) {
        final ItemStack item = new ItemStack(material, 1);
        final ItemMeta meta = item.getItemMeta();

        meta.setLore(Collections.singletonList(String.valueOf(Main.getInstance().getConfig().getBoolean(configBoolean))));
        if (Main.getInstance().getConfig().getBoolean(configBoolean)) { // if config true
            meta.setDisplayName(ChatColor.DARK_GREEN + name);
            item.setItemMeta(meta);
            item.addUnsafeEnchantment(Enchantment.ARROW_INFINITE, 1);
        } else {
            meta.setDisplayName(ChatColor.DARK_RED + name);
            item.setItemMeta(meta);
        }
        inv.setItem(slot, item);
    }

    // Nice little method to create a gui item with a custom name, and description
    protected ItemStack createGuiItem(final Material material, final String name, final String... lore) {
        final ItemStack item = new ItemStack(material, 1);
        final ItemMeta meta = item.getItemMeta();

        // Set the name of the item
        meta.setDisplayName(name);

        // Set the lore of the item
        meta.setLore(Arrays.asList(lore));

        item.setItemMeta(meta);

        return item;
    }

    // You can open the inventory with this
    public void openInventory(final HumanEntity ent) {
        initializeItems();
        ent.openInventory(inv);
    }

    // Check for clicks on items
    @EventHandler
    public void onInventoryClick(final InventoryClickEvent e) {
        if (!e.getInventory().equals(inv)) return;

        e.setCancelled(true);

        final ItemStack clickedItem = e.getCurrentItem();

        // verify current item is not null
        if (clickedItem == null || clickedItem.getType().equals(Material.AIR)) return;

        final Player p = (Player) e.getWhoClicked();

        // Using slots click is a best option for your inventory click's
        //p.sendMessage("You clicked at slot " + e.getRawSlot());
        if (e.getRawSlot() > inv.getSize()) return;


        if (e.getRawSlot() == slot_start) { // start game
            Main.getGamemanager().startGame(e.getWhoClicked());
            e.getWhoClicked().closeInventory();
        } else if (e.getRawSlot() == slot_teams) { // toggle teams
            if (Main.getInstance().getConfig().getBoolean("settings.isTeamGame")) {
                Main.getInstance().getConfig().set("settings.isTeamGame", false);
                Bukkit.getOnlinePlayers().forEach(player -> {
                    Main.getGamemanager().removePlayerFromTeam(player);
                    while (player.getInventory().contains(Material.PAPER)) {
                        player.getInventory().clear(player.getInventory().first(Material.PAPER));
                    }
                });
            } else {
                Main.getInstance().getConfig().set("settings.isTeamGame", true);
                Bukkit.getOnlinePlayers().forEach(player -> {
                    player.getInventory().setItem(0, createGuiItem(Material.PAPER, ChatColor.GREEN + "Teams", "right click to choose your team"));
                });
            }
        } else if (e.getRawSlot() == slot_command_top) { // toggle command top
            Main.getInstance().getConfig().set("settings.commandTop", !Main.getInstance().getConfig().getBoolean("settings.commandTop"));
        } else if (e.getRawSlot() == slot_command_bp) { // toggle command bp
            Main.getInstance().getConfig().set("settings.commandBp", !Main.getInstance().getConfig().getBoolean("settings.commandBp"));
        } else if (e.getRawSlot() == slot_damage) { // toggle damage
            Main.getInstance().getConfig().set("settings.damage", !Main.getInstance().getConfig().getBoolean("settings.damage"));
        } else if (e.getRawSlot() == slot_pvp) { // toggle pvp
            Main.getInstance().getConfig().set("settings.pvp", !Main.getInstance().getConfig().getBoolean("settings.pvp"));
        } else if (e.getRawSlot() == slot_keepinv) { // toggle keepInventory
            Main.getInstance().getConfig().set("settings.keepinventory", !Main.getInstance().getConfig().getBoolean("settings.keepinventory"));
            Bukkit.getWorlds().forEach(world -> world.setGameRule(GameRule.KEEP_INVENTORY, Main.getInstance().getConfig().getBoolean("settings.keepinventory")));
        } else if (e.getRawSlot() == slot_changeWalkingSpeed) { // toggle change walk speed
            Main.getInstance().getConfig().set("settings.changeWalkingSpeed", !Main.getInstance().getConfig().getBoolean("settings.changeWalkingSpeed"));
            walkSpeedArrayIndex = 2;
            Bukkit.getOnlinePlayers().forEach(player -> {
                player.setWalkSpeed(0.2f);
            });
        } else if (e.getRawSlot() == slot_fly) { // toggle fly
            Main.getInstance().getConfig().set("settings.fly", !Main.getInstance().getConfig().getBoolean("settings.fly"));
            flySpeedArrayIndex = 2;
            Bukkit.getOnlinePlayers().forEach(player -> {
                player.setAllowFlight(Main.getInstance().getConfig().getBoolean("settings.fly"));
                player.setFlySpeed(0.1f);
            });
        } else if (e.getRawSlot() == slot_haste) { // toggle give haste effect
            Main.getInstance().getConfig().set("settings.haste", !Main.getInstance().getConfig().getBoolean("settings.haste"));
            hasteArrayIndex = 0;
            Bukkit.getOnlinePlayers().forEach(player -> {
                if (Main.getInstance().getConfig().getBoolean("settings.haste")) {
                    player.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 999999999, hasteArrayPresetValues[hasteArrayIndex], false, false, false));
                } else player.removePotionEffect(PotionEffectType.FAST_DIGGING);
            });
        } else if (e.getRawSlot() == slot_difficulty) { // toggle difficulty
            if (Main.getInstance().getConfig().getBoolean("difficulty.easy")) {
                Main.getInstance().getConfig().set("difficulty.easy", false);
                Main.getInstance().getConfig().set("difficulty.medium", true);
                Main.getInstance().getConfig().set("difficulty.hard", false);
            } else if (Main.getInstance().getConfig().getBoolean("difficulty.medium")) {
                Main.getInstance().getConfig().set("difficulty.easy", false);
                Main.getInstance().getConfig().set("difficulty.medium", false);
                Main.getInstance().getConfig().set("difficulty.hard", true);
            } else if (Main.getInstance().getConfig().getBoolean("difficulty.hard")) {
                Main.getInstance().getConfig().set("difficulty.easy", true);
                Main.getInstance().getConfig().set("difficulty.medium", false);
                Main.getInstance().getConfig().set("difficulty.hard", false);
            }
        } else if (e.getRawSlot() == slot_walkingSpeed) { // toggle walk speed (default: 0.1f (from -1 to 1))
            float walkSpeed = ((Player) e.getWhoClicked()).getPlayer().getWalkSpeed();
            walkSpeedArrayIndex = 0;
            while (walkSpeed != walkSpeedArrayPresetValues[walkSpeedArrayIndex]) {
                walkSpeedArrayIndex++;
            }
            if (e.isLeftClick()) { // increase
                if (walkSpeedArrayIndex + 1 >= walkSpeedArrayPresetValues.length) walkSpeedArrayIndex = 0;
                else walkSpeedArrayIndex++;
            } else if (e.isRightClick()) { // decrease
                if (walkSpeedArrayIndex - 1 < 0) walkSpeedArrayIndex = walkSpeedArrayPresetValues.length - 1;
                else walkSpeedArrayIndex--;
            }
            Bukkit.getOnlinePlayers().forEach(player -> player.setWalkSpeed(walkSpeedArrayPresetValues[walkSpeedArrayIndex]));
        } else if (e.getRawSlot() == slot_flySpeed) { // toggle fly speed (default: 0.1f (from -1 to 1))
            float flySpeed = ((Player) e.getWhoClicked()).getPlayer().getFlySpeed();
            flySpeedArrayIndex = 0;
            while (flySpeed != flySpeedArrayPresetValues[flySpeedArrayIndex]) {
                flySpeedArrayIndex++;
            }
            if (e.isLeftClick()) { // increase
                if (flySpeedArrayIndex + 1 >= flySpeedArrayPresetValues.length) flySpeedArrayIndex = 0;
                else flySpeedArrayIndex++;
            } else if (e.isRightClick()) { // decrease
                if (flySpeedArrayIndex - 1 < 0) flySpeedArrayIndex = flySpeedArrayPresetValues.length - 1;
                else flySpeedArrayIndex--;
            }
            Bukkit.getOnlinePlayers().forEach(player -> player.setFlySpeed(flySpeedArrayPresetValues[flySpeedArrayIndex]));
        } else if (e.getRawSlot() == slot_hasteLevel) { // toggle haste level
            int hasteLevel = ((Player) e.getWhoClicked()).getPlayer().getPotionEffect(PotionEffectType.FAST_DIGGING).getAmplifier();
            hasteArrayIndex = 0;
            while (hasteLevel != hasteArrayPresetValues[hasteArrayIndex]) {
                hasteArrayIndex++;
            }
            if (e.isLeftClick()) { // increase
                if (hasteArrayIndex + 1 >= hasteArrayPresetValues.length) hasteArrayIndex = 0;
                else hasteArrayIndex++;
            } else if (e.isRightClick()) { // decrease
                if (hasteArrayIndex - 1 < 0) hasteArrayIndex = hasteArrayPresetValues.length - 1;
                else hasteArrayIndex--;
            }
            Bukkit.getOnlinePlayers().forEach(player -> {
                player.removePotionEffect(PotionEffectType.FAST_DIGGING);
                player.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 999999999, hasteArrayPresetValues[hasteArrayIndex], false, false, false));
            });
        } else if (e.getRawSlot() == slot_jumpBoost) { // toggle give jumpBoost effect
            Main.getInstance().getConfig().set("settings.jumpBoost", !Main.getInstance().getConfig().getBoolean("settings.jumpBoost"));
            jumpBoostArrayIndex = 0;
            Bukkit.getOnlinePlayers().forEach(player -> {
                if (Main.getInstance().getConfig().getBoolean("settings.jumpBoost")) {
                    player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 999999999, jumpBoostArrayPresetValues[jumpBoostArrayIndex], false, false, false));
                } else player.removePotionEffect(PotionEffectType.JUMP);
            });
        } else if (e.getRawSlot() == slot_jumpBoostLevel) { // toggle jumpBoost level
            int jumpBoostLevel = ((Player) e.getWhoClicked()).getPlayer().getPotionEffect(PotionEffectType.JUMP).getAmplifier();
            jumpBoostArrayIndex = 0;
            while (jumpBoostLevel != jumpBoostArrayPresetValues[jumpBoostArrayIndex]) {
                jumpBoostArrayIndex++;
            }
            if (e.isLeftClick()) { // increase
                if (jumpBoostArrayIndex + 1 >= jumpBoostArrayPresetValues.length) jumpBoostArrayIndex = 0;
                else jumpBoostArrayIndex++;
            } else if (e.isRightClick()) { // decrease
                if (jumpBoostArrayIndex - 1 < 0) jumpBoostArrayIndex = jumpBoostArrayPresetValues.length - 1;
                else jumpBoostArrayIndex--;
            }
            Bukkit.getOnlinePlayers().forEach(player -> {
                player.removePotionEffect(PotionEffectType.JUMP);
                player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 999999999, jumpBoostArrayPresetValues[jumpBoostArrayIndex], false, false, false));
            });
        }

        Main.getInstance().saveConfig();
        ((Player) e.getWhoClicked()).playSound(e.getWhoClicked(), Sound.BLOCK_NOTE_BLOCK_HAT, 1, 1);
        initializeItems();
    }

    // Cancel dragging in our inventory
    @EventHandler
    public void onInventoryClick(final InventoryDragEvent e) {
        if (e.getInventory().equals(inv)) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onClick(PlayerInteractEvent e) {
        HumanEntity p = e.getPlayer();
        if (!p.isOp()) return;
        if (Main.getTimer().isRunning()) return;
        if (p.getInventory().getItemInMainHand().getType() == Material.COMMAND_BLOCK_MINECART) openInventory(p);
        if (p.getInventory().getItemInMainHand().getType() == Material.LIME_DYE) Main.getGamemanager().startGame(p);
    }

    public Inventory getInv() {
        return inv;
    }

    public float getWalkSpeed() {
        return walkSpeedArrayPresetValues[walkSpeedArrayIndex];
    }

    public float getFlySpeed() {
        return flySpeedArrayPresetValues[flySpeedArrayIndex];
    }

    public int getHasteLevel() {
        return hasteArrayPresetValues[hasteArrayIndex];
    }

    public int getJumpBoostLevel() {
        return jumpBoostArrayPresetValues[jumpBoostArrayIndex];
    }
}
