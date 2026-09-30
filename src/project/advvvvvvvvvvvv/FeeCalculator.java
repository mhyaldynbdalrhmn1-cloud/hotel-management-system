package project.advvvvvvvvvvvv;

// Helper class to calculate fees using the Payable interface.

public class FeeCalculator {

    // Generic method: any Payable object can be used (Reservation here)
    public double calculateTotalFee(Payable payable) {
        return payable.calculateTotal();
    }
}
