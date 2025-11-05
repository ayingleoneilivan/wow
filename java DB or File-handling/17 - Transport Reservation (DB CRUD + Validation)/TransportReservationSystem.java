import java.sql.*;
import java.util.*;

public class TransportReservationSystem {

    static final String DB_URL = "jdbc:sqlite:transport.db";
    static Connection conn;

    public static void main(String[] args) {
        try {
            connectDB();
            createTables();
            runMenu();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        } finally {
            try { if (conn != null) conn.close(); } catch (SQLException ignored) {}
        }
    }

    // Connect to SQLite DB
    static void connectDB() throws SQLException {
        conn = DriverManager.getConnection(DB_URL);
    }

    // Create tables
    static void createTables() throws SQLException {
        Statement stmt = conn.createStatement();

        stmt.execute("CREATE TABLE IF NOT EXISTS Trips (" +
                "trip_id TEXT PRIMARY KEY," +
                "route TEXT," +
                "date TEXT," +
                "time TEXT," +
                "total_seats INT," +
                "available_seats INT," +
                "fare REAL)");

        stmt.execute("CREATE TABLE IF NOT EXISTS Reservations (" +
                "reservation_id TEXT PRIMARY KEY," +
                "name TEXT," +
                "contact TEXT," +
                "trip_id TEXT," +
                "seat_no INT," +
                "payment_status TEXT," +
                "FOREIGN KEY(trip_id) REFERENCES Trips(trip_id))");

        stmt.close();
    }

