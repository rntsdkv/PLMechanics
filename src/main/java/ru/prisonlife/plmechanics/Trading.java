package ru.prisonlife.plmechanics;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;
import ru.prisonlife.plugin.PLPlugin;
import ru.prisonlife.util.InventoryUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static ru.prisonlife.plmechanics.Main.colorize;
import static ru.prisonlife.plmechanics.commands.Trade.trades;

/**
 * @author rntsdkv
 * @project PLMechanics
 */

public class Trading {

    private PLPlugin plugin;
    public Trading(PLPlugin plugin) {
        this.plugin = plugin;
    }

    enum Status { NOT_READY, READY }

    private Player trader;
    private Player player;
    private int level;
    private int time;
    private Status traderStatus;
    private Status playerStatus;
    private List<ItemStack> traderItems = new ArrayList<>();
    private List<ItemStack> playerItems = new ArrayList<>();
    private BukkitTask task;
    public BukkitTask particles;

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

    public List<ItemStack> getTraderItems() { return traderItems; }

    public void putPlayerItem(ItemStack item) { playerItems.add(item); }

    public List<ItemStack> getPlayerItems() { return playerItems; }

    public void setTraderStatus(String status) {
        traderStatus = Status.valueOf(status);

        if (traderStatus == Status.NOT_READY) {
            if (task.isSync()) task.cancel();
            trader.getOpenInventory().setItem(3, new ItemStack(Material.COMPASS));
            player.getOpenInventory().setItem(5, new ItemStack(Material.COMPASS));
        } else if (traderStatus == Status.READY) {
            trader.playSound(trader.getLocation(), Sound.BLOCK_BELL_USE, 1, 1);
            player.playSound(player.getLocation(), Sound.BLOCK_BELL_USE, 1, 1);
            trader.getOpenInventory().setItem(3, new ItemStack(Material.BELL));
            player.getOpenInventory().setItem(5, new ItemStack(Material.BELL));
        }

        if (traderStatus == Status.NOT_READY || playerStatus == Status.NOT_READY) return;

        task = Bukkit.getScheduler().runTaskLater(plugin, () -> {
            close();
            trades.removeIf(trading -> trading.getTrader() == trader);
        }, 100);
    }

    public String getTraderStatus() { return traderStatus.name(); }

    public void setPlayerStatus(String status) {
        playerStatus = Status.valueOf(status);

        if (playerStatus == Status.NOT_READY) {
            if (task.isSync()) task.cancel();
            player.getOpenInventory().setItem(3, new ItemStack(Material.COMPASS));
            trader.getOpenInventory().setItem(5, new ItemStack(Material.COMPASS));
        } else if (playerStatus == Status.READY) {
            trader.playSound(trader.getLocation(), Sound.BLOCK_BELL_USE, 1, 1);
            player.playSound(player.getLocation(), Sound.BLOCK_BELL_USE, 1, 1);
            player.getOpenInventory().setItem(3, new ItemStack(Material.BELL));
            trader.getOpenInventory().setItem(5, new ItemStack(Material.BELL));
        }

        if (traderStatus == Status.NOT_READY || playerStatus == Status.NOT_READY) return;

        task = Bukkit.getScheduler().runTaskLater(plugin, () -> {
            for (Trading trading : trades) if (trading.getTrader().equals(trader)) trades.remove(trading);
            close();
        }, 100);
    }

    public String getPlayerStatus() { return playerStatus.name(); }

    public void updateInventory() {
        if (level == 1) {
            trader.getOpenInventory().setItem(16, playerItems.get(0));
            player.getOpenInventory().setItem(16, traderItems.get(0));
        }
    }

    public void close() {
        if (!canPuttedItems(player.getInventory(), traderItems)) {
            player.sendMessage(colorize("&l&cУ вас недостаточно места в карманах! Сделка разорвана."));
            trader.sendMessage(colorize("&l&cУ " + player.getName() + " недостаточно места в карманах! Сделка разорвана."));
            return;
        }

        if (!canPuttedItems(trader.getInventory(), playerItems)) {
            trader.sendMessage(colorize("&l&cУ вас недостаточно места в карманах! Сделка разорвана."));
            player.sendMessage(colorize("&l&cУ " + trader.getName() + " недостаточно места в карманах! Сделка разорвана."));
            return;
        }

        if (particles.isSync()) particles.cancel();

        InventoryUtil.putItemStacks(trader.getInventory(), playerItems);
        InventoryUtil.putItemStacks(player.getInventory(), traderItems);

        trader.sendMessage(colorize("&l&6Сделка завершена"));
        player.sendMessage(colorize("&l&6Сделка завершена"));
    }

    public void start() {
        traderStatus = Status.NOT_READY;
        playerStatus = Status.NOT_READY;
        createGUI(trader, player);

        particles = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            trader.spawnParticle(Particle.LAVA, trader.getLocation(), 1);
            player.spawnParticle(Particle.LAVA, player.getLocation(), 1);
        }, 0, 20);
    }

    public BukkitTask getTask() { return task; }

    private void createGUI(Player trader, Player player) {
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

    private boolean canPuttedItems(Inventory inventory, List<ItemStack> items) {
        int inventorySze = inventory instanceof PlayerInventory ? 36 : inventory.getSize();
        int counter = 0;
        Iterator var = items.iterator();

        while (true) {
            while (var.hasNext()) {
                ItemStack item = (ItemStack) var.next();
                String localizedName = item.getItemMeta().getLocalizedName();
                int amount1 = item.getAmount();

                for (int j = 0; j < inventorySze; j++) {
                    ItemStack itemStack = inventory.getItem(j);
                    if (itemStack == null) {
                        counter++;
                        break;
                    }

                    if (itemStack.getItemMeta().getLocalizedName().equals(localizedName)) {
                        int amount2 = itemStack.getAmount();
                        if (amount1 + amount2 <= 64) {
                            counter++;
                            break;
                        }

                        amount1 -= 64 - amount2;
                    }
                }
            }

            return counter == items.size();
        }
    }
}
