public class Customer {
    private String name;
    private String stateOfResidence; // e.g., "IL", "CA", "NY", "TX"

    public Customer(String name, String stateOfResidence) {
        this.name = name;
        this.stateOfResidence = stateOfResidence;
    }

    public String getName() {
        return name;
    }

    public String getStateOfResidence() {
        return stateOfResidence;
    }
}