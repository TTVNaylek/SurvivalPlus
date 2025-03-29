package fr.naylek.survivalplus;

import fr.naylek.survivalplus.managers.PlayerStatusManager;
import fr.naylek.survivalplus.playerevents.DrinkSystem;
import fr.naylek.survivalplus.playerevents.ExhaustSystem;
import fr.naylek.survivalplus.playerevents.TemperatureSystem;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class SurvivalPlus extends JavaPlugin {
    private static SurvivalPlus instance;
    private DrinkSystem drinkSystem;
    private TemperatureSystem temperatureSystem;
    private ExhaustSystem exhaustSystem;
    private PlayerStatusManager playerStatusManager;

    @Override
    public void onEnable() {
        instance = this;
        playerStatusManager = new PlayerStatusManager();
        drinkSystem = new DrinkSystem();
        temperatureSystem = new TemperatureSystem(drinkSystem);
        exhaustSystem = new ExhaustSystem();

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
        pm.registerEvents(temperatureSystem, this);
        pm.registerEvents(drinkSystem, this);
        pm.registerEvents(exhaustSystem, this);
        pm.registerEvents(playerStatusManager, this);
    }

    public static SurvivalPlus getInstance() {
        return instance;
    }

    public PlayerStatusManager getPlayerStatusManager() {
        return playerStatusManager;
    }
}
