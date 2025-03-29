package fr.naylek.survivalplus.managers;

import fr.naylek.survivalplus.objects.PlayerStatus;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerStatusManager implements Listener {
    private final Map<UUID, PlayerStatus> playerStatus = new HashMap<>();

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
