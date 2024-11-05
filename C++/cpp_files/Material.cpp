#include "Material.h"

Material::Material(const std::string& name, const std::string& description, int id,
                 double level1, double level2, double level3)
    : Entity(name, description, id),
      level1(level1),
      level2(level2),
      level3(level3) {
}

Material::~Material() = default;

double Material::getLevel1() const {
    return level1;
}

double Material::getLevel2() const {
    return level2;
}

double Material::getLevel3() const {
    return level3;
}

std::string Material::getDetails() const {
    return "Type: Material\n"
           "Level 1 (1 person): " + std::to_string(level1) + "\n"
           "Level 2 (2-4 persons): " + std::to_string(level2) + "\n"
           "Level 3 (5+ persons): " + std::to_string(level3);
}