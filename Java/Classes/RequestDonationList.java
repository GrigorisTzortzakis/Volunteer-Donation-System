// RequestDonationList.java
import java.util.ArrayList;

public class RequestDonationList {
    private final ArrayList<RequestDonation> rdEntities;

    public RequestDonationList() {
        this.rdEntities = new ArrayList<>();
    }

    public RequestDonation get(int entityId) {
        for (RequestDonation rd : rdEntities) {
            if (rd.getEntity().getId() == entityId) {
                return rd;
            }
        }
        return null;
    }

    public void add(RequestDonation requestDonation, Organization organization)
            throws Exceptions.EntityNotFoundException {
        // Check if entity exists in organization
        if (organization.getEntity(requestDonation.getEntity().getId()) == null) {
            throw new Exceptions.EntityNotFoundException("Entity not found in organization");
        }

        // Remove any existing entry for this entity before adding new one
        rdEntities.removeIf(rd -> rd.getEntity().getId() == requestDonation.getEntity().getId());

        // Only add if no exception was thrown
        rdEntities.add(requestDonation);
    }

    public void remove(RequestDonation requestDonation) {
        rdEntities.removeIf(rd -> rd.getEntity().getId() == requestDonation.getEntity().getId());
    }

    public void monitor() {
        if (rdEntities.isEmpty()) {
            System.out.println("No items in list");
            return;
        }

        for (RequestDonation rd : rdEntities) {
            System.out.println("Item: " + rd.getEntity().getName() +
                    ", Quantity: " + rd.getQuantity());
        }
    }

    public void reset() {
        rdEntities.clear();
    }

    public ArrayList<RequestDonation> getRdEntities() {
        return new ArrayList<>(rdEntities);
    }

    public void commit() throws Exceptions.EntityNotFoundException {
        // Base implementation - can be empty or throw UnsupportedOperationException
        throw new UnsupportedOperationException("Commit operation not supported in base class");
    }
}