package project.advvvvvvvvvvvv;



public class Room implements java.io.Serializable {

    private int roomNumber;       
    private String type;          
    private double pricePerNight; 
    private boolean isAvailable;  

    // Default constructor
    public Room() {
        this.roomNumber = 0;
        this.type = "Standard";
        this.pricePerNight = 100.0;
        this.isAvailable = true;
    }

    // Full constructor
    public Room(int roomNumber, String type, double pricePerNight, boolean isAvailable) {
        this.roomNumber = roomNumber;
        this.type = type;
        this.pricePerNight = pricePerNight;
        this.isAvailable = isAvailable;
    }

    // Book the room (make it unavailable)
    public void bookRoom() {
        this.isAvailable = false;
    }

    // Release the room (make it available)
    public void releaseRoom() {
        this.isAvailable = true;
    }

    // Getters and setters
    public int getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(int roomNumber) {
        this.roomNumber = roomNumber;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getPricePerNight() {
        return pricePerNight;
    }

    public void setPricePerNight(double pricePerNight) {
        this.pricePerNight = pricePerNight;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    // toString to print room info
    @Override
    public String toString() {
        String status = isAvailable ? "Available" : "Reserved";
        return "Room{" +
                "roomNumber=" + roomNumber +
                ", type='" + type + '\'' +
                ", pricePerNight=" + pricePerNight +
                ", status=" + status +
                '}';
    }
}
