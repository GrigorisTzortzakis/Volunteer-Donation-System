public class Main {
    public static void main(String[] args) {
        // Get the Organization instance
        Organization org = Organization.getInstance();

        try {
            // Create 3 Materials
            Material milk = new Material("Milk", "Fresh dairy milk", 1, 4, 8, 12);
            Material sugar = new Material("Sugar", "White crystal sugar", 2, 2, 4, 6);
            Material rice = new Material("Rice", "Long grain rice", 3, 3, 6, 9);

            // Create 3 Services
            Service medicalSupport = new Service("Medical Support", "Basic medical consultation", 4);
            Service nurserySupport = new Service("Nursery Support", "Basic nursing care", 5);
            Service babySitting = new Service("Baby Sitting", "Child care service", 6);

            // Add all entities to organization
            org.addEntity(milk);
            org.addEntity(sugar);
            org.addEntity(rice);
            org.addEntity(medicalSupport);
            org.addEntity(nurserySupport);
            org.addEntity(babySitting);

            // Create and set Admin
            Admin admin = new Admin("John Admin", "6911111111");
            org.setAdmin(admin);

            // Create beneficiaries
            Beneficiary ben1 = new Beneficiary("Alice Smith", "6922222222", 3);
            Beneficiary ben2 = new Beneficiary("Bob Johnson", "6933333333", 1);
            Beneficiary ben3 = new Beneficiary("Carol White", "6955555555", 6);

            // Add beneficiaries to organization
            org.insertBeneficiary(ben1);
            org.insertBeneficiary(ben2);
            org.insertBeneficiary(ben3);

            // Create donators
            Donator donator1 = new Donator("Charlie Brown", "6944444444");
            Donator donator2 = new Donator("David Wilson", "6966666666");
            org.insertDonator(donator1);
            org.insertDonator(donator2);

            // Create menu and start application
            Menu menu = new Menu(org);
            menu.start();

        } catch (Exception e) {
            System.out.println("Error during initialization: " + e.getMessage());
            e.printStackTrace();
        }
    }
}