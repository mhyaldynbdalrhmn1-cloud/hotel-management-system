package project.advvvvvvvvvvvv;

import java.sql.*;
import java.util.ArrayList;

public class DatabaseConnection {

    public static Connection dpconn() {
        Connection con = null;
        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            con = DriverManager.getConnection(
                    "jdbc:oracle:thin:@localhost:1521:XE",
                    "dp100",
                    "dp100"
            );
            con.setAutoCommit(true);
        } catch (Exception e) {
            System.err.println("Database Connection Failed: " + e.getMessage());
        }
        return con;
    }

    // --- Users Operations ---
    public static User authenticateUser(String username, String password) {
        String sql = "SELECT * FROM USERS WHERE USERNAME = ? AND PASSWORD = ?";
        try (Connection con = dpconn(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new User(rs.getString("USERNAME"), rs.getString("PASSWORD"), rs.getString("ROLE"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean registerUser(User user) {
        String checkSql = "SELECT USERNAME FROM USERS WHERE USERNAME = ?";
        String insertSql = "INSERT INTO USERS (USERNAME, PASSWORD, ROLE) VALUES (?, ?, ?)";
        try (Connection con = dpconn();
             PreparedStatement checkPs = con.prepareStatement(checkSql)) {
            checkPs.setString(1, user.getUsername());
            ResultSet rs = checkPs.executeQuery();
            if (rs.next()) return false;

            try (PreparedStatement insertPs = con.prepareStatement(insertSql)) {
                insertPs.setString(1, user.getUsername());
                insertPs.setString(2, user.getPassword());
                insertPs.setString(3, user.getRole());
                return insertPs.executeUpdate() > 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // --- Rooms Operations ---
    public static ArrayList<Room> getAllRooms() {
        ArrayList<Room> rooms = new ArrayList<>();
        String query = "SELECT * FROM ROOMS";
        try (Connection con = dpconn(); PreparedStatement ps = con.prepareStatement(query); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Room r = new Room(
                        rs.getInt("ROOM_NUMBER"),
                        rs.getString("TYPE"),
                        rs.getDouble("PRICE"),
                        rs.getInt("IS_AVAILABLE") == 1
                );
                rooms.add(r);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rooms;
    }

    public static void updateRoomAvailability(int roomNumber, boolean isAvailable) {
        String sql = "UPDATE ROOMS SET IS_AVAILABLE = ? WHERE ROOM_NUMBER = ?";
        try (Connection con = dpconn(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, isAvailable ? 1 : 0);
            ps.setInt(2, roomNumber);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // --- Guests Operations ---
    public static ArrayList<Guest> getAllGuests() {
        ArrayList<Guest> list = new ArrayList<>();
        String sql = "SELECT * FROM GUESTS";
        try (Connection con = dpconn(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Guest(
                        rs.getString("GUEST_ID"),
                        rs.getString("NAME"),
                        rs.getString("PHONE_NUMBER"),
                        rs.getString("EMAIL")
                ));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public static boolean addGuest(Guest guest) {
        String sql = "INSERT INTO GUESTS (GUEST_ID, NAME, PHONE_NUMBER, EMAIL) VALUES (?, ?, ?, ?)";
        try (Connection con = dpconn(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, guest.getId());
            ps.setString(2, guest.getName());
            ps.setString(3, guest.getPhoneNumber());
            ps.setString(4, guest.getEmail());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // --- Reservations Operations ---
    public static ArrayList<Reservation> getAllReservations(RoomManager roomManager, ArrayList<Guest> guestsList) {
        ArrayList<Reservation> list = new ArrayList<>();
        String sql = "SELECT * FROM RESERVATIONS";
        try (Connection con = dpconn(); PreparedStatement ps = con.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                String resId = rs.getString("RESERVATION_ID");
                String gId = rs.getString("GUEST_ID");
                int rNum = rs.getInt("ROOM_NUMBER");
                int days = rs.getInt("DAYS");
                String statusStr = rs.getString("STATUS");

                Guest guest = guestsList.stream().filter(g -> g.getId().equals(gId)).findFirst()
                        .orElse(new Guest(gId, "Customer", "000", "N/A"));
                Room room = roomManager.findRoomByNumber(rNum);

                if (room != null) {
                    Reservation r = new Reservation(resId, guest, room, days);
                    if ("CHECKED_IN".equalsIgnoreCase(statusStr)) r.checkIn();
                    else if ("CHECKED_OUT".equalsIgnoreCase(statusStr)) r.checkOut();
                    else if ("CANCELLED".equalsIgnoreCase(statusStr)) r.cancel();
                    list.add(r);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public static boolean addReservation(Reservation res) {
        String sql = "INSERT INTO RESERVATIONS (RESERVATION_ID, GUEST_ID, ROOM_NUMBER, DAYS, TOTAL, STATUS) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection con = dpconn(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, res.getReservationId());
            ps.setString(2, res.getGuest().getId());
            ps.setInt(3, res.getRoom().getRoomNumber());
            ps.setInt(4, res.getNumberOfDays());
            ps.setDouble(5, res.calculateTotal());
            ps.setString(6, res.getStatus().name());
            int rows = ps.executeUpdate();
            if (rows > 0) {
                updateRoomAvailability(res.getRoom().getRoomNumber(), false);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void updateReservationStatus(String resId, String status, int roomNumber) {
        String sql = "UPDATE RESERVATIONS SET STATUS = ? WHERE RESERVATION_ID = ?";
        try (Connection con = dpconn(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setString(2, resId);
            ps.executeUpdate();

            if ("CHECKED_OUT".equalsIgnoreCase(status) || "CANCEL".equalsIgnoreCase(status) || "CANCELLED".equalsIgnoreCase(status)) {
                updateRoomAvailability(roomNumber, true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}