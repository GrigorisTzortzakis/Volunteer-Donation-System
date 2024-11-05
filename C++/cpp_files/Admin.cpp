#include "Admin.h"

Admin::Admin(const std::string& name, const std::string& phone)
    : User(name, phone), isAdmin(true) {
}

Admin::~Admin() = default;