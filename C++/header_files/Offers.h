// Offers.h
#ifndef OFFERS_H
#define OFFERS_H

#include "RequestDonationList.h"
#include "Organization.h"

class Donator;  // Forward declaration

class Offers : public RequestDonationList {
private:
    Donator* donator;

public:
    Offers();
    ~Offers() override;

    void setDonator(Donator* don);
    void commit();  // The only required additional method
};

#endif // OFFERS_H