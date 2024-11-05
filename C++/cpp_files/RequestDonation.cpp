#include "RequestDonation.h"

RequestDonation::RequestDonation(Entity* entity, double quantity)
    : entity(entity), quantity(quantity) {
}

RequestDonation::~RequestDonation() = default;

Entity* RequestDonation::getEntity() const {
    return entity;
}

double RequestDonation::getQuantity() const {
    return quantity;
}

void RequestDonation::setQuantity(double newQuantity) {
    quantity = newQuantity;
}

bool RequestDonation::operator==(const RequestDonation& other) const {
    return entity->getId() == other.entity->getId();
}

bool RequestDonation::operator<(const RequestDonation& other) const {
    return entity->getId() < other.entity->getId();
}