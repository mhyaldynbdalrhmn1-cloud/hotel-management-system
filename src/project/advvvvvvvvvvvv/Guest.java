package project.advvvvvvvvvvvv;

// Represents a hotel guest. Extends Person to use abstract class.

public class Guest extends Person implements java.io.Serializable {

    private String guestId; // unique ID for guest (e.g. G101)

    // Constructor with all data
    public Guest(String guestId, String name, String phoneNumber, String email) {
        super(name, phoneNumber, email); // call Person constructor
        this.guestId = guestId;
    }

    // No-argument constructor (optional)
    public Guest() {
        super();
    }

    // Implementation of abstract method from Person
    @Override
    public String getId() {
        return guestId;
    }

    // Getter & Setter for guestId
    public String getGuestId() {
        return guestId;
    }

    public void setGuestId(String guestId) {
        this.guestId = guestId;
    }

    // toString for printing guest info
    @Override
    public String toString() {
        return "Guest{" +
                "guestId='" + guestId + '\'' +
                ", " + super.toString() + // Person data
                '}';
    }
}
