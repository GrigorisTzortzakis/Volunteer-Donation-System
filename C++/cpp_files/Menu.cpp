// Menu.cpp

#include "Menu.h"
#include "Organization.h"
#include "Material.h"
#include "Service.h"
#include "Donator.h"
#include "Beneficiary.h"
#include "Admin.h"
#include "RequestDonation.h"
#include "Offers.h"  // Add this line
#include "exceptions.h"
#include <iostream>
#include <limits>
#include <iomanip>
#include "Requests.h"  // Add this include

Menu::Menu(Organization* org) : organization(org) {}

Menu::~Menu() = default;

std::string Menu::getPhoneNumber()  {
    std::string phone;
    std::cout << "Please enter your phone number: ";
    std::getline(std::cin, phone);
    return phone;
}

void Menu::handleNewUser(const std::string& phone) {
    std::cout << "You are not registered in our system.\n";
    std::cout << "Would you like to register? (y/n): ";

    char response;
    std::cin >> response;
    std::cin.ignore(std::numeric_limits<std::streamsize>::max(), '\n');

    if (response == 'y' || response == 'Y') {
        std::cout << "Register as:\n";
        std::cout << "1. Donator\n";
        std::cout << "2. Beneficiary\n";
        std::cout << "Choose (1-2): ";

        char choice;
        std::cin >> choice;
        std::cin.ignore(std::numeric_limits<std::streamsize>::max(), '\n');

        if (choice == '1' || choice == '2') {
            registerNewUser(phone, choice);
        } else {
            std::cout << "Invalid choice. Registration cancelled.\n";
        }
    } else {
        std::cout << "Thank you for visiting. Goodbye!\n";
    }
}

void Menu::registerNewUser(const std::string& phone, char userType) {
    std::cout << "Enter your name: ";
    std::string name;
    std::getline(std::cin, name);

    try {
        if (userType == '1') {
            Donator* newDonator = new Donator(name, phone);
            organization->insertDonator(newDonator);
            std::cout << "Registration successful! Welcome, " << name << "!\n";
        } else {
            std::cout << "Enter number of family members: ";
            int familyMembers;
            std::cin >> familyMembers;
            std::cin.ignore(std::numeric_limits<std::streamsize>::max(), '\n');

            Beneficiary* newBeneficiary = new Beneficiary(name, phone, familyMembers);
            organization->insertBeneficiary(newBeneficiary);
            std::cout << "Registration successful! Welcome, " << name << "!\n";
        }
    } catch (const std::exception& e) {
        std::cout << "Registration failed: " << e.what() << "\n";
    }
}

void Menu::start() {
    bool running = true;
    while (running) {
        std::string phone = getPhoneNumber();

        if (organization->getAdmin() && organization->getAdmin()->getPhone() == phone) {
            adminMenu(organization->getAdmin());
        } else if (Donator* donator = organization->findDonator(phone)) {
            donatorMenu(donator);
        } else if (Beneficiary* beneficiary = organization->findBeneficiary(phone)) {
            beneficiaryMenu(beneficiary);
        } else {
            handleNewUser(phone);
        }
    }
}

void Menu::donatorMenu(Donator* donator) {
    while (true) {
        std::cout << "\n=================================\n";
        std::cout << "Welcome back, " << donator->getName() << "!\n";
        std::cout << "Logged in as: Donator\n";
        std::cout << "=================================\n\n";

        std::cout << "=== Relief Organization System ===\n";
        std::cout << "User Details:\n";
        std::cout << "Name: " << donator->getName() << "\n";
        std::cout << "Phone: " << donator->getPhone() << "\n";
        std::cout << "Type: Donator\n\n";

        std::cout << "Menu Options:\n";
        std::cout << "1. Add Offer\n";
        std::cout << "2. Show Offers\n";
        std::cout << "3. Commit\n";
        std::cout << "4. Logout\n";
        std::cout << "5. Exit\n";

        char choice;
        std::cin >> choice;
        std::cin.ignore(std::numeric_limits<std::streamsize>::max(), '\n');

        switch (choice) {
            case '1':
                showAddOfferMenu(donator);
            break;
            case '2':
                showOffersMenu(donator);
            break;
            case '3':
                handleCommit(donator);
            break;
            case '4':
                return;
            case '5':
                exit(0);
            default:
                std::cout << "Invalid option. Please try again.\n";
            break;
        }
    }
}

