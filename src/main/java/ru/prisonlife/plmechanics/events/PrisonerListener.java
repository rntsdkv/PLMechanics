package ru.prisonlife.plmechanics.events;

import fr.minuskube.netherboard.Netherboard;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.player.*;
import org.bukkit.scheduler.BukkitTask;
import ru.prisonlife.PositionManager;
import ru.prisonlife.PrisonLife;
import ru.prisonlife.Prisoner;
import ru.prisonlife.SpawnID;
import ru.prisonlife.behavior.SpawnBehavior;
import ru.prisonlife.behavior.SpawnBehaviorFactory;
import ru.prisonlife.database.json.BoldPoint;
import ru.prisonlife.plmechanics.HospitalBarrier;
import ru.prisonlife.plugin.PLPlugin;

import java.util.*;

import static ru.prisonlife.plmechanics.Main.colorize;
import static ru.prisonlife.plmechanics.commands.HospitalBarriers.hospitals;

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
    public static Map<Player, BukkitTask> messages = new HashMap<>();

    public List<Player> deadPlayers = new ArrayList<>();
    public Map<Player, BukkitTask> taskRegain = new HashMap<>();

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
        deadPlayers.add(player);
    }

    @EventHandler
    public void onHealthRegain(EntityRegainHealthEvent event) {
        Player player = (Player) event.getEntity();

        if (deadPlayers.contains(player)) event.setCancelled(true);
    }

    @EventHandler
    public void onSleep(PlayerBedEnterEvent event) {
        Player player = event.getPlayer();
        Random random = new Random();
        Block block = event.getBed();

        if (!block.getType().name().contains("BED")) return;

        boolean in = false;
        for (HospitalBarrier hb : hospitals) {
            if (hb.isInside(block.getLocation())) in = true; break;
        }
        if (!in) return;

        int p = random.nextInt(60) + 60;

        BukkitTask task;
        task = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            if (!player.isSleeping()) return;
            if (player.getHealth() == 10) return;
            player.setHealth(player.getHealth() + 0.5);
            player.sendTitle("+0.5<3", "Вы восстановили здоровье!", 1, 20, 1);
            player.playSound(player.getLocation(), Sound.ENTITY_WANDERING_TRADER_DRINK_POTION, 1, 1);
            if (player.getHealth() == 10) deadPlayers.remove(player);
        }, p * 20, p * 20);
        taskRegain.put(player, task);
    }

    @EventHandler
    public void onSleepUp(PlayerBedLeaveEvent event) {
        Player player = event.getPlayer();

        if (taskRegain.containsKey(player)) {
            taskRegain.get(player).cancel();
            taskRegain.remove(player);
        }
    }

    @EventHandler
    public void onMessageSend(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage();
        int messageLength = message.length();
        Prisoner prisoner = PrisonLife.getPrisoner(player);

        event.setCancelled(true);

        if (messageLength > 100) {
            player.sendMessage(colorize(plugin.getConfig().getString("messages.messageLength")));
            return;
        }

        ChatColor chatColor;
        if (PrisonLife.getPrisoner(player).getFaction() == null) chatColor = ChatColor.WHITE;
        else chatColor = prisoner.getFaction().getColor();

        BoldPoint locationPoint = BoldPoint.fromLocation(player.getLocation());

        for (Player p : Bukkit.getServer().getOnlinePlayers()) {
            BoldPoint boldPoint = BoldPoint.fromLocation(p.getLocation());
            if (!PositionManager.instance().atSector(locationPoint, 20, boldPoint)) return;
            if (PositionManager.instance().atSector(locationPoint, 10, boldPoint)) {
                p.sendMessage(message + chatColor + " (" + player.getName() + ")");
            } else if (PositionManager.instance().atSector(locationPoint, 15, boldPoint)) {
                p.sendMessage(ChatColor.GRAY + message + " (" + player.getName() + ")");
            } else {
                p.sendMessage(ChatColor.DARK_GRAY + message + " (" + player.getName() + ")");
            }
        }

        Location location = player.getLocation();
        double x = location.getX();
        double y = location.getY();
        double z = location.getZ();

        if (messages.containsKey(player) && messages.get(player).isSync()) {
            for (ArmorStand as : messagesStands.get(player)) as.remove();
            messagesStands.remove(player);
            messages.get(player).cancel();
            messages.remove(player);
        }

        if (messageLength <= 20) {
            createArmorStand(player, message, x, y + 0.15, z);
        } else if (messageLength <= 30) {
            createArmorStand(player, message.substring(0, 20), x, y + 0.4, z);
            createArmorStand(player, message.substring(20), x, y + 0.15, z);
        } else if (messageLength <= 50) {
            createArmorStand(player, message.substring(0, 35), x, y + 0.4, z);
            createArmorStand(player, message.substring(35), x, y + 0.15, z);
        } else if (messageLength <= 70) {
            createArmorStand(player, message.substring(0, 45), x, y + 0.4, z);
            createArmorStand(player, message.substring(45), x, y + 0.15, z);
        } else if (messageLength <= 80) {
            createArmorStand(player, message.substring(0, 45), x, y + 0.65, z);
            createArmorStand(player, message.substring(45, 65), x, y + 0.4, z);
            createArmorStand(player, message.substring(65), x, y + 0.15, z);
        } else {
            createArmorStand(player, message.substring(0, 50), x, y + 0.65, z);
            createArmorStand(player, message.substring(50, 80), x, y + 0.4, z);
            createArmorStand(player, message.substring(80), x, y + 0.15, z);
        }

        BukkitTask taskMessage;
        taskMessage = Bukkit.getScheduler().runTaskLater(plugin, () -> {
            Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                for (ArmorStand as : messagesStands.get(player)) as.remove();
            });
            messages.remove(player);
        }, 200);
        messages.put(player, taskMessage);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        if (messagesStands.containsKey(player)) {
            List<ArmorStand> armorsStands = messagesStands.get(player);
            Location location = player.getLocation();

            if (armorsStands.size() == 1) {
                armorsStands.get(0).teleport(location.add(0, 0.15, 0));
            } else if (armorsStands.size() == 2) {
                armorsStands.get(0).teleport(location.add(0, 0.4, 0));
                armorsStands.get(1).teleport(location.add(0, 0.15, 0));
            } else if (armorsStands.size() == 3) {
                armorsStands.get(0).teleport(location.add(0, 0.65, 0));
                armorsStands.get(1).teleport(location.add(0, 0.4, 0));
                armorsStands.get(2).teleport(location.add(0, 0.15, 0));
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

            if (!messagesStands.containsKey(player)) {
                messagesStands.put(player, new ArrayList<>());
            }

            messagesStands.get(player).add(armorStand);
        });
    }
}
