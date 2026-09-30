package project.advvvvvvvvvvvv;

// Manages rooms: add, search, display, and sort

import java.util.ArrayList;

public class RoomManager {

    private ArrayList<Room> rooms;

    public RoomManager() {
        rooms = new ArrayList<>();
    }

    // Add room to system
    public void addRoom(Room room) {
        rooms.add(room);
    }

    // Get all rooms
    public ArrayList<Room> getRooms() {
        return rooms;
    }

    // Manual search: available rooms only
    public ArrayList<Room> searchAvailableRooms() {
        ArrayList<Room> availableRooms = new ArrayList<>();

        for (int i = 0; i < rooms.size(); i++) {
            if (rooms.get(i).isAvailable()) {
                availableRooms.add(rooms.get(i));
            }
        }
        return availableRooms;
    }

    // Manual bubble sort by price (ascending)
    public void sortRoomsByPrice() {
        int n = rooms.size();

        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - 1 - i; j++) {
                Room r1 = rooms.get(j);
                Room r2 = rooms.get(j + 1);

                if (r1.getPricePerNight() > r2.getPricePerNight()) {
                    rooms.set(j, r2);
                    rooms.set(j + 1, r1);
                }
            }
        }
    }

    // New method: Find room by number
    public Room findRoomByNumber(int roomNumber) {
        for (int i = 0; i < rooms.size(); i++) {
            Room r = rooms.get(i);
            if (r.getRoomNumber() == roomNumber) {
                return r;
            }
        }
        return null;
    }
}
