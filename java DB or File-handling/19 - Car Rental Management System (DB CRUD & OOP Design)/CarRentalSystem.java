import java.sql.*;
import java.util.*;
import java.time.*;
import java.time.temporal.ChronoUnit;

abstract class Car {
    protected String plateNumber, model, type;
    protected double dailyRate;
    protected boolean available;

    public Car(String plateNumber, String model, String type, double dailyRate, boolean available) {
        this.plateNumber = plateNumber;
        this.model = model;
        this.type = type;
        this.dailyRate = dailyRate;
        this.available = available;
    }

    public String getPlateNumber() { return plateNumber; }
    public String getModel() { return model; }
    public String getType() { return type; }
    public double getDailyRate() { return dailyRate; }
    public boolean isAvailable() { return available; }

    public void setAvailable(boolean available) { this.available = available; }

    public void rentCar() { this.available = false; }
    public void returnCar() { this.available = true; }

    public abstract double getRateMultiplier();

    public double calculateFee(long days) {
        return days * dailyRate * getRateMultiplier();
    }
}

class EconomyCar extends Car {
    public EconomyCar(String plate, String model, double rate, boolean avail) {
        super(plate, model, "Economy", rate, avail);
    }
    public double getRateMultiplier() { return 1.0; }
}

class SUVCar extends Car {
    public SUVCar(String plate, String model, double rate, boolean avail) {
        super(plate, model, "SUV", rate, avail);
    }
    public double getRateMultiplier() { return 1.2; }
}

class LuxuryCar extends Car {
    public LuxuryCar(String plate, String model, double rate, boolean avail) {
        super(plate, model, "Luxury", rate, avail);
    }
    public double getRateMultiplier() { return 1.5; }
}

class Customer {
    private String customerID, fullName, licenseNumber, contactInfo;

    public Customer(String id, String name, String license, String contact) {
        this.customerID = id;
        this.fullName = name;
        this.licenseNumber = license;
        this.contactInfo = contact;
    }

    public String getCustomerID() { return customerID; }
    public String getFullName() { return fullName; }
    public String getLicenseNumber() { return licenseNumber; }
    public String getContactInfo() { return contactInfo; }
}

class Rental {
    private String rentalID, customerID, plateNumber;
    private LocalDate startDate, endDate;
    private double totalFee;

    public Rental(String id, String cust, String plate, LocalDate start, LocalDate end, double fee) {
        this.rentalID = id;
        this.customerID = cust;
        this.plateNumber = plate;
        this.startDate = start;
        this.endDate = end;
        this.totalFee = fee;
    }

    public String getRentalID() { return rentalID; }
    public String getCustomerID() { return customerID; }
    public String getPlateNumber() { return plateNumber; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; }
    public double getTotalFee() { return totalFee; }
}

public class CarRentalSystem {
    static final String DB_URL = "jdbc:sqlite:car_rental.db";

