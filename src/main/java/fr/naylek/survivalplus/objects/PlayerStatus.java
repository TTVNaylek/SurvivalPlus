package fr.naylek.survivalplus.objects;

import fr.naylek.survivalplus.managers.PlayerStatusManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class PlayerStatus {
    private double playerWaterReserve = 20D;
    private double playerConsumeWaterCoef = 0D;
    private double playerEnergy = 20D;
    private double playerExhaustCoef = 0D;

    public double getPlayerWaterReserve() {
        return playerWaterReserve;
    }

    public void setPlayerWaterReserve(double playerWaterReserve) {
        this.playerWaterReserve = playerWaterReserve;
    }

    public double getPlayerConsumeWaterCoef() {
        return playerConsumeWaterCoef;
    }

    public void setPlayerConsumeWaterCoef(double playerConsumeWaterCoef) {
        this.playerConsumeWaterCoef = playerConsumeWaterCoef;
    }

    public void incrementConsumeWaterCoef(double playerConsumeWaterCoef) {
        this.playerConsumeWaterCoef += playerConsumeWaterCoef;
    }

    public double getPlayerExhaustCoef() {
        return playerExhaustCoef;
    }

    public double getPlayerEnergy() {
        return playerEnergy;
    }

    public void setPlayerEnergy(double playerEnergy) {
        this.playerEnergy = playerEnergy;
    }

    public void setPlayerExhaustCoef(double playerExhaustCoef) {
        this.playerExhaustCoef = playerExhaustCoef;
    }

    public void incrementExhaustCoef(double playerExhaustCoef){
        this.playerExhaustCoef += playerExhaustCoef;
    }

    /**
     * Calcul the percentage of a current player's energy reserve
     * @param reserve The player's energy reserve
     * @return The percentage of the player's energy reserve
     */
    private double getPercent(double reserve){
        return Math.round(((100 * reserve) / 20));
    }

    /**
     * Permit to draw the energy level of the player
     * @param player The player to obtain its reserve
     * @return The level of energy as string to print
     */
    public String getPercentString(PlayerStatusManager playerStatusManager, Player player, ChatColor color){
        String energyBar = color + "█";
        return energyBar.repeat(((int) getPercent(playerStatusManager.getPlayerStatus(player).getPlayerEnergy())) / 10);
    }

}
