#ifndef ORGANIZATION_H
#define ORGANIZATION_H

#include <string>
#include <vector>
#include <memory>
#include "Admin.h"
#include "Entity.h"
#include "Material.h"
#include "Service.h"
#include "Donator.h"
#include "Beneficiary.h"
#include "RequestDonationList.h"

class Organization {
private:
    std::string name;
    Admin* admin;
    std::vector<Entity*> entityList;
    std::vector<Donator*> donatorList;
    std::vector<Beneficiary*> beneficiaryList;
    RequestDonationList* currentDonations;

    // Helper method to find entity by ID
    Entity* findEntity(int entityId) const;
    // Helper method to find donator by phone

public:
    explicit Organization(const std::string& name);
    ~Organization();

    Donator* findDonator(const std::string& phone) const;
    // Helper method to find beneficiary by phone
    Beneficiary* findBeneficiary(const std::string& phone) const;


    // Admin management
    void setAdmin(Admin* newAdmin);
    Admin* getAdmin() const;

    // Entity management
    void addEntity(Entity* entity);  // Throws if entity already exists
    void removeEntity(int entityId); // Only admin can do this
    const std::vector<Entity*>& getEntityList() const { return entityList; }
    Entity* getEntityById(int entityId) const; // Added public method to get entity

    // Donator management
    void insertDonator(Donator* donator);    // Throws if donator already exists
    void removeDonator(const std::string& phone);
    const std::vector<Donator*>& getDonatorList() const { return donatorList; }

    // Beneficiary management
    void insertBeneficiary(Beneficiary* beneficiary);  // Throws if beneficiary already exists
    void removeBeneficiary(const std::string& phone);
    const std::vector<Beneficiary*>& getBeneficiaryList() const { return beneficiaryList; }

    // Listing methods
    void listEntities() const;
    void listBeneficiaries() const;
    void listDonators() const;

    // RequestDonation management
    RequestDonation* getRequestDonation(int entityId) const;
    const RequestDonationList* getCurrentDonations() const { return currentDonations; }
RequestDonationList* getCurrentDonationsList() { return currentDonations; }

    // Wrapper methods for currentDonations
    void addCurrentDonation(int entityId, double quantity);
    void removeCurrentDonation(int entityId);
    void updateCurrentDonation(int entityId, double quantity);
    void resetCurrentDonations();
    void monitorCurrentDonations() const;

    // Getter for name
    std::string getName() const { return name; }
};

#endif // ORGANIZATION_H