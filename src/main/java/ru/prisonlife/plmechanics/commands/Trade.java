package ru.prisonlife.plmechanics.commands;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import ru.prisonlife.plmechanics.Trading;
import ru.prisonlife.plugin.PLPlugin;

import java.util.ArrayList;
import java.util.List;

import static ru.prisonlife.plmechanics.Main.colorize;

/**
 * @author rntsdkv
 * @project PLMechanics
 */

public class Trade implements CommandExecutor {

    private PLPlugin plugin;
    public Trade(PLPlugin plugin) {
        this.plugin = plugin;
    }

    public static List<Trading> trades = new ArrayList<>();
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        FileConfiguration config = plugin.getConfig();
        if (!(commandSender instanceof Player)) {
            commandSender.sendMessage("Only players can use this command!");
            return true;
        }

        Player player = (Player) commandSender;

        if (strings.length != 1) return false;

        String name = strings[0];

        Player t = Bukkit.getPlayer(name);

        if (!t.isOnline()) {
            player.sendMessage(colorize(config.getString("messages.playerIsOffline")));
            return true;
        }

        for (Trading trading : trades) {
            if (trading.getPlayer().equals(t)) {
                player.sendMessage(colorize(config.getString("messages.playerAlreadyTrading")));
                return true;
            }
        }

        trades.add(new Trading(player, t));

        t.sendMessage(colorize("&l&bИгрок &1" + player.getName() + "&b предлагает вам трейд"));
        TextComponent textComponent = new TextComponent(colorize("&l&2Принять"));
        textComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(colorize("&l&2Принять трейд"))));
        textComponent.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/phoneaccept true"));
        TextComponent textComponent1 = new TextComponent(colorize("&l&cОтменить"));
        textComponent1.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(colorize("&l&2Отменить трейд"))));
        textComponent1.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/phoneaccept false"));
        t.spigot().sendMessage(textComponent);
        t.spigot().sendMessage(textComponent1);
        return true;
    }
}
