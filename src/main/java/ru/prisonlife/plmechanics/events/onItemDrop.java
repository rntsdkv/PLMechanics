package ru.prisonlife.plmechanics.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;

/**
 * @author rntsdkv
 * @project PLMechanics
 */

public class onItemDrop implements Listener {

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        event.setCancelled(true);
    }
}
