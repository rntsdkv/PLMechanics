package ru.prisonlife.plmechanics;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 * @author rntsdkv
 * @project PLMechanics
 */

public class Trading {

    private Player trader;
    private Player player;
    private int level;
    private int time;
    private String traderStatus;
    private String playerStatus;
    private List<ItemStack> traderItems;
    private List<ItemStack> playerItems;

    public Trading(Player trader, Player player, int level) {
        this.trader = trader;
        this.player = player;
        this.level = level;
        time = 30;
    }

    public Player getTrader() { return trader; }

    public Player getPlayer() { return player; }

    public int getLevel() { return level; }

    public int getTime() { return time; }

    public void setTime(int time) { this.time = time; }

    public void reduceTime() { if (time != -1) time--; }

    public void clearTraderItems() { traderItems.clear(); }

    public void clearPlayerItems() { playerItems.clear(); }

    public void putTraderItem(ItemStack item) { traderItems.add(item); }

    public void putPlayerItem(ItemStack item) { playerItems.add(item); }

    public void start() {
        if (level == 1) {
            Inventory gui = Bukkit.createInventory(null, 27, "Сделка");
            ItemStack whiteGlass = new ItemStack(Material.WHITE_STAINED_GLASS_PANE);
            ItemStack yellowGlass = new ItemStack(Material.YELLOW_STAINED_GLASS_PANE);
            ItemStack blackGlass = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
            ItemStack accept = new ItemStack(Material.GREEN_DYE);
            ItemStack decline = new ItemStack(Material.RED_DYE);
            ItemStack compass = new ItemStack(Material.COMPASS);
            gui.setItem(0, whiteGlass); gui.setItem(9, whiteGlass); gui.setItem(18, yellowGlass);
            gui.setItem(1, yellowGlass); gui.setItem(10, null); gui.setItem(19, yellowGlass);
            gui.setItem(2, yellowGlass); gui.setItem(11, whiteGlass); gui.setItem(20, whiteGlass);
            gui.setItem(3, compass); gui.setItem(12, blackGlass); gui.setItem(21, accept);
            gui.setItem(4, blackGlass); gui.setItem(13, blackGlass); gui.setItem(22, blackGlass);
            gui.setItem(5, compass); gui.setItem(14, blackGlass); gui.setItem(23, decline);
            gui.setItem(6, yellowGlass); gui.setItem(15, yellowGlass); gui.setItem(24, whiteGlass);
            gui.setItem(7, whiteGlass); gui.setItem(16, null); gui.setItem(25, whiteGlass);
            gui.setItem(8, whiteGlass); gui.setItem(17, yellowGlass); gui.setItem(26, yellowGlass);

            trader.openInventory(gui);
            player.openInventory(gui);
        }
    }
}
