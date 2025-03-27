package fr.naylek.survivalplus.objects;

public class PlayerStatus {
    private double playerWaterReserve = 20D;
    private double playerConsumeWaterCoef = 0;

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
}