void Menu::showAddOfferMenu(Donator* donator) {
    while (true) {
        // Calculate total quantities for materials and services
        double materialTotal = 0;
        double serviceTotal = 0;
        auto currentDonations = organization->getCurrentDonations();

        for (const auto& donation : currentDonations->getRdEntities()) {
            if (dynamic_cast<Material*>(donation->getEntity())) {
                materialTotal += donation->getQuantity();
            } else if (dynamic_cast<Service*>(donation->getEntity())) {
                serviceTotal += donation->getQuantity();
            }
        }

        std::cout << "\nAvailable Categories:\n";
        std::cout << std::fixed << std::setprecision(1);
        std::cout << "[1] Material (" << materialTotal << " items available)\n";
        std::cout << "[2] Services (" << serviceTotal << " hours available)\n";
        std::cout << "[3] Commit\n";
        std::cout << "[4] Back\n";

        char choice;
        std::cin >> choice;
        std::cin.ignore(std::numeric_limits<std::streamsize>::max(), '\n');

        switch (choice) {
            case '1':
                showCategoryItems(donator, true);
                break;
            case '2':
                showCategoryItems(donator, false);
                break;
            case '3':
                handleCommit(donator);
                break;
            case '4':
                return;
            default:
                break;
        }
    }
}

void Menu::showCategoryItems(Donator* donator, bool isMaterial) {
    while (true) {
        std::cout << "\nAvailable " << (isMaterial ? "Materials" : "Services") << ":\n";
        std::vector<Entity*> items;
        auto currentDonations = organization->getCurrentDonations();

        for (Entity* entity : organization->getEntityList()) {
            bool isEntityMaterial = dynamic_cast<Material*>(entity) != nullptr;
            if (isEntityMaterial == isMaterial) {
                items.push_back(entity);
                // Find current quantity
                double currentQty = 0;
                try {
                    auto donation = currentDonations->get(entity->getId());
                    currentQty = donation->getQuantity();
                } catch (const EntityNotFoundException&) {
                    // No current donations for this entity
                }
                std::cout << items.size() << ". " << entity->getName();
                std::cout << std::fixed << std::setprecision(1);
                if (isMaterial) {
                    std::cout << " (Current quantity: " << currentQty << ")\n";
                } else {
                    std::cout << " (Current hours: " << currentQty << ")\n";
                }
            }
        }

        std::cout << (items.size() + 1) << ". Back\n\n";
        std::cout << "Select option: ";

        int choice;
        std::cin >> choice;
        std::cin.ignore(std::numeric_limits<std::streamsize>::max(), '\n');

        if (choice > 0 && choice <= static_cast<int>(items.size())) {
            Entity* selected = items[choice - 1];
            std::cout << "\n" << selected->toString() << "\n\n";
            std::cout << "Would you like to make an offer? (y/n): ";

            char response;
            std::cin >> response;
            std::cin.ignore(std::numeric_limits<std::streamsize>::max(), '\n');

            // Inside both showCategoryItems and showOfferDetails, replace the quantity input section with:

            if (response == 'y' || response == 'Y') {
                std::cout << "Enter quantity" << (isMaterial ? ": " : " (in hours): ");
                std::string input;
                int quantity;

                while (true) {
                    std::getline(std::cin, input);

                    // Check if input contains any non-digit characters
                    if (input.find_first_not_of("0123456789") != std::string::npos) {
                        std::cout << "Please enter a whole number: ";
                        continue;
                    }

                    try {
                        quantity = std::stoi(input);
                        if (quantity <= 0) {
                            std::cout << "Quantity must be greater than 0. Please try again: ";
                            continue;
                        }
                        break;
                    } catch (const std::exception&) {
                        std::cout << "Please enter a valid whole number: ";
                    }
                }

                try {
                    donator->add(selected->getId(), quantity);
                    std::cout << "Successfully added offer of " << quantity
                             << (isMaterial ? " units" : " hours")
                             << " of " << selected->getName() << "\n";
                } catch (const std::exception& e) {
                    std::cout << "Error: " << e.what() << "\n";
                }
            }
        } else if (choice == items.size() + 1) {
            return;
        }
    }
}

