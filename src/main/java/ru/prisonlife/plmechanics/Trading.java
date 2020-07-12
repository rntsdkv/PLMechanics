package ru.prisonlife.plmechanics;

import org.bukkit.entity.Player;

/**
 * @author rntsdkv
 * @project PLMechanics
 */

public class Trading {

    private Player trader;
    private Player player;
    private int time;

    public Trading(Player trader, Player player) {
        this.trader = trader;
        this.player = player;
        time = 30;
    }

    public Player getTrader() { return trader; }

    public Player getPlayer() { return player; }

    public int getTime() { return time; }

    public void setTime(int time) { this.time = time; }

    public void reduceTime() { if (time != -1) time--; }
}
