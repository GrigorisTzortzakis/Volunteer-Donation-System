#ifndef REQUESTDONATION_H
#define REQUESTDONATION_H

#include "Entity.h"

class RequestDonation {
private:
    Entity* entity;
    double quantity;

public:
    RequestDonation(Entity* entity, double quantity);
    ~RequestDonation();

    // Getters
    Entity* getEntity() const;
    double getQuantity() const;
    
    // Setter for quantity
    void setQuantity(double newQuantity);

    // Comparison operators based on entity id
    bool operator==(const RequestDonation& other) const;
    bool operator<(const RequestDonation& other) const;
};

#endif // REQUESTDONATION_H