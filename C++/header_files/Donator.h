#ifndef DONATOR_H
#define DONATOR_H

#include "User.h"

// Forward declaration
class Offers;

class Donator : public User {
private:
    Offers* offersList;

public:
    Donator(const std::string& name, const std::string& phone);
    ~Donator() override;

    // Add getter for offersList
    Offers* getOffersList() const { return offersList; }

    // Wrapper methods for Offers class
    void add(int entityId, double quantity);
    void remove(int entityId);
    void commit();
    void monitor() const;
    void reset();
};

#endif // DONATOR_H