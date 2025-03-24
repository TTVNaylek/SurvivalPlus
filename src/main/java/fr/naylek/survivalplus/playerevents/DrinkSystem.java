package fr.naylek.survivalplus.playerevents;


import fr.naylek.survivalplus.SurvivalPlus;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionType;

import java.util.HashMap;
import java.util.Map;


public class DrinkSystem implements Listener {
    //public double waterReserve = 20D;
    private Map<Player, Double> playerWaterReserve = new HashMap<>();
    private double playerReserve;

    public DrinkSystem(Plugin pg) {
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent pje){
        playerWaterReserve.putIfAbsent(pje.getPlayer(), 20D);
    }

    @EventHandler
    public void supplyWater(PlayerItemConsumeEvent pice){
        Player player = pice.getPlayer();
        ItemStack is = pice.getItem();

        // Vérifie si c'est bien une potion
        if (is.getType().equals(Material.POTION)){
            // Propriété de la potion
            PotionMeta pMeta = (PotionMeta) is.getItemMeta();
            if (pMeta != null && pMeta.getBasePotionType() == PotionType.WATER){
                // Ajouter eau au joueur
                for (Map.Entry<Player, Double> playerDoubleEntry : playerWaterReserve.entrySet()) {
                    Player playerMap = playerDoubleEntry.getKey().getPlayer();
                    playerReserve = playerDoubleEntry.getValue();
                    player.sendMessage("Current water reserve" + playerReserve);

                    if (playerReserve + 2 >= 20){
                        player.sendMessage("SUP A 20");
                        playerWaterReserve.put(playerMap, Math.max(20, 20D));
                        player.sendMessage("Refreshing (new water reserve) " + playerDoubleEntry.getValue());
                        break;
                    }

                    playerWaterReserve.put(playerMap, Math.max(20, playerReserve + 2));
                    player.sendMessage("Refreshing (new water reserve) " + playerDoubleEntry.getValue());
                }
            }
        }
    }

    public void consumeWater(double consumption){
        Plugin instance = SurvivalPlus.getInstance();

        instance.getLogger().info("BEFORE LOOP");

        // BUG: NE RENTRE PAS DANS LA BOUCLE
        // Retirer l'eau du joueur
        for (Map.Entry<Player, Double> playerDoubleEntry : playerWaterReserve.entrySet()) {
            Player player = playerDoubleEntry.getKey();
            playerReserve = playerDoubleEntry.getValue();

            instance.getLogger().info("Current water reserve" + playerReserve);

            player.sendMessage("Current water reserve" + playerReserve);

            // Soustraire X à la valeur initiale
            playerWaterReserve.put(player, Math.max(0, playerReserve - consumption));

            instance.getLogger().info("New water reserve" + playerReserve);
            player.sendMessage("New water reserve" + playerReserve);
        }

        instance.getLogger().info("AFTER LOOP");
    }
}
