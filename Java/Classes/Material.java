public class Material extends Entity {
    private final double level1;  // quantity for 1 person
    private final double level2;  // quantity for 2-4 people
    private final double level3;  // quantity for 5+ people

    // Constructor
    public Material(String name, String description, int id,
                    double level1, double level2, double level3) {
        super(name, description, id);
        this.level1 = level1;
        this.level2 = level2;
        this.level3 = level3;
    }

    // Getters for levels (no setters as levels are final)
    public double getLevel1() {
        return level1;
    }

    public double getLevel2() {
        return level2;
    }

    public double getLevel3() {
        return level3;
    }

    @Override
    public String getDetails() {
        return "Type: Material\n" +
                "Level 1 (1 person): " + level1 + "\n" +
                "Level 2 (2-4 people): " + level2 + "\n" +
                "Level 3 (5+ people): " + level3;
    }
}