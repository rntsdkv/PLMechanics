package ru.prisonlife.plmechanics.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import ru.prisonlife.plmechanics.Trading;
import ru.prisonlife.util.InventoryUtil;

import static ru.prisonlife.plmechanics.Main.colorize;
import static ru.prisonlife.plmechanics.commands.Trade.trades;

/**
 * @author rntsdkv
 * @project PLMechanics
 */

public class GUIListener implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();

        if (event.getClickedInventory().getType() == InventoryType.PLAYER) return;

        Trading trading = null;

        for (Trading t : trades) {
            if (!(t.getTrader() == player) && !(t.getPlayer() == player)) continue;
            trading = t;
            break;
        }

        if (trading == null) return;

        if (trading.getLevel() == 1) {
            if (event.getSlot() == 21) {
                event.setCancelled(true);
                if (player == trading.getTrader()) trading.setTraderStatus("READY");
                else trading.setPlayerStatus("READY");
            } else if (event.getSlot() == 23) {
                event.setCancelled(true);
                if (player == trading.getTrader()) trading.setTraderStatus("NOT_READY");
                else trading.setPlayerStatus("NOT_READY");
            } else if (event.getSlot() != 10) {
                event.setCancelled(true);
                return;
            }
            if (trading.getTrader() == player && trading.getTraderStatus().equals("NOT_READY")) {
                ItemStack item = event.getClickedInventory().getItem(10);
                if (item == null) return;
                trading.clearTraderItems();
                trading.putTraderItem(item);
                trading.updateInventory();
            } else if (trading.getPlayer() == player && trading.getPlayerStatus().equals("NOT_READY")) {
                ItemStack item = event.getClickedInventory().getItem(10);
                if (item == null) return;
                trading.clearPlayerItems();
                trading.putPlayerItem(item);
                trading.updateInventory();
            } else {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        Player p = (Player) event.getPlayer();

        for (Trading trading : trades) {
            Player trader = trading.getTrader();
            Player player = trading.getPlayer();

            if (!p.equals(trader) && !p.equals(player)) return;

            if (p.equals(trader)) player.closeInventory();
            else trader.closeInventory();

            trader.sendMessage(colorize("&l&cСделка разорвана!"));
            player.sendMessage(colorize("&l&cСделка разорвана!"));

            trader.removePotionEffect(PotionEffectType.GLOWING);
            player.removePotionEffect(PotionEffectType.GLOWING);

            InventoryUtil.putItemStacks(trader.getInventory(), trading.getTraderItems());
            InventoryUtil.putItemStacks(player.getInventory(), trading.getPlayerItems());

            if (trading.getTask().isSync()) trading.getTask().cancel();

            trades.remove(trading);

            break;
        }
    }
}
