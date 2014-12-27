package reit;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Vector;

/**
 * Created by airbag on 12/9/14.
 */
public class CustomerGroupDetails {

    private String groupManagerName;
    private Queue<RentalRequest> rentalRequests;
    private Vector<Customer> customers;


    protected CustomerGroupDetails(String groupManagerName) {
        this.groupManagerName = groupManagerName;
        this.customers = new Vector<Customer>();
        this.rentalRequests = new LinkedList<RentalRequest>();
    }

    public void addCustomer(Customer customer) {
        customers.add(customer);

    }

    public void addRentalRequest(RentalRequest rentalRequest) {
        rentalRequests.add(rentalRequest);

    }

    public RentalRequest pullRentalRequest() {
        if (!rentalRequests.isEmpty())
            return rentalRequests.remove();
        return null;
    }

    public boolean isRequestsLeft() {
        return !rentalRequests.isEmpty();
    }

    public Iterator<Customer> customerIterator() {
        return customers.iterator();
    }

    public int numOfCustomers() {
        return customers.size();

    }

    public String getName() {
        return "[" + groupManagerName + "]";

    }

    public String toString() {
        return "[" + groupManagerName + "]";

    }

}