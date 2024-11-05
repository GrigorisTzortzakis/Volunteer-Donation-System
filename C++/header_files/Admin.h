#ifndef ADMIN_H
#define ADMIN_H

#include "User.h"

class Admin : public User {
private:
    bool isAdmin;

public:
    Admin(const std::string& name, const std::string& phone);
    ~Admin() override;
};

#endif // ADMIN_H