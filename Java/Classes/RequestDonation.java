public class RequestDonation implements Comparable<RequestDonation> {
    private Entity entity;
    private double quantity;

    // Constructor
    public RequestDonation(Entity entity, double quantity) {
        this.entity = entity;
        this.quantity = quantity;
    }

    // Getters and setters
    public Entity getEntity() {
        return entity;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    // Implementing comparison based on entity ID
    @Override
    public int compareTo(RequestDonation other) {
        return Integer.compare(this.entity.getId(), other.entity.getId());
    }

    // Override equals to compare based on entity ID
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        RequestDonation other = (RequestDonation) obj;
        return this.entity.getId() == other.entity.getId();
    }

    // Override hashCode to be consistent with equals
    @Override
    public int hashCode() {
        return entity.getId();
    }
}