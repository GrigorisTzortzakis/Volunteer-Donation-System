#ifndef MATERIAL_H
#define MATERIAL_H

#include "Entity.h"

class Material : public Entity {
private:
    const double level1;  // quantity for 1 person
    const double level2;  // quantity for 2-4 persons
    const double level3;  // quantity for 5+ persons

public:
    Material(const std::string& name, const std::string& description, int id,
            double level1, double level2, double level3);
    ~Material() override;

    // Getters for levels (since they're const)
    double getLevel1() const;
    double getLevel2() const;
    double getLevel3() const;

    // Override getDetails from Entity
    std::string getDetails() const override;
};

#endif // MATERIAL_H