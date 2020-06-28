package ru.prisonlife.plmechanics.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import ru.prisonlife.plugin.PLPlugin;

public class onDeath implements Listener {

    private PLPlugin plugin;
    public onDeath(PLPlugin main) {
        this.plugin = main;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDeath(PlayerDeathEvent event) {
        event.getDrops().clear();
    }
}
