// User.cpp
#include "User.h"

User::User(const std::string& name, const std::string& phone)
    : name(name), phone(phone) {
}

User::~User() = default;