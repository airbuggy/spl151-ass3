package reit;

/**
 * AssetContent:
 * This class represents a single asset content object.
 * @version 1.0
 */
class AssetContent {
    private final int DAMAGE_THRESHOLD = 65;
    private final String name;
    private double health;
    private final double repairCostMultiplier;

    AssetContent(String contentName, double repairCostMultiplier) {
        name = contentName;
        health = 100;
        this.repairCostMultiplier = repairCostMultiplier;
    }

    void breakAsset(double percentage) {
        health -= percentage;
    }

    void fix() {
        health = 100;
    }

    double timeToFix() {
        return (100 - health) * repairCostMultiplier;
    }
    boolean isBroken() {
        return health <= DAMAGE_THRESHOLD;
    }

    String getName() {
        return name;
    }
    public String toString() {
        return "[Content=" + name + "][Health=" + health + "][RepairMultiplier=" + repairCostMultiplier + "]";
    }
}