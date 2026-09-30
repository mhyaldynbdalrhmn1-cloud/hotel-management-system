package project.advvvvvvvvvvvv;

// Reservation.java
// Represents a room reservation using number of days only.

public class Reservation implements Payable , java.io.Serializable {

    private String reservationId;
    private Guest guest;
    private Room room;
    private int numberOfDays;  
    private Status status;

    public enum Status {
        RESERVED,
        CHECKED_IN,
        CHECKED_OUT,
        CANCELLED
    }

    // Constructor 
    public Reservation(String reservationId, Guest guest, Room room, int numberOfDays) {
        this.reservationId = reservationId;
        this.guest = guest;
        this.room = room;
        this.numberOfDays = numberOfDays;
        this.status = Status.RESERVED;

        // Mark room as reserved
        this.room.bookRoom();
    }

    // Getters
    public String getReservationId() {
        return reservationId;
    }

    public Guest getGuest() {
        return guest;
    }

    public Room getRoom() {
        return room;
    }

    public int getNumberOfDays() {
        return numberOfDays;
    }

    public Status getStatus() {
        return status;
    }

    // Calculate total cost
    @Override
    public double calculateTotal() {

        double total = numberOfDays * room.getPricePerNight();

        // If guest is VIP, apply discount
        if (guest instanceof VIPGuest) {
            VIPGuest vip = (VIPGuest) guest;
            total = total - (total * vip.getDiscountRate());
        }

    return total;
}


    public void checkIn() {
        status = Status.CHECKED_IN;
    }

    public void checkOut() {
        status = Status.CHECKED_OUT;
        room.releaseRoom();
    }

    public void cancel() {
        status = Status.CANCELLED;
        room.releaseRoom();
    }

    @Override
    public String toString() {
        return "Reservation{" +
                "id='" + reservationId + '\'' +
                ", guest=" + guest.getName() +
                ", room=" + room.getRoomNumber() +
                ", days=" + numberOfDays +
                ", total=" + calculateTotal() +
                ", status=" + status +
                '}';
    }
}
