package reit;

import java.util.Iterator;
import java.util.concurrent.*;

/**
 * Created by airbag on 12/9/14.
 */
class RunnableCustomerGroupManager implements Runnable {

    // field

    private CustomerGroupDetails customerGroupDetails;
    private Management management;


    public RunnableCustomerGroupManager(Management management,
                                        CustomerGroupDetails customerGroupDetails) {
        this.management = management;
        this.customerGroupDetails = customerGroupDetails;

    }


    @Override
    public void run() {
        while (customerGroupDetails.isRequestsLeft()) { // let's iterate over these rental requests..

            RentalRequest currentRequest = customerGroupDetails.pullRentalRequest();
            management.addRentalRequest(currentRequest); // send rentalRequest to reit.Management

            synchronized (currentRequest) {
                try {
                    while (!currentRequest.isFulfilled()) {
                        currentRequest.wait();
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            try {
                simulateStay(currentRequest);
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        Management.logger.info(customerGroupDetails.getName() + " has no rental requests left. TERMINATING.");
    }

    private void simulateStay(RentalRequest currentRequest) throws ExecutionException, InterruptedException {
        currentRequest.inProgress();
        double totalDamage = 0;
        StringBuilder damageDetails = new StringBuilder(customerGroupDetails.getName().toString());

        // we'll sum up the damage values returned by each simulated customer
        Executor executor = Executors.newCachedThreadPool();
        CompletionService<Double> completionService = new ExecutorCompletionService<Double>(executor);
        Management.logger.info(customerGroupDetails.getName() + " is simulating a " + currentRequest.getDurationOfStay() + " day stay.");
        Iterator<Customer> customerIterator = customerGroupDetails.customerIterator();
        while (customerIterator.hasNext()) { // launch each customer as a reit.CallableSimulateStayInAsset
            completionService.submit(new CallableSimulateStayInAsset(currentRequest, customerIterator.next()));
        }

        for (int i = 0; i < customerGroupDetails.numOfCustomers(); i++) { // sum up damages
            double currentDamage = completionService.take().get();
            totalDamage += currentDamage;
            damageDetails.append("[Damage=").append(currentDamage).append("]");
        }


        // vacate asset
        currentRequest.complete();
        // submit reit.DamageReport to management
        Management.logger.info(damageDetails.append("====> [Total Damage=").append(Math.round(totalDamage)).append("]").toString());
        management.submitDamageReport(currentRequest.createDamageReport(totalDamage));
        Management.logger.info(customerGroupDetails.getName() + " has submitted a damage report to management.");

    }
}