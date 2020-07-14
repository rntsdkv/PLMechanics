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
        World world = player.getWorld();

        Location location = player.getLocation();
        double x = location.getX();
        double y = location.getY();
        double z = location.getZ();

        if (messagesStands.containsKey(player)) {
            for (ArmorStand armorStand : messagesStands.get(player)) armorStand.remove();
            messagesTimer.remove(player);
        }

        if (message.length() <= 25) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                        ArmorStand armorStand = (ArmorStand) world.spawnEntity(new Location(world, x, y + 0.5, z), EntityType.ARMOR_STAND);
                        armorStand.setGravity(false);
                        armorStand.setVisible(false);
                        armorStand.setCustomNameVisible(true);
                        armorStand.setCustomName(message);
                messagesStands.put(player, new ArrayList<>());
                messagesStands.get(player).add(armorStand);
            });
        } else if (message.length() <= 50) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                ArmorStand armorStand1 = (ArmorStand) world.spawnEntity(new Location(world, x, y + 0.75, z), EntityType.ARMOR_STAND);
                armorStand1.setGravity(false);
                armorStand1.setVisible(false);
                armorStand1.setCustomNameVisible(true);
                armorStand1.setCustomName(message.substring(0, 24));
                messagesStands.put(player, new ArrayList<>());
                messagesStands.get(player).add(armorStand1);

                ArmorStand armorStand2 = (ArmorStand) world.spawnEntity(new Location(world, x, y + 0.5, z), EntityType.ARMOR_STAND);
                armorStand2.setGravity(false);
                armorStand2.setVisible(false);
                armorStand2.setCustomNameVisible(true);
                armorStand2.setCustomName(message.substring(25, 49));
                messagesStands.get(player).add(armorStand2);
            });
        } else if (message.length() <= 75) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {

                ArmorStand armorStand1 = (ArmorStand) world.spawnEntity(new Location(world, x, y + 1, z), EntityType.ARMOR_STAND);
                armorStand1.setGravity(false);
                armorStand1.setVisible(false);
                armorStand1.setCustomNameVisible(true);
                armorStand1.setCustomName(message.substring(0, 24));
                messagesStands.put(player, new ArrayList<>());
                messagesStands.get(player).add(armorStand1);

                ArmorStand armorStand2 = (ArmorStand) world.spawnEntity(new Location(world, x, y + 0.75, z), EntityType.ARMOR_STAND);
                armorStand2.setGravity(false);
                armorStand2.setVisible(false);
                armorStand2.setCustomNameVisible(true);
                armorStand2.setCustomName(message.substring(25, 49));
                messagesStands.get(player).add(armorStand2);

                ArmorStand armorStand3 = (ArmorStand) world.spawnEntity(new Location(world, x, y + 0.5, z), EntityType.ARMOR_STAND);
                armorStand3.setGravity(false);
                armorStand3.setVisible(false);
                armorStand3.setCustomNameVisible(true);
                armorStand3.setCustomName(message.substring(50, 74));
                messagesStands.get(player).add(armorStand3);
            });
        } else if (message.length() <= 100) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                ArmorStand armorStand1 = (ArmorStand) world.spawnEntity(new Location(world, x, y + 1.25, z), EntityType.ARMOR_STAND);
                armorStand1.setGravity(false);
                armorStand1.setVisible(false);
                armorStand1.setCustomNameVisible(true);
                armorStand1.setCustomName(message.substring(0, 24));
                messagesStands.put(player, new ArrayList<>());
                messagesStands.get(player).add(armorStand1);

                ArmorStand armorStand2 = (ArmorStand) world.spawnEntity(new Location(world, x, y + 1, z), EntityType.ARMOR_STAND);
                armorStand2.setGravity(false);
                armorStand2.setVisible(false);
                armorStand2.setCustomNameVisible(true);
                armorStand2.setCustomName(message.substring(25, 49));
                messagesStands.get(player).add(armorStand2);

                ArmorStand armorStand3 = (ArmorStand) world.spawnEntity(new Location(world, x, y + 0.75, z), EntityType.ARMOR_STAND);
                armorStand3.setGravity(false);
                armorStand3.setVisible(false);
                armorStand3.setCustomNameVisible(true);
                armorStand3.setCustomName(message.substring(50, 74));
                messagesStands.get(player).add(armorStand3);

                ArmorStand armorStand4 = (ArmorStand) world.spawnEntity(new Location(world, x, y + 0.5, z), EntityType.ARMOR_STAND);
                armorStand4.setGravity(false);
                armorStand4.setVisible(false);
                armorStand4.setCustomNameVisible(true);
                armorStand4.setCustomName(message.substring(75, 99));
                messagesStands.get(player).add(armorStand4);
            });
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
            World world = player.getWorld();
            double x = player.getLocation().getX();
            double y = player.getLocation().getY();
            double z = player.getLocation().getZ();

            if (armorsStands.size() == 1) {
                armorsStands.get(0).teleport(new Location(world, x, y + 0.5, z));
            } else if (armorsStands.size() == 2) {
                armorsStands.get(0).teleport(new Location(world, x, y + 0.75, z));
                armorsStands.get(1).teleport(new Location(world, x, y + 0.5, z));
            } else if (armorsStands.size() == 3) {
                armorsStands.get(0).teleport(new Location(world, x, y + 1, z));
                armorsStands.get(1).teleport(new Location(world, x, y + 0.75, z));
                armorsStands.get(2).teleport(new Location(world, x, y + 0.5, z));
            } else if (armorsStands.size() == 4) {
                armorsStands.get(0).teleport(new Location(world, x, y + 1.25, z));
                armorsStands.get(1).teleport(new Location(world, x, y + 1, z));
                armorsStands.get(2).teleport(new Location(world, x, y + 0.75, z));
                armorsStands.get(3).teleport(new Location(world, x, y + 0.5, z));
            }
        }
    }
}
