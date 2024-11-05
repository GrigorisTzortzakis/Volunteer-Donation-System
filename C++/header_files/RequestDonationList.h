#ifndef REQUESTDONATIONLIST_H
#define REQUESTDONATIONLIST_H

#include <vector>
#include "RequestDonation.h"
#include "Entity.h"

class Organization; // Forward declaration

class RequestDonationList {
protected:
    std::vector<RequestDonation*> rdEntities;
    Organization* organization;  // Reference to organization for entity validation

    // Helper method to find RequestDonation by entity ID
    RequestDonation* findRequestDonation(int entityId) const;

public:
    explicit RequestDonationList();
    virtual ~RequestDonationList();

    // Main operations
    RequestDonation* get(int entityId) const;
    virtual void add(int entityId, double quantity);  // Made virtual for derived classes
    virtual void remove(int entityId);
    virtual void modify(int entityId, double quantity);
    virtual void monitor() const;
    virtual void reset();

    // Getters and setters
    void setOrganization(Organization* org);
    Organization* getOrganization() const;
    const std::vector<RequestDonation*>& getRdEntities() const;
};

#endif // REQUESTDONATIONLIST_H