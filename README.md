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

3. **User Interaction Options**:

 - Initial login and register is handled via the command line menu:
   <div style="display: flex; flex-wrap: wrap; gap: 10px;">
         <img src="https://github.com/GrigorisTzortzakis/Volunteer-Donation-System/blob/main/Java/Pics/Initial_Login_Phone.png" alt="CLI - Selection Menu" width="300" height="300">
         <img src="https://github.com/GrigorisTzortzakis/Volunteer-Donation-System/blob/main/Java/Pics/User_Not_Found.png" alt="CLI - Admin Main Menu" width="300" height="300">
         <img src="https://github.com/GrigorisTzortzakis/Volunteer-Donation-System/blob/main/Java/Pics/Register.png" alt="CLI - Admin View" width="300" height="300">
        
   </div>
   
   - After a successfull login, the system can be handled via 2 options:

     - **Command Line Interface (CLI)**: Provides text-based interaction for users, allowing for quick navigation and management of tasks.
      <div style="display: flex; flex-wrap: wrap; gap: 10px;">
         <img src="https://github.com/GrigorisTzortzakis/Volunteer-Donation-System/blob/main/Java/Pics/Selection_Gui_Command_LIne.png" alt="CLI - Selection Menu" width="300" height="300">
         <img src="https://github.com/GrigorisTzortzakis/Volunteer-Donation-System/blob/main/Java/Pics/Admin_command_Main_Menu.png" alt="CLI - Admin Main Menu" width="300" height="300">
         <img src="https://github.com/GrigorisTzortzakis/Volunteer-Donation-System/blob/main/Java/Pics/Admin_View.png" alt="CLI - Admin View" width="300" height="300">
         <img src="https://github.com/GrigorisTzortzakis/Volunteer-Donation-System/blob/main/Java/Pics/Admin_View_2.png" alt="CLI - Admin View 2" width="300" height="300">
         <img src="https://github.com/GrigorisTzortzakis/Volunteer-Donation-System/blob/main/Java/Pics/Admin_Monitor.png" alt="CLI - Admin Monitor" width="300" height="300">
         <img src="https://github.com/GrigorisTzortzakis/Volunteer-Donation-System/blob/main/Java/Pics/Admin_Monitor_2.png" alt="CLI - Admin Monitor 2" width="300" height="300">
         <img src="https://github.com/GrigorisTzortzakis/Volunteer-Donation-System/blob/main/Java/Pics/Add_Request.png" alt="CLI - Add Request" width="300" height="300">
         <img src="https://github.com/GrigorisTzortzakis/Volunteer-Donation-System/blob/main/Java/Pics/Show_Offers.png" alt="CLI - Show Offers" width="300" height="300">
       </div>
      
   - **Graphical User Interface (GUI)**: A visual, user-friendly interface for managing donations and requests, particularly helpful for non-technical users.

<div style="display: flex; flex-wrap: wrap; gap: 15px; justify-content: center;">

   <img src="https://github.com/GrigorisTzortzakis/Volunteer-Donation-System/blob/main/Java/Pics/Admin_Gui_Main.png" alt="Admin GUI Main">
   <img src="https://github.com/GrigorisTzortzakis/Volunteer-Donation-System/blob/main/Java/Pics/Admin_Gui_View.png" alt="Admin GUI View">

   <img src="https://github.com/GrigorisTzortzakis/Volunteer-Donation-System/blob/main/Java/Pics/Admin_Gui_Manage.png" alt="Admin GUI Manage">
   <img src="https://github.com/GrigorisTzortzakis/Volunteer-Donation-System/blob/main/Java/Pics/Admin_Gui_Beneficiary_Manage.png" alt="Admin GUI Beneficiary Manage">

   <img src="https://github.com/GrigorisTzortzakis/Volunteer-Donation-System/blob/main/Java/Pics/Beneficiary_Gui_Main.png" alt="Beneficiary GUI Main">
   <img src="https://github.com/GrigorisTzortzakis/Volunteer-Donation-System/blob/main/Java/Pics/Beneficiary_Gui_Request.png" alt="Beneficiary GUI Request">

   <img src="https://github.com/GrigorisTzortzakis/Volunteer-Donation-System/blob/main/Java/Pics/Beneficiary_Gui_Modify.png" alt="Beneficiary GUI Modify">
   <img src="https://github.com/GrigorisTzortzakis/Volunteer-Donation-System/blob/main/Java/Pics/Beneficiary_Gui_View_Received.png" alt="Beneficiary GUI View Received">

   <img src="https://github.com/GrigorisTzortzakis/Volunteer-Donation-System/blob/main/Java/Pics/Beneficiary_Gui_Commit.png" alt="Beneficiary GUI Commit">
   <img src="https://github.com/GrigorisTzortzakis/Volunteer-Donation-System/blob/main/Java/Pics/Donator_Gui_Offer.png" alt="Donator GUI Offer">

   <img src="https://github.com/GrigorisTzortzakis/Volunteer-Donation-System/blob/main/Java/Pics/Donator_Gui_Offer_2.png" alt="Donator GUI Offer 2">
   <img src="https://github.com/GrigorisTzortzakis/Volunteer-Donation-System/blob/main/Java/Pics/Donator_Gui_View.png" alt="Donator GUI View">

   <img src="https://github.com/GrigorisTzortzakis/Volunteer-Donation-System/blob/main/Java/Pics/Donator_Gui_Commit.png" alt="Donator GUI Commit">

</div>

   - Both interfaces offer full functionality for user management, donation tracking, and request handling, ensuring ease of use and accessibility.

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

- **Exceptions**: Custom exceptions handle invalid actions, such as requesting more than the available quantity or attempting to add duplicate items.



## Instructions

### 1. Required Files
To execute this project, you need to ensure that relevant files are properly organized and accessible in the same directory. Intellij ide by Jetbrains was used for the java part and Clion by Jetbrains was used for the c++ part.

### 2. Running the Project
#### Java
1. Compile the `.java` files.
2. Execute the main application file.

#### C++
1. Compile the `.cpp` files with a compatible compiler.
2. Run the compiled executable to start the command-line menu.







