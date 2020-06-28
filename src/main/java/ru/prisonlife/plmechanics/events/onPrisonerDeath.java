package ru.prisonlife.plmechanics.events;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import ru.prisonlife.PrisonLife;
import ru.prisonlife.Prisoner;
import ru.prisonlife.currency.CurrencyManager;
import ru.prisonlife.plugin.PLPlugin;

import java.util.List;

import static ru.prisonlife.plmechanics.Main.colorize;

public class onPrisonerDeath implements Listener {

    private PLPlugin plugin;
    public onPrisonerDeath(PLPlugin main) {
        this.plugin = main;
    }

    FileConfiguration config = plugin.getConfig();
    CurrencyManager currencyManager = PrisonLife.getCurrencyManager();

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        event.setKeepInventory(false);
        event.getDrops().clear();

        Player killer = event.getEntity().getKiller();
        Player dead = event.getEntity().getPlayer();

        Prisoner killerPrisoner = PrisonLife.getPrisoner(killer);
        Prisoner deadPrisoner = PrisonLife.getPrisoner(dead);

        int respect = respectManager(killerPrisoner, deadPrisoner);

        int money = moneyManager(dead);

        killer.sendMessage(colorize(config.getString("messages.toKillerOnDeath"))
                .replace("%respect%", String.valueOf(respect))
                .replace("%money%", String.valueOf(money)));

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

    private int moneyManager(Player dead) {
        int amount = currencyManager.countMoney(dead.getInventory());

        int reduceMoneyAmount = config.getInt("settings.reduceMoneyOnDeath");

        int killer;

        if (amount >= reduceMoneyAmount) {
            amount -= reduceMoneyAmount;
            killer = createMoney(dead, reduceMoneyAmount);
        } else {
            killer = createMoney(dead, amount);
            amount = 0;
        }

        createDeadMoney(dead, amount);
        return killer;
    }

    private int createMoney(Player dead, int reduceAmount) {
        List<ItemStack> money = currencyManager.createMoney(reduceAmount);

        for (ItemStack item : money) {
            dead.getWorld().dropItemNaturally(dead.getLocation(), item);
        }

        return reduceAmount;
    }

    private void createDeadMoney(Player dead, int amount) {
        List<ItemStack> money = currencyManager.createMoney(amount);

        for (ItemStack item : money) {
            dead.getInventory().addItem(item);
        }
    }
}
