package ru.prisonlife.plmechanics.events;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import ru.prisonlife.PrisonLife;
import ru.prisonlife.Prisoner;
import ru.prisonlife.item.PrisonItem;
import ru.prisonlife.plugin.PLPlugin;

import java.util.List;

import static ru.prisonlife.plmechanics.Main.colorize;

public class onPrisonerDeath implements Listener {

    private PLPlugin plugin;
    public onPrisonerDeath(PLPlugin main) {
        this.plugin = main;
    }

    FileConfiguration config = plugin.getConfig();

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDeath(PlayerDeathEvent event) {
        event.getDrops().clear();
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player killer = event.getEntity().getKiller();
        Player dead = event.getEntity().getPlayer();

        Prisoner killerPrisoner = PrisonLife.getPrisoner(killer);
        Prisoner deadPrisoner = PrisonLife.getPrisoner(dead);

        int respect = respectManager(killerPrisoner, deadPrisoner);

        moneyManager(event.getDrops(), event.getEntity().getLocation());

        killer.sendMessage(colorize(config.getString("messages.toKillerOnDeath"))
                .replace("%respect%", String.valueOf(respect)));

        dead.getInventory().clear();
    }

    private int respectManager(Prisoner killer, Prisoner dead) {
        int respect = config.getInt("settings.reduceRespectOnDeath");
        if (killer.getRespect() < dead.getRespect()) {
            dead.setRespect(dead.getRespect() - respect);
            return respect;
        }
        return 0;
    }

    private void moneyManager(List<ItemStack> drops, Location location) {
        for (ItemStack drop : drops) {
            String itemName = drop.getItemMeta().getLocalizedName();

            boolean one = itemName.equals(PrisonItem.DOLLAR_ONE.getNamespace());
            boolean two = itemName.equals(PrisonItem.DOLLAR_TWO.getNamespace());
            boolean five = itemName.equals(PrisonItem.DOLLAR_FIVE.getNamespace());
            boolean ten = itemName.equals(PrisonItem.DOLLAR_TEN.getNamespace());
            boolean twenty = itemName.equals(PrisonItem.DOLLAR_TWENTY.getNamespace());
            boolean fifty = itemName.equals(PrisonItem.DOLLAR_FIFTY.getNamespace());
            boolean hundred = itemName.equals(PrisonItem.DOLLAR_HUNDRED.getNamespace());

            if (one || two || five || ten || twenty || fifty || hundred) {
                location.getWorld().dropItemNaturally(location, drop);
            }
        }
    }
}
