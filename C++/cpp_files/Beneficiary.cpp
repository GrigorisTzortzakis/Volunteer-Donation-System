#include "Beneficiary.h"
#include "RequestDonationList.h" // Will be created later
#include "Requests.h" // Will be created later

Beneficiary::Beneficiary(const std::string& name, const std::string& phone, int noPersons)
    : User(name, phone), 
      noPersons(noPersons), 
      receivedList(new RequestDonationList()),
      requestsList(new Requests()) {
    requestsList->setBeneficiary(this);  // Set the beneficiary reference
}

Beneficiary::~Beneficiary() {
    delete receivedList;
    delete requestsList;
}

int Beneficiary::getNoPersons() const {
    return noPersons;
}

void Beneficiary::setNoPersons(int number) {
    noPersons = number;
}

// Wrapper methods for receivedList
void Beneficiary::addReceived(int entityId, double quantity) {
    receivedList->add(entityId, quantity);
}

void Beneficiary::removeReceived(int entityId) {
    receivedList->remove(entityId);
}

void Beneficiary::monitorReceived() const {
    receivedList->monitor();
}

void Beneficiary::resetReceived() {
    receivedList->reset();
}

// Wrapper methods for requestsList
void Beneficiary::addRequest(int entityId, double quantity) {
    requestsList->add(entityId, quantity);
}

void Beneficiary::removeRequest(int entityId) {
    requestsList->remove(entityId);
}

void Beneficiary::commitRequests() {
    requestsList->commit();
}

void Beneficiary::monitorRequests() const {
    requestsList->monitor();
}

void Beneficiary::resetRequests() {
    requestsList->reset();
}