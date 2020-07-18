package ru.prisonlife.plmechanics.events;

import com.sainttx.holograms.api.Hologram;
import fr.minuskube.netherboard.Netherboard;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import ru.prisonlife.PositionManager;
import ru.prisonlife.PrisonLife;
import ru.prisonlife.Prisoner;
import ru.prisonlife.SpawnID;
import ru.prisonlife.behavior.SpawnBehavior;
import ru.prisonlife.behavior.SpawnBehaviorFactory;
import ru.prisonlife.database.json.BoldPoint;
import ru.prisonlife.item.PrisonItem;
import ru.prisonlife.item.PrisonItemBuilder;
import ru.prisonlife.item.PrisonItemFactory;
import ru.prisonlife.plmechanics.HospitalBarrier;
import ru.prisonlife.plugin.PLPlugin;

import java.util.*;

import static ru.prisonlife.plmechanics.Main.colorize;
import static ru.prisonlife.plmechanics.commands.HospitalBarriers.hospitals;
import static ru.prisonlife.plmechanics.commands.Regeneration.playerBed;
import static ru.prisonlife.plmechanics.events.PrisonerDeath.deadPlayers;

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


    @EventHandler
    public void onHealthRegain(EntityRegainHealthEvent event) {
        Player player = (Player) event.getEntity();

        boolean onBed = playerBed.containsKey(player);

        if (deadPlayers.contains(player) && !onBed) event.setCancelled(true);

        if (onBed) {
            player.sendTitle("+1 HP", null, 1, 20, 1);
            if (player.getHealth() == 10) {
                player.sendMessage(colorize("&l&6Вы восстановили здоровье!"));
                playerBed.remove(player);
                deadPlayers.remove(player);
            }
        }
    }

    @EventHandler
    public void onPotionUse(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack ammoBox = PrisonItemFactory.createItem(null, PrisonItem.AMMO_BOX);

        if (!player.getInventory().getItemInMainHand().equals(ammoBox)) return;

        player.setHealth(player.getHealth() + 3);
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
