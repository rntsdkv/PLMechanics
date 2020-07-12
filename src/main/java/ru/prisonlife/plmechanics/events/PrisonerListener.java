package ru.prisonlife.plmechanics.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import ru.prisonlife.PrisonLife;
import ru.prisonlife.Prisoner;
import ru.prisonlife.SpawnID;
import ru.prisonlife.behavior.SpawnBehavior;
import ru.prisonlife.behavior.SpawnBehaviorFactory;
import ru.prisonlife.plugin.PLPlugin;

import java.util.Random;

/**
 * @author rntsdkv
 * @project PLMechanics
 */

public class PrisonerListener implements Listener {

    private final PLPlugin plugin;
    public PrisonerListener(PLPlugin main) {
        this.plugin = main;
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        Prisoner prisoner = PrisonLife.getPrisoner(player);

        Random random = new Random();

        int x = random.nextInt(3);

        if (x == 0) player.setHealth(1);
        else if (x == 1) player.setHealth(2);
        else if (x == 2) player.setHealth(3);

        SpawnBehavior spawnBehavior = SpawnBehaviorFactory.createBehavior(SpawnID.HOSPITAL_SPAWN);

        spawnBehavior.spawn(prisoner);
    }

    @EventHandler
    public void onHealthRegain(EntityRegainHealthEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onHungry(FoodLevelChangeEvent event) {
        Player player = (Player) event.getEntity();

        int level = event.getFoodLevel();

        if (player.hasPotionEffect(PotionEffectType.WEAKNESS)) player.removePotionEffect(PotionEffectType.WEAKNESS);
        if (player.hasPotionEffect(PotionEffectType.SLOW)) player.removePotionEffect(PotionEffectType.SLOW);
        if (player.hasPotionEffect(PotionEffectType.CONFUSION)) player.removePotionEffect(PotionEffectType.CONFUSION);

        if (level == 14 || level == 13) player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 0, 1));
        else if (level == 12 || level == 11) player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 0, 1));
        else if (level == 10 || level == 9) player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 0, 2));
        else if (level == 8 || level == 7) player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 0, 3));
        else if (level >= 6) player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 0, 4));

        if (level >= 10) player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 400, 1));
    }
}
