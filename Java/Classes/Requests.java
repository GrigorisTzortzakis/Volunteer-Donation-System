// Requests.java
import java.util.ArrayList;

public class Requests extends RequestDonationList {
    private final Beneficiary beneficiary;
    private final Organization organization;

    public Requests(Beneficiary beneficiary, Organization organization) {
        super();
        this.beneficiary = beneficiary;
        this.organization = organization;
    }

    @Override
    public void add(RequestDonation requestDonation, Organization organization)
            throws Exceptions.EntityNotFoundException {
        try {
            validateRequest(requestDonation);
            super.add(requestDonation, organization);
        } catch (Exceptions.QuantityException | Exceptions.BeneficiaryLimitException e) {
            throw new Exceptions.EntityNotFoundException(e.getMessage());
        }
    }

    public void validateRequest(RequestDonation rd) throws Exceptions.QuantityException,
            Exceptions.BeneficiaryLimitException {
        if (rd.getQuantity() <= 0) {
            throw new Exceptions.QuantityException("Quantity must be positive");
        }

        RequestDonation available = organization.getCurrentDonation(rd.getEntity());
        if (available == null || available.getQuantity() < rd.getQuantity()) {
            throw new Exceptions.QuantityException("Requested quantity not available");
        }

        if (rd.getEntity() instanceof Material) {
            validateMaterialRequest((Material)rd.getEntity(), rd.getQuantity());
        }
    }

    private void validateMaterialRequest(Material material, double quantity)
            throws Exceptions.BeneficiaryLimitException {
        double allowedQuantity;
        if (beneficiary.getNoPersons() == 1) {
            allowedQuantity = material.getLevel1();
        } else if (beneficiary.getNoPersons() <= 4) {
            allowedQuantity = material.getLevel2();
        } else {
            allowedQuantity = material.getLevel3();
        }

        RequestDonation received = beneficiary.getReceivedList().get(material.getId());
        double currentReceived = (received != null) ? received.getQuantity() : 0;

        if (currentReceived + quantity > allowedQuantity) {
            throw new Exceptions.BeneficiaryLimitException(
                    "Request exceeds allowed limit for this material");
        }
    }

    @Override
    public void commit() throws Exceptions.EntityNotFoundException {
        if (getRdEntities().isEmpty()) {
            System.out.println("No requests to commit.");
            return;
        }

        System.out.println("\nCommit Results:");
        System.out.println("---------------");

        ArrayList<RequestDonation> requestsToRemove = new ArrayList<>();
        for (RequestDonation request : getRdEntities()) {
            try {
                validateRequest(request);
                RequestDonation currentDonation = organization.getCurrentDonation(request.getEntity());

                currentDonation.setQuantity(currentDonation.getQuantity() - request.getQuantity());
                beneficiary.getReceivedList().add(request, organization);
                System.out.println("Success: " + request.getEntity().getName() +
                        " - Received: " + request.getQuantity() +
                        (request.getEntity() instanceof Material ? " items" : " hours"));
                requestsToRemove.add(request);

            } catch (Exceptions.QuantityException e) {
                System.out.println("Failed: " + request.getEntity().getName() + " - " + e.getMessage());
            } catch (Exceptions.BeneficiaryLimitException e) {
                System.out.println("Failed: " + request.getEntity().getName() + " - Exceeded allowed limit");
            } catch (Exception e) {
                System.out.println("Failed: " + request.getEntity().getName() + " - " + e.getMessage());
            }
        }

        // Remove successful requests
        for (RequestDonation request : requestsToRemove) {
            remove(request);
        }
    }
}