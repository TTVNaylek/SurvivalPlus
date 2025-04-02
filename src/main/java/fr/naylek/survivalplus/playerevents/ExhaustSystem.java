package fr.naylek.survivalplus.playerevents;

import fr.naylek.survivalplus.SurvivalPlus;
import fr.naylek.survivalplus.managers.PlayerStatusManager;
import fr.naylek.survivalplus.objects.PlayerStatus;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class ExhaustSystem implements Listener {
    final SurvivalPlus instance = SurvivalPlus.getInstance();
    private final PlayerStatusManager playerStatusManager = instance.getPlayerStatusManager();

    /**
     * When the player interact with bed he can sleep if he's tired
     * @param pie The player from the PlayerInteractEvent event
     */
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent pie){
        Block block = pie.getClickedBlock();
        if (block == null || !block.getType().toString().endsWith("_BED")){
            return;
        }
        if (playerStatusManager.getPlayerStatus(pie.getPlayer()).getPlayerEnergyReserve() > 5){
            pie.getPlayer().sendMessage("You are not tired");
            pie.getPlayer().playSound(pie.getPlayer().getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1.0f, 1.0f);
            return;
        }
        // Faire en sorte qu'il reste dans le lit
        pie.getPlayer().sleep(pie.getClickedBlock().getLocation(), true);
    }

    /**
     * While the player is Sprinting/Swimming his exhaust coef increase, at certain value exhaust coef make player loose energy
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
     * When the player break a block his exhaust coef increase, at certain value exhaust coef make player loose energy
     * @param bbe The player from the BlockBreakEvent event
     */
    @EventHandler
    public void onPlayerBreakBlock(BlockBreakEvent bbe){
        exhaustProcess(bbe.getPlayer(), 0.25, 0.005);
    }

    /**
     * Process to consume player energy
     * @param player The player affected by the energy consumption
     * @param consumption The consumption level 0-20
     * @param coef The exhaust level to make player loose energy
     */
    private void exhaustProcess(Player player, double consumption, double coef){
        PlayerStatus status = playerStatusManager.getPlayerStatus(player);
        status.incrementExhaustCoef(coef);
        // Si le coef est supérieur à 4 (Comme la nourriture du jeu) alors consommer l'eau
        if (status.getPlayerExhaustCoef() > 4){
            consumeEnergy(player, consumption);
            status.setPlayerExhaustCoef(0);
        }
    }

    /**
     * Remove X (consumption) energy level from the player
     * @param player The player affected by the energy consumption
     * @param consumption The consumption level 0-20
     */
    public void consumeEnergy(Player player, double consumption){
        PlayerStatus playerStatus = playerStatusManager.getPlayerStatus(player);
        // Soustraire X à la valeur initiale
        double playerEnergy = playerStatus.getPlayerEnergyReserve();
        if (playerEnergy - consumption <= 0){
            playerStatus.setPlayerEnergyReserve(0);
        }else {
            playerStatus.setPlayerEnergyReserve(playerEnergy - consumption);
        }
    }
}
