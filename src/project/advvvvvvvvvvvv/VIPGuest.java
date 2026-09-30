package project.advvvvvvvvvvvv;

// Special type of Guest with discount benefit.
// This class shows Inheritance and Polymorphism.

public class VIPGuest extends Guest implements java.io.Serializable {

    private double discountRate; 

    public VIPGuest(String guestId, String name, String phoneNumber,
                    String email, double discountRate) {
        super(guestId, name, phoneNumber, email);
        this.discountRate = discountRate;
    }

    // Getter
    public double getDiscountRate() {
        return discountRate;
    }

    // Override method to apply discount
    @Override
    public String toString() {
        return "VIPGuest{" +
                "guestId='" + getGuestId() + '\'' +
                ", name='" + getName() + '\'' +
                ", discountRate=" + (discountRate * 100) + "%" +
                '}';
    }
}
