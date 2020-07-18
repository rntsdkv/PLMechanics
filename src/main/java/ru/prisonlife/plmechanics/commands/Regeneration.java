package ru.prisonlife.plmechanics.commands;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ru.prisonlife.plmechanics.Main.colorize;

/**
 * @author rntsdkv
 * @project PLMechanics
 */

public class Regeneration implements CommandExecutor {
    
    public static Map<Player, List<Integer>> playerBed = new HashMap<>();
    public static List<List<Integer>> useBeds = new ArrayList<>();
    
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage("Only players can use this command!");
            return true;
        }
        
        Player player = (Player) commandSender;
        
        Location location = player.getLocation();
        int x = location.getBlockX();
        int y = location.getBlockY();
        int z = location.getBlockZ();
        
        if (!new Location(player.getWorld(), x, y - 1, z).getBlock().getType().name().contains("BED")) {
            player.sendMessage(colorize("&l&cНа кровати удобнее!"));
            return true;
        }
        
        if (player.getHealth() >= 10) {
            player.sendMessage(colorize("&l&cУ вас полное здоровье!"));
            return true;
        }
        
        if (playerBed.containsKey(player)) {
            player.sendMessage(colorize("&l&cУ вас уже есть койка!"));
            return true;
        }

        for (List<Integer> bed : useBeds) {
            if (bed.get(1) == y - 1 && bed.get(0) == x && bed.get(2) == z) {
                player.sendMessage(colorize("&l&cЭта койка занята!"));
                return true;
            }
        }

        playerBed.put(player, new ArrayList<>());
        playerBed.get(player).add(x);
        playerBed.get(player).add(y - 1);
        playerBed.get(player).add(z);
        return true;
    }
}
