package fr.naylek.survivalplus.managers;

import fr.naylek.survivalplus.SurvivalPlus;
import fr.naylek.survivalplus.objects.PlayerStatus;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerStatusManager implements Listener {
    private final Map<UUID, PlayerStatus> playerStatus = new HashMap<>();
    private boolean showStats = true;

    /**
     * When the class is instanced the server will create an action bar for the energy and the water reserve of the player
     * and refresh it every second
     */
    public PlayerStatusManager(){
        Plugin instance = SurvivalPlus.getInstance();
        instance.getServer().getScheduler().runTaskTimer(instance, () -> {
            for (Map.Entry<UUID, PlayerStatus> playerStatusEntry : this.getPlayerStatusMap().entrySet()) {
                Player player = Bukkit.getPlayer(playerStatusEntry.getKey());
                if (player != null){
                    PlayerStatus status = this.getPlayerStatus(player);
                    // Affiche la bar d'energie ou d'eau du joueur
                    if (showStats){
                        player.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                                TextComponent.fromLegacy("Water: " + status.getWaterPercentString(this,
                                        player, ChatColor.GREEN)));
                        showStats = false;
                    }else {
                        player.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                                TextComponent.fromLegacy("Energy: " + status.getEnergyPercentString(this,
                                        player, ChatColor.YELLOW)));
                        showStats = true;
                    }

                    // Effets si son énergie est à 0
                    if (playerStatusEntry.getValue().getPlayerEnergyReserve() == 0){
                        player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 120,0));
                        player.playSound(player.getLocation(), Sound.AMBIENT_CAVE, 1.0f, 1.0f);
                    }

                    if (playerStatusEntry.getValue().getPlayerWaterReserve() == 0){
                        player.addPotionEffect(new PotionEffect(PotionEffectType.NAUSEA, 120,0));
                    }
                }
            }
        }, 0, 20);
    }

    /**
     * When the player join the server a player status is created with his UUID
     * @param pje The player from the PlayerJoinEvent event
     */
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent pje){
        if (!playerStatus.containsKey(pje.getPlayer().getUniqueId())){
            playerStatus.put(pje.getPlayer().getUniqueId(), new PlayerStatus());
        }
    }

    /**
     * When the player quit the server the player status is saved
     * @param pje The player from the PlayerJoinEvent event
     */
    @EventHandler
    public void onPlayerQuit(PlayerJoinEvent pje){
        // Enregistrer le status du joueur
    }

    public PlayerStatus getPlayerStatus(Player player){
        return playerStatus.get(player.getUniqueId());
    }

    public Map<UUID, PlayerStatus> getPlayerStatusMap(){
        return playerStatus;
    }

}