void Menu::showOffersMenu(Donator* donator) {
    while (true) {
        std::cout << "\n=== Current Offers ===\n";
        const auto& offers = donator->getOffersList()->getRdEntities();

        if (offers.empty()) {
            std::cout << "No current offers.\n";
        } else {
            std::cout << std::fixed << std::setprecision(1);
            for (size_t i = 0; i < offers.size(); i++) {
                bool isMaterial = dynamic_cast<Material*>(offers[i]->getEntity()) != nullptr;
                std::cout << (i + 1) << ". " << offers[i]->getEntity()->getName()
                         << " - " << (isMaterial ? "Quantity: " : "Hours: ")
                         << offers[i]->getQuantity() << "\n";
            }
        }

        std::cout << "\nOptions:\n";
        if (!offers.empty()) {
            std::cout << "1-" << offers.size() << ". Select an offer\n";
        }
        std::cout << "2. Commit\n";
        std::cout << "3. Clear all offers\n";
        std::cout << "4. Back\n";

        int choice;
        std::cin >> choice;
        std::cin.ignore(std::numeric_limits<std::streamsize>::max(), '\n');

        if (choice > 0 && choice <= static_cast<int>(offers.size())) {
            showOfferDetails(donator, offers[choice - 1]);
        } else {
            switch (choice) {
                case 2:
                    if (handleCommit(donator)) return;
                    break;
                case 3:
                    donator->reset();
                    std::cout << "All offers cleared.\n";
                    break;
                case 4:
                    return;
                default:
                    break;
            }
        }
    }
}

void Menu::showOfferDetails(Donator* donator, RequestDonation* offer) {
    while (true) {
        bool isMaterial = dynamic_cast<Material*>(offer->getEntity()) != nullptr;

        std::cout << "\nOffer Details:\n";
        std::cout << "Entity: " << offer->getEntity()->getName() << "\n";
        std::cout << std::fixed << std::setprecision(1);
        std::cout << "Current " << (isMaterial ? "quantity: " : "hours: ")
                 << offer->getQuantity() << "\n\n";

        std::cout << "1. Modify quantity\n";
        std::cout << "2. Delete offer\n";
        std::cout << "3. Back\n";

        char choice;
        std::cin >> choice;
        std::cin.ignore(std::numeric_limits<std::streamsize>::max(), '\n');

        switch (choice) {
            case '1': {
                std::cout << "Enter new " << (isMaterial ? "quantity: " : "hours: ");
                std::string input;
                int quantity;

                while (true) {
                    std::getline(std::cin, input);

                    // Check if input contains any non-digit characters
                    if (input.find_first_not_of("0123456789") != std::string::npos) {
                        std::cout << "Please enter a whole number: ";
                        continue;
                    }

                    try {
                        quantity = std::stoi(input);
                        if (quantity <= 0) {
                            std::cout << "Value must be greater than 0. Please try again: ";
                            continue;
                        }
                        break;
                    } catch (const std::exception&) {
                        std::cout << "Please enter a valid whole number: ";
                    }
                }

                try {
                    donator->getOffersList()->modify(offer->getEntity()->getId(), quantity);
                    std::cout << "Successfully updated " << offer->getEntity()->getName()
                              << " to " << quantity << (isMaterial ? " units" : " hours") << "\n";
                    return;
                } catch (const std::exception& e) {
                    std::cout << "Error: " << e.what() << "\n";
                }
                break;
            }
            case '2': {
                try {
                    std::string entityName = offer->getEntity()->getName();
                    double quantity = offer->getQuantity();
                    bool isMaterial = dynamic_cast<Material*>(offer->getEntity()) != nullptr;
                    donator->remove(offer->getEntity()->getId());
                    std::cout << "Successfully deleted offer of " << std::fixed << std::setprecision(1)
                             << quantity << (isMaterial ? " units of " : " hours of ")
                             << entityName << "\n";
                    return;
                } catch (const std::exception& e) {
                    std::cout << "Error: " << e.what() << "\n";
                }
                break;
            }
            case '3':
                return;
            default:
                break;
        }
    }
}

