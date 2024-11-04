// Beneficiary.java
public class Beneficiary extends User {
    private int noPersons;
    private RequestDonationList receivedList;
    private Requests requestsList;

    public Beneficiary(String name, String phone) {
        super(name, phone);
        this.noPersons = 1;
        this.receivedList = new RequestDonationList();
        this.requestsList = new Requests(this, Organization.getInstance());
    }

    public Beneficiary(String name, String phone, int noPersons) {
        super(name, phone);
        this.noPersons = noPersons;
        this.receivedList = new RequestDonationList();
        this.requestsList = new Requests(this, Organization.getInstance());
    }

    public void add(RequestDonation request) {
        try {
            requestsList.add(request, Organization.getInstance());
            System.out.println("Request added successfully!");
        } catch (Exceptions.EntityNotFoundException e) {
            System.out.println("Error: " + e.getMessage());
            return;
        }
    }

    public void remove(RequestDonation request) {
        requestsList.remove(request);
    }

    public void commit() {
        try {
            requestsList.commit();
        } catch (Exception e) {
            System.out.println("Error committing requests: " + e.getMessage());
        }
    }

    public int getNoPersons() {
        return noPersons;
    }

    public void setNoPersons(int noPersons) {
        this.noPersons = noPersons;
    }

    public RequestDonationList getReceivedList() {
        return receivedList;
    }

    public Requests getRequestsList() {
        return requestsList;
    }
}