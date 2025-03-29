package fr.naylek.survivalplus.playerevents;

import fr.naylek.survivalplus.SurvivalPlus;
import fr.naylek.survivalplus.managers.PlayerStatusManager;
import fr.naylek.survivalplus.objects.PlayerStatus;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Map;
import java.util.UUID;

public class ExhaustSystem implements Listener {
    final SurvivalPlus instance = SurvivalPlus.getInstance();
    private final PlayerStatusManager playerStatusManager = instance.getPlayerStatusManager();

    public ExhaustSystem(){
        Plugin instance = SurvivalPlus.getInstance();
        instance.getServer().getScheduler().runTaskTimer(instance, () -> {
            for (Map.Entry<UUID, PlayerStatus> playerStatusEntry : playerStatusManager.getPlayerStatusMap().entrySet()) {
                Player player = Bukkit.getPlayer(playerStatusEntry.getKey());
                if (player != null){
                    PlayerStatus status = playerStatusManager.getPlayerStatus(player);
                    // Affiche la bar d'energie du joueur
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                            TextComponent.fromLegacy("Energy: " + getPercentString(player)));

                            // A verifier
                            TextComponent.fromLegacy("Energy: " + status.getPercentString(playerStatusManager, player, ChatColor.YELLOW));


                    // Effets si son énergie est à 0
                    if (playerStatusEntry.getValue().getPlayerWaterReserve() == 0){
                        player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 120,0));
                        player.playSound(player.getLocation(), Sound.AMBIENT_CAVE, 1.0f, 1.0f);
                    }
                }
            }
        }, 0, 1);
    }

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
        if (playerStatusManager.getPlayerStatus(pie.getPlayer()).getPlayerEnergy() > 5){
            pie.getPlayer().sendMessage("You are not tired");
            pie.getPlayer().playSound(pie.getPlayer().getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1.0f, 1.0f);
            return;
        }

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
        double playerEnergy = playerStatus.getPlayerEnergy();
        if (playerEnergy - consumption <= 0){
            playerStatus.setPlayerEnergy(0);
        }else {
            playerStatus.setPlayerEnergy(playerEnergy - consumption);
        }
    }

    /**
     * Calcul the percentage of a current player's energy reserve
     * @param energyReserve The player's energy reserve
     * @return The percentage of the player's energy reserve
     */
    private double getPercent(double energyReserve){
        return Math.round(((100 * energyReserve) / 20));
    }

    /**
     * Permit to draw the energy level of the player
     * @param player The player to obtain its reserve
     * @return The level of energy as string to print
     */
    private String getPercentString(Player player){
        String energyBar = ChatColor.YELLOW + "█";
        return energyBar.repeat(((int) getPercent(playerStatusManager.getPlayerStatus(player).getPlayerEnergy())) / 10);
    }
}
