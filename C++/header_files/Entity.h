#ifndef ENTITY_H
#define ENTITY_H

#include <string>

class Entity {
protected:
    std::string name;
    std::string description;
    int id;

    // Protected constructor to prevent instantiation
    Entity(std::string name, std::string description, int id);

public:
    // Pure virtual destructor to make class abstract
    virtual ~Entity() = 0;

    // Getters
    std::string getName() const;
    std::string getDescription() const;
    int getId() const;

    // Required methods
    std::string getEntityInfo() const;
    virtual std::string getDetails() const = 0;  // Pure virtual
    std::string toString() const;  // Will call getEntityInfo and getDetails
};

#endif // ENTITY_H