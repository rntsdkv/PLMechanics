package ru.prisonlife.plmechanics.commands;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.prisonlife.plmechanics.Trading;

import static ru.prisonlife.plmechanics.Main.colorize;
import static ru.prisonlife.plmechanics.commands.Trade.trades;

/**
 * @author rntsdkv
 * @project PLMechanics
 */

public class TradeAcceptDecline implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player)) return true;

        Player player = (Player) commandSender;

        for (int i = 0; i <= trades.size(); i++) {
            Trading trading = trades.get(i);
            if (trading.getPlayer() != player) continue;

            if (command.getName().equals("tradeaccept")) {
                trading.setTime(-1);
                trading.start();
                return true;
            } else if (command.getName().equals("tradedecline")) {
                trades.remove(i);
                player.sendMessage(colorize("&l&6Вы отказались от сделки"));
                trading.getTrader().sendMessage(colorize("&l&c" + player.getName() + " отказался от сделки"));
                return true;
            }
        }

        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.RED + "У вас нет предложений о сделке!"));
        return true;
    }
}
