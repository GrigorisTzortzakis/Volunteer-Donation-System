#include "Organization.h"
#include <iostream>
#include <algorithm>
#include "RequestDonationList.h"
#include "exceptions.h"
#include "Offers.h"
#include "Requests.h"

Organization::Organization(const std::string& name)
    : name(name), admin(nullptr), currentDonations(new RequestDonationList()) {
    currentDonations->setOrganization(this);
}

Organization::~Organization() {
    delete admin;
    delete currentDonations;

    // Clean up entity list
    for (Entity* entity : entityList) {
        delete entity;
    }

    // Clean up donator list
    for (Donator* donator : donatorList) {
        delete donator;
    }

    // Clean up beneficiary list
    for (Beneficiary* beneficiary : beneficiaryList) {
        delete beneficiary;
    }
}

Entity* Organization::findEntity(int entityId) const {
    auto it = std::find_if(entityList.begin(), entityList.end(),
        [entityId](const Entity* e) { return e->getId() == entityId; });
    return it != entityList.end() ? *it : nullptr;
}

Donator* Organization::findDonator(const std::string& phone) const {
    auto it = std::find_if(donatorList.begin(), donatorList.end(),
        [&phone](const Donator* d) { return d->getPhone() == phone; });
    return it != donatorList.end() ? *it : nullptr;
}

Beneficiary* Organization::findBeneficiary(const std::string& phone) const {
    auto it = std::find_if(beneficiaryList.begin(), beneficiaryList.end(),
        [&phone](const Beneficiary* b) { return b->getPhone() == phone; });
    return it != beneficiaryList.end() ? *it : nullptr;
}

Entity* Organization::getEntityById(int entityId) const {
    Entity* entity = findEntity(entityId);
    if (!entity) {
        throw EntityNotFoundException("Entity with ID " + std::to_string(entityId) + " not found");
    }
    return entity;
}

void Organization::setAdmin(Admin* newAdmin) {
    delete admin;
    admin = newAdmin;
}

Admin* Organization::getAdmin() const {
    return admin;
}

void Organization::addEntity(Entity* entity) {
    if (findEntity(entity->getId()) != nullptr) {
        throw EntityExistsException("Entity with ID " + std::to_string(entity->getId()) + " already exists");
    }
    entityList.push_back(entity);
}

void Organization::removeEntity(int entityId) {
    Entity* entity = findEntity(entityId);
    if (entity != nullptr) {
        entityList.erase(std::remove(entityList.begin(), entityList.end(), entity), entityList.end());
        delete entity;
    }
}

void Organization::insertDonator(Donator* donator) {
    if (findDonator(donator->getPhone()) != nullptr) {
        throw UserExistsException("Donator with phone " + donator->getPhone() + " already exists");
    }
    donatorList.push_back(donator);

    if (auto* offersList = dynamic_cast<RequestDonationList*>(donator->getOffersList())) {
        offersList->setOrganization(this);
    }
}

void Organization::removeDonator(const std::string& phone) {
    Donator* donator = findDonator(phone);
    if (donator != nullptr) {
        donatorList.erase(std::remove(donatorList.begin(), donatorList.end(), donator), donatorList.end());
        delete donator;
    }
}

void Organization::insertBeneficiary(Beneficiary* beneficiary) {
    if (findBeneficiary(beneficiary->getPhone()) != nullptr) {
        throw UserExistsException("Beneficiary with phone " + beneficiary->getPhone() + " already exists");
    }
    beneficiaryList.push_back(beneficiary);

    beneficiary->getReceivedList()->setOrganization(this);
    if (auto* requestsList = dynamic_cast<RequestDonationList*>(beneficiary->getRequestsList())) {
        requestsList->setOrganization(this);
    }
}

void Organization::removeBeneficiary(const std::string& phone) {
    Beneficiary* beneficiary = findBeneficiary(phone);
    if (beneficiary != nullptr) {
        beneficiaryList.erase(std::remove(beneficiaryList.begin(), beneficiaryList.end(), beneficiary),
                             beneficiaryList.end());
        delete beneficiary;
    }
}

void Organization::listEntities() const {
    std::cout << "\nMaterials:\n";
    for (const Entity* entity : entityList) {
        if (dynamic_cast<const Material*>(entity) != nullptr) {
            std::cout << entity->toString() << "\n\n";
        }
    }

    std::cout << "\nServices:\n";
    for (const Entity* entity : entityList) {
        if (dynamic_cast<const Service*>(entity) != nullptr) {
            std::cout << entity->toString() << "\n\n";
        }
    }
}

void Organization::listBeneficiaries() const {
    std::cout << "\nBeneficiaries:\n";
    for (const Beneficiary* beneficiary : beneficiaryList) {
        std::cout << "Name: " << beneficiary->getName() << "\n";
        std::cout << "Phone: " << beneficiary->getPhone() << "\n";
        std::cout << "Number of persons: " << beneficiary->getNoPersons() << "\n";
        std::cout << "Received items:\n";
        beneficiary->monitorReceived();
        std::cout << "\n";
    }
}

void Organization::listDonators() const {
    std::cout << "\nDonators:\n";
    for (const Donator* donator : donatorList) {
        std::cout << "Name: " << donator->getName() << "\n";
        std::cout << "Phone: " << donator->getPhone() << "\n\n";
    }
}

RequestDonation* Organization::getRequestDonation(int entityId) const {
    return currentDonations->get(entityId);
}

void Organization::addCurrentDonation(int entityId, double quantity) {
    currentDonations->add(entityId, quantity);
}

void Organization::removeCurrentDonation(int entityId) {
    currentDonations->remove(entityId);
}

void Organization::updateCurrentDonation(int entityId, double quantity) {
    currentDonations->modify(entityId, quantity);
}

void Organization::resetCurrentDonations() {
    currentDonations->reset();
}

void Organization::monitorCurrentDonations() const {
    currentDonations->monitor();
}