    // Menu System
    static void runMenu() throws SQLException {
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println("\n--- Transport Reservation System ---");
            System.out.println("1. Passenger Menu");
            System.out.println("2. Admin Menu");
            System.out.println("3. Exit");
            System.out.print("Enter choice: ");

            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1 -> passengerMenu(sc);
                case 2 -> adminMenu(sc);
                case 3 -> {
                    System.out.println("Exiting system...");
                    return;
                }
                default -> System.out.println("Invalid choice!");
            }
        }
    }

    static void passengerMenu(Scanner sc) throws SQLException {
        while (true) {
            System.out.println("\n--- Passenger Menu ---");
            System.out.println("1. Add Trip");
            System.out.println("2. View Trips");
            System.out.println("3. Book Reservation");
            System.out.println("4. Cancel Reservation");
            System.out.println("5. Back to Main Menu");
            System.out.print("Enter choice: ");

            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1 -> addTrip(sc);
                case 2 -> viewTrips();
                case 3 -> bookReservation(sc);
                case 4 -> cancelReservation(sc);
                case 5 -> {
                    System.out.println("Returning to Main Menu...");
                    return;
                }
                default -> System.out.println("Invalid choice!");
            }
        }
    }

    static void adminMenu(Scanner sc) throws SQLException {
        while (true) {
            System.out.println("\n--- Admin Menu ---");
            System.out.println("1. View All Trips with Available Seats");
            System.out.println("2. View Reservations per Trip");
            System.out.println("3. Search Reservation by Passenger Name or ReservationID");
            System.out.println("4. Back to Main Menu");
            System.out.print("Enter choice: ");

            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1 -> viewTripsWithAvailableSeats();
                case 2 -> viewReservationsPerTrip(sc);
                case 3 -> searchReservation(sc);
                case 4 -> {
                    System.out.println("Returning to Main Menu...");
                    return;
                }
                default -> System.out.println("Invalid choice!");
            }
        }
    }

    // Add Trip
    static void addTrip(Scanner sc) throws SQLException {
        System.out.print("Trip ID: ");
        String tripID = sc.nextLine();

        if (tripExists(tripID)) {
            System.out.println("Trip ID already exists!");
            return;
        }

        System.out.print("Route: ");
        String route = sc.nextLine();
        System.out.print("Date (YYYY-MM-DD): ");
        String date = sc.nextLine();
        System.out.print("Time (HH:MM): ");
        String time = sc.nextLine();
        System.out.print("Total Seats: ");
        int total = sc.nextInt();
        System.out.print("Fare: ");
        double fare = sc.nextDouble();

        PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO Trips VALUES (?,?,?,?,?,?,?)");
        ps.setString(1, tripID);
        ps.setString(2, route);
        ps.setString(3, date);
        ps.setString(4, time);
        ps.setInt(5, total);
        ps.setInt(6, total);
        ps.setDouble(7, fare);
        ps.executeUpdate();

        System.out.println("Trip added successfully!");
    }

    // View Trips
    static void viewTrips() throws SQLException {
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM Trips");
        System.out.println("\n--- Available Trips ---");
        while (rs.next()) {
            System.out.printf("%s | %s | %s %s | Seats: %d/%d | Fare: %.2f%n",
                    rs.getString("trip_id"), rs.getString("route"),
                    rs.getString("date"), rs.getString("time"),
                    rs.getInt("available_seats"), rs.getInt("total_seats"),
                    rs.getDouble("fare"));
        }
    }

    // Book Reservation (Fixed version — prevents duplicate seat bookings)
  static void bookReservation(Scanner sc) throws SQLException {
      viewTrips();
      System.out.print("\nEnter Trip ID: ");
      String tripID = sc.nextLine();

      if (!tripExists(tripID)) {
          System.out.println("Invalid Trip ID!");
          return;
      }

      ResultSet rs = conn.createStatement().executeQuery(
              "SELECT * FROM Trips WHERE trip_id='" + tripID + "'");
      if (!rs.next()) return;

      int available = rs.getInt("available_seats");
      double fare = rs.getDouble("fare");
      String route = rs.getString("route");
      String date = rs.getString("date");
      String time = rs.getString("time");

      if (available <= 0) {
          System.out.println("No seats available!");
          return;
      }

      System.out.print("Reservation ID: ");
      String resID = sc.nextLine();
      System.out.print("Name: ");
      String name = sc.nextLine();
      System.out.print("Contact No: ");
      String contact = sc.nextLine();
      if (!contact.matches("\\d+")) {
          System.out.println("Invalid contact number!");
          return;
      }

      System.out.print("Seat No: ");
      int seat = sc.nextInt();
      sc.nextLine();

      // ✅ Validate if this seat number is already taken for the same trip
      PreparedStatement checkSeat = conn.prepareStatement(
              "SELECT * FROM Reservations WHERE trip_id=? AND seat_no=?");
      checkSeat.setString(1, tripID);
      checkSeat.setInt(2, seat);
      ResultSet seatCheck = checkSeat.executeQuery();
      if (seatCheck.next()) {
          System.out.println("Seat number " + seat + " is already booked for this trip!");
          return;
      }

      System.out.print("Payment Status (Paid/Unpaid): ");
      String status = sc.nextLine();

      PreparedStatement ps = conn.prepareStatement(
              "INSERT INTO Reservations VALUES (?,?,?,?,?,?)");
      ps.setString(1, resID);
      ps.setString(2, name);
      ps.setString(3, contact);
      ps.setString(4, tripID);
      ps.setInt(5, seat);
      ps.setString(6, status);
      ps.executeUpdate();

      // Update available seats
      PreparedStatement ps2 = conn.prepareStatement(
              "UPDATE Trips SET available_seats=? WHERE trip_id=?");
      ps2.setInt(1, available - 1);
      ps2.setString(2, tripID);
      ps2.executeUpdate();

      System.out.println("\n✅ Reservation successful!");
      System.out.println("--- Transport Reservation Ticket ---");
      System.out.println("ReservationID: " + resID);
      System.out.println("Passenger: " + name);
      System.out.println("Trip: " + route);
      System.out.println("Date: " + date);
      System.out.println("Time: " + time);
      System.out.println("Seat: " + seat);
      System.out.println("Fare: " + fare);
      System.out.println("Payment: " + status);
      System.out.println("------------------------------------");
  }


    // Cancel Reservation
    static void cancelReservation(Scanner sc) throws SQLException {
        System.out.print("Enter Reservation ID to cancel: ");
        String resID = sc.nextLine();

        PreparedStatement ps = conn.prepareStatement(
                "SELECT * FROM Reservations WHERE reservation_id=?");
        ps.setString(1, resID);
        ResultSet rs = ps.executeQuery();

        if (!rs.next()) {
            System.out.println("Reservation not found!");
            return;
        }

        String tripID = rs.getString("trip_id");
        int seat = rs.getInt("seat_no");

        // Delete reservation
        PreparedStatement del = conn.prepareStatement(
                "DELETE FROM Reservations WHERE reservation_id=?");
        del.setString(1, resID);
        del.executeUpdate();

        // Increase available seat
        conn.createStatement().executeUpdate(
                "UPDATE Trips SET available_seats = available_seats + 1 WHERE trip_id='" + tripID + "'");

        System.out.println("Reservation cancelled. Seat " + seat + " is now available.");
    }

    // View All Reservations
    static void viewReservations() throws SQLException {
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM Reservations");
        System.out.println("\n--- Reservations ---");
        while (rs.next()) {
            System.out.printf("%s | %s | Trip: %s | Seat: %d | Status: %s%n",
                    rs.getString("reservation_id"), rs.getString("name"),
                    rs.getString("trip_id"), rs.getInt("seat_no"),
                    rs.getString("payment_status"));
        }
    }

    static void viewTripsWithAvailableSeats() throws SQLException {
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM Trips WHERE available_seats > 0");
        System.out.println("\n--- Trips with Available Seats ---");
        while (rs.next()) {
            System.out.printf("%s | %s | %s %s | Seats: %d/%d | Fare: %.2f%n",
                    rs.getString("trip_id"), rs.getString("route"),
                    rs.getString("date"), rs.getString("time"),
                    rs.getInt("available_seats"), rs.getInt("total_seats"),
                    rs.getDouble("fare"));
        }
    }

    static void viewReservationsPerTrip(Scanner sc) throws SQLException {
        System.out.print("Enter Trip ID: ");
        String tripID = sc.nextLine();

        PreparedStatement ps = conn.prepareStatement(
                "SELECT * FROM Reservations WHERE trip_id = ?");
        ps.setString(1, tripID);
        ResultSet rs = ps.executeQuery();

        System.out.println("\n--- Reservations for Trip " + tripID + " ---");
        while (rs.next()) {
            System.out.printf("%s | %s | Seat: %d | Status: %s%n",
                    rs.getString("reservation_id"), rs.getString("name"),
                    rs.getInt("seat_no"), rs.getString("payment_status"));
        }
    }

    static void searchReservation(Scanner sc) throws SQLException {
        System.out.print("Enter Passenger Name or Reservation ID: ");
        String search = sc.nextLine();

        PreparedStatement ps = conn.prepareStatement(
                "SELECT * FROM Reservations WHERE name LIKE ? OR reservation_id = ?");
        ps.setString(1, "%" + search + "%");
        ps.setString(2, search);
        ResultSet rs = ps.executeQuery();

        System.out.println("\n--- Search Results ---");
        while (rs.next()) {
            System.out.printf("%s | %s | Trip: %s | Seat: %d | Status: %s%n",
                    rs.getString("reservation_id"), rs.getString("name"),
                    rs.getString("trip_id"), rs.getInt("seat_no"),
                    rs.getString("payment_status"));
        }
    }

    // Helper: Check if Trip Exists
    static boolean tripExists(String tripID) throws SQLException {
        PreparedStatement ps = conn.prepareStatement(
                "SELECT trip_id FROM Trips WHERE trip_id=?");
        ps.setString(1, tripID);
        ResultSet rs = ps.executeQuery();
        return rs.next();
    }
}