    public static void main(String[] args) {
        try (Scanner sc = new Scanner(System.in)) {
            createTables();
            while (true) {
                System.out.println("\n--- Car Rental Management System ---");
                System.out.println("1. Manage Cars");
                System.out.println("2. Manage Customers");
                System.out.println("3. Manage Rentals");
                System.out.println("4. Exit");
                System.out.print("Enter choice: ");
                int choice = sc.nextInt();
                sc.nextLine();

                switch (choice) {
                    case 1 -> manageCars(sc);
                    case 2 -> manageCustomers(sc);
                    case 3 -> manageRentals(sc);
                    case 4 -> { System.out.println("Exiting..."); return; }
                    default -> System.out.println("Invalid choice.");
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
    }

    static void createTables() throws SQLException {
        try (Connection conn = DriverManager.getConnection(DB_URL);
            Statement stmt = conn.createStatement()) {

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS Cars (
                    PlateNumber TEXT PRIMARY KEY,
                    Model TEXT,
                    Type TEXT,
                    DailyRate REAL,
                    Status TEXT
                )
            """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS Customers (
                    CustomerID TEXT PRIMARY KEY,
                    FullName TEXT,
                    LicenseNumber TEXT UNIQUE,
                    ContactInfo TEXT
                )
            """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS Rentals (
                    RentalID TEXT PRIMARY KEY,
                    CustomerID TEXT,
                    PlateNumber TEXT,
                    StartDate TEXT,
                    EndDate TEXT,
                    TotalFee REAL
                )
            """);
        }
    }

    static void manageCars(Scanner sc) throws SQLException {
        while (true) {
            System.out.println("\n--- Manage Cars ---");
            System.out.println("1. Add Car");
            System.out.println("2. View Cars");
            System.out.println("3. Return to Main");
            System.out.print("Choice: ");
            int c = sc.nextInt();
            sc.nextLine();

            if (c == 3) break;

            switch (c) {
                case 1 -> addCar(sc);
                case 2 -> viewCars();
                default -> System.out.println("Invalid option.");
            }
        }
    }

    static void addCar(Scanner sc) throws SQLException {
        System.out.print("Plate Number: "); String plate = sc.nextLine();
        System.out.print("Model: "); String model = sc.nextLine();
        System.out.print("Type (Economy/SUV/Luxury): "); String type = sc.nextLine();
        System.out.print("Daily Rate: "); double rate = sc.nextDouble(); sc.nextLine();

        try (Connection conn = DriverManager.getConnection(DB_URL);
            PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO Cars VALUES (?, ?, ?, ?, 'Available')")) {
            ps.setString(1, plate);
            ps.setString(2, model);
            ps.setString(3, type);
            ps.setDouble(4, rate);
            ps.executeUpdate();
            System.out.println("Car added successfully.");
        }
    }

    static void viewCars() throws SQLException {
        try (Connection conn = DriverManager.getConnection(DB_URL);
            Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM Cars")) {

            System.out.println("\n--- Cars ---");
            while (rs.next()) {
                System.out.printf("[%s] %s - %s - %.2f/day - %s%n",
                    rs.getString("PlateNumber"),
                    rs.getString("Model"),
                    rs.getString("Type"),
                    rs.getDouble("DailyRate"),
                    rs.getString("Status"));
            }
        }
    }

    static void manageCustomers(Scanner sc) throws SQLException {
        while (true) {
            System.out.println("\n--- Manage Customers ---");
            System.out.println("1. Add Customer");
            System.out.println("2. View Customers");
            System.out.println("3. Return");
            System.out.print("Choice: ");
            int c = sc.nextInt(); sc.nextLine();

            if (c == 3) break;
            switch (c) {
                case 1 -> addCustomer(sc);
                case 2 -> viewCustomers();
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    static void addCustomer(Scanner sc) throws SQLException {
        System.out.print("Customer ID: "); String id = sc.nextLine();
        System.out.print("Full Name: "); String name = sc.nextLine();
        System.out.print("License Number: "); String license = sc.nextLine();
        System.out.print("Contact Info: "); String contact = sc.nextLine();

        if (license.isEmpty()) {
            System.out.println("License number cannot be empty.");
            return;
        }

        try (Connection conn = DriverManager.getConnection(DB_URL);
            PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO Customers VALUES (?, ?, ?, ?)")) {
            ps.setString(1, id);
            ps.setString(2, name);
            ps.setString(3, license);
            ps.setString(4, contact);
            ps.executeUpdate();
            System.out.println("Customer added successfully.");
        }
    }

    static void viewCustomers() throws SQLException {
        try (Connection conn = DriverManager.getConnection(DB_URL);
            Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM Customers")) {
            while (rs.next()) {
                System.out.printf("[%s] %s - License: %s - Contact: %s%n",
                    rs.getString("CustomerID"),
                    rs.getString("FullName"),
                    rs.getString("LicenseNumber"),
                    rs.getString("ContactInfo"));
            }
        }
    }

    static void manageRentals(Scanner sc) throws SQLException {
        while (true) {
            System.out.println("\n--- Manage Rentals ---");
            System.out.println("1. Rent Car");
            System.out.println("2. Return Car");
            System.out.println("3. View Rentals");
            System.out.println("4. Back");
            System.out.print("Choice: ");
            int c = sc.nextInt(); sc.nextLine();
            if (c == 4) break;

            switch (c) {
                case 1 -> rentCar(sc);
                case 2 -> returnCar(sc);
                case 3 -> viewRentals();
                default -> System.out.println("Invalid option.");
            }
        }
    }

    static void rentCar(Scanner sc) throws SQLException {
        System.out.print("Rental ID: "); String rid = sc.nextLine();
        System.out.print("Customer ID: "); String cid = sc.nextLine();
        System.out.print("Car Plate Number: "); String plate = sc.nextLine();

        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            PreparedStatement psCar = conn.prepareStatement("SELECT * FROM Cars WHERE PlateNumber=?");
            psCar.setString(1, plate);
            ResultSet rsCar = psCar.executeQuery();

            if (!rsCar.next()) { System.out.println("Car not found."); return; }
            if (rsCar.getString("Status").equalsIgnoreCase("Rented")) {
                System.out.println("Car already rented!");
                return;
            }

            System.out.print("Start Date (YYYY-MM-DD): ");
            LocalDate start = LocalDate.parse(sc.nextLine());
            System.out.print("End Date (YYYY-MM-DD): ");
            LocalDate end = LocalDate.parse(sc.nextLine());

            if (!end.isAfter(start)) {
                System.out.println("End date must be after start date.");
                return;
            }

            long days = ChronoUnit.DAYS.between(start, end);
            double dailyRate = rsCar.getDouble("DailyRate");

            double totalFee = days * dailyRate;
            PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO Rentals VALUES (?, ?, ?, ?, ?, ?)");
            ps.setString(1, rid);
            ps.setString(2, cid);
            ps.setString(3, plate);
            ps.setString(4, start.toString());
            ps.setString(5, end.toString());
            ps.setDouble(6, totalFee);
            ps.executeUpdate();

            PreparedStatement update = conn.prepareStatement(
                "UPDATE Cars SET Status='Rented' WHERE PlateNumber=?");
            update.setString(1, plate);
            update.executeUpdate();

            System.out.println("Rental successful. Fee: " + totalFee);
        }
    }

    static void returnCar(Scanner sc) throws SQLException {
        System.out.print("Rental ID: "); String rid = sc.nextLine();
        System.out.print("Car Plate Number: "); String plate = sc.nextLine();

        try (Connection conn = DriverManager.getConnection(DB_URL)) {
            PreparedStatement ps = conn.prepareStatement("DELETE FROM Rentals WHERE RentalID=?");
            ps.setString(1, rid);
            int deleted = ps.executeUpdate();

            if (deleted > 0) {
                PreparedStatement update = conn.prepareStatement("UPDATE Cars SET Status='Available' WHERE PlateNumber=?");
                update.setString(1, plate);
                update.executeUpdate();
                System.out.println("Car returned successfully.");
            } else {
                System.out.println("Rental not found.");
            }
        }
    }

    static void viewRentals() throws SQLException {
        try (Connection conn = DriverManager.getConnection(DB_URL);
            Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM Rentals")) {

            System.out.println("\n--- Rentals ---");
            while (rs.next()) {
                System.out.printf("[%s] Cust:%s Car:%s %s → %s | Fee: %.2f%n",
                    rs.getString("RentalID"),
                    rs.getString("CustomerID"),
                    rs.getString("PlateNumber"),
                    rs.getString("StartDate"),
                    rs.getString("EndDate"),
                    rs.getDouble("TotalFee"));
            }
        }
    }
}
