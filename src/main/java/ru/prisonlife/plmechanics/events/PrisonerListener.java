package ru.prisonlife.plmechanics.events;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;
import ru.prisonlife.PrisonLife;
import ru.prisonlife.Prisoner;
import ru.prisonlife.SpawnID;
import ru.prisonlife.behavior.SpawnBehavior;
import ru.prisonlife.behavior.SpawnBehaviorFactory;
import ru.prisonlife.plugin.PLPlugin;

import java.util.*;

/**
 * @author rntsdkv
 * @project PLMechanics
 */

public class PrisonerListener implements Listener {

    private final PLPlugin plugin;
    public PrisonerListener(PLPlugin main) {
        this.plugin = main;
    }

    public static Map<Player, List<ArmorStand>> messagesStands = new HashMap<>();
    public static Map<Player, Integer> messagesTimer = new HashMap<>();
    public static BukkitTask taskMessages;

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

        if (level == 14 || level == 13) player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, Integer.MAX_VALUE, 1));
        else if (level == 12 || level == 11) player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 1));
        else if (level == 10 || level == 9) player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 2));
        else if (level == 8 || level == 7) player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 3));
        else if (level >= 6) player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 4));

        if (level >= 10 && !player.hasPotionEffect(PotionEffectType.CONFUSION)) player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 400, 1));
    }

    @EventHandler
    public void onMessageSend(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage();

        Location location = player.getLocation();
        double x = location.getX();
        double y = location.getY();
        double z = location.getZ();

        if (messagesStands.containsKey(player)) {
            for (ArmorStand armorStand : messagesStands.get(player)) armorStand.remove();
            messagesTimer.remove(player);
        }


        if (message.length() <= 25) {
            createArmorStand(player, message, x, y + 0.5, z);
        } else if (message.length() <= 50) {
            createArmorStand(player, message.substring(0, 24), x, y + 0.75, z);
            createArmorStand(player, message.substring(25, 49), x, y + 0.5, z);
        } else if (message.length() <= 75) {
            createArmorStand(player, message.substring(0, 24), x, y + 1, z);
            createArmorStand(player, message.substring(25, 49), x, y + 0.75, z);
            createArmorStand(player, message.substring(50, 74), x, y + 0.5, z);
        } else if (message.length() <= 100) {
            createArmorStand(player, message.substring(0, 24), x, y + 1.25, z);
            createArmorStand(player, message.substring(25, 49), x, y + 1, z);
            createArmorStand(player, message.substring(50, 74), x, y + 0.75, z);
            createArmorStand(player, message.substring(75, 99), x, y + 0.5, z);
        }
        messagesTimer.put(player, 0);

        if (messagesStands.size() == 1) {
            taskMessages = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
                for (Player p : messagesTimer.keySet()) {
                    messagesTimer.replace(p, messagesTimer.get(p) + 1);
                    if (messagesTimer.get(p) == 10) {
                        for (ArmorStand a : messagesStands.get(p)) {
                            messagesTimer.remove(p);
                            messagesStands.remove(p);
                            a.remove();
                        }
                    }
                }
                if (messagesTimer.size() == 0) taskMessages.cancel();
            }, 20, 20);
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        if (messagesStands.containsKey(player)) {
            List<ArmorStand> armorsStands = messagesStands.get(player);
            Location location = player.getLocation();

            if (armorsStands.size() == 1) {
                armorsStands.get(0).teleport(location.add(0, 0.5, 0));
            }
            else if (armorsStands.size() == 2) {
                armorsStands.get(0).teleport(location.add(0, 0.75, 0));
                armorsStands.get(1).teleport(location.add(0, 0.5, 0));
            }
            else if (armorsStands.size() == 3) {
                armorsStands.get(0).teleport(location.add(0, 1, 0));
                armorsStands.get(1).teleport(location.add(0, 0.75, 0));
                armorsStands.get(2).teleport(location.add(0, 0.5, 0));
            }
            else if (armorsStands.size() == 4) {
                armorsStands.get(0).teleport(location.add(0, 1.25, 0));
                armorsStands.get(1).teleport(location.add(0, 1, 0));
                armorsStands.get(2).teleport(location.add(0, 0.75, 0));
                armorsStands.get(3).teleport(location.add(0, 0.5, 0));
            }
        }
    }

    private void createArmorStand(Player player, String text, double x, double y, double z) {
        World world = player.getWorld();
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            ArmorStand armorStand = (ArmorStand) world.spawnEntity(new Location(world, x, y, z), EntityType.ARMOR_STAND);
            armorStand.setGravity(false);
            armorStand.setVisible(false);
            armorStand.setCustomNameVisible(true);
            armorStand.setCustomName(text);

            if (messagesStands.containsKey(player)) {
                messagesStands.put(player, new ArrayList<>());
            }

            messagesStands.get(player).add(armorStand);
        });
    }
}
