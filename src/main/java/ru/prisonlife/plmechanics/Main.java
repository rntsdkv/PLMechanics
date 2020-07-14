package ru.prisonlife.plmechanics;

import org.bukkit.ChatColor;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitTask;
import ru.prisonlife.plmechanics.commands.Trade;
import ru.prisonlife.plmechanics.commands.TradeAcceptDecline;
import ru.prisonlife.plmechanics.events.GUIListener;
import ru.prisonlife.plmechanics.events.PrisonerListener;
import ru.prisonlife.plmechanics.events.onItemDrop;
import ru.prisonlife.plmechanics.events.PrisonerDeath;
import ru.prisonlife.plugin.PLPlugin;
import ru.prisonlife.plugin.PromisedPluginFile;

import java.io.File;
import java.util.List;

public class Main extends PLPlugin {

    public static BukkitTask taskTrades;

    public String getPluginName() {
        return "PLMechanics";
    }

    public List<PromisedPluginFile> initPluginFiles() {
        return null;
    }
    
    public void onEnable() {
        copyConfigFile();
        registerListeners();
        registerCommands();
    }

    private void registerListeners() {
        PluginManager pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(new PrisonerDeath(this), this);
        pluginManager.registerEvents(new onItemDrop(), this);
        pluginManager.registerEvents(new PrisonerListener(this), this);
        pluginManager.registerEvents(new GUIListener(), this);
    }

    private void registerCommands() {
        getCommand("trade").setExecutor(new Trade(this));
        getCommand("tradeaccept").setExecutor(new TradeAcceptDecline());
        getCommand("tradedecline").setExecutor(new TradeAcceptDecline());
    }

    private void copyConfigFile() {
        File config = new File(getDataFolder() + File.separator + "config.yml");
        if (!config.exists()) {
            getLogger().info("PLMechanics | Default Config copying...");
            getConfig().options().copyDefaults(true);
            saveDefaultConfig();
        }
    }

    public static String colorize(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }
}
