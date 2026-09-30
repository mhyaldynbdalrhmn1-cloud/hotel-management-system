# 🏨 Hotel Management System (Java & JavaFX)

A comprehensive, desktop-based Hotel Management System built with **Java** and **JavaFX**, integrated with an **Oracle Database** via JDBC, and featuring **real-time client-server synchronization** via Java Sockets.

---

## 🌟 Key Features

- **Authentication & Role-Based Access:** Secure login and user registration integrated with Oracle Database (`USERS` table) supporting roles like *Manager* and *Receptionist*.
- **Room Management:** Add, inspect, and sort rooms by price with dynamic availability tracking (`Single`, `Double`, `Suite`).
- **Guest Management:** Register and maintain guest records (`Guest` and `VIPGuest` with polymorphic discount calculation).
- **Reservation Lifecycle:** Make reservations, check in, check out, and cancel with automatic database updates and room status synchronization.
- **Real-Time Client-Server Networking:** 
  - Multi-threaded socket server (`HotelServer`) on port `5000`.
  - Live broadcast of guest registrations, bookings, and updates across connected instances.
  - In-app staff support chat room.
- **Modern JavaFX UI:** Styled interface utilizing CSS sheets and responsive layouts.

---

## 🛠️ Tech Stack & OOP Concepts

- **Language:** Java (JDK 8 / 1.8)
- **GUI Framework:** JavaFX
- **Database:** Oracle Database (Express Edition / APEX) via `ojdbc6.jar`
- **Networking:** Java Sockets (`ServerSocket`, `Socket`, `ObjectOutputStream`, `ObjectInputStream`)
- **OOP Architecture:**
  - **Inheritance & Polymorphism:** `Person` $\rightarrow$ `Guest` $\rightarrow$ `VIPGuest`.
  - **Interfaces:** `Payable` implemented by `Reservation`.
  - **Encapsulation & Data Separation:** Business logic decoupled into managers (`RoomManager`, `AuthService`, `DatabaseConnection`).

---

## 🗄️ Database Schema (Oracle SQL)

```sql
-- Users Table
CREATE TABLE USERS (
    USERNAME VARCHAR2(50) PRIMARY KEY,
    PASSWORD VARCHAR2(50),
    ROLE VARCHAR2(50)
);

-- Rooms Table
CREATE TABLE ROOMS (
    ROOM_NUMBER NUMBER PRIMARY KEY,
    TYPE VARCHAR2(50),
    PRICE NUMBER,
    IS_AVAILABLE NUMBER(1)
);

-- Guests Table
CREATE TABLE GUESTS (
    GUEST_ID VARCHAR2(50) PRIMARY KEY,
    NAME VARCHAR2(100),
    PHONE_NUMBER VARCHAR2(50),
    EMAIL VARCHAR2(100)
);

-- Reservations Table
CREATE TABLE RESERVATIONS (
    RESERVATION_ID VARCHAR2(50) PRIMARY KEY,
    GUEST_ID VARCHAR2(50),
    ROOM_NUMBER NUMBER,
    DAYS NUMBER,
    TOTAL NUMBER,
    STATUS VARCHAR2(50)
);
🚀 Getting Started
Prerequisites
JDK 1.8 installed and configured.

Oracle Database XE running on localhost:1521:XE.

JDBC Driver: ojdbc6.jar added to project libraries.

Running the Application
Clone the Repository:

Bash
git clone [https://github.com/your-username/hotel-management-system.git](https://github.com/your-username/hotel-management-system.git)
Start the Networking Server:

Run HotelServer.java first to initialize port 5000.

Launch the UI Client:

Run HotelMainFX.java to start the GUI application.

Alternatively, run Main.java for the console CLI mode.

Default Credentials (from seed data):

Username: khaled

Password: 123
