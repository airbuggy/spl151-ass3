import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.ConsoleHandler;
import java.util.logging.Logger;


class Assets {

    private ArrayList<Asset> assets;
    private BlockingQueue<DamageReport> damageReports;
    private Object assetLock;

    public Assets() {
        this.assets = new ArrayList<Asset>();
        assetLock = new Object();
        this.damageReports = new LinkedBlockingQueue<DamageReport>();
    }

    public void addAsset(Asset asset) {
        assets.add(asset);
    }

    public Asset find(RentalRequest rentalRequest) {
        boolean assetFound = false;
        Asset suitableAsset = null;
        synchronized (assetLock) {
            while (!assetFound) { // while no suitable asset has been found
                for (Asset asset : assets) { // look for assets
                    if (rentalRequest.isSuitable(asset)) { // if suitable asset has been found
                        assetFound = true;
                        Management.logger.info("Asset Found!");
                        return asset; // TODO: UGLY HACK.
                    }
                }
                try { // no available assets have been found
                    Management.logger.info("NO SUITABLE ASSET CURRENTLY AVAILABLE. PLEASE WAIT.");
                    assetLock.wait(); // so we wait.
                    Management.logger.info("NOTIFY ALL CLERKS! AN ASSET HAS BEEN VACATED!!");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        return suitableAsset;
    }

    public void submitDamageReport(DamageReport damageReport) { // adds another damageReport to
        damageReports.add(damageReport);
        synchronized (assetLock) {
            try {
                assetLock.notifyAll(); // let everyone know a new asset is now available
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public ArrayList<Asset> getDamagedAssets() {
        ArrayList<Asset> damagedProperty = new ArrayList<Asset>();

        for (Asset asset : assets) { // TODO: figure out where the hell we should use damageReport..
            if (asset.isWrecked())
                damagedProperty.add(asset);
        }

        return damagedProperty;
    }

    public void applyDamage() { // iterate over damage reports, inflict damage
        for (DamageReport damageReport : damageReports) {
            damageReport.inflictDamage();
        }

        damageReports.clear();
    }
}