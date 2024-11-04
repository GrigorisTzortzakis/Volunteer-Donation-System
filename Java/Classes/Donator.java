public class Donator extends User {
    private Offers offersList;

    public Donator(String name, String phone) {
        super(name, phone);
        this.offersList = new Offers(Organization.getInstance());
    }

    // In Donator.java
    public void add(RequestDonation donation) {
        try {
            offersList.add(donation, Organization.getInstance());
            System.out.println("Successfully added donation: " + donation.getEntity().getName() +
                    " - Quantity: " + donation.getQuantity()); // Add this debug line
        } catch (Exception e) {
            System.out.println("Error adding donation: " + e.getMessage());
        }
    }

    public void remove(RequestDonation donation) {
        offersList.remove(donation);
    }

    public void commit() {
        try {
            offersList.commit();
        } catch (Exception e) {
            System.out.println("Error committing offers: " + e.getMessage());
        }
    }

    public Offers getOffersList() {
        return offersList;
    }
}