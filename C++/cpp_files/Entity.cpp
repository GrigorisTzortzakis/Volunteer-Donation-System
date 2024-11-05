#include "Entity.h"

Entity::Entity(std::string name, std::string description, int id)
    : name(std::move(name)), description(std::move(description)), id(id) {
}

Entity::~Entity() = default;

std::string Entity::getName() const {
    return name;
}

std::string Entity::getDescription() const {
    return description;
}

int Entity::getId() const {
    return id;
}

std::string Entity::getEntityInfo() const {
    return "ID: " + std::to_string(id) + "\nName: " + name + "\nDescription: " + description;
}

std::string Entity::toString() const {
    return getEntityInfo() + "\n" + getDetails();
}