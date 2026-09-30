package project.advvvvvvvvvvvv;

// Abstract base class for common personal data.
// Used to show "Abstract Class" concept.

public abstract class Person implements java.io.Serializable {

    // Common fields for any person in the system
    protected String name;
    protected String phoneNumber;
    protected String email;

    // Constructor with data
    public Person(String name, String phoneNumber, String email) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.email = email;
    }

    // Empty constructor (optional use)
    public Person() {
    }

    // Abstract method: every subclass must provide its own ID
    public abstract String getId();

    // Getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    // Common toString for Person
    @Override
    public String toString() {
        return "name='" + name + '\'' +
               ", phoneNumber='" + phoneNumber + '\'' +
               ", email='" + email + '\'';
    }
}
