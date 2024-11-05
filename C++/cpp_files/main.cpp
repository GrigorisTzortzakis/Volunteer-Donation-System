// main.cpp
#include <iostream>
#include "Organization.h"
#include "Admin.h"
#include "Beneficiary.h"
#include "Donator.h"
#include "Material.h"
#include "Service.h"
#include "Menu.h"

int main() {
    try {
        // Create Organization
        Organization organization("Help Foundation");
        std::cout << "Organization created successfully.\n\n";

        // Create and set Admin
        Admin* admin = new Admin("John Admin", "1234567890");
        organization.setAdmin(admin);

        // Add some initial entities
        Material* milk = new Material("Milk", "Fresh dairy milk", 1, 2.0, 4.0, 6.0);
        Material* sugar = new Material("Sugar", "White sugar", 2, 1.0, 2.0, 3.0);
        Material* rice = new Material("Rice", "Long grain rice", 3, 1.0, 2.0, 4.0);
        Service* medical = new Service("Medical Support", "Basic medical care", 4);
        Service* nursing = new Service("Nursing Support", "Home nursing care", 5);
        Service* babysitting = new Service("Babysitting", "Child care service", 6);

        organization.addEntity(milk);
        organization.addEntity(sugar);
        organization.addEntity(rice);
        organization.addEntity(medical);
        organization.addEntity(nursing);
        organization.addEntity(babysitting);

        // Create an initial donator and beneficiary
        Donator* donator = new Donator("Alice Donor", "9876543210");
        organization.insertDonator(donator);

        Beneficiary* beneficiary = new Beneficiary("Bob Beneficiary", "5555555555", 3);
        organization.insertBeneficiary(beneficiary);

        // Add some initial donations
        donator->add(1, 100.0);  // 100 units of milk
        donator->add(2, 50.0);   // 50 units of sugar
        donator->commit();

        std::cout << "Initial setup completed.\n";
        std::cout << "----------------------------------------\n";
        std::cout << "Existing users for testing:\n";
        std::cout << "Admin phone: 1234567890\n";
        std::cout << "Donator phone: 9876543210\n";
        std::cout << "Beneficiary phone: 5555555555\n";
        std::cout << "----------------------------------------\n\n";

        // Create and start the menu
        Menu menu(&organization);
        menu.start();

    } catch (const std::exception& e) {
        std::cerr << "Error: " << e.what() << std::endl;
    }

    return 0;
}