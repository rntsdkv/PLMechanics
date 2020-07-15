package ru.prisonlife.plmechanics.commands;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import ru.prisonlife.plmechanics.HospitalBarrier;
import ru.prisonlife.plugin.PLPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ru.prisonlife.plmechanics.Main.colorize;

/**
 * @author rntsdkv
 * @project PLMechanics
 */

public class HospitalBarriers implements CommandExecutor {

    public static Map<Player, Location> position1 = new HashMap<>();
    public static Map<Player, Location> position2 = new HashMap<>();
    public static List<HospitalBarrier> hospitals = new ArrayList<>();

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage("Only players can use this command!");
            return true;
        }

        int length = strings.length;

        if (length == 1) {
            if (strings[0].equals("pos1")) pos1(commandSender);
            else if (strings[0].equals("pos2")) pos2(commandSender);
            create(commandSender);
        }
        return true;
    }

    private boolean pos1(CommandSender sender) {
        Player player = (Player) sender;
        Location location = player.getLocation();

        if (position1.containsKey(player)) position1.replace(player, location);
        else position1.put(player, location);
        return true;
    }

    private boolean pos2(CommandSender sender) {
        Player player = (Player) sender;
        Location location = player.getLocation();

        if (position2.containsKey(player)) position2.replace(player, location);
        else position2.put(player, location);
        return true;
    }

    private boolean create(CommandSender sender) {
        Player player = (Player) sender;

        if (!position1.containsKey(player) || !position2.containsKey(player)) {
            player.sendMessage(colorize("&l&cНеобходимо установить точки!"));
            return true;
        }

        Location location1 = position1.get(player);
        Location location2 = position2.get(player);

        World world = location1.getWorld();

        int x1 = location1.getBlockX();
        int y1 = location1.getBlockY();
        int z1 = location1.getBlockZ();

        int x2 = location2.getBlockX();
        int y2 = location2.getBlockY();
        int z2 = location2.getBlockZ();

        HospitalBarrier hospitalBarrier = new HospitalBarrier(world, x1, y1, z1, x2, y2, z2);
        hospitals.add(hospitalBarrier);

        player.sendMessage(colorize("&l&6Барьер больницы создан!"));
        return true;
    }
}
