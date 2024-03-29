package ru.prisonlife.plmechanics;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitTask;
import ru.prisonlife.plmechanics.commands.*;
import ru.prisonlife.plmechanics.events.PrisonerListener;
import ru.prisonlife.plmechanics.events.onItemDrop;
import ru.prisonlife.plmechanics.events.PrisonerDeath;
import ru.prisonlife.plugin.PLPlugin;
import ru.prisonlife.plugin.PromisedPluginFile;

import java.io.File;
import java.util.List;

import static ru.prisonlife.plmechanics.commands.HospitalBarriers.hospitals;

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

        ConfigurationSection section = getConfig().getConfigurationSection("hb");
        if (section != null) {
            for (String key : section.getKeys(false)) {
                World world = Bukkit.getWorld(getConfig().getString("hb." + key + ".world"));

                int x1 = getConfig().getInt("hb." + key + ".x1");
                int y1 = getConfig().getInt("hb." + key + ".y1");
                int z1 = getConfig().getInt("hb." + key + ".z1");

                int x2 = getConfig().getInt("hb." + key + ".x2");
                int y2 = getConfig().getInt("hb." + key + ".y2");
                int z2 = getConfig().getInt("hb." + key + ".z2");

                HospitalBarrier hospitalBarrier = new HospitalBarrier(world, x1, y1, z1, x2, y2, z2);
                hospitals.add(hospitalBarrier);
            }
        }
        getConfig().set("hb", null);
        saveConfig();
    }

    public void onDisable() {
        for (int i = 0; i <= hospitals.size(); i++) {
            HospitalBarrier hospitalBarrier = hospitals.get(i);
            getConfig().set("hb." + i + ".world", hospitalBarrier.world.getName());
            getConfig().set("hb." + i + ".x1", hospitalBarrier.x1);
            getConfig().set("hb." + i + ".y1", hospitalBarrier.y1);
            getConfig().set("hb." + i + ".z1", hospitalBarrier.z1);
            getConfig().set("hb." + i + ".x2", hospitalBarrier.x2);
            getConfig().set("hb." + i + ".y2", hospitalBarrier.y2);
            getConfig().set("hb." + i + ".z2", hospitalBarrier.z2);
        }
        saveConfig();
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
        getCommand("s").setExecutor(new Messages());
        getCommand("w").setExecutor(new Messages());
        getCommand("i").setExecutor(new Messages());
        getCommand("do").setExecutor(new Messages());
        getCommand("hospbarrier").setExecutor(new HospitalBarriers());
        getCommand("med").setExecutor(new Regeneration());
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