bool Menu::handleCommit(Donator* donator) {
    const auto& offers = donator->getOffersList()->getRdEntities();
    if (offers.empty()) {
        std::cout << "No offers to commit.\n";
        return false;
    }

    try {
        std::cout << "\nCommitting the following offers:\n";
        for (const auto& offer : offers) {
            bool isMaterial = dynamic_cast<Material*>(offer->getEntity()) != nullptr;
            std::cout << "- " << offer->getEntity()->getName() << ": "
                     << std::fixed << std::setprecision(1) << offer->getQuantity()
                     << (isMaterial ? " units" : " hours") << "\n";
        }

        donator->commit();
        std::cout << "\nAll offers committed successfully!\n";
        return true;
    } catch (const std::exception& e) {
        std::cout << "Error committing offers: " << e.what() << "\n";
        return false;
    }
}

// In Menu.cpp, add these implementations:
void Menu::beneficiaryMenu(Beneficiary* beneficiary) {
    while (true) {
        std::cout << "\n=================================\n";
        std::cout << "Welcome back, " << beneficiary->getName() << "!\n";
        std::cout << "Logged in as: Beneficiary\n";
        std::cout << "=================================\n\n";

        std::cout << "=== Relief Organization System ===\n";
        std::cout << "Organization: " << organization->getName() << "\n";
        std::cout << "User Details:\n";
        std::cout << "Name: " << beneficiary->getName() << "\n";
        std::cout << "Phone: " << beneficiary->getPhone() << "\n";
        std::cout << "Family Members: " << beneficiary->getNoPersons() << "\n";
        std::cout << "Type: Beneficiary\n\n";

        std::cout << "Menu Options:\n";
        std::cout << "1. Add Request\n";
        std::cout << "2. Show Requests\n";
        std::cout << "3. Commit\n";
        std::cout << "4. Logout\n";
        std::cout << "5. Exit\n";

        char choice;
        std::cin >> choice;
        std::cin.ignore(std::numeric_limits<std::streamsize>::max(), '\n');

        switch (choice) {
            case '1':
                showAddRequestMenu(beneficiary);
                break;
            case '2':
                showRequestsMenu(beneficiary);
                break;
            case '3':
                handleBeneficiaryCommit(beneficiary);
                break;
            case '4':
                return;
            case '5':
                exit(0);
            default:
                std::cout << "Invalid option. Please try again.\n";
                break;
        }
    }
}

void Menu::showAddRequestMenu(Beneficiary* beneficiary) {
    while (true) {
        double materialTotal = 0;
        double serviceTotal = 0;
        auto currentDonations = organization->getCurrentDonations();

        for (const auto& donation : currentDonations->getRdEntities()) {
            if (dynamic_cast<Material*>(donation->getEntity())) {
                materialTotal += donation->getQuantity();
            } else if (dynamic_cast<Service*>(donation->getEntity())) {
                serviceTotal += donation->getQuantity();
            }
        }

        std::cout << "\nAvailable Categories:\n";
        std::cout << std::fixed << std::setprecision(1);
        std::cout << "[1] Material (" << materialTotal << " items available)\n";
        std::cout << "[2] Services (" << serviceTotal << " hours available)\n";
        std::cout << "[3] Back\n";

        char choice;
        std::cin >> choice;
        std::cin.ignore(std::numeric_limits<std::streamsize>::max(), '\n');

        switch (choice) {
            case '1':
                showCategoryItemsForRequest(beneficiary, true);
                break;
            case '2':
                showCategoryItemsForRequest(beneficiary, false);
                break;
            case '3':
                return;
            default:
                std::cout << "Invalid option. Please try again.\n";
                break;
        }
    }
}

