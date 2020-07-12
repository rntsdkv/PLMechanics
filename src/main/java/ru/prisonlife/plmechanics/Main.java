package ru.prisonlife.plmechanics;

import org.bukkit.ChatColor;
import org.bukkit.plugin.PluginManager;
import ru.prisonlife.plmechanics.events.onItemDrop;
import ru.prisonlife.plmechanics.events.onPrisonerDeath;
import ru.prisonlife.plugin.PLPlugin;
import ru.prisonlife.util.Pair;

import java.io.File;
import java.util.List;

public class Main extends PLPlugin {

    public String getPluginName() {
        return "PLMechanics";
    }

    public List<Pair<String, Object>> initPluginFiles() {
        return null;
    }

    public void onCreate() {
        copyConfigFile();
    }
    
    public void onEnable() {
        registerListeners();
    }

    private void registerListeners() {
        PluginManager pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(new onPrisonerDeath(this), this);
        pluginManager.registerEvents(new onItemDrop(), this);
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
