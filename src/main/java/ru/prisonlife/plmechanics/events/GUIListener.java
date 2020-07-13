package ru.prisonlife.plmechanics.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import ru.prisonlife.plmechanics.Trading;

import static ru.prisonlife.plmechanics.commands.Trade.trades;

/**
 * @author rntsdkv
 * @project PLMechanics
 */

public class GUIListener implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();

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
                trading.updateInventory();
            } else if (event.getSlot() == 23) {
                event.setCancelled(true);
                if (player == trading.getTrader()) trading.setTraderStatus("NOT_READY");
                else trading.setPlayerStatus("NOT_READY");
                trading.updateInventory();
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
            } else if (trading.getPlayer() == player && trading.getPlayerStatusStatus().equals("NOT_READY")) {
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
        Player player = (Player) event.getPlayer();

        for (Trading trading : trades) {
            trading.close();
            if (player == trading.getTrader() || player == trading.getPlayer()) trades.remove(trading);
        }
    }
}
