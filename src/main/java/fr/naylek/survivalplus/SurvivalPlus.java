package fr.naylek.survivalplus;

import fr.naylek.survivalplus.playerevents.TemperatureSystem;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;


public final class SurvivalPlus extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        getLogger().info("Plugin is enabled.");
        registerEvents();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().info("Plugin is disabled.");
    }

    public void registerEvents() {
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new TemperatureSystem(this), this);
    }
}
