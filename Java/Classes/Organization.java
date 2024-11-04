import java.util.ArrayList;

public class Organization {
    private static Organization instance = null;
    private final String name;
    private final ArrayList<Entity> entityList;
    private final ArrayList<Donator> donatorList;
    private final ArrayList<Beneficiary> beneficiaryList;
    private final RequestDonationList currentDonations;
    private Admin admin;

    // Private constructor for singleton
    private Organization(String name) {
        this.name = name;
        this.entityList = new ArrayList<>();
        this.donatorList = new ArrayList<>();
        this.beneficiaryList = new ArrayList<>();
        this.currentDonations = new RequestDonationList();
    }

    // Singleton getInstance method
    public static Organization getInstance() {
        if (instance == null) {
            instance = new Organization("Relief Organization System");
        }
        return instance;
    }

    public void setAdmin(Admin admin) {
        this.admin = admin;
    }

    public Admin getAdmin() {
        return admin;
    }

    public void addEntity(Entity entity) throws Exceptions.EntityExistsException {
        for (Entity e : entityList) {
            if (e.getId() == entity.getId()) {
                throw new Exceptions.EntityExistsException("Entity with ID " + entity.getId() + " already exists");
            }
            if (e.getName().equals(entity.getName()) && e.getClass().equals(entity.getClass())) {
                throw new Exceptions.EntityExistsException("Entity with name " + entity.getName() + " already exists");
            }
        }
        entityList.add(entity);
    }

    public void removeEntity(Entity entity, User requestingUser) throws Exceptions.InvalidMenuException {
        if (!(requestingUser instanceof Admin)) {
            throw new Exceptions.InvalidMenuException("Only admin can remove entities");
        }
        entityList.remove(entity);
    }

    public Entity getEntity(int id) {
        for (Entity entity : entityList) {
            if (entity.getId() == id) {
                return entity;
            }
        }
        return null;
    }

    public RequestDonation getCurrentDonation(Entity entity) {
        return currentDonations.get(entity.getId());
    }

    public void addCurrentDonation(RequestDonation donation) throws Exceptions.EntityNotFoundException {
        currentDonations.add(donation, this);
    }

    public void insertDonator(Donator donator) throws Exceptions.EntityExistsException {
        for (Donator d : donatorList) {
            if (d.getPhone().equals(donator.getPhone())) {
                throw new Exceptions.EntityExistsException("Donator with phone " + donator.getPhone() + " already exists");
            }
        }
        donatorList.add(donator);
    }

    public void removeDonator(Donator donator) {
        donatorList.remove(donator);
    }

    public void insertBeneficiary(Beneficiary beneficiary) throws Exceptions.EntityExistsException {
        for (Beneficiary b : beneficiaryList) {
            if (b.getPhone().equals(beneficiary.getPhone())) {
                throw new Exceptions.EntityExistsException("Beneficiary with phone " + beneficiary.getPhone() + " already exists");
            }
        }
        beneficiaryList.add(beneficiary);
    }

    public void removeBeneficiary(Beneficiary beneficiary) {
        beneficiaryList.remove(beneficiary);
    }

    public void listEntities() {
        if (entityList.isEmpty()) {
            System.out.println("\nNo entities available");
            return;
        }

        System.out.println("\n=== Available Entities ===");

        System.out.println("\nMaterials:");
        boolean hasMaterials = false;
        for (Entity e : entityList) {
            if (e instanceof Material) {
                hasMaterials = true;
                RequestDonation current = getCurrentDonation(e);
                double quantity = (current != null) ? current.getQuantity() : 0;
                System.out.println("ID: " + e.getId() + " - " + e.getName() + " (Available: " + quantity + ")");
                System.out.println(e.getDetails());
            }
        }
        if (!hasMaterials) {
            System.out.println("No materials available");
        }

        System.out.println("\nServices:");
        boolean hasServices = false;
        for (Entity e : entityList) {
            if (e instanceof Service) {
                hasServices = true;
                RequestDonation current = getCurrentDonation(e);
                double quantity = (current != null) ? current.getQuantity() : 0;
                System.out.println("ID: " + e.getId() + " - " + e.getName() + " (Available hours: " + quantity + ")");
                System.out.println(e.getDetails());
            }
        }
        if (!hasServices) {
            System.out.println("No services available");
        }
    }

    public void listBeneficiaries() {
        if (beneficiaryList.isEmpty()) {
            System.out.println("No beneficiaries registered");
            return;
        }

        System.out.println("\n=== Registered Beneficiaries ===");
        for (Beneficiary b : beneficiaryList) {
            System.out.println("\nBeneficiary: " + b.getName());
            System.out.println("Phone: " + b.getPhone());
            System.out.println("Family Members: " + b.getNoPersons());
            System.out.println("Received Donations:");
            b.getReceivedList().monitor();
        }
    }

    public void listDonators() {
        if (donatorList.isEmpty()) {
            System.out.println("No donators registered");
            return;
        }

        System.out.println("\n=== Registered Donators ===");
        for (Donator d : donatorList) {
            System.out.println("\nDonator: " + d.getName());
            System.out.println("Phone: " + d.getPhone());
            System.out.println("Current Offers:");
            d.getOffersList().monitor();
        }
    }

    public void resetBeneficiaryLists() {
        for (Beneficiary b : beneficiaryList) {
            b.getReceivedList().reset();
        }
        System.out.println("All beneficiary received lists have been reset");
    }

    public String getName() {
        return name;
    }

    public ArrayList<Entity> getEntityList() {
        return new ArrayList<>(entityList);
    }

    public ArrayList<Donator> getDonatorList() {
        return new ArrayList<>(donatorList);
    }

    public ArrayList<Beneficiary> getBeneficiaryList() {
        return new ArrayList<>(beneficiaryList);
    }

    public RequestDonationList getCurrentDonations() {
        return currentDonations;
    }
}