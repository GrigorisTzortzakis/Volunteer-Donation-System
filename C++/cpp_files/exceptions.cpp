// exceptions.cpp
#include "exceptions.h"

// Base exception
DonationSystemException::DonationSystemException(const std::string& message)
    : std::runtime_error(message) {
}

// Entity exceptions
EntityException::EntityException(const std::string& message)
    : DonationSystemException(message) {
}

EntityExistsException::EntityExistsException(const std::string& message)
    : EntityException(message) {
}

EntityNotFoundException::EntityNotFoundException(const std::string& message)
    : EntityException(message) {
}

DuplicateEntityIdException::DuplicateEntityIdException(const std::string& message)
    : EntityException(message) {
}

// Quantity exceptions
QuantityException::QuantityException(const std::string& message)
    : DonationSystemException(message) {
}

InvalidQuantityException::InvalidQuantityException(const std::string& message)
    : QuantityException(message) {
}

InsufficientQuantityException::InsufficientQuantityException(const std::string& message)
    : QuantityException(message) {
}

QuantityLimitException::QuantityLimitException(const std::string& message)
    : QuantityException(message) {
}

// User exceptions
UserException::UserException(const std::string& message)
    : DonationSystemException(message) {
}

UserExistsException::UserExistsException(const std::string& message)
    : UserException(message) {
}

UserNotFoundException::UserNotFoundException(const std::string& message)
    : UserException(message) {
}

UnauthorizedAccessException::UnauthorizedAccessException(const std::string& message)
    : UserException(message) {
}

// Request/Offer exceptions
RequestException::RequestException(const std::string& message)
    : DonationSystemException(message) {
}

InvalidRequestException::InvalidRequestException(const std::string& message)
    : RequestException(message) {
}

CommitFailedException::CommitFailedException(const std::string& message)
    : RequestException(message) {
}

// Menu exceptions
MenuException::MenuException(const std::string& message)
    : DonationSystemException(message) {
}

InvalidMenuOptionException::InvalidMenuOptionException(const std::string& message)
    : MenuException(message) {
}

InvalidInputException::InvalidInputException(const std::string& message)
    : MenuException(message) {
}