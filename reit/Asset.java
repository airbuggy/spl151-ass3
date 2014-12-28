package reit;

import java.util.ArrayList;
import java.util.ListIterator;

/**
 * Created by airbag on 12/9/14.
 */
public class Asset {

    private String name;
    private String type;
    private Location location;
    private ArrayList<AssetContent> assetContentContainer;
    private enum Status {AVAILABLE, BOOKED, OCCUPIED, UNAVAILABLE}
    private Status status;
    private int costPerNight;
    private int size;

    Asset(String name, String type, Location location,
                 int costPerNight, int size) {
        this.name = name;
        this.type = type;
        this.location = location;
        assetContentContainer = new ArrayList<AssetContent>();
        status = Status.AVAILABLE;
        this.costPerNight = costPerNight;
        this.size = size;
    }

    public void addContent(String name, double repairMultiplier) {
        assetContentContainer.add(new AssetContent(name, repairMultiplier));
    }

    boolean isWrecked() {
        return (status == Status.UNAVAILABLE);
    }

    synchronized void updateDamage(double percentage) {

        for (AssetContent assetContent : assetContentContainer) {
            assetContent.breakAsset(percentage);
        }
        checkHealth();
    }

    synchronized void repairAsset() {
        for (AssetContent assetContent : assetContentContainer) {
            assetContent.fix();
        }
        status = Status.AVAILABLE; // making asset available
        Management.logger.info(getName() + " has been repaired.");
    }

    long timeToFix() {
        double timeToFix = 0;

        for (AssetContent assetContent : assetContentContainer) {
            timeToFix += assetContent.timeToFix();
        }

        return Math.round(timeToFix);
    }

    private void checkHealth() {
        boolean stillAvailable = true;
        ListIterator<AssetContent> it = assetContentContainer.listIterator();

        while (it.hasNext() && stillAvailable) {
            if (it.next().isBroken()) {
                status = Status.UNAVAILABLE;
                stillAvailable = false;
            }
        }
    }

    String whatsDamaged() {
        String damagedGoods = "";

        for (AssetContent assetContent : assetContentContainer) {
            damagedGoods += assetContent.getName() + ",";
        }
        if (damagedGoods.length() > 0) {
            damagedGoods = damagedGoods.substring(0, damagedGoods.length() - 1); // remove pesky trailing comma
        }
        return damagedGoods;
    }

    boolean isSuitable(String type, int size) {
        return (this.type.equals(type) &&
                this.size >= size &&
                this.status.equals(Status.AVAILABLE));
    }

    long distanceToClerk(Location location) {
        return (Math.round(this.location.calculateDistance(location)));
    }

    synchronized void book() {
        status = Status.BOOKED;
    }

    synchronized void occupy() {
        status = Status.OCCUPIED;
    }

    synchronized void vacate() {
        status = Status.AVAILABLE;
        Management.logger.info(getName() + " has been vacated.");
    }

    String listContent() {
        StringBuilder contentList = new StringBuilder();

        for (AssetContent content : assetContentContainer) {
            contentList.append(content).append("\n");
        }

        return contentList.toString();
    }

    String getName() {
        return "[" + name + "]";
    }

    public String toString() {
        return "[Name=" + name + "][Type=" + type + "][Size=" + size + "][Status=" + status.toString() + "]";
    }
}