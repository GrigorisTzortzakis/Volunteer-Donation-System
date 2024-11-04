// Exceptions.java
public class Exceptions {
    public static class BeneficiaryLimitException extends Exception {
        public BeneficiaryLimitException(String message) {
            super(message);
        }
    }

    public static class QuantityException extends Exception {
        public QuantityException(String message) {
            super(message);
        }
    }

    public static class EntityExistsException extends Exception {
        public EntityExistsException(String message) {
            super(message);
        }
    }

    public static class EntityNotFoundException extends Exception {
        public EntityNotFoundException(String message) {
            super(message);
        }
    }

    public static class InvalidMenuException extends Exception {
        public InvalidMenuException(String message) {
            super(message);
        }
    }
}