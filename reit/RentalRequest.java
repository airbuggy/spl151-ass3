package reit;

/**
 * Created by airbag on 12/9/14.
 */
class RentalRequest {

    // fields
    private enum RequestStatus {INCOMPLETE, FULFILLED, INPROGRESS, COMPLETE}
    private RequestStatus requestStatus;
    private String id;
    private String assetType;
    private int assetSize;
    private int durationOfStay;
    private Asset asset;

    RentalRequest(String id, String assetType, int assetSize, int durationOfStay) {
        this.id = id;
        this.assetType = assetType;
        this.assetSize = assetSize;
        this.durationOfStay = durationOfStay;
        requestStatus = RequestStatus.INCOMPLETE;
    }

    boolean isFulfilled() {
        return (requestStatus == RequestStatus.FULFILLED);
    }

    void fulfill(Asset asset) {
        this.asset = asset;
        requestStatus = RequestStatus.FULFILLED;
        Management.logger.info(getId() + " status changed to FULFILLED.");
    }

    boolean isSuitable(Asset asset) {
        return asset.isSuitable(assetType, assetSize);
    }

    protected void inProgress() {
        requestStatus = RequestStatus.INPROGRESS;
        asset.occupy();
        Management.logger.info(getId() + " status changed to IN PROGRESS.");
    }

    void complete() {
        asset.vacate();
        requestStatus = RequestStatus.COMPLETE;
        Management.logger.info(getId() + " status changed to COMPLETE.");
    }

    void incomplete() {
        requestStatus = RequestStatus.INCOMPLETE;
    }

    int stay() {
        return durationOfStay * Management.DAYS_TO_MILLISECONDS;
    }

    void updateDamage(double damagePercentage) {
        asset.updateDamage(damagePercentage);
    }

    DamageReport createDamageReport(double totalDamage) {
        return new DamageReport(asset, totalDamage);
    }

    int getDurationOfStay () {
        return durationOfStay;
    }

    String getId() {
        return "[Rental Request " + id + "]";

    }
    public String toString() {
        return "[ID=" + id +
                "][Type=" + assetType +
                "][Size=" + assetSize +
                "][Duration=" + durationOfStay +
                "][Status=" + requestStatus.toString() +
                "]";
    }
}
