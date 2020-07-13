package ru.prisonlife.plmechanics;

import net.minecraft.server.v1_15_R1.Behavior;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;

import static ru.prisonlife.plmechanics.Main.colorize;

/**
 * @author rntsdkv
 * @project PLMechanics
 */

public class Trading {

    enum Status { NOT_READY, READY }

    private Player trader;
    private Player player;
    private int level;
    private int time;
    private Status traderStatus;
    private Status playerStatus;
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

    public void setTraderStatus(String status) { traderStatus = Status.valueOf(status); }

    public String getTraderStatus() { return traderStatus.name(); }

    public void setPlayerStatus(String status) { traderStatus = Status.valueOf(status); }

    public String getPlayerStatusStatus() { return playerStatus.name(); }

    public void updateInventory() {
        if (level == 1) {
            trader.getOpenInventory().setItem(16, playerItems.get(0));
            player.getOpenInventory().setItem(16, traderItems.get(0));
        }

        if (traderStatus == Status.NOT_READY) {
            trader.getOpenInventory().setItem(3, new ItemStack(Material.COMPASS));
            player.getOpenInventory().setItem(5, new ItemStack(Material.COMPASS));
        } else if (traderStatus == Status.READY) {
            trader.getOpenInventory().setItem(3, new ItemStack(Material.BELL));
            player.getOpenInventory().setItem(5, new ItemStack(Material.BELL));
        }

        if (playerStatus == Status.NOT_READY) {
            player.getOpenInventory().setItem(3, new ItemStack(Material.COMPASS));
            trader.getOpenInventory().setItem(5, new ItemStack(Material.COMPASS));
        } else if (playerStatus == Status.READY) {
            player.getOpenInventory().setItem(3, new ItemStack(Material.BELL));
            trader.getOpenInventory().setItem(5, new ItemStack(Material.BELL));
        }
    }

    public void close() {
        trader.closeInventory();
        player.closeInventory();
        trader.sendMessage(colorize("&l&6Сделка разорвана"));
        player.sendMessage(colorize("&l&6Сделка разорвана"));
    }

    public void start() {
        traderStatus = Status.NOT_READY;
        playerStatus = Status.NOT_READY;
        if (level == 1) {
            ItemStack whiteGlass = new ItemStack(Material.WHITE_STAINED_GLASS_PANE);
            ItemStack yellowGlass = new ItemStack(Material.YELLOW_STAINED_GLASS_PANE);
            ItemStack blackGlass = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
            ItemStack accept = new ItemStack(Material.GREEN_DYE);
            ItemStack decline = new ItemStack(Material.RED_DYE);
            ItemStack compass = new ItemStack(Material.COMPASS);

            Inventory gui1 = Bukkit.createInventory(null, 27, "Сделка с " + player.getName());
            gui1.setItem(0, whiteGlass); gui1.setItem(9, whiteGlass); gui1.setItem(18, yellowGlass);
            gui1.setItem(1, yellowGlass); gui1.setItem(10, null); gui1.setItem(19, yellowGlass);
            gui1.setItem(2, yellowGlass); gui1.setItem(11, whiteGlass); gui1.setItem(20, whiteGlass);
            gui1.setItem(3, compass); gui1.setItem(12, blackGlass); gui1.setItem(21, accept);
            gui1.setItem(4, blackGlass); gui1.setItem(13, blackGlass); gui1.setItem(22, blackGlass);
            gui1.setItem(5, compass); gui1.setItem(14, blackGlass); gui1.setItem(23, decline);
            gui1.setItem(6, yellowGlass); gui1.setItem(15, yellowGlass); gui1.setItem(24, whiteGlass);
            gui1.setItem(7, whiteGlass); gui1.setItem(16, null); gui1.setItem(25, whiteGlass);
            gui1.setItem(8, whiteGlass); gui1.setItem(17, yellowGlass); gui1.setItem(26, yellowGlass);

            Inventory gui2 = Bukkit.createInventory(null, 27, "Сделка с " + trader.getName());
            gui2.setItem(0, whiteGlass); gui2.setItem(9, whiteGlass); gui2.setItem(18, yellowGlass);
            gui2.setItem(1, yellowGlass); gui2.setItem(10, null); gui2.setItem(19, yellowGlass);
            gui2.setItem(2, yellowGlass); gui2.setItem(11, whiteGlass); gui2.setItem(20, whiteGlass);
            gui2.setItem(3, compass); gui2.setItem(12, blackGlass); gui2.setItem(21, accept);
            gui2.setItem(4, blackGlass); gui2.setItem(13, blackGlass); gui2.setItem(22, blackGlass);
            gui2.setItem(5, compass); gui2.setItem(14, blackGlass); gui2.setItem(23, decline);
            gui2.setItem(6, yellowGlass); gui2.setItem(15, yellowGlass); gui2.setItem(24, whiteGlass);
            gui2.setItem(7, whiteGlass); gui2.setItem(16, null); gui2.setItem(25, whiteGlass);
            gui2.setItem(8, whiteGlass); gui2.setItem(17, yellowGlass); gui2.setItem(26, yellowGlass);

            trader.openInventory(gui1);
            player.openInventory(gui2);
        }
    }
}
