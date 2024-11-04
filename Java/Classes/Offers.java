public class Offers extends RequestDonationList {
    private final Organization organization;

    public Offers(Organization organization) {
        super();
        this.organization = organization;
    }

    @Override
    public void add(RequestDonation rd, Organization organization) throws Exceptions.EntityNotFoundException {
        super.add(rd, organization);
    }

    // In Offers.java
    public void commit() throws Exceptions.EntityNotFoundException {
        System.out.println("Starting commit process..."); // Add debug line
        for (RequestDonation offer : getRdEntities()) {
            RequestDonation currentDonation = organization.getCurrentDonation(offer.getEntity());
            System.out.println("Processing offer: " + offer.getEntity().getName() +
                    " - Quantity: " + offer.getQuantity()); // Add debug line

            if (currentDonation != null) {
                double newQuantity = currentDonation.getQuantity() + offer.getQuantity();
                currentDonation.setQuantity(newQuantity);
                System.out.println("Updated quantity: " + newQuantity); // Add debug line
            } else {
                organization.addCurrentDonation(new RequestDonation(offer.getEntity(), offer.getQuantity()));
                System.out.println("Added new donation"); // Add debug line
            }
        }
        reset();
        System.out.println("Commit completed"); // Add debug line
    }
}