void Menu::showCategoryItemsForRequest(Beneficiary* beneficiary, bool isMaterial) {
    while (true) {
        std::cout << "\nAvailable " << (isMaterial ? "Materials" : "Services") << ":\n";
        std::vector<Entity*> items;
        auto currentDonations = organization->getCurrentDonations();

        for (Entity* entity : organization->getEntityList()) {
            bool isEntityMaterial = dynamic_cast<Material*>(entity) != nullptr;
            if (isEntityMaterial == isMaterial) {
                items.push_back(entity);
                double currentQty = 0;
                try {
                    auto donation = currentDonations->get(entity->getId());
                    currentQty = donation->getQuantity();
                } catch (const EntityNotFoundException&) {}

                std::cout << items.size() << ". " << entity->getName();
                std::cout << std::fixed << std::setprecision(1);
                if (isMaterial) {
                    std::cout << " (Available: " << currentQty << ")\n";
                } else {
                    std::cout << " (Available hours: " << currentQty << ")\n";
                }
            }
        }

        std::cout << (items.size() + 1) << ". Commit\n";
        std::cout << (items.size() + 2) << ". Back\n\n";
        std::cout << "Select option: ";

        int choice;
        std::cin >> choice;
        std::cin.ignore(std::numeric_limits<std::streamsize>::max(), '\n');

        if (choice > 0 && choice <= static_cast<int>(items.size())) {
            Entity* selected = items[choice - 1];
            std::cout << "\n" << selected->toString() << "\n\n";
            std::cout << "Would you like to make a request? (y/n): ";

            char response;
            std::cin >> response;
            std::cin.ignore(std::numeric_limits<std::streamsize>::max(), '\n');

            if (response == 'y' || response == 'Y') {
                std::cout << "Enter quantity" << (isMaterial ? ": " : " (in hours): ");
                std::string input;
                int quantity;

                while (true) {
                    std::getline(std::cin, input);
                    if (input.find_first_not_of("0123456789") != std::string::npos) {
                        std::cout << "Please enter a whole number: ";
                        continue;
                    }

                    try {
                        quantity = std::stoi(input);
                        if (quantity <= 0) {
                            std::cout << "Quantity must be greater than 0. Please try again: ";
                            continue;
                        }
                        break;
                    } catch (const std::exception&) {
                        std::cout << "Please enter a valid whole number: ";
                    }
                }

                try {
                    beneficiary->addRequest(selected->getId(), quantity);
                    std::cout << "Successfully added request for " << quantity
                             << (isMaterial ? " units" : " hours")
                             << " of " << selected->getName() << "\n";
                } catch (const InsufficientQuantityException& e) {
                    std::cout << "Error: Requested quantity exceeds available amount\n";
                } catch (const InvalidRequestException& e) {
                    std::cout << "Error: Request exceeds your allowed limit\n";
                } catch (const std::exception& e) {
                    std::cout << "Error: " << e.what() << "\n";
                }
            }
        } else if (choice == items.size() + 1) {
            handleBeneficiaryCommit(beneficiary);
        } else if (choice == items.size() + 2) {
            return;
        } else {
            std::cout << "Invalid option. Please try again.\n";
        }
    }
}

void Menu::showRequestsMenu(Beneficiary* beneficiary) {
    while (true) {
        std::cout << "\n=== Current Requests ===\n";
        const auto& requests = beneficiary->getRequestsList()->getRdEntities();

        if (requests.empty()) {
            std::cout << "No current requests.\n";
        } else {
            std::cout << std::fixed << std::setprecision(1);
            for (size_t i = 0; i < requests.size(); i++) {
                bool isMaterial = dynamic_cast<Material*>(requests[i]->getEntity()) != nullptr;
                std::cout << (i + 1) << ". " << requests[i]->getEntity()->getName()
                         << " - " << (isMaterial ? "Quantity: " : "Hours: ")
                         << requests[i]->getQuantity() << "\n";
            }
        }

        std::cout << "\nOptions:\n";
        if (!requests.empty()) {
            std::cout << "1-" << requests.size() << ". Select a request\n";
        }
        std::cout << "2. Commit\n";
        std::cout << "3. Clear all requests\n";
        std::cout << "4. Back\n";

        int choice;
        std::cin >> choice;
        std::cin.ignore(std::numeric_limits<std::streamsize>::max(), '\n');

        if (choice > 0 && choice <= static_cast<int>(requests.size())) {
            showRequestDetails(beneficiary, requests[choice - 1]);
        } else {
            switch (choice) {
                case 2:
                    if (handleBeneficiaryCommit(beneficiary)) return;
                    break;
                case 3:
                    beneficiary->resetRequests();
                    std::cout << "All requests cleared.\n";
                    break;
                case 4:
                    return;
                default:
                    std::cout << "Invalid option. Please try again.\n";
                    break;
            }
        }
    }
}

