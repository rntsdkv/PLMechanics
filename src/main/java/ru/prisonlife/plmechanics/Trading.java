package ru.prisonlife.plmechanics;

import org.bukkit.entity.Player;
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
}
