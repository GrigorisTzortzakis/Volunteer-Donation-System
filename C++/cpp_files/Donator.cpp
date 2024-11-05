#include "Donator.h"
#include "Offers.h" // This will be created later

Donator::Donator(const std::string& name, const std::string& phone)
    : User(name, phone), offersList(new Offers()) {
}

Donator::~Donator() {
    delete offersList;
}

void Donator::add(int entityId, double quantity) {
    offersList->add(entityId, quantity);
}

void Donator::remove(int entityId) {
    offersList->remove(entityId);
}

void Donator::commit() {
    offersList->commit();
}

void Donator::monitor() const {
    offersList->monitor();
}

void Donator::reset() {
    offersList->reset();
}