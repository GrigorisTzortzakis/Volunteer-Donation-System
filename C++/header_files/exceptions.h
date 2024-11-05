// exceptions.h
#ifndef EXCEPTIONS_H
#define EXCEPTIONS_H

#include <stdexcept>
#include <string>

// Base exception class for the donation system
class DonationSystemException : public std::runtime_error {
public:
    explicit DonationSystemException(const std::string& message);
};

// Entity-related exceptions
class EntityException : public DonationSystemException {
public:
    explicit EntityException(const std::string& message);
};

class EntityExistsException : public EntityException {
public:
    explicit EntityExistsException(const std::string& message);
};

class EntityNotFoundException : public EntityException {
public:
    explicit EntityNotFoundException(const std::string& message);
};

class DuplicateEntityIdException : public EntityException {
public:
    explicit DuplicateEntityIdException(const std::string& message);
};

// Quantity-related exceptions
class QuantityException : public DonationSystemException {
public:
    explicit QuantityException(const std::string& message);
};

class InvalidQuantityException : public QuantityException {
public:
    explicit InvalidQuantityException(const std::string& message);
};

class InsufficientQuantityException : public QuantityException {
public:
    explicit InsufficientQuantityException(const std::string& message);
};

class QuantityLimitException : public QuantityException {
public:
    explicit QuantityLimitException(const std::string& message);
};

// User-related exceptions
class UserException : public DonationSystemException {
public:
    explicit UserException(const std::string& message);
};

class UserExistsException : public UserException {
public:
    explicit UserExistsException(const std::string& message);
};

class UserNotFoundException : public UserException {
public:
    explicit UserNotFoundException(const std::string& message);
};

class UnauthorizedAccessException : public UserException {
public:
    explicit UnauthorizedAccessException(const std::string& message);
};

// Request/Offer-related exceptions
class RequestException : public DonationSystemException {
public:
    explicit RequestException(const std::string& message);
};

class InvalidRequestException : public RequestException {
public:
    explicit InvalidRequestException(const std::string& message);
};

class CommitFailedException : public RequestException {
public:
    explicit CommitFailedException(const std::string& message);
};

// Menu-related exceptions
class MenuException : public DonationSystemException {
public:
    explicit MenuException(const std::string& message);
};

class InvalidMenuOptionException : public MenuException {
public:
    explicit InvalidMenuOptionException(const std::string& message);
};

class InvalidInputException : public MenuException {
public:
    explicit InvalidInputException(const std::string& message);
};

#endif // EXCEPTIONS_H