void Menu::showRequestDetails(Beneficiary* beneficiary, RequestDonation* request) {
    while (true) {
        bool isMaterial = dynamic_cast<Material*>(request->getEntity()) != nullptr;

        std::cout << "\nRequest Details:\n";
        std::cout << "Entity: " << request->getEntity()->getName() << "\n";
        std::cout << std::fixed << std::setprecision(1);
        std::cout << "Current " << (isMaterial ? "quantity: " : "hours: ")
                 << request->getQuantity() << "\n\n";

        std::cout << "1. Modify quantity\n";
        std::cout << "2. Delete request\n";
        std::cout << "3. Back\n";

        char choice;
        std::cin >> choice;
        std::cin.ignore(std::numeric_limits<std::streamsize>::max(), '\n');

        switch (choice) {
            case '1': {
                std::cout << "Enter new " << (isMaterial ? "quantity: " : "hours: ");
                std::string input;
                int quantity;

                while (true) {
                    std::getline(std::cin, input);
                    if (input.find_first_not_of("0123456789") != std::string::npos) {
                        std::cout << "Please enter a whole number: ";
                        continue;
                    }

                    try {
                        quantity = std::stoi(input);
                        if (quantity <= 0) {
                            std::cout << "Value must be greater than 0. Please try again: ";
                            continue;
                        }
                        break;
                    } catch (const std::exception&) {
                        std::cout << "Please enter a valid whole number: ";
                    }
                }

                try {
                    beneficiary->getRequestsList()->modify(request->getEntity()->getId(), quantity);
                    std::cout << "Successfully updated " << request->getEntity()->getName()
                             << " to " << quantity << (isMaterial ? " units" : " hours") << "\n";
                    return;
                } catch (const InsufficientQuantityException& e) {
                    std::cout << "Error: Requested quantity exceeds available amount\n";
                } catch (const InvalidRequestException& e) {
                    std::cout << "Error: Request exceeds your allowed limit\n";
                } catch (const std::exception& e) {
                    std::cout << "Error: " << e.what() << "\n";
                }
                break;
            }
            case '2': {
                try {
                    std::string entityName = request->getEntity()->getName();
                    double quantity = request->getQuantity();
                    bool isMaterial = dynamic_cast<Material*>(request->getEntity()) != nullptr;
                    beneficiary->removeRequest(request->getEntity()->getId());
                    std::cout << "Successfully deleted request for " << std::fixed << std::setprecision(1)
                             << quantity << (isMaterial ? " units of " : " hours of ")
                             << entityName << "\n";
                    return;
                } catch (const std::exception& e) {
                    std::cout << "Error: " << e.what() << "\n";
                }
                break;
            }
            case '3':
                return;
            default:
                std::cout << "Invalid option. Please try again.\n";
                break;
        }
    }
}

bool Menu::handleBeneficiaryCommit(Beneficiary* beneficiary) {
    const auto& requests = beneficiary->getRequestsList()->getRdEntities();
    if (requests.empty()) {
        std::cout << "No requests to commit.\n";
        return false;
    }

    try {
        std::cout << "\nProcessing the following requests:\n";
        for (const auto& request : requests) {
            bool isMaterial = dynamic_cast<Material*>(request->getEntity()) != nullptr;
            std::cout << "- " << request->getEntity()->getName() << ": "
                     << std::fixed << std::setprecision(1) << request->getQuantity()
                     << (isMaterial ? " units" : " hours") << "\n";
        }

        beneficiary->commitRequests();
        return true;
    } catch (const std::exception& e) {
        std::cout << "Error processing requests: " << e.what() << "\n";
        return false;
    }
}

void Menu::adminMenu(Admin* admin) {
    while (true) {
        std::cout << "\n=================================\n";
        std::cout << "Welcome, " << admin->getName() << "!\n";
        std::cout << "Logged in as: Administrator\n";
        std::cout << "=================================\n\n";

        std::cout << "=== Relief Organization System ===\n";
        std::cout << "Organization: " << organization->getName() << "\n";
        std::cout << "User Details:\n";
        std::cout << "Name: " << admin->getName() << "\n";
        std::cout << "Phone: " << admin->getPhone() << "\n";
        std::cout << "Role: Administrator\n\n";

        std::cout << "Menu Options:\n";
        std::cout << "1. View\n";
        std::cout << "2. Monitor Organization\n";
        std::cout << "3. Logout\n";
        std::cout << "4. Exit\n";

        char choice;
        std::cin >> choice;
        std::cin.ignore(std::numeric_limits<std::streamsize>::max(), '\n');

        switch (choice) {
            case '1':
                showViewMenu(admin);
                break;
            case '2':
                showMonitorMenu(admin);
                break;
            case '3':
                return;
            case '4':
                exit(0);
            default:
                std::cout << "Invalid option. Please try again.\n";
                break;
        }
    }
}

