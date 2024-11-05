#include "Service.h"

Service::Service(const std::string& name, const std::string& description, int id)
    : Entity(name, description, id) {
}

Service::~Service() = default;

std::string Service::getDetails() const {
    return "Type: Service";
}