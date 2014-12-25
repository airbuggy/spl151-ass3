import java.util.concurrent.Semaphore;

/**
 * Created by gal on 12/8/2014.
 */
class RepairMaterial implements Comparable<RepairMaterial> {
    private String materialName;
    private Semaphore quantity;


    public RepairMaterial(String materialName, int quantity) {
        this.materialName = materialName;
        this.quantity = new Semaphore(quantity);

    }

    public void acquireMaterial (int quantity) {
        try {
            this.quantity.acquire(quantity);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    public String materialName() {
        return materialName;
    }



    @Override
    public int compareTo(RepairMaterial o) {
        return materialName.compareTo(o.materialName);
    }
}
