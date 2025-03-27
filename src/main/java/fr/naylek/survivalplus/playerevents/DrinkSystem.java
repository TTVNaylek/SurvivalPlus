package fr.naylek.survivalplus.playerevents;


import fr.naylek.survivalplus.SurvivalPlus;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionType;

import java.util.HashMap;
import java.util.Map;


public class DrinkSystem implements Listener {
    private Map<Player, Double> playerWaterReserve = new HashMap<>();
    private double playerReserve;
    private final Plugin instance = SurvivalPlus.getInstance();

    public DrinkSystem(){
        instance.getServer().getScheduler().runTaskTimer(instance, () -> {
            for (Map.Entry<Player, Double> playerDoubleEntry : playerWaterReserve.entrySet()) {
                Player player = playerDoubleEntry.getKey();
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                        TextComponent.fromLegacy("Water: " + getPercentString(player)));
            }
        }, 0, 20);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent pje){

        instance.getLogger().info(String.valueOf(playerWaterReserve.containsKey(pje.getPlayer())));

        if (!playerWaterReserve.containsKey(pje.getPlayer())){
            playerWaterReserve.put(pje.getPlayer(), 20D);
        }
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
                    player.sendMessage(ChatColor.GREEN + "Current water reserve " + playerReserve);

                    if (playerReserve + 2 >= 20){
                        player.sendMessage(ChatColor.GREEN + "SUP A 20");
                        playerWaterReserve.put(playerMap, Math.max(20, 20D));
                        player.sendMessage(ChatColor.GREEN + "Refreshing (new water reserve) " + playerDoubleEntry.getValue());
                        break;
                    }

                    playerWaterReserve.put(playerMap, Math.max(20, playerReserve + 2));
                    player.sendMessage(ChatColor.GREEN + "Refreshing (new water reserve) " + playerDoubleEntry.getValue());
                }
            }
        }
    }

    public void consumeWater(double consumption){
        instance.getLogger().info("BEFORE LOOP");
        instance.getLogger().info(String.valueOf(playerWaterReserve.size()));

        // Retirer l'eau du joueur
        for (Map.Entry<Player, Double> playerDoubleEntry : playerWaterReserve.entrySet()) {
            Player player = playerDoubleEntry.getKey();
            playerReserve = playerDoubleEntry.getValue();

            player.sendMessage(ChatColor.RED + "Current water reserve " + playerReserve);

            // Soustraire X à la valeur initiale
            playerWaterReserve.put(player, Math.max(0, playerReserve - consumption));

            player.sendMessage(ChatColor.GREEN + "New water reserve ");
        }

        instance.getLogger().info("AFTER LOOP");
    }

    private double getPercent(double waterReserve){

        instance.getLogger().info(String.valueOf(Math.round(((100 * waterReserve) / 20))));

        return Math.round(((100 * waterReserve) / 20));
    }

    private String getPercentString(Player p){
        String waterBar = ChatColor.GREEN + "█";

        return waterBar.repeat(((int) getPercent(playerWaterReserve.get(p))) / 10);
    }
}
