#ifndef BENEFICIARY_H
#define BENEFICIARY_H

#include "User.h"
#include "RequestDonationList.h"

// Forward declaration
class Requests;

class Beneficiary : public User {
private:
    int noPersons;
    RequestDonationList* receivedList;
    Requests* requestsList;

public:
    Beneficiary(const std::string& name, const std::string& phone, int noPersons = 1);
    ~Beneficiary() override;

    int getNoPersons() const;
    void setNoPersons(int number);
    RequestDonationList* getReceivedList() const { return receivedList; }
    Requests* getRequestsList() const { return requestsList; }

    void addReceived(int entityId, double quantity);
    void removeReceived(int entityId);
    void monitorReceived() const;
    void resetReceived();

    void addRequest(int entityId, double quantity);
    void removeRequest(int entityId);
    void commitRequests();
    void monitorRequests() const;
    void resetRequests();
};

#endif // BENEFICIARY_H
