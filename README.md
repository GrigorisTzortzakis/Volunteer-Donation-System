# Volunteer Donation System - Object-Oriented Programming Project

## Overview

This project, created for the **UOP CEID department**, is a simplified **Volunteer-Donor Organization System** designed for managing donations and requests from beneficiaries. The purpose is to support vulnerable groups and economically disadvantaged families by organizing donations of items and services.

Developed as part of the **Object-Oriented Programming course** in the **2020-2021 academic year**, this project applies key OOP concepts and is implemented in both **Java** and **C++**.

## Project Structure

### Key Components:
1. **User Management**:
   - Different roles include **Admin**, **Donator**, and **Beneficiary**.
   - The **Admin** manages items and services provided by the organization.
   - **Donators** offer items or services from the available list.
   - **Beneficiaries** can request items or services, with quantity limits based on family size.

2. **Donation and Request Management**:
   - **Material** items (e.g., food supplies) with quantity limits.
   - **Service** items (e.g., medical, nursing) with no quantity restrictions.
   - Beneficiaries have a record of received items which the **Admin** can reset.

### Implemented Classes:
- **User**: Abstract class for all users with subclasses:
  - **Admin**: Manages the system with additional privileges.
  - **Donator**: Manages a list of items they wish to donate.
  - **Beneficiary**: Requests items with restrictions based on family size.

- **Entity**: Abstract class representing an item (either Material or Service).
  - **Material**: Has specific quantity levels based on family size.
  - **Service**: Has no quantity restrictions.

- **RequestDonation**: Represents a specific quantity of an item or service.

- **Organization**: Manages lists of users and items, tracks current donations, and provides methods for user management and item distribution.

- **RequestDonationList**: Manages a collection of `RequestDonation` objects, providing methods to add, modify, and monitor requests or offers.

- **Requests**: Subclass of `RequestDonationList` for items requested by beneficiaries, with methods to check quantity eligibility.

- **Offers**: Subclass of `RequestDonationList` for items offered by donators.

- **Menu**: Provides a command-line interface for user interactions.

### Exception Handling
Custom exceptions handle invalid actions, such as requesting more than the available quantity, exceeding allowed request limits, or attempting to add duplicate items.

## Technologies Used
- **Java** and **C++**: Both languages are used to implement the system, demonstrating flexibility in OOP design and language-specific features.

## Instructions

### 1. Required Files
To execute this project, you need to ensure that relevant files are properly organized and accessible. Specific files for configuration or dataset loading are necessary for running the project in Java or C++.

### 2. Running the Project
#### Java
1. Compile the `.java` files.
2. Execute the main application file.

#### C++
1. Compile the `.cpp` files with a compatible compiler.
2. Run the compiled executable to start the command-line menu.

### 3. Additional Resources
For more information on the project requirements and implementation:
- View the project instructions and guidelines in the accompanying documentation.

---

## Project Requirements and Evaluation Criteria

This project will be evaluated on the following criteria:
1. **OOP Principles**: Proper class structure, encapsulation, method overriding, and polymorphism.
2. **Code Quality**: Readability, maintainability, and comments for clarity.
3. **Functionality**: Complete and correct implementation of features such as user role management, donation processing, and error handling.
4. **Documentation**: Adequate in-code comments and README for understanding the project setup and usage.

---

## Deliverables

1. **Source Code**: Complete source code in Java and C++.
2. **Documentation**: README and additional documents explaining class design and implementation.
3. **UML Class Diagram**: Visual representation of the class hierarchy and relationships.
4. **Execution Guide**: Step-by-step instructions for compiling and running the project.

---

## License

This project is developed for educational purposes within the **University of Patras** and may have restrictions on external sharing.

---

## Acknowledgements

Developed for the **UOP CEID department** as part of the **Object-Oriented Programming course**.

---

