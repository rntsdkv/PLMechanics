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
import ru.prisonlife.SpawnID;
import ru.prisonlife.behavior.SpawnBehavior;
import ru.prisonlife.behavior.SpawnBehaviorFactory;
import ru.prisonlife.item.PrisonItem;
import ru.prisonlife.plugin.PLPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static ru.prisonlife.plmechanics.Main.colorize;

public class PrisonerDeath implements Listener {

    public static List<Player> deadPlayers = new ArrayList<>();

    private PLPlugin plugin;
    public PrisonerDeath(PLPlugin main) {
        this.plugin = main;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDeath(PlayerDeathEvent event) {
        event.getDrops().clear();
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        FileConfiguration config = plugin.getConfig();
        Player killer = event.getEntity().getKiller();
        Player dead = event.getEntity().getPlayer();

        Prisoner killerPrisoner = PrisonLife.getPrisoner(killer);
        Prisoner deadPrisoner = PrisonLife.getPrisoner(dead);

        int respect = respectManager(killerPrisoner, deadPrisoner);

        moneyManager(event.getDrops(), event.getEntity().getLocation());

        killer.sendMessage(colorize(config.getString("messages.toKillerOnDeath"))
                .replace("%respect%", String.valueOf(respect)));

        dead.getInventory().clear();

        Random random = new Random();
        int x = random.nextInt(3);

        SpawnBehavior spawnBehavior = SpawnBehaviorFactory.createBehavior(SpawnID.HOSPITAL_SPAWN);
        spawnBehavior.spawn(deadPrisoner);

        if (x == 0) dead.setHealth(1);
        else if (x == 1) dead.setHealth(2);
        else if (x == 2) dead.setHealth(3);
        deadPlayers.add(dead);
    }

    private int respectManager(Prisoner killer, Prisoner dead) {
        FileConfiguration config = plugin.getConfig();
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
