// User.h
#ifndef USER_H
#define USER_H

#include <string>

class User {
protected:
    std::string name;
    std::string phone;

    // Protected constructor to prevent direct instantiation
    User(const std::string& name, const std::string& phone);

public:
    // Pure virtual destructor to make class abstract
    virtual ~User() = 0;

    // Add getters for name and phone
    std::string getName() const { return name; }
    std::string getPhone() const { return phone; }
};

#endif // USER_H