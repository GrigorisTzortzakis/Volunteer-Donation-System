#ifndef SERVICE_H
#define SERVICE_H

#include "Entity.h"

class Service : public Entity {
public:
    Service(const std::string& name, const std::string& description, int id);
    ~Service() override;

    // Override getDetails from Entity
    std::string getDetails() const override;
};

#endif // SERVICE_H