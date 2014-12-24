import java.util.concurrent.BlockingQueue;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * Created by airbag on 12/9/14.
 */
public class RunnableClerk implements Runnable{

    private final int SEC_TO_MILL = 1000;
    private double shiftLength;
    private ClerkDetails clerkDetails;
    private BlockingQueue<RentalRequest> rentalRequests;
    private RentalRequest currentRequest;
    private Assets assets;
    private CyclicBarrier barrier;
    private AtomicInteger nUnhandledRequests;
    private Semaphore reportSemaphore;
    private Object beginNewShift;

    public RunnableClerk(ClerkDetails clerkDetails,
                         BlockingQueue<RentalRequest> rentalRequests,
                         CyclicBarrier barrier,
                         Assets assets,
                         AtomicInteger nUnhandledRequests,
                         Semaphore reportSemaphore,
                         Object beginNewShift) {
        this.clerkDetails = clerkDetails;
        this.rentalRequests = rentalRequests;
        this.barrier = barrier;
        this.assets = assets;
        this.currentRequest = null;
        this.nUnhandledRequests = nUnhandledRequests;
        this.reportSemaphore = reportSemaphore;
        this.beginNewShift = beginNewShift;
    }


    @Override
    public void run() {
        while (nUnhandledRequests.get() > 0) {
            while (shiftLength < 8) { // 8 hour shifts..
                try {
                    currentRequest = rentalRequests.take(); // pull rental request from queue

                    synchronized (currentRequest) {
                        Asset asset = assets.find(currentRequest);
                        asset.book();
                        travelToAsset(asset); // wasting time
                        currentRequest.fulfill(asset);
                        reportSemaphore.release(1);
                        nUnhandledRequests.getAndDecrement(); // decrement unhandled requests count by one, safely..
                        notify(); // asset is booked, request fulfilled. notify customers.
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            try {
                barrier.await(); // done for today. waiting for everyone else to finish so that I, too, could go home..

                synchronized (beginNewShift) { // praying next shift won't be as bad as the last.
                    beginNewShift.wait(); //
                }
                beginShift(); // crap.

            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (BrokenBarrierException e) {
                e.printStackTrace();
            }
        }
    }

    private void travelToAsset(Asset asset) throws InterruptedException {
        long timeToSleep = clerkDetails.distanceToTravel(asset);
        shiftLength += timeToSleep;
        Thread.sleep(timeToSleep * SEC_TO_MILL); // sleep at the wheel
    }

    private void beginShift() { // i hate mondays
        shiftLength = 0;
    }
}