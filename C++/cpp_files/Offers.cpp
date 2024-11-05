// Offers.cpp
#include "Offers.h"
#include "exceptions.h"
#include "Organization.h"
#include <iostream>

Offers::Offers() : donator(nullptr) {
}

Offers::~Offers() = default;

void Offers::setDonator(Donator* don) {
    donator = don;
}

void Offers::commit() {
    if (rdEntities.empty()) {
        return;  // Nothing to commit
    }

    try {
        // Update or add each offer to currentDonations
        for (const auto& rd : rdEntities) {
            auto entityId = rd->getEntity()->getId();
            auto quantity = rd->getQuantity();

            try {
                // Try to get current quantity and update it
                RequestDonation* currentRd = organization->getRequestDonation(entityId);
                if (currentRd) {
                    double newQuantity = currentRd->getQuantity() + quantity;
                    organization->updateCurrentDonation(entityId, newQuantity);
                } else {
                    organization->addCurrentDonation(entityId, quantity);
                }
            } catch (const EntityNotFoundException&) {
                // If entity not found, add as new donation
                organization->addCurrentDonation(entityId, quantity);
            }
        }

        // Clear the list after successful commit
        reset();
    } catch (const std::exception& e) {
        throw CommitFailedException("Failed to commit offers: " + std::string(e.what()));
    }
}