package project.advvvvvvvvvvvv;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class HotelMainFX extends Application {

    private AuthService authService = new AuthService();
    private RoomManager roomManager = new RoomManager();

    private ObservableList<Guest> guests = FXCollections.observableArrayList();
    private ObservableList<Reservation> reservations = FXCollections.observableArrayList();

    private ObjectOutputStream out;
    private ObjectInputStream in;
    private String currentUser = "Employee";
    private TextArea chatDisplay = new TextArea();

    @Override
    public void start(Stage primaryStage) {
        loadDataFromDatabase();
        connectToServer();
        showLoginScreen(primaryStage);
    }

    private void loadDataFromDatabase() {
        ArrayList<Room> dbRooms = DatabaseConnection.getAllRooms();
        for (Room r : dbRooms) {
            roomManager.addRoom(r);
        }

        ArrayList<Guest> dbGuests = DatabaseConnection.getAllGuests();
        guests.addAll(dbGuests);

        ArrayList<Reservation> dbRes = DatabaseConnection.getAllReservations(roomManager, new ArrayList<>(guests));
        reservations.addAll(dbRes);
    }

    private void showSuccessAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setStyle("-fx-background-color: #e8f5e9;");
        dialogPane.lookup(".content.label").setStyle("-fx-text-fill: #2e7d32; -fx-font-weight: bold;");
        alert.show();
    }

    private void connectToServer() {
        new Thread(() -> {
            try {
                Socket socket = new Socket("localhost", 5000);
                out = new ObjectOutputStream(socket.getOutputStream());
                out.flush();
                in = new ObjectInputStream(socket.getInputStream());

                while (true) {
                    Object data = in.readObject();

                    if (data instanceof String) {
                        Platform.runLater(() -> chatDisplay.appendText((String) data + "\n"));
                    } else if (data instanceof Guest) {
                        Guest newG = (Guest) data;
                        Platform.runLater(() -> {
                            boolean alreadyExists = guests.stream().anyMatch(g -> g.getId().equals(newG.getId()));
                            if (!alreadyExists) {
                                guests.add(newG);
                                chatDisplay.appendText("[System] Shared Update: Guest " + newG.getName() + " added.\n");
                            }
                        });
                    } else if (data instanceof Reservation) {
                        Reservation newRes = (Reservation) data;
                        Platform.runLater(() -> {
                            boolean resExists = reservations.stream().anyMatch(r -> r.getReservationId().equals(newRes.getReservationId()));
                            if (!resExists) {
                                reservations.add(newRes);
                                roomManager.getRooms().stream()
                                        .filter(room -> room.getRoomNumber() == newRes.getRoom().getRoomNumber())
                                        .findFirst()
                                        .ifPresent(room -> room.setAvailable(false));
                                chatDisplay.appendText("[System] Network Update: Room " + newRes.getRoom().getRoomNumber() + " is now BOOKED.\n");
                            }
                        });
                    }
                }
            } catch (Exception e) {
                Platform.runLater(() -> chatDisplay.appendText("[System] Offline Mode: Server not connected.\n"));
            }
        }).start();
    }

    private void sendToServer(Object obj) {
        try {
            if (out != null) {
                out.reset();
                out.writeObject(obj);
                out.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void applyCSS(Scene scene) {
        try {
            scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        } catch (Exception ignored) {}
    }

    private void showLoginScreen(Stage stage) {
        VBox layout = new VBox(15);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(40));

        Label title = new Label("Hotel System Login");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        TextField userField = new TextField(); userField.setPromptText("Username");
        PasswordField passField = new PasswordField(); passField.setPromptText("Password");

        Button loginBtn = new Button("Login");
        Button signUpBtn = new Button("Sign Up");
        loginBtn.setMinWidth(150); signUpBtn.setMinWidth(150);

        loginBtn.setOnAction(e -> {
            User u = authService.authenticateUser(userField.getText().trim(), passField.getText().trim());
            if (u != null) {
                this.currentUser = u.getUsername();
                sendToServer("[Server] " + currentUser + " has joined the system.");
                showDashboard(stage, u.getUsername());
                showSuccessAlert("Login Successful! Welcome " + currentUser);
            } else {
                new Alert(Alert.AlertType.ERROR, "Invalid Credentials! Check Oracle USERS table.").show();
            }
        });

        signUpBtn.setOnAction(e -> showSignUpScreen(stage));

        layout.getChildren().addAll(title, userField, passField, loginBtn, signUpBtn);

        Scene scene = new Scene(layout, 400, 500);
        applyCSS(scene);
        stage.setScene(scene);
        stage.setTitle("Login");
        stage.show();
    }

    private void showSignUpScreen(Stage stage) {
        VBox layout = new VBox(15);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(40));

        TextField userField = new TextField(); userField.setPromptText("New Username");
        PasswordField passField = new PasswordField(); passField.setPromptText("New Password");
        ComboBox<String> roleBox = new ComboBox<>();
        roleBox.getItems().addAll("Manager", "Receptionist");
        roleBox.setValue("Receptionist");
        roleBox.setMinWidth(150);

        Button registerBtn = new Button("Register");
        Button backBtn = new Button("Back");
        registerBtn.setMinWidth(150); backBtn.setMinWidth(150);

        registerBtn.setOnAction(e -> {
            if (!userField.getText().trim().isEmpty() && !passField.getText().trim().isEmpty()) {
                boolean success = authService.register(userField.getText().trim(), passField.getText().trim(), roleBox.getValue());
                if (success) {
                    showSuccessAlert("Account Registered in Oracle Database!");
                    showLoginScreen(stage);
                } else {
                    new Alert(Alert.AlertType.ERROR, "Username already exists or DB Error!").show();
                }
            }
        });

        backBtn.setOnAction(e -> showLoginScreen(stage));

        layout.getChildren().addAll(new Label("Create Account"), userField, passField, roleBox, registerBtn, backBtn);
        Scene scene = new Scene(layout, 400, 500);
        applyCSS(scene);
        stage.setScene(scene);
    }

    private void showDashboard(Stage stage, String adminName) {
        Label titleLabel = new Label("Hotel Dashboard");
        titleLabel.setStyle("-fx-font-size: 22px; -fx-font-weight: bold;");
        Label welcomeLabel = new Label("Welcome, " + adminName);

        Button btnAddGuest = createStyledButton("Add Guest");
        Button btnShowGuests = createStyledButton("Show All Guests");
        Button btnShowRooms = createStyledButton("Show Available Rooms");
        Button btnSortRooms = createStyledButton("Sort Rooms by Price");
        Button btnMakeRes = createStyledButton("Make Reservation");
        Button btnCheckIn = createStyledButton("Check-In");
        Button btnCheckOut = createStyledButton("Check-Out");
        Button btnCancel = createStyledButton("Cancel Reservation");
        Button btnShowRes = createStyledButton("Show All Reservations");
        Button btnSupportChat = createStyledButton("Support Chat (Online)");
        btnSupportChat.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");

        Button btnLogout = createStyledButton("Logout");

        btnAddGuest.setOnAction(e -> showAddGuestWindow());
        btnShowGuests.setOnAction(e -> showListWindow("Guests List", "Current Guests", guests));
        btnShowRooms.setOnAction(e -> showListWindow("Available Rooms", "Available Rooms", FXCollections.observableArrayList(roomManager.searchAvailableRooms())));
        btnSortRooms.setOnAction(e -> {
            roomManager.sortRoomsByPrice();
            showListWindow("Sorted Rooms", "Rooms Sorted by Price", FXCollections.observableArrayList(roomManager.getRooms()));
        });

        btnMakeRes.setOnAction(e -> showMakeReservationWindow());
        btnCheckIn.setOnAction(e -> showActionWindow("Check-In"));
        btnCheckOut.setOnAction(e -> showActionWindow("Check-Out"));
        btnCancel.setOnAction(e -> showActionWindow("Cancel"));
        btnShowRes.setOnAction(e -> showListWindow("Reservations", "All Reservations", reservations));
        btnSupportChat.setOnAction(e -> showChatWindow());

        btnLogout.setOnAction(e -> {
            sendToServer("[Server] " + currentUser + " has logged out.");
            showLoginScreen(stage);
        });

        VBox mainLayout = new VBox(10, titleLabel, welcomeLabel, new Separator(), btnAddGuest, btnShowGuests,
                btnShowRooms, btnSortRooms, btnMakeRes, btnCheckIn, btnCheckOut,
                btnCancel, btnShowRes, btnSupportChat, btnLogout);
        mainLayout.setAlignment(Pos.CENTER);
        mainLayout.setPadding(new Insets(20));

        Scene scene = new Scene(mainLayout, 500, 850);
        applyCSS(scene);
        stage.setScene(scene);
    }

    private void showChatWindow() {
        Stage stage = new Stage();
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(15));

        chatDisplay.setEditable(false);
        chatDisplay.setWrapText(true);

        TextField inputField = new TextField();
        inputField.setPromptText("Type a message...");

        Button sendBtn = new Button("Send");
        sendBtn.setMinWidth(70);

        sendBtn.setOnAction(e -> {
            String msg = inputField.getText();
            if (msg != null && !msg.trim().isEmpty()) {
                sendToServer(currentUser + ": " + msg);
                inputField.clear();
            }
        });

        inputField.setOnAction(e -> sendBtn.fire());

        HBox inputBox = new HBox(5, inputField, sendBtn);
        HBox.setHgrow(inputField, Priority.ALWAYS);

        layout.getChildren().addAll(new Label("Group Support Chat:"), chatDisplay, inputBox);
        Scene scene = new Scene(layout, 400, 500);
        applyCSS(scene);
        stage.setTitle("Chat Room - " + currentUser);
        stage.setScene(scene);
        stage.show();
    }

    private void showAddGuestWindow() {
        Stage stage = new Stage();
        VBox layout = new VBox(10); layout.setPadding(new Insets(20));
        TextField idF = new TextField(); idF.setPromptText("Guest ID (e.g. G101)");
        TextField nameF = new TextField(); nameF.setPromptText("Name");
        TextField phoneF = new TextField(); phoneF.setPromptText("Phone Number");
        TextField emailF = new TextField(); emailF.setPromptText("Email (Optional)");

        Button b = new Button("Save Guest");
        b.setOnAction(e -> {
            String email = emailF.getText().isEmpty() ? "No Email" : emailF.getText();
            Guest newG = new Guest(idF.getText().trim(), nameF.getText().trim(), phoneF.getText().trim(), email);
            DatabaseConnection.addGuest(newG);
            guests.add(newG);
            sendToServer(newG);
            stage.close();
            showSuccessAlert("Guest Added and Saved to Database!");
        });

        layout.getChildren().addAll(new Label("Add New Guest"), idF, nameF, phoneF, emailF, b);
        Scene scene = new Scene(layout, 300, 350);
        applyCSS(scene);
        stage.setScene(scene);
        stage.show();
    }

    private void showMakeReservationWindow() {
        Stage stage = new Stage();
        VBox layout = new VBox(10); layout.setPadding(new Insets(20));
        TextField resId = new TextField(); resId.setPromptText("Reservation ID (e.g. R101)");
        TextField roomNum = new TextField(); roomNum.setPromptText("Room Number");
        TextField guestId = new TextField(); guestId.setPromptText("Guest ID");
        TextField daysF = new TextField(); daysF.setPromptText("Number of Days");

        Button b = new Button("Confirm Reservation");
        b.setOnAction(e -> {
            try {
                Room r = roomManager.findRoomByNumber(Integer.parseInt(roomNum.getText().trim()));
                if (r != null && r.isAvailable()) {
                    Guest g = guests.stream().filter(gst -> gst.getId().equalsIgnoreCase(guestId.getText().trim()))
                            .findFirst()
                            .orElse(new Guest(guestId.getText().trim(), "Walk-in Guest", "000", ""));

                    Reservation res = new Reservation(resId.getText().trim(), g, r, Integer.parseInt(daysF.getText().trim()));
                    r.setAvailable(false);
                    DatabaseConnection.addReservation(res);
                    reservations.add(res);

                    sendToServer(res);
                    stage.close();
                    showSuccessAlert("Reservation Confirmed and Saved to Database!");
                } else {
                    new Alert(Alert.AlertType.ERROR, "Room is not available or doesn't exist!").show();
                }
            } catch (Exception ex) {
                new Alert(Alert.AlertType.ERROR, "Invalid Input! Please check your entries.").show();
            }
        });

        layout.getChildren().addAll(new Label("New Reservation"), resId, roomNum, guestId, daysF, b);
        Scene scene = new Scene(layout, 300, 400);
        applyCSS(scene);
        stage.setScene(scene);
        stage.show();
    }

    private void showActionWindow(String type) {
        Stage stage = new Stage();
        VBox layout = new VBox(10); layout.setPadding(new Insets(20));
        TextField idF = new TextField(); idF.setPromptText("Enter Reservation ID");
        Button b = new Button("Confirm " + type);
        b.setOnAction(e -> {
            for (Reservation r : reservations) {
                if (r.getReservationId().equalsIgnoreCase(idF.getText().trim())) {
                    if (type.equals("Check-In")) r.checkIn();
                    else if (type.equals("Check-Out")) r.checkOut();
                    else if (type.equals("Cancel")) r.cancel();

                    DatabaseConnection.updateReservationStatus(r.getReservationId(), type, r.getRoom().getRoomNumber());
                    sendToServer("[System] Reservation " + idF.getText() + " status: " + type);
                    stage.close();
                    showSuccessAlert(type + " Operation Successful & Synced!");
                    return;
                }
            }
            new Alert(Alert.AlertType.WARNING, "Reservation ID Not Found!").show();
        });
        layout.getChildren().addAll(new Label(type), idF, b);
        Scene scene = new Scene(layout, 300, 200);
        applyCSS(scene);
        stage.setScene(scene);
        stage.show();
    }

    private void showListWindow(String stageTitle, String headerText, ObservableList<?> dataList) {
        Stage stage = new Stage();
        Label headerLabel = new Label(headerText);
        headerLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        ListView<Object> listView = new ListView<>();
        listView.setItems((ObservableList<Object>) dataList);

        VBox layout = new VBox(10, headerLabel, listView);
        layout.setPadding(new Insets(15));
        VBox.setVgrow(listView, Priority.ALWAYS);

        Scene scene = new Scene(layout, 550, 400);
        applyCSS(scene);
        stage.setScene(scene);
        stage.setTitle(stageTitle);
        stage.show();
    }

    private Button createStyledButton(String text) {
        Button b = new Button(text);
        b.setMinWidth(320);
        b.setMinHeight(40);
        return b;
    }

    public static void main(String[] args) {
        launch(args);
    }
}