void Menu::showViewMenu(Admin* admin) {
    while (true) {
        double materialTotal = 0;
        double serviceTotal = 0;
        auto currentDonations = organization->getCurrentDonations();

        for (const auto& donation : currentDonations->getRdEntities()) {
            if (dynamic_cast<Material*>(donation->getEntity())) {
                materialTotal += donation->getQuantity();
            } else if (dynamic_cast<Service*>(donation->getEntity())) {
                serviceTotal += donation->getQuantity();
            }
        }

        std::cout << "\nAvailable Categories:\n";
        std::cout << std::fixed << std::setprecision(1);
        std::cout << "1. Material (" << materialTotal << " items available)\n";
        std::cout << "2. Services (" << serviceTotal << " hours available)\n";
        std::cout << "3. Back\n";

        char choice;
        std::cin >> choice;
        std::cin.ignore(std::numeric_limits<std::streamsize>::max(), '\n');

        switch (choice) {
            case '1':
                showCategoryItemsForAdmin(admin, true);
                break;
            case '2':
                showCategoryItemsForAdmin(admin, false);
                break;
            case '3':
                return;
            default:
                std::cout << "Invalid option. Please try again.\n";
                break;
        }
    }
}

void Menu::showCategoryItemsForAdmin(Admin* admin, bool isMaterial) {
    while (true) {
        std::cout << "\nAvailable " << (isMaterial ? "Materials" : "Services") << ":\n";
        std::vector<Entity*> items;
        auto currentDonations = organization->getCurrentDonations();

        for (Entity* entity : organization->getEntityList()) {
            bool isEntityMaterial = dynamic_cast<Material*>(entity) != nullptr;
            if (isEntityMaterial == isMaterial) {
                items.push_back(entity);
                double currentQty = 0;
                try {
                    auto donation = currentDonations->get(entity->getId());
                    currentQty = donation->getQuantity();
                } catch (const EntityNotFoundException&) {}

                std::cout << items.size() << ". " << entity->getName();
                std::cout << std::fixed << std::setprecision(1);
                if (isMaterial) {
                    std::cout << " (Current quantity: " << currentQty << ")\n";
                } else {
                    std::cout << " (Current hours: " << currentQty << ")\n";
                }
            }
        }

        std::cout << (items.size() + 1) << ". Back\n\n";
        std::cout << "Select option: ";

        int choice;
        std::cin >> choice;
        std::cin.ignore(std::numeric_limits<std::streamsize>::max(), '\n');

        if (choice > 0 && choice <= static_cast<int>(items.size())) {
            Entity* selected = items[choice - 1];
            std::cout << "\n" << selected->toString() << "\n\n";
            std::cout << "Press Enter to continue...";
            std::cin.get();
        } else if (choice == items.size() + 1) {
            return;
        } else {
            std::cout << "Invalid option. Please try again.\n";
        }
    }
}

void Menu::showMonitorMenu(Admin* admin) {
    while (true) {
        std::cout << "\nMonitor Organization Options:\n";
        std::cout << "1. List Beneficiaries\n";
        std::cout << "2. List Donators\n";
        std::cout << "3. Reset All Beneficiaries Lists\n";
        std::cout << "4. Back\n";

        char choice;
        std::cin >> choice;
        std::cin.ignore(std::numeric_limits<std::streamsize>::max(), '\n');

        switch (choice) {
            case '1':
                showBeneficiariesList(admin);
                break;
            case '2':
                showDonatorsList(admin);
                break;
            case '3': {
                for (Beneficiary* beneficiary : organization->getBeneficiaryList()) {
                    beneficiary->resetReceived();
                }
                std::cout << "All beneficiaries' received lists have been reset.\n";
                break;
            }
            case '4':
                return;
            default:
                std::cout << "Invalid option. Please try again.\n";
                break;
        }
    }
}

void Menu::showBeneficiariesList(Admin* admin) {
    while (true) {
        const auto& beneficiaries = organization->getBeneficiaryList();
        if (beneficiaries.empty()) {
            std::cout << "No beneficiaries registered.\n";
            return;
        }

        std::cout << "\nBeneficiaries List:\n";
        for (size_t i = 0; i < beneficiaries.size(); i++) {
            std::cout << (i + 1) << ". " << beneficiaries[i]->getName()
                     << " (Phone: " << beneficiaries[i]->getPhone() << ")\n";
        }

        std::cout << (beneficiaries.size() + 1) << ". Back\n\n";
        std::cout << "Select beneficiary: ";

        int choice;
        std::cin >> choice;
        std::cin.ignore(std::numeric_limits<std::streamsize>::max(), '\n');

        if (choice > 0 && choice <= static_cast<int>(beneficiaries.size())) {
            showBeneficiaryDetails(admin, beneficiaries[choice - 1]);
        } else if (choice == beneficiaries.size() + 1) {
            return;
        } else {
            std::cout << "Invalid option. Please try again.\n";
        }
    }
}

void Menu::showBeneficiaryDetails(Admin* admin, Beneficiary* beneficiary) {
    while (true) {
        std::cout << "\nBeneficiary Details:\n";
        std::cout << "Name: " << beneficiary->getName() << "\n";
        std::cout << "Phone: " << beneficiary->getPhone() << "\n";
        std::cout << "Number of persons: " << beneficiary->getNoPersons() << "\n\n";

        std::cout << "Current Received Items:\n";
        beneficiary->monitorReceived();

        std::cout << "\nOptions:\n";
        std::cout << "1. Clear received list\n";
        std::cout << "2. Delete beneficiary\n";
        std::cout << "3. Back\n";

        char choice;
        std::cin >> choice;
        std::cin.ignore(std::numeric_limits<std::streamsize>::max(), '\n');

        switch (choice) {
            case '1': {
                beneficiary->resetReceived();
                std::cout << "Received list cleared for " << beneficiary->getName() << "\n";
                break;
            }
            case '2': {
                std::string phone = beneficiary->getPhone();
                std::cout << "Are you sure you want to delete " << beneficiary->getName() << "? (y/n): ";
                char confirm;
                std::cin >> confirm;
                std::cin.ignore(std::numeric_limits<std::streamsize>::max(), '\n');

                if (confirm == 'y' || confirm == 'Y') {
                    organization->removeBeneficiary(phone);
                    std::cout << "Beneficiary deleted successfully.\n";
                    return;
                }
                break;
            }
            case '3':
                return;
            default:
                std::cout << "Invalid option. Please try again.\n";
                break;
        }
    }
}

void Menu::showDonatorsList(Admin* admin) {
    while (true) {
        const auto& donators = organization->getDonatorList();
        if (donators.empty()) {
            std::cout << "No donators registered.\n";
            return;
        }

        std::cout << "\nDonators List:\n";
        for (size_t i = 0; i < donators.size(); i++) {
            std::cout << (i + 1) << ". " << donators[i]->getName()
                     << " (Phone: " << donators[i]->getPhone() << ")\n";
        }

        std::cout << (donators.size() + 1) << ". Back\n\n";
        std::cout << "Select donator: ";

        int choice;
        std::cin >> choice;
        std::cin.ignore(std::numeric_limits<std::streamsize>::max(), '\n');

        if (choice > 0 && choice <= static_cast<int>(donators.size())) {
            showDonatorDetails(admin, donators[choice - 1]);
        } else if (choice == donators.size() + 1) {
            return;
        } else {
            std::cout << "Invalid option. Please try again.\n";
        }
    }
}

void Menu::showDonatorDetails(Admin* admin, Donator* donator) {
    while (true) {
        std::cout << "\nDonator Details:\n";
        std::cout << "Name: " << donator->getName() << "\n";
        std::cout << "Phone: " << donator->getPhone() << "\n\n";

        std::cout << "Current Offers:\n";
        donator->monitor();

        std::cout << "\nOptions:\n";
        std::cout << "1. Delete donator\n";
        std::cout << "2. Back\n";

        char choice;
        std::cin >> choice;
        std::cin.ignore(std::numeric_limits<std::streamsize>::max(), '\n');

        switch (choice) {
            case '1': {
                std::string phone = donator->getPhone();
                std::cout << "Are you sure you want to delete " << donator->getName() << "? (y/n): ";
                char confirm;
                std::cin >> confirm;
                std::cin.ignore(std::numeric_limits<std::streamsize>::max(), '\n');

                if (confirm == 'y' || confirm == 'Y') {
                    organization->removeDonator(phone);
                    std::cout << "Donator deleted successfully.\n";
                    return;
                }
                break;
            }
            case '2':
                return;
            default:
                std::cout << "Invalid option. Please try again.\n";
                break;
        }
    }
}