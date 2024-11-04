public class Admin extends User {
    private final boolean isAdmin = true;

    public Admin(String name, String phone) {
        super(name, phone);
    }

    public boolean isAdmin() {
        return isAdmin;
    }
}