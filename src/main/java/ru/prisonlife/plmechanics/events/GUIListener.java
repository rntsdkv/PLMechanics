package ru.prisonlife.plmechanics.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
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

        if (!event.getView().getTitle().equals("Сделка")) return;

        Trading trading = null;

        for (Trading t : trades) {
            if (!(t.getTrader() == player) && !(t.getPlayer() == player)) continue;
            trading = t;
            break;
        }

        if (trading.getLevel() == 1) {
            if (event.getSlot() != 10) {
                event.setCancelled(true);
                return;
            }
            if (trading.getTrader() == player) {
                ItemStack item = event.getClickedInventory().getItem(10);
                if (item == null) return;
                trading.clearTraderItems();
                trading.putTraderItem(item);
            } else if (trading.getPlayer() == player) {
                ItemStack item = event.getClickedInventory().getItem(10);
                if (item == null) return;
                trading.clearPlayerItems();
                trading.putPlayerItem(item);
            }
        }
    }
}
