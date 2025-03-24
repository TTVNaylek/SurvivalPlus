package fr.naylek.survivalplus;

import fr.naylek.survivalplus.playerevents.DrinkSystem;
import fr.naylek.survivalplus.playerevents.TemperatureSystem;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;


public final class SurvivalPlus extends JavaPlugin {
    private static SurvivalPlus instance;

    @Override
    public void onEnable() {
        // Définit le plugin
        instance = this;
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
        pm.registerEvents(new DrinkSystem(this), this);
    }

    public static SurvivalPlus getInstance() {
        return instance;
    }
}
