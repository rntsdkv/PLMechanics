package ru.prisonlife.plmechanics.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.prisonlife.PositionManager;
import ru.prisonlife.PrisonLife;
import ru.prisonlife.database.json.BoldPoint;

/**
 * @author rntsdkv
 * @project PLMechanics
 */

public class Messages implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage("Only players can use this command");
            return true;
        }

        String commandName = command.getName();
        Player player = (Player) commandSender;
        BoldPoint locationPoint = BoldPoint.fromLocation(player.getLocation());
        String message = String.join(" ", strings);

        if (commandName.equals("s")) {
            for (Player p : Bukkit.getServer().getOnlinePlayers()) {
                if (!PositionManager.instance().atSector(locationPoint, 40, BoldPoint.fromLocation(p.getLocation()))) continue;
                p.sendMessage(ChatColor.BOLD + message + String.format(" (%d)", player.getName()));
            }
        } else if (commandName.equals("w")) {
            for (Player p : Bukkit.getServer().getOnlinePlayers()) {
                if (!PositionManager.instance().atSector(locationPoint, 1, BoldPoint.fromLocation(p.getLocation()))) continue;
                p.sendMessage(ChatColor.DARK_GREEN + message + String.format(" (%d)", player.getName()));
            }
        } else if (commandName.equals("i")) {
            for (Player p : Bukkit.getServer().getOnlinePlayers()) {
                if (!PositionManager.instance().atSector(locationPoint, 20, BoldPoint.fromLocation(p.getLocation()))) continue;
                p.sendMessage(ChatColor.LIGHT_PURPLE + player.getName() + ": " + message);
            }
        } else if (commandName.equals("do")) {
            for (Player p : Bukkit.getServer().getOnlinePlayers()) {
                if (!PositionManager.instance().atSector(locationPoint, 20, BoldPoint.fromLocation(p.getLocation()))) continue;
                p.sendMessage(ChatColor.LIGHT_PURPLE + message + String.format(" (%d)", player.getName()));
            }
        }

        return true;
    }
}
