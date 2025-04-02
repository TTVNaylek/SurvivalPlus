package fr.naylek.survivalplus.playerevents;

import fr.naylek.survivalplus.SurvivalPlus;
import fr.naylek.survivalplus.managers.PlayerStatusManager;
import fr.naylek.survivalplus.objects.PlayerStatus;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionType;

import java.util.Map;
import java.util.UUID;

public class DrinkSystem implements Listener {
    final SurvivalPlus instance = SurvivalPlus.getInstance();
    private final PlayerStatusManager playerStatusManager = instance.getPlayerStatusManager();

    /**
     * While the player is Sprinting/Swimming his water consume coef increase, at certain value water consume coef make player loose water
     * @param pme The player from the PlayerMoveEvent event
     */
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent pme){
        if (pme.getPlayer().isSprinting()) {
            exhaustProcess(pme.getPlayer(), 0.5, 0.1);
        }
        else if (pme.getPlayer().isSwimming()) {
            exhaustProcess(pme.getPlayer(), 0.25, 0.01);
        }
    }

    /**
     * When the player break a block his water consume coef  increase, at certain value water consume coef make player loose water
     * @param bbe The player from the BlockBreakEvent event
     */
    @EventHandler
    public void onPlayerBreakBlock(BlockBreakEvent bbe){
        exhaustProcess(bbe.getPlayer(), 0.25, 0.005);
    }

    /**
     * When the player drink a water potion his water reserve increase
     * @param pice The player from the PlayerItemConsumeEvent event
     */
    @EventHandler
    public void onPlayerDrinkWater(PlayerItemConsumeEvent pice){
        ItemStack is = pice.getItem();
        // Vérifie si c'est bien une potion
        if (is.getType().equals(Material.POTION)){
            // Propriété de la potion
            PotionMeta pMeta = (PotionMeta) is.getItemMeta();
            if (pMeta != null && pMeta.getBasePotionType() == PotionType.WATER){
                // Ajouter eau au joueur
                for (Map.Entry<UUID, PlayerStatus> playerDoubleEntry : playerStatusManager.getPlayerStatusMap().entrySet()) {
                    double playerReserve = playerDoubleEntry.getValue().getPlayerWaterReserve();
                    if (playerReserve + 2 >= 20){
                        playerDoubleEntry.getValue().setPlayerWaterReserve(20D);
                        break;
                    }
                    playerDoubleEntry.getValue().setPlayerWaterReserve(playerReserve + 2);
                }
            }
        }
    }

    /**
     * Process to consume player water
     * @param player The player affected by the water consumption
     * @param consumption The consumption level 0-20
     * @param coef The exhaust level to make player loose water
     */
    private void exhaustProcess(Player player, double consumption, double coef){
        PlayerStatus status = playerStatusManager.getPlayerStatus(player);
        status.incrementConsumeWaterCoef(coef);
        // Si le coef est supérieur à 4 (Comme la nourriture du jeu) alors consommer l'eau
            if (status.getPlayerConsumeWaterCoef() > 4){
                consumeWater(player, consumption);
                status.setPlayerConsumeWaterCoef(0);
            }
    }

    /**
     * Remove X (consumption) water level from the player
     * @param player The player affected by the water consumption
     * @param consumption The consumption level 0-20
     */
    public void consumeWater(Player player, double consumption){
        PlayerStatus status = playerStatusManager.getPlayerStatus(player);
        double playerReserve = status.getPlayerWaterReserve();

        // Soustraire X à la valeur initiale
        if (playerReserve - consumption <= 0){
            status.setPlayerWaterReserve(0D);
        }else {
            status.setPlayerWaterReserve(playerReserve - consumption);
        }
    }

}