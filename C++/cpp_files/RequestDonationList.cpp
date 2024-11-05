#include "RequestDonationList.h"
#include <iostream>
#include "exceptions.h"
#include <algorithm>
#include "Organization.h"

RequestDonationList::RequestDonationList() : organization(nullptr) {
}

RequestDonationList::~RequestDonationList() {
    reset();  // Clean up all RequestDonation objects
}

RequestDonation* RequestDonationList::findRequestDonation(int entityId) const {
    auto it = std::find_if(rdEntities.begin(), rdEntities.end(),
        [entityId](const RequestDonation* rd) {
            return rd->getEntity()->getId() == entityId;
        });
    return it != rdEntities.end() ? *it : nullptr;
}

void RequestDonationList::setOrganization(Organization* org) {
    organization = org;
}

Organization* RequestDonationList::getOrganization() const {
    return organization;
}

RequestDonation* RequestDonationList::get(int entityId) const {
    RequestDonation* rd = findRequestDonation(entityId);
    if (!rd) {
        throw EntityNotFoundException("Entity with ID " + std::to_string(entityId) + " not found in request list");
    }
    return rd;
}

void RequestDonationList::add(int entityId, double quantity) {
    if (!organization) {
        throw std::runtime_error("Organization not set");
    }

    if (quantity <= 0) {
        throw InvalidQuantityException("Quantity must be positive");
    }

    // Get entity through Organization's public interface
    Entity* entity = nullptr;
    const auto& entities = organization->getEntityList();
    for (Entity* e : entities) {
        if (e->getId() == entityId) {
            entity = e;
            break;
        }
    }

    if (!entity) {
        throw EntityNotFoundException("Entity with ID " + std::to_string(entityId) + " not found in organization");
    }

    RequestDonation* existingRd = findRequestDonation(entityId);
    if (existingRd) {
        // Update existing RequestDonation
        existingRd->setQuantity(existingRd->getQuantity() + quantity);
    } else {
        // Create new RequestDonation
        rdEntities.push_back(new RequestDonation(entity, quantity));
    }
}

void RequestDonationList::remove(int entityId) {
    auto it = std::find_if(rdEntities.begin(), rdEntities.end(),
        [entityId](const RequestDonation* rd) {
            return rd->getEntity()->getId() == entityId;
        });

    if (it != rdEntities.end()) {
        delete *it;
        rdEntities.erase(it);
    } else {
        throw EntityNotFoundException("Entity with ID " + std::to_string(entityId) + " not found in request list");
    }
}

void RequestDonationList::modify(int entityId, double quantity) {
    if (quantity <= 0) {
        throw InvalidQuantityException("Quantity must be positive");
    }

    RequestDonation* rd = findRequestDonation(entityId);
    if (!rd) {
        throw EntityNotFoundException("Entity with ID " + std::to_string(entityId) + " not found in request list");
    }

    rd->setQuantity(quantity);
}

void RequestDonationList::monitor() const {
    if (rdEntities.empty()) {
        std::cout << "No donations in the list.\n";
        return;
    }

    std::cout << "Current Donations:\n";
    for (const RequestDonation* rd : rdEntities) {
        Entity* entity = rd->getEntity();
        std::cout << "Entity: " << entity->getName()
                 << " (ID: " << entity->getId() << ")"
                 << " - Quantity: " << rd->getQuantity() << "\n";
    }
}

void RequestDonationList::reset() {
    for (RequestDonation* rd : rdEntities) {
        delete rd;
    }
    rdEntities.clear();
}

const std::vector<RequestDonation*>& RequestDonationList::getRdEntities() const {
    return rdEntities;
}