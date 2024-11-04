import java.util.ArrayList;
import java.util.Scanner;
import javax.swing.SwingUtilities;
// Menu.java
public class Menu {
    private final Organization organization;
    private final Scanner scanner;
    public static User currentUser;

    // In Menu.java, add this field
    private static volatile boolean forceLogout = false;


    public static synchronized void triggerLogout() {
        forceLogout = true;
        currentUser = null;
        // Notify any waiting threads that logout has been triggered
        Menu.class.notifyAll();
    }



    public static void resetLogout() {
        forceLogout = false;
    }


    public Menu(Organization organization) {
        this.organization = organization;
        this.scanner = new Scanner(System.in);
    }

    // In Menu.java, modify the start() method

    public void start() {
        while (true) {
            try {
                synchronized (Menu.class) {
                    // If user is logged out or no user is logged in, prompt for login
                    if (forceLogout || currentUser == null) {
                        if (forceLogout) {
                            System.out.println("\nLogged out from GUI.");
                            resetLogout(); // Reset the forceLogout flag
                        }

                        currentUser = null; // Ensure no user is logged in
                        System.out.println("\n======================================");
                        System.out.println("Welcome to " + organization.getName());
                        System.out.println("======================================");
                        System.out.print("Please enter your phone number (or 'exit' to quit): ");
                        String phone = scanner.nextLine().trim();

                        if (phone.equalsIgnoreCase("exit")) {
                            System.out.println("Thank you for using our system. Goodbye!");
                            System.exit(0);
                        }

                        if (!isValidPhoneNumber(phone)) {
                            System.out.println("Invalid phone number format. Please enter a 10-digit number.");
                            continue;
                        }

                        authenticateUser(phone);

                        if (currentUser == null) {
                            System.out.println("\nNo user found with this phone number.");
                            offerRegistration(phone);
                        } else {
                            printWelcomeMessage();
                        }
                    }
                }

                // Automatically proceed to main menu without additional button press
                if (currentUser != null) {
                    showMainMenu();
                }

            } catch (Exceptions.InvalidMenuException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }



    private boolean isValidPhoneNumber(String phone) {
        return phone != null && phone.matches("\\d{10}");
    }

    private void authenticateUser(String phone) {
        Admin admin = organization.getAdmin();
        if (admin != null && admin.getPhone().equals(phone)) {
            currentUser = admin;
            return;
        }

        for (Donator donator : organization.getDonatorList()) {
            if (donator.getPhone().equals(phone)) {
                currentUser = donator;
                return;
            }
        }

        for (Beneficiary beneficiary : organization.getBeneficiaryList()) {
            if (beneficiary.getPhone().equals(phone)) {
                currentUser = beneficiary;
                return;
            }
        }

        currentUser = null;
    }

    private void printWelcomeMessage() {
        System.out.println("\n======================================");
        System.out.println("Welcome back, " + currentUser.getName() + "!");
        if (currentUser instanceof Admin) {
            System.out.println("Logged in as: Administrator");
        } else if (currentUser instanceof Donator) {
            System.out.println("Logged in as: Donator");
        } else {
            System.out.println("Logged in as: Beneficiary");
        }
        System.out.println("======================================");
    }

    private void offerRegistration(String phone) throws Exceptions.InvalidMenuException {
        System.out.println("\nWould you like to register in our system? (y/n)");
        String response = scanner.nextLine().trim().toLowerCase();

        if (!response.equals("y") && !response.equals("yes")) {
            System.out.println("Thank you for your interest. Goodbye!");
            return;
        }

        register(phone);
    }

    private void register(String phone) throws Exceptions.InvalidMenuException {
        System.out.println("\n=== Registration ===");
        System.out.println("Please select how you would like to register:");
        System.out.println("1. Register as a Donator (to provide donations)");
        System.out.println("2. Register as a Beneficiary (to receive donations)");
        System.out.print("Enter your choice (1-2): ");

        String choice = scanner.nextLine().trim();

        System.out.print("\nPlease enter your full name: ");
        String name = scanner.nextLine().trim();

        if (name.isEmpty()) {
            throw new Exceptions.InvalidMenuException("Name cannot be empty");
        }

        try {
            switch (choice) {
                case "1":
                    Donator donator = new Donator(name, phone);
                    organization.insertDonator(donator);
                    currentUser = donator;
                    System.out.println("\nRegistration successful! You are now registered as a Donator.");
                    break;

                case "2":
                    System.out.print("Enter number of family members (including yourself): ");
                    int members = Integer.parseInt(scanner.nextLine().trim());

                    if (members <= 0) {
                        throw new Exceptions.InvalidMenuException("Number of family members must be positive");
                    }

                    Beneficiary beneficiary = new Beneficiary(name, phone, members);
                    organization.insertBeneficiary(beneficiary);
                    currentUser = beneficiary;
                    System.out.println("\nRegistration successful! You are now registered as a Beneficiary.");
                    break;

                default:
                    throw new Exceptions.InvalidMenuException("Invalid choice. Please select 1 or 2");
            }
        } catch (NumberFormatException e) {
            throw new Exceptions.InvalidMenuException("Please enter a valid number for family members");
        } catch (Exceptions.EntityExistsException e) {
            throw new Exceptions.InvalidMenuException("A user with this phone number already exists");
        }
    }

    private void showMainMenu() throws Exceptions.InvalidMenuException {
        while (true) {
            if (forceLogout) {
                // If forceLogout is triggered, return immediately to the login prompt
                return;
            }

            if (currentUser instanceof Admin) {
                showAdminMenu();
            } else if (currentUser instanceof Donator) {
                showDonatorMenu();
            } else if (currentUser instanceof Beneficiary) {
                showBeneficiaryMenu();
            } else {
                throw new Exceptions.InvalidMenuException("Unknown user type");
            }

            // If logout was triggered inside the role-specific menu, break the main menu loop
            if (forceLogout) {
                return;
            }
        }
    }


    /// In Menu.java

    private void showAdminMenu() throws Exceptions.InvalidMenuException {
        while (true) {
            if (forceLogout) {
                return; // Exit immediately if forced logout is triggered
            }

            System.out.println("\n=== Admin Menu ===");
            System.out.println("1. Use GUI Interface");
            System.out.println("2. Use Command Line Interface");
            System.out.println("3. Logout");
            System.out.println("4. Exit");

            String choice = scanner.nextLine();

            if (forceLogout) {
                return;  // Break out of the menu if forceLogout is set
            }

            try {
                switch (choice) {
                    case "1":
                        SwingUtilities.invokeLater(() -> new AdminGUI(organization));
                        return;  // Leave this menu as GUI will take over
                    case "2":
                        showCommandLineAdminMenu();
                        break;
                    case "3":
                        triggerLogout();  // Set logout flag
                        return;  // Return to re-login prompt
                    case "4":
                        System.out.println("Thank you for using our system. Goodbye!");
                        System.exit(0);
                    default:
                        throw new Exceptions.InvalidMenuException("Invalid choice");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }

            if (forceLogout) {
                return;  // Immediately return if logout is triggered
            }
        }
    }




    // Add this new method to contain your existing admin menu code
    private void showCommandLineAdminMenu() throws Exceptions.InvalidMenuException {
        while (true) {
            System.out.println("\n=== Admin Command Line Menu ===");
            System.out.println("1. View");
            System.out.println("2. Monitor Organization");
            System.out.println("3. Back");
            System.out.println("4. Exit");

            String choice = scanner.nextLine();
            try {
                switch (choice) {
                    // Your existing admin menu code here
                    // Just move it from the old showAdminMenu() to here
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private void handleAdminView() {
        while (true) {
            System.out.println("\nAvailable Categories:");
            System.out.println("1. Materials");
            System.out.println("2. Services");
            System.out.println("3. Back");

            String choice = scanner.nextLine();

            try {
                switch (choice) {
                    case "1":
                        showSimpleEntityList(true);  // true for Materials
                        break;
                    case "2":
                        showSimpleEntityList(false); // false for Services
                        break;
                    case "3":
                        return;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private void showSimpleEntityList(boolean isMaterial) {
        ArrayList<Entity> categoryItems = new ArrayList<>();
        System.out.println("\nAvailable " + (isMaterial ? "Materials" : "Services") + ":");

        // First, just show the names and IDs
        for (Entity entity : organization.getEntityList()) {
            if ((isMaterial && entity instanceof Material) || (!isMaterial && entity instanceof Service)) {
                categoryItems.add(entity);
                RequestDonation current = organization.getCurrentDonation(entity);
                double quantity = (current != null) ? current.getQuantity() : 0;
                System.out.println(categoryItems.size() + ". " + entity.getName() +
                        " (Available: " + quantity + ")");
            }
        }

        if (categoryItems.isEmpty()) {
            System.out.println("No items available in this category");
            return;
        }

        System.out.println((categoryItems.size() + 1) + ". Back");

        // Then allow selection for details
        System.out.println("\nEnter a number to view details:");
        try {
            int choice = Integer.parseInt(scanner.nextLine());
            if (choice == categoryItems.size() + 1) {
                return;
            }
            if (choice >= 1 && choice <= categoryItems.size()) {
                Entity selected = categoryItems.get(choice - 1);
                System.out.println("\n=== Detailed Information ===");
                System.out.println(selected.getEntityInfo());
                System.out.println(selected.getDetails());
                System.out.println("\nPress Enter to continue...");
                scanner.nextLine();
            } else {
                System.out.println("Invalid choice. Please try again.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number");
        }
    }

    private void handleMonitorOrganization() {
        while (true) {
            System.out.println("\n=== Monitor Organization ===");
            System.out.println("1. List Beneficiaries");
            System.out.println("2. List Donators");
            System.out.println("3. Reset All Beneficiaries Lists");
            System.out.println("4. Back");

            String choice = scanner.nextLine();

            try {
                switch (choice) {
                    case "1":
                        handleBeneficiaryList();
                        break;
                    case "2":
                        handleDonatorList();
                        break;
                    case "3":
                        organization.resetBeneficiaryLists();
                        break;
                    case "4":
                        return;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private void handleBeneficiaryList() {
        while (true) {
            ArrayList<Beneficiary> beneficiaries = organization.getBeneficiaryList();

            if (beneficiaries.isEmpty()) {
                System.out.println("\nNo beneficiaries registered");
                return;
            }

            System.out.println("\n=== Registered Beneficiaries ===");
            for (int i = 0; i < beneficiaries.size(); i++) {
                Beneficiary b = beneficiaries.get(i);
                System.out.println((i + 1) + ". " + b.getName() + " (Phone: " + b.getPhone() + ")");
            }

            System.out.println("\nOptions:");
            System.out.println("1-" + beneficiaries.size() + ". Select a beneficiary");
            System.out.println((beneficiaries.size() + 1) + ". Back");

            try {
                int choice = Integer.parseInt(scanner.nextLine());
                if (choice == beneficiaries.size() + 1) {
                    return;
                }
                if (choice >= 1 && choice <= beneficiaries.size()) {
                    handleBeneficiaryDetails(beneficiaries.get(choice - 1));
                } else {
                    System.out.println("Invalid choice. Please try again.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number");
            }
        }
    }

    private void handleBeneficiaryDetails(Beneficiary beneficiary) {
        while (true) {
            System.out.println("\n=== Beneficiary Details ===");
            System.out.println("Name: " + beneficiary.getName());
            System.out.println("Phone: " + beneficiary.getPhone());
            System.out.println("Family Members: " + beneficiary.getNoPersons());
            System.out.println("\nReceived Donations:");
            beneficiary.getReceivedList().monitor();

            System.out.println("\nOptions:");
            System.out.println("1. Clear received items list");
            System.out.println("2. Delete beneficiary");
            System.out.println("3. Back");

            try {
                int choice = Integer.parseInt(scanner.nextLine());
                switch (choice) {
                    case 1:
                        beneficiary.getReceivedList().reset();
                        System.out.println("Received items list cleared successfully");
                        return;
                    case 2:
                        organization.removeBeneficiary(beneficiary);
                        System.out.println("Beneficiary removed successfully");
                        return;
                    case 3:
                        return;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number");
            }
        }
    }

    private void handleDonatorList() {
        while (true) {
            ArrayList<Donator> donators = organization.getDonatorList();

            if (donators.isEmpty()) {
                System.out.println("\nNo donators registered");
                return;
            }

            System.out.println("\n=== Registered Donators ===");
            for (int i = 0; i < donators.size(); i++) {
                Donator d = donators.get(i);
                System.out.println((i + 1) + ". " + d.getName() + " (Phone: " + d.getPhone() + ")");
            }

            System.out.println("\nOptions:");
            System.out.println("1-" + donators.size() + ". Select a donator");
            System.out.println((donators.size() + 1) + ". Back");

            try {
                int choice = Integer.parseInt(scanner.nextLine());
                if (choice == donators.size() + 1) {
                    return;
                }
                if (choice >= 1 && choice <= donators.size()) {
                    handleDonatorDetails(donators.get(choice - 1));
                } else {
                    System.out.println("Invalid choice. Please try again.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number");
            }
        }
    }

    private void handleDonatorDetails(Donator donator) {
        while (true) {
            System.out.println("\n=== Donator Details ===");
            System.out.println("Name: " + donator.getName());
            System.out.println("Phone: " + donator.getPhone());
            System.out.println("\nCurrent Offers:");
            donator.getOffersList().monitor();

            System.out.println("\nOptions:");
            System.out.println("1. Delete donator");
            System.out.println("2. Back");

            try {
                int choice = Integer.parseInt(scanner.nextLine());
                switch (choice) {
                    case 1:
                        organization.removeDonator(donator);
                        System.out.println("Donator removed successfully");
                        return;
                    case 2:
                        return;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number");
            }
        }
    }

    private void showDonatorMenu() throws Exceptions.InvalidMenuException {
        while (true) {
            if (forceLogout) {
                return; // Exit immediately if forced logout is triggered
            }

            Donator donator = (Donator) currentUser;
            System.out.println("\n=== " + organization.getName() + " ===");
            System.out.println("User Details:");
            System.out.println("Name: " + donator.getName());
            System.out.println("Phone: " + donator.getPhone());
            System.out.println("Type: Donator");

            System.out.println("\nMenu Options:");
            System.out.println("1. Use GUI Interface");
            System.out.println("2. Use Command Line Interface");
            System.out.println("3. Logout");
            System.out.println("4. Exit");

            String choice = scanner.nextLine();

            if (forceLogout) {
                return;  // Break out of the menu if forceLogout is set
            }

            try {
                switch (choice) {
                    case "1":
                        SwingUtilities.invokeLater(() -> new DonatorGUI(organization, donator));
                        return;
                    case "2":
                        showCommandLineDonatorMenu(donator);
                        break;
                    case "3":
                        triggerLogout();  // Set logout flag
                        return;
                    case "4":
                        System.out.println("Thank you for using our system. Goodbye!");
                        System.exit(0);
                    default:
                        throw new Exceptions.InvalidMenuException("Invalid choice");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }

            if (forceLogout) {
                return;  // Immediately return if logout is triggered
            }
        }
    }



    // Add this new method to contain your existing donator menu code
    private void showCommandLineDonatorMenu(Donator donator) throws Exceptions.InvalidMenuException {
        while (true) {
            System.out.println("\nMenu Options:");
            System.out.println("1. Add Offer");
            System.out.println("2. Show Offers");
            System.out.println("3. Commit");
            System.out.println("4. Back");
            System.out.println("5. Exit");

            String choice = scanner.nextLine();
            try {
                switch (choice) {
                    case "1":
                        showDonationCategories();
                        break;
                    case "2":
                        showCurrentOffers(donator);
                        break;
                    case "3":
                        commitOffers(donator);
                        break;
                    case "4":
                        return;
                    case "5":
                        System.out.println("Thank you for using our system. Goodbye!");
                        System.exit(0);
                    default:
                        throw new Exceptions.InvalidMenuException("Invalid choice");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private void showDonationCategories() {
        while (true) {
            System.out.println("\nAvailable Categories:");
            // Show current quantities for each category
            int materialCount = 0;
            int servicesCount = 0;

            for (Entity entity : organization.getEntityList()) {
                RequestDonation current = organization.getCurrentDonation(entity);
                double quantity = (current != null) ? current.getQuantity() : 0;

                if (entity instanceof Material) {
                    materialCount += quantity;
                } else if (entity instanceof Service) {
                    servicesCount += quantity;
                }
            }

            System.out.println("[1] Material (" + materialCount + " items available)");
            System.out.println("[2] Services (" + servicesCount + " hours available)");
            System.out.println("[3] Commit");   // Added Commit option here
            System.out.println("[4] Back");     // Changed from 3 to 4

            String choice = scanner.nextLine();
            try {
                switch (choice) {
                    case "1":
                        showCategoryItems(true);
                        break;
                    case "2":
                        showCategoryItems(false);
                        break;
                    case "3":                    // Added this case
                        commitOffers((Donator) currentUser);
                        return;
                    case "4":                    // Changed from 3 to 4
                        return;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private void showCategoryItems(boolean isMaterial) {
        ArrayList<Entity> categoryItems = new ArrayList<>();
        System.out.println("\nAvailable " + (isMaterial ? "Materials" : "Services") + ":");

        for (Entity entity : organization.getEntityList()) {
            if ((isMaterial && entity instanceof Material) || (!isMaterial && entity instanceof Service)) {
                categoryItems.add(entity);
                RequestDonation current = organization.getCurrentDonation(entity);
                double quantity = (current != null) ? current.getQuantity() : 0;
                System.out.println("[" + categoryItems.size() + "] " + entity.getName() +
                        " (" + quantity + (isMaterial ? " items" : " hours") + " available)");
            }
        }

        if (categoryItems.isEmpty()) {
            System.out.println("No items available in this category");
            return;
        }

        System.out.println("[" + (categoryItems.size() + 1) + "] Back");

        try {
            int choice = Integer.parseInt(scanner.nextLine());
            if (choice == categoryItems.size() + 1) {
                return;
            }
            if (choice >= 1 && choice <= categoryItems.size()) {
                Entity selected = categoryItems.get(choice - 1);
                handleEntitySelection(selected);
            } else {
                System.out.println("Invalid choice. Please try again.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number");
        }
    }

    private void handleEntitySelection(Entity entity) {
        System.out.println("\nEntity Details:");
        System.out.println(entity.getEntityInfo());
        System.out.println(entity.getDetails());

        boolean isService = entity instanceof Service;
        System.out.print("\nWould you like to offer this " +
                (isService ? "service" : "item") + "? (y/n): ");
        String response = scanner.nextLine().trim().toLowerCase();

        if (response.equals("y") || response.equals("yes")) {
            System.out.print("Enter whole number of " +
                    (isService ? "hours" : "items") + " to offer: ");
            try {
                String input = scanner.nextLine();
                // Ensure whole numbers for both materials and services
                if (!input.matches("\\d+")) {
                    System.out.println("Error: Please enter a whole number");
                    return;
                }
                int quantity = Integer.parseInt(input);

                if (quantity <= 0) {
                    System.out.println("Amount must be positive");
                    return;
                }

                Donator donator = (Donator) currentUser;
                RequestDonation donation = new RequestDonation(entity, quantity);
                donator.add(donation);

            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number");
            }
        }
    }

    private void handleDonation(Entity entity) {
        Donator donator = (Donator) currentUser;
        System.out.println("\nSelected item: " + entity.getName());
        System.out.println(entity.getDetails());

        System.out.print("Would you like to make an offer? (y/n): ");
        String response = scanner.nextLine().trim().toLowerCase();

        if (response.equals("y") || response.equals("yes")) {
            try {
                System.out.print("Enter quantity: ");
                double quantity = Double.parseDouble(scanner.nextLine());
                if (quantity <= 0) {
                    System.out.println("Quantity must be positive");
                    return;
                }

                RequestDonation donation = new RequestDonation(entity, quantity);
                donator.add(donation);
                System.out.println("Offer added successfully");
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number");
            }
        }
    }

    private void showCurrentOffers(Donator donator) {
        while (true) {
            System.out.println("\n=== Current Offers ===");
            ArrayList<RequestDonation> offers = donator.getOffersList().getRdEntities();

            if (offers.isEmpty()) {
                System.out.println("You have no offers at the moment.");
                return;
            }

            // Display numbered list of offers
            for (int i = 0; i < offers.size(); i++) {
                RequestDonation offer = offers.get(i);
                System.out.println((i + 1) + ". " + offer.getEntity().getName() +
                        " - Quantity: " + offer.getQuantity());
            }

            // Show options
            System.out.println("\nOptions:");
            System.out.println("1-" + offers.size() + ". Select an offer");
            System.out.println((offers.size() + 1) + ". Commit");
            System.out.println((offers.size() + 2) + ". Clear all offers");
            System.out.println((offers.size() + 3) + ". Back");

            try {
                int choice = Integer.parseInt(scanner.nextLine());

                if (choice >= 1 && choice <= offers.size()) {
                    handleOfferSelection(donator, offers.get(choice - 1));
                } else if (choice == offers.size() + 1) {
                    commitOffers(donator);
                    return;
                } else if (choice == offers.size() + 2) {
                    donator.getOffersList().reset();
                    System.out.println("All offers have been cleared.");
                    return;
                } else if (choice == offers.size() + 3) {
                    return;
                } else {
                    System.out.println("Invalid choice. Please try again.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }
    private void handleOfferSelection(Donator donator, RequestDonation offer) {
        while (true) {
            System.out.println("\nSelected offer: " + offer.getEntity().getName() +
                    " - Quantity: " + offer.getQuantity());
            System.out.println("\nOptions:");
            System.out.println("1. Delete this offer");
            System.out.println("2. Modify quantity");
            System.out.println("3. Back");

            try {
                int choice = Integer.parseInt(scanner.nextLine());

                switch (choice) {
                    case 1:
                        donator.getOffersList().remove(offer);
                        System.out.println("Offer deleted successfully.");
                        return;
                    case 2:
                        System.out.print("Enter new quantity: ");
                        double newQuantity = Double.parseDouble(scanner.nextLine());
                        if (newQuantity <= 0) {
                            System.out.println("Quantity must be positive.");
                            continue;
                        }
                        offer.setQuantity(newQuantity);
                        System.out.println("Quantity modified successfully.");
                        return;
                    case 3:
                        return;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    private void commitOffers(Donator donator) {
        try {
            donator.getOffersList().commit();
            System.out.println("Offers committed successfully");
        } catch (Exception e) {
            System.out.println("Error committing offers: " + e.getMessage());
        }
    }

    private void showBeneficiaryMenu() throws Exceptions.InvalidMenuException {
        while (true) {
            if (forceLogout) {
                return; // Exit immediately if forced logout is triggered
            }

            Beneficiary beneficiary = (Beneficiary) currentUser;
            System.out.println("\n=== " + organization.getName() + " ===");
            System.out.println("User Details:");
            System.out.println("Name: " + beneficiary.getName());
            System.out.println("Phone: " + beneficiary.getPhone());
            System.out.println("Type: Beneficiary");

            System.out.println("\nMenu Options:");
            System.out.println("1. Use GUI Interface");
            System.out.println("2. Add Request");
            System.out.println("3. Show Requests");
            System.out.println("4. Commit");
            System.out.println("5. Logout");
            System.out.println("6. Exit");

            String choice = scanner.nextLine();

            if (forceLogout) {
                return;  // Break out of the menu if forceLogout is set
            }

            try {
                switch (choice) {
                    case "1":
                        SwingUtilities.invokeLater(() -> new BeneficiaryGUI(organization, beneficiary));
                        return;
                    case "2":
                        showRequestCategories();
                        break;
                    case "3":
                        showCurrentRequests(beneficiary);
                        break;
                    case "4":
                        commitRequests(beneficiary);
                        break;
                    case "5":
                        triggerLogout();  // Set logout flag
                        return;
                    case "6":
                        System.out.println("Thank you for using our system. Goodbye!");
                        System.exit(0);
                    default:
                        throw new Exceptions.InvalidMenuException("Invalid choice");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }

            if (forceLogout) {
                return;  // Immediately return if logout is triggered
            }
        }
    }



    private void showRequestCategories() {
        while (true) {
            System.out.println("\nAvailable Categories:");
            int materialCount = 0;
            int servicesCount = 0;

            for (Entity entity : organization.getEntityList()) {
                RequestDonation current = organization.getCurrentDonation(entity);
                double quantity = (current != null) ? current.getQuantity() : 0;

                if (entity instanceof Material) {
                    materialCount += quantity;
                } else if (entity instanceof Service) {
                    servicesCount += quantity;
                }
            }

            System.out.println("[1] Material (" + materialCount + " items available)");
            System.out.println("[2] Services (" + servicesCount + " hours available)");
            System.out.println("[3] Commit");    // Added Commit option
            System.out.println("[4] Back");      // Changed from 3 to 4

            String choice = scanner.nextLine();
            try {
                switch (choice) {
                    case "1":
                        showRequestCategoryItems(true);
                        break;
                    case "2":
                        showRequestCategoryItems(false);
                        break;
                    case "3":
                        commitRequests((Beneficiary) currentUser);
                        break;
                    case "4":
                        return;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private void showRequestCategoryItems(boolean isMaterial) {
        ArrayList<Entity> categoryItems = new ArrayList<>();
        System.out.println("\nAvailable " + (isMaterial ? "Materials" : "Services") + ":");

        for (Entity entity : organization.getEntityList()) {
            if ((isMaterial && entity instanceof Material) || (!isMaterial && entity instanceof Service)) {
                categoryItems.add(entity);
                RequestDonation current = organization.getCurrentDonation(entity);
                double quantity = (current != null) ? current.getQuantity() : 0;
                System.out.println("[" + categoryItems.size() + "] " + entity.getName() +
                        " (" + quantity + (isMaterial ? " items" : " hours") + " available)");
            }
        }

        if (categoryItems.isEmpty()) {
            System.out.println("No items available in this category");
            return;
        }

        System.out.println("[" + (categoryItems.size() + 1) + "] Back");

        try {
            int choice = Integer.parseInt(scanner.nextLine());
            if (choice == categoryItems.size() + 1) {
                return;
            }
            if (choice >= 1 && choice <= categoryItems.size()) {
                Entity selected = categoryItems.get(choice - 1);
                handleRequestEntitySelection(selected);
            } else {
                System.out.println("Invalid choice. Please try again.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Please enter a valid number");
        }
    }

    private void handleRequestEntitySelection(Entity entity) {
        Beneficiary beneficiary = (Beneficiary) currentUser;
        System.out.println("\nEntity Details:");
        System.out.println(entity.getEntityInfo());
        System.out.println(entity.getDetails());

        boolean isService = entity instanceof Service;

        // Check availability first
        RequestDonation currentDonation = organization.getCurrentDonation(entity);
        double availableQuantity = (currentDonation != null) ? currentDonation.getQuantity() : 0;

        // Add the beneficiary's level and limit information for materials
        if (entity instanceof Material) {
            Material material = (Material) entity;
            int noPersons = beneficiary.getNoPersons();
            double allowedQuantity;
            String levelInfo;

            if (noPersons == 1) {
                allowedQuantity = material.getLevel1();
                levelInfo = "Level 1 (1 person)";
            } else if (noPersons <= 4) {
                allowedQuantity = material.getLevel2();
                levelInfo = "Level 2 (2-4 people)";
            } else {
                allowedQuantity = material.getLevel3();
                levelInfo = "Level 3 (5+ people)";
            }

            // Get currently received amount
            RequestDonation received = beneficiary.getReceivedList().get(entity.getId());
            double currentReceived = (received != null) ? received.getQuantity() : 0;

            System.out.println("\nYour Family Size: " + noPersons + " people");
            System.out.println("Your Level: " + levelInfo);
            System.out.println("Maximum allowed quantity: " + allowedQuantity);
            System.out.println("Already received: " + currentReceived);
            System.out.println("You can still request up to: " + (allowedQuantity - currentReceived));
        }

        if (availableQuantity <= 0) {
            System.out.println("Error: This " + (isService ? "service" : "item") +
                    " is currently not available");
            return;
        }

        System.out.print("\nWould you like to request this " +
                (isService ? "service" : "item") + "? (y/n): ");
        String response = scanner.nextLine().trim().toLowerCase();

        if (response.equals("y") || response.equals("yes")) {
            System.out.print("Enter whole number of " +
                    (isService ? "hours" : "items") + " to request: ");
            try {
                String input = scanner.nextLine();
                // Ensure whole numbers for both materials and services
                if (!input.matches("\\d+")) {
                    System.out.println("Error: Please enter a whole number");
                    return;
                }
                int quantity = Integer.parseInt(input);

                if (quantity <= 0) {
                    System.out.println("Amount must be positive");
                    return;
                }
                if (quantity > availableQuantity) {
                    System.out.println("Error: Requested quantity not available");
                    return;
                }

                RequestDonation request = new RequestDonation(entity, quantity);
                beneficiary.add(request);
            } catch (NumberFormatException e) {
                System.out.println("Error: Please enter a valid number");
            }
        }
    }

    private void showCurrentRequests(Beneficiary beneficiary) {
        while (true) {
            System.out.println("\n=== Current Requests ===");
            ArrayList<RequestDonation> requests = beneficiary.getRequestsList().getRdEntities();

            if (requests.isEmpty()) {
                System.out.println("You have no requests at the moment.");
                return;
            }

            // Display numbered list of requests
            for (int i = 0; i < requests.size(); i++) {
                RequestDonation request = requests.get(i);
                System.out.println((i + 1) + ". " + request.getEntity().getName() +
                        " - Quantity: " + request.getQuantity());
            }

            // Show options
            System.out.println("\nOptions:");
            System.out.println("1-" + requests.size() + ". Select a request");
            System.out.println((requests.size() + 1) + ". Clear all requests");
            System.out.println((requests.size() + 2) + ". Back");

            try {
                int choice = Integer.parseInt(scanner.nextLine());

                if (choice >= 1 && choice <= requests.size()) {
                    handleRequestSelection(beneficiary, requests.get(choice - 1));
                } else if (choice == requests.size() + 1) {
                    beneficiary.getRequestsList().reset();
                    System.out.println("All requests have been cleared.");
                    return;
                } else if (choice == requests.size() + 2) {
                    return;
                } else {
                    System.out.println("Invalid choice. Please try again.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    private void handleRequestSelection(Beneficiary beneficiary, RequestDonation request) {
        while (true) {
            System.out.println("\nSelected request: " + request.getEntity().getName() +
                    " - Quantity: " + request.getQuantity());
            System.out.println("\nOptions:");
            System.out.println("1. Delete this request");
            System.out.println("2. Modify quantity");
            System.out.println("3. Back");

            try {
                int choice = Integer.parseInt(scanner.nextLine());

                switch (choice) {
                    case 1:
                        beneficiary.getRequestsList().remove(request);
                        System.out.println("Request deleted successfully.");
                        return;
                    case 2:
                        // Check current availability
                        RequestDonation currentDonation = organization.getCurrentDonation(request.getEntity());
                        double availableQuantity = (currentDonation != null) ? currentDonation.getQuantity() : 0;

                        if (availableQuantity <= 0) {
                            System.out.println("Error: This " +
                                    (request.getEntity() instanceof Service ? "service" : "item") +
                                    " is currently not available");
                            return;
                        }

                        System.out.print("Enter new quantity (whole numbers only, max available: " +
                                (int)availableQuantity + "): ");
                        String input = scanner.nextLine();

                        // Validate whole number input
                        if (!input.matches("\\d+")) {
                            System.out.println("Error: Please enter a whole number");
                            continue;
                        }

                        int newQuantity = Integer.parseInt(input);

                        if (newQuantity <= 0) {
                            System.out.println("Error: Quantity must be positive");
                            continue;
                        }

                        if (newQuantity > availableQuantity) {
                            System.out.println("Error: Requested quantity exceeds available amount");
                            continue;
                        }

                        // Create a temporary request to validate
                        RequestDonation tempRequest = new RequestDonation(request.getEntity(), newQuantity);
                        try {
                            beneficiary.getRequestsList().validateRequest(tempRequest);
                            request.setQuantity(newQuantity);
                            System.out.println("Quantity modified successfully.");
                            return;
                        } catch (Exception e) {
                            System.out.println("Error: " + e.getMessage());
                            continue;
                        }
                    case 3:
                        return;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    // In Menu.java, modify the commitRequests method:
    private void commitRequests(Beneficiary beneficiary) {
        try {
            // Get list of requests before commit to compare
            ArrayList<RequestDonation> beforeCommit = new ArrayList<>(beneficiary.getRequestsList().getRdEntities());

            System.out.println("\nProcessing requests...");
            beneficiary.getRequestsList().commit();

            // Get list of received items after commit
            ArrayList<RequestDonation> afterCommit = beneficiary.getReceivedList().getRdEntities();

            System.out.println("\nCommit Results:");
            System.out.println("---------------");

            for (RequestDonation request : beforeCommit) {
                boolean wasCommitted = false;
                for (RequestDonation received : afterCommit) {
                    if (received.getEntity().getId() == request.getEntity().getId()) {
                        wasCommitted = true;
                        break;
                    }
                }

                System.out.println(request.getEntity().getName() + " (" + request.getQuantity() +
                        (request.getEntity() instanceof Material ? " items" : " hours") + "): " +
                        (wasCommitted ? "✓ Successfully received" : "✗ Could not be fulfilled"));
            }

            if (beforeCommit.isEmpty()) {
                System.out.println("No requests to commit.");
            }
        } catch (Exception e) {
            System.out.println("\nError during commit: " + e.getMessage());
            System.out.println("Please check your requests and try again.");
        }
    }
}