#ifndef REQUESTS_H
#define REQUESTS_H

#include "RequestDonationList.h"
#include "Material.h"
#include "Service.h"
#include "Organization.h"

// Forward declaration to resolve circular dependency
class Beneficiary;

class Requests : public RequestDonationList {
private:
    Beneficiary* beneficiary;

    bool isQuantityAvailable(int entityId, double quantity) const;

public:
    Requests();
    ~Requests() override;

    void setBeneficiary(Beneficiary* ben);

    void add(int entityId, double quantity) override;
    void modify(int entityId, double quantity) override;

    bool validRequestDonation(RequestDonation* rd) const;
    void commit();
};

#endif // REQUESTS_H
