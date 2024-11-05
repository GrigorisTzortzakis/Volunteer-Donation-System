#include "Requests.h"
#include "Beneficiary.h"  // Include full header here for implementation details
#include "exceptions.h"
#include "Organization.h"
#include <iostream>


Requests::Requests() : beneficiary(nullptr) {
}

Requests::~Requests() = default;

void Requests::setBeneficiary(Beneficiary* ben) {
    beneficiary = ben;
}

bool Requests::isQuantityAvailable(int entityId, double quantity) const {
    try {
        RequestDonation* orgRd = getOrganization()->getCurrentDonations()->get(entityId);
        return orgRd->getQuantity() >= quantity;
    } catch (const EntityNotFoundException&) {
        return false;
    }
}

bool Requests::validRequestDonation(RequestDonation* rd) const {
    if (!beneficiary) {
        throw std::runtime_error("Beneficiary not set");
    }

    Entity* entity = rd->getEntity();
    double requestedQuantity = rd->getQuantity();

    // For Services, always valid
    if (dynamic_cast<Service*>(entity) != nullptr) {
        return true;
    }

    // For Materials, check levels
    Material* material = dynamic_cast<Material*>(entity);
    if (!material) {
        throw std::runtime_error("Invalid entity type");
    }

    // Determine level based on number of persons
    int persons = beneficiary->getNoPersons();
    double levelLimit;

    if (persons == 1) {
        levelLimit = material->getLevel1();
    } else if (persons >= 2 && persons <= 4) {
        levelLimit = material->getLevel2();
    } else { // persons >= 5
        levelLimit = material->getLevel3();
    }

    // Calculate total quantity (already received + requested)
    double totalQuantity = requestedQuantity;
    try {
        RequestDonation* receivedRd = beneficiary->getReceivedList()->get(entity->getId());
        totalQuantity += receivedRd->getQuantity();
    } catch (const EntityNotFoundException&) {
        // No previous donations of this entity, continue with just requested quantity
    }

    return totalQuantity <= levelLimit;
}

void Requests::add(int entityId, double quantity) {
    // First check if quantity is available in organization
    if (!isQuantityAvailable(entityId, quantity)) {
        throw InsufficientQuantityException("Requested quantity not available in organization");
    }

    // Call parent's add to create/update RequestDonation
    RequestDonationList::add(entityId, quantity);

    // Validate the request
    RequestDonation* rd = get(entityId);
    if (!validRequestDonation(rd)) {
        // Remove the just added request and throw exception
        remove(entityId);
        throw InvalidRequestException("Request exceeds beneficiary's level limit");
    }
}

void Requests::modify(int entityId, double quantity) {
    // First check if quantity is available in organization
    if (!isQuantityAvailable(entityId, quantity)) {
        throw InsufficientQuantityException("Requested quantity not available in organization");
    }

    // Temporarily store old quantity in case we need to revert
    double oldQuantity = get(entityId)->getQuantity();

    // Call parent's modify
    RequestDonationList::modify(entityId, quantity);

    // Validate the modified request
    RequestDonation* rd = get(entityId);
    if (!validRequestDonation(rd)) {
        // Revert to old quantity and throw exception
        RequestDonationList::modify(entityId, oldQuantity);
        throw InvalidRequestException("Modified request exceeds beneficiary's level limit");
    }
}

void Requests::commit() {
    if (!beneficiary || !getOrganization()) {
        throw std::runtime_error("Beneficiary or Organization not set");
    }

    std::vector<int> successfulRequests;
    RequestDonationList* currentDonations = getOrganization()->getCurrentDonationsList();

    for (RequestDonation* rd : getRdEntities()) {
        int entityId = rd->getEntity()->getId();
        double quantity = rd->getQuantity();

        if (!isQuantityAvailable(entityId, quantity)) {
            std::cout << "Request for " << rd->getEntity()->getName()
                     << " failed: Insufficient quantity available\n";
            continue;
        }

        if (!validRequestDonation(rd)) {
            std::cout << "Request for " << rd->getEntity()->getName()
                     << " failed: Exceeds level limit\n";
            continue;
        }

        try {
            double currentQuantity = currentDonations->get(entityId)->getQuantity();
            currentDonations->modify(entityId, currentQuantity - quantity);
            beneficiary->addReceived(entityId, quantity);
            successfulRequests.push_back(entityId);

            std::cout << "Request for " << rd->getEntity()->getName()
                     << " successfully processed\n";
        } catch (const std::exception& e) {
            std::cout << "Error processing request for " << rd->getEntity()->getName()
                     << ": " << e.what() << "\n";
        }
    }

    for (int entityId : successfulRequests) {
        remove(entityId);
    }
}