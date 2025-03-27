package fr.naylek.survivalplus.playerevents;

import fr.naylek.survivalplus.SurvivalPlus;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import java.util.*;


public class TemperatureSystem implements Listener {
    private Map<Player, Biome> lastBiome = new HashMap<>();
    private static final Biome[] HOT = {
            Biome.DESERT,
            Biome.NETHER_WASTES,
            Biome.BADLANDS,
            Biome.ERODED_BADLANDS,
            Biome.WOODED_BADLANDS,
            Biome.SOUL_SAND_VALLEY,
            Biome.CRIMSON_FOREST,
            Biome.WARPED_FOREST,
            Biome.BASALT_DELTAS
    };
    private static final Biome[] COLD = {
            Biome.FROZEN_OCEAN,
            Biome.FROZEN_RIVER,
            Biome.SNOWY_PLAINS,
            Biome.SNOWY_BEACH,
            Biome.SNOWY_TAIGA,
            Biome.ICE_SPIKES,
            Biome.GROVE,
            Biome.SNOWY_SLOPES,
            Biome.FROZEN_PEAKS,
            Biome.JAGGED_PEAKS
    };
    final Plugin instance = SurvivalPlus.getInstance();
    //private DrinkSystem playerWater = new DrinkSystem();

    private final DrinkSystem playerWater;

    // Constructeur
    public TemperatureSystem(DrinkSystem drinkSystem) {

        this.playerWater = drinkSystem;

        // Tâche automatique qui va attribuer des effets au joueur
        instance.getServer().getScheduler().runTaskTimer(instance, () -> {
            // PlayerBiomeEntry va retourner le joueur + biome
            for (Map.Entry<Player, Biome> playerBiomeEntry : lastBiome.entrySet()) {
                Player player = playerBiomeEntry.getKey();
                // Récupère le biome actuel du joueur
                Biome currentBiome = player.getWorld().getBiome((int) player.getLocation().getX(),
                        (int) player.getLocation().getY(),
                        (int) player.getLocation().getZ());
                // Applique les effets au joueur
                if (Arrays.asList(HOT).contains(currentBiome) && !hasGoldArmor(player.getInventory())){
                    player.damage(0.25);

                    playerWater.consumeWater(0.25);

                    player.sendMessage(ChatColor.RED + "Water consumed :  0.25");
                } else if (Arrays.asList(COLD).contains(currentBiome) && !hasLeatherArmor(player.getInventory())) {
                    player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 120,0));
                    player.setFreezeTicks(80);
                }
            }
        }, 0L, 100L);
    }

    @EventHandler
    public void playerChangedBiome(PlayerMoveEvent event){
        Player player = event.getPlayer();

        // Dis au joueur dans quel biome il se trouve
        Biome currentBiome = player.getWorld().getBiome((int) player.getLocation().getX(),
                (int) player.getLocation().getY(),
                (int) player.getLocation().getZ());

        // Si le joueur n'est pas dans le meme biome
        if (lastBiome.get(player) != currentBiome){
            player.sendMessage("You are in " + currentBiome + " biome");
            lastBiome.put(player, currentBiome);

            // Si le biome actuel est chaud ou froid
            if (Arrays.asList(HOT).contains(currentBiome)){
                player.sendMessage("OUH LA LA IL FAIT CHAUD ICI");
            } else if (Arrays.asList(COLD).contains(currentBiome)) {
                player.sendMessage("OUH LA LA IL FAIT FROID ICI");
            }
        }
    }

    private boolean hasLeatherArmor(PlayerInventory inventory){
        // Retourne le nb de pièce de cuire
        return Arrays.stream(inventory.getArmorContents())
                .filter(Objects::nonNull)
                // Récupère le type des pièces d'armure (cuire, fer etc)
                .map(ItemStack::getType)
                // Garde que les pièces en cuire
                .filter(this::isLeather)
                // True si + de 1
                .count() > 1;
    }

    private boolean isLeather(Material material){
        return material.toString().startsWith("LEATHER_");
    }

    private boolean hasGoldArmor(PlayerInventory inventory){
        // Retourne le nb de pièce de cuire
        return Arrays.stream(inventory.getArmorContents())
                .filter(Objects::nonNull)
                // Récupère le type des pièces d'armure (cuire, fer etc)
                .map(ItemStack::getType)
                // Garde que les pièces en gold
                .filter(this::isGold)
                // True si + de 1
                .count() > 1;
    }

    private boolean isGold(Material material){
        return material.toString().startsWith("GOLDEN_");
    }
}
