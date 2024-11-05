// Menu.h
#ifndef MENU_H
#define MENU_H

#include <string>

// Forward declarations
class Organization;
class Donator;
class Beneficiary;  // Add this forward declaration
class RequestDonation;
class Entity;
class Admin;

class Menu {
private:
    Organization* organization;

    // Helper methods for the initial menu
    std::string getPhoneNumber();
    void handleNewUser(const std::string& phone);
    void registerNewUser(const std::string& phone, char userType);

    // Donator menu methods
private:
    void donatorMenu(Donator* donator);
    void showAddOfferMenu(Donator* donator);
    void showCategoryItems(Donator* donator, bool isMaterial);
    void showOffersMenu(Donator* donator);
    void showOfferDetails(Donator* donator, RequestDonation* offer);
    bool handleCommit(Donator* donator);

    //Beneficiary menu methods
private:
    void beneficiaryMenu(Beneficiary* beneficiary);
    void showAddRequestMenu(Beneficiary* beneficiary);
    void showCategoryItemsForRequest(Beneficiary* beneficiary, bool isMaterial);
    void showRequestsMenu(Beneficiary* beneficiary);
    void showRequestDetails(Beneficiary* beneficiary, RequestDonation* request);
    bool handleBeneficiaryCommit(Beneficiary* beneficiary);

private:
    void adminMenu(Admin* admin);
    void showViewMenu(Admin* admin);
    void showCategoryItemsForAdmin(Admin* admin, bool isMaterial);
    void showMonitorMenu(Admin* admin);
    void showBeneficiariesList(Admin* admin);
    void showBeneficiaryDetails(Admin* admin, Beneficiary* beneficiary);
    void showDonatorsList(Admin* admin);
    void showDonatorDetails(Admin* admin, Donator* donator);

public:
    explicit Menu(Organization* org);
    ~Menu();
    void start();
};

#endif // MENU_H