package project.advvvvvvvvvvvv;

import java.util.ArrayList;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        AuthService authService = new AuthService();
        RoomManager roomManager = new RoomManager();

        for (Room r : DatabaseConnection.getAllRooms()) {
            roomManager.addRoom(r);
        }

        ArrayList<Guest> guests = DatabaseConnection.getAllGuests();
        ArrayList<Reservation> reservations = DatabaseConnection.getAllReservations(roomManager, guests);

        System.out.println("--- Welcome to Hotel Management System ---");
        System.out.println("1. Login");
        System.out.println("2. Sign Up (Register)");
        System.out.print("Choose: ");
        int startChoice = scanner.nextInt();

        if (startChoice == 2) {
            System.out.print("Enter New Username: ");
            String newU = scanner.next();
            System.out.print("Enter New Password: ");
            String newP = scanner.next();
            System.out.print("Enter Role (Manager/Receptionist): ");
            String newR = scanner.next();
            authService.register(newU, newP, newR);
        }

        User loggedInUser = null;
        while (loggedInUser == null) {
            System.out.print("Enter Username: ");
            String user = scanner.next();
            System.out.print("Enter Password: ");
            String pass = scanner.next();
            loggedInUser = authService.authenticateUser(user, pass);
            if (loggedInUser == null) {
                System.out.println("Invalid credentials, try again.");
            }
        }

        System.out.println("\nWelcome, " + loggedInUser.getUsername() + " (" + loggedInUser.getRole() + ")");

        boolean running = true;
        while (running) {
            System.out.println("\n--- Hotel System Dashboard ---");
            System.out.println("1. Add Guest");
            System.out.println("2. Show All Guests");
            System.out.println("3. Show Available Rooms");
            System.out.println("4. Sort Rooms by Price");
            System.out.println("5. Make Reservation");
            System.out.println("6. Check-In");
            System.out.println("7. Check-Out");
            System.out.println("8. Cancel Reservation");
            System.out.println("9. Show All Reservations");
            System.out.println("10. Logout");
            System.out.print("Choose an option: ");

            int choice = scanner.nextInt();
            switch (choice) {
                case 1:
                    System.out.print("Enter Guest ID: "); String id = scanner.next();
                    System.out.print("Enter Name: "); String name = scanner.next();
                    Guest g = new Guest(id, name, "0123456", "email@test.com");
                    guests.add(g);
                    DatabaseConnection.addGuest(g);
                    System.out.println("Guest added successfully!");
                    break;
                case 2:
                    for (Guest gst : guests) System.out.println(gst);
                    break;
                case 3:
                    for (Room r : roomManager.searchAvailableRooms()) System.out.println(r);
                    break;
                case 4:
                    roomManager.sortRoomsByPrice();
                    System.out.println("Rooms sorted!");
                    for (Room r : roomManager.getRooms()) System.out.println(r);
                    break;
                case 5:
                    System.out.print("Enter Room Number: ");
                    int rNum = scanner.nextInt();
                    Room room = roomManager.findRoomByNumber(rNum);
                    if (room != null && room.isAvailable()) {
                        System.out.print("Enter Guest Name: "); String gName = scanner.next();
                        Guest guest = new Guest("G" + (guests.size() + 1), gName, "000", "mail");
                        guests.add(guest);
                        DatabaseConnection.addGuest(guest);

                        Reservation res = new Reservation("RES" + (reservations.size() + 1), guest, room, 3);
                        reservations.add(res);
                        DatabaseConnection.addReservation(res);
                        System.out.println("Reservation Created: " + res);
                    } else {
                        System.out.println("Room not available or doesn't exist.");
                    }
                    break;
                case 6:
                    if (!reservations.isEmpty()) {
                        Reservation r1 = reservations.get(0);
                        r1.checkIn();
                        DatabaseConnection.updateReservationStatus(r1.getReservationId(), "CHECKED_IN", r1.getRoom().getRoomNumber());
                        System.out.println("Reservation checked in.");
                    }
                    break;
                case 7:
                    if (!reservations.isEmpty()) {
                        Reservation r2 = reservations.get(0);
                        r2.checkOut();
                        DatabaseConnection.updateReservationStatus(r2.getReservationId(), "CHECKED_OUT", r2.getRoom().getRoomNumber());
                        System.out.println("Checked out successfully.");
                    }
                    break;
                case 8:
                    if (!reservations.isEmpty()) {
                        Reservation r3 = reservations.get(0);
                        r3.cancel();
                        DatabaseConnection.updateReservationStatus(r3.getReservationId(), "CANCELLED", r3.getRoom().getRoomNumber());
                        System.out.println("Cancelled successfully.");
                    }
                    break;
                case 9:
                    for (Reservation resv : reservations) System.out.println(resv);
                    break;
                case 10:
                    running = false;
                    System.out.println("Logged out. Goodbye!");
                    break;
                default:
                    System.out.println("Invalid option.");
            }
        }
        scanner.close();
    }
}