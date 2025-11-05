import java.sql.*;
import java.util.*;
import java.time.*;

public class ClinicScheduler {
    private static final String DB_URL = "jdbc:sqlite:clinic.db";
    private static Connection conn;

    public static void main(String[] args) {
        try {
            connect();
            createTables();
            mainMenu();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void connect() throws SQLException {
        conn = DriverManager.getConnection(DB_URL);
    }

    private static void createTables() throws SQLException {
        String doctorsTable = """
            CREATE TABLE IF NOT EXISTS Doctors (
                doctor_id TEXT PRIMARY KEY,
                name TEXT,
                specialization TEXT,
                available_slots TEXT
            )
        """;

        String patientsTable = """
            CREATE TABLE IF NOT EXISTS Patients (
                patient_id TEXT PRIMARY KEY,
                name TEXT,
                contact TEXT
            )
        """;

        String appointmentsTable = """
            CREATE TABLE IF NOT EXISTS Appointments (
                appointment_id TEXT PRIMARY KEY,
                patient_id TEXT,
                doctor_id TEXT,
                date TEXT,
                timeslot TEXT,
                status TEXT,
                FOREIGN KEY(patient_id) REFERENCES Patients(patient_id),
                FOREIGN KEY(doctor_id) REFERENCES Doctors(doctor_id)
            )
        """;

        try (Statement st = conn.createStatement()) {
            st.execute(doctorsTable);
            st.execute(patientsTable);
            st.execute(appointmentsTable);
        }
    }

    private static void mainMenu() {
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println("\nClinic Appointment Scheduler");
            System.out.println("1. Admin Menu");
            System.out.println("2. Doctor Menu");
            System.out.println("3. Patient Menu");
            System.out.println("4. Exit");
            System.out.print("Enter choice: ");
            String ch = sc.nextLine();

            switch (ch) {
                case "1" -> adminMenu();
                case "2" -> doctorMenu();
                case "3" -> patientMenu();
                case "4" -> {
                    System.out.println("Exiting...");
                    return;
                }
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    // ====================== ADMIN MENU ======================
    private static void adminMenu() {
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println("\nAdmin Menu");
            System.out.println("1. Add Doctor");
            System.out.println("2. Update Doctor");
            System.out.println("3. Delete Doctor");
            System.out.println("4. View All Doctors");
            System.out.println("5. Back");
            System.out.print("Enter choice: ");
            String ch = sc.nextLine();

            switch (ch) {
                case "1" -> addDoctor();
                case "2" -> updateDoctor();
                case "3" -> deleteDoctor();
                case "4" -> viewDoctors();
                case "5" -> { return; }
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    private static void addDoctor() {
        Scanner sc = new Scanner(System.in);
        try {
            System.out.print("Doctor ID: ");
            String id = sc.nextLine();
            System.out.print("Name: ");
            String name = sc.nextLine();
            System.out.print("Specialization: ");
            String spec = sc.nextLine();
            System.out.print("Available Slots (comma-separated, e.g., 09:00,10:00,11:00): ");
            String slots = sc.nextLine();

            String sql = "INSERT INTO Doctors VALUES(?,?,?,?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, id);
            ps.setString(2, name);
            ps.setString(3, spec);
            ps.setString(4, slots);
            ps.executeUpdate();
            System.out.println("Doctor added successfully.");
        } catch (SQLException e) {
            System.out.println("Error adding doctor: " + e.getMessage());
        }
    }

    private static void updateDoctor() {
        Scanner sc = new Scanner(System.in);
        try {
            System.out.print("Enter Doctor ID to update: ");
            String id = sc.nextLine();
            System.out.print("New Available Slots: ");
            String slots = sc.nextLine();
            String sql = "UPDATE Doctors SET available_slots=? WHERE doctor_id=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, slots);
            ps.setString(2, id);
            int updated = ps.executeUpdate();
            if (updated > 0) System.out.println("Doctor updated.");
            else System.out.println("Doctor not found.");
        } catch (SQLException e) {
            System.out.println("Error updating doctor: " + e.getMessage());
        }
    }

    private static void deleteDoctor() {
        Scanner sc = new Scanner(System.in);
        try {
            System.out.print("Doctor ID to delete: ");
            String id = sc.nextLine();
            String sql = "DELETE FROM Doctors WHERE doctor_id=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, id);
            int deleted = ps.executeUpdate();
            if (deleted > 0) System.out.println("Doctor deleted.");
            else System.out.println("Doctor not found.");
        } catch (SQLException e) {
            System.out.println("Error deleting doctor: " + e.getMessage());
        }
    }

    private static void viewDoctors() {
        try (Statement st = conn.createStatement()) {
            ResultSet rs = st.executeQuery("SELECT * FROM Doctors");
            System.out.println("\nDoctors List:");
            System.out.println("-----------------------------------------------------");
            System.out.printf("%-10s %-20s %-20s %-20s%n", "ID", "Name", "Specialization", "Slots");
            while (rs.next()) {
                System.out.printf("%-10s %-20s %-20s %-20s%n",
                        rs.getString("doctor_id"), rs.getString("name"),
                        rs.getString("specialization"), rs.getString("available_slots"));
            }
        } catch (SQLException e) {
            System.out.println("Error viewing doctors: " + e.getMessage());
        }
    }

    // ====================== PATIENT MENU ======================
    private static void patientMenu() {
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println("\nPatient Menu");
            System.out.println("1. Register Patient");
            System.out.println("2. Book Appointment");
            System.out.println("3. Cancel Appointment");
            System.out.println("4. View Appointments");
            System.out.println("5. Back");
            System.out.print("Enter choice: ");
            String ch = sc.nextLine();

            switch (ch) {
                case "1" -> registerPatient();
                case "2" -> bookAppointment();
                case "3" -> cancelAppointment();
                case "4" -> viewAppointments();
                case "5" -> { return; }
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    private static void registerPatient() {
        Scanner sc = new Scanner(System.in);
        try {
            System.out.print("Patient ID: ");
            String id = sc.nextLine();
            System.out.print("Name: ");
            String name = sc.nextLine();
            System.out.print("Contact No: ");
            String contact = sc.nextLine();
            if (!contact.matches("\\d+")) {
                System.out.println("Contact must be numeric.");
                return;
            }

            String sql = "INSERT INTO Patients VALUES(?,?,?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, id);
            ps.setString(2, name);
            ps.setString(3, contact);
            ps.executeUpdate();
            System.out.println("Patient registered successfully.");
        } catch (SQLException e) {
            System.out.println("Error registering patient: " + e.getMessage());
        }
    }

    private static void bookAppointment() {
        Scanner sc = new Scanner(System.in);
        try {
            viewDoctors();
            System.out.print("Patient ID: ");
            String pid = sc.nextLine();
            System.out.print("Doctor ID: ");
            String did = sc.nextLine();
            System.out.print("Date (YYYY-MM-DD): ");
            String date = sc.nextLine();

            LocalDate apptDate = LocalDate.parse(date);
            if (apptDate.isBefore(LocalDate.now())) {
                System.out.println("Appointment date cannot be in the past!");
                return;
            }

            System.out.print("Time Slot (e.g., 09:00): ");
            String slot = sc.nextLine();
            System.out.print("Appointment ID: ");
            String aid = sc.nextLine();

            // Check overlapping
            String check = "SELECT * FROM Appointments WHERE doctor_id=? AND date=? AND timeslot=? AND status='Scheduled'";
            PreparedStatement chk = conn.prepareStatement(check);
            chk.setString(1, did);
            chk.setString(2, date);
            chk.setString(3, slot);
            ResultSet rs = chk.executeQuery();
            if (rs.next()) {
                System.out.println("That time slot is already booked!");
                return;
            }

            String sql = "INSERT INTO Appointments VALUES(?,?,?,?,?,?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, aid);
            ps.setString(2, pid);
            ps.setString(3, did);
            ps.setString(4, date);
            ps.setString(5, slot);
            ps.setString(6, "Scheduled");
            ps.executeUpdate();
            System.out.println("Appointment scheduled successfully.");
        } catch (Exception e) {
            System.out.println("Error booking appointment: " + e.getMessage());
        }
    }

    private static void cancelAppointment() {
        Scanner sc = new Scanner(System.in);
        try {
            System.out.print("Enter Appointment ID to cancel: ");
            String aid = sc.nextLine();
            String sql = "UPDATE Appointments SET status='Cancelled' WHERE appointment_id=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, aid);
            int updated = ps.executeUpdate();
            if (updated > 0) System.out.println("Appointment cancelled.");
            else System.out.println("Appointment not found.");
        } catch (SQLException e) {
            System.out.println("Error cancelling appointment: " + e.getMessage());
        }
    }

    private static void viewAppointments() {
        try (Statement st = conn.createStatement()) {
            ResultSet rs = st.executeQuery("SELECT * FROM Appointments");
            System.out.println("\nAppointments List:");
            System.out.println("----------------------------------------------------------------");
            System.out.printf("%-10s %-10s %-10s %-12s %-8s %-12s%n", 
                "AppID", "Patient", "Doctor", "Date", "Slot", "Status");
            while (rs.next()) {
                System.out.printf("%-10s %-10s %-10s %-12s %-8s %-12s%n",
                        rs.getString("appointment_id"),
                        rs.getString("patient_id"),
                        rs.getString("doctor_id"),
                        rs.getString("date"),
                        rs.getString("timeslot"),
                        rs.getString("status"));
            }
        } catch (SQLException e) {
            System.out.println("Error viewing appointments: " + e.getMessage());
        }
    }

    // ====================== DOCTOR MENU ======================
    private static void doctorMenu() {
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println("\nDoctor Menu");
            System.out.println("1. View Appointments");
            System.out.println("2. Mark Appointment as Completed");
            System.out.println("3. Back");
            System.out.print("Enter choice: ");
            String ch = sc.nextLine();

            switch (ch) {
                case "1" -> viewAppointments();
                case "2" -> markCompleted();
                case "3" -> { return; }
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    private static void markCompleted() {
        Scanner sc = new Scanner(System.in);
        try {
            System.out.print("Enter Appointment ID to mark completed: ");
            String aid = sc.nextLine();
            String sql = "UPDATE Appointments SET status='Completed' WHERE appointment_id=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, aid);
            int updated = ps.executeUpdate();
            if (updated > 0) System.out.println("Appointment marked as completed.");
            else System.out.println("Appointment not found.");
        } catch (SQLException e) {
            System.out.println("Error updating appointment: " + e.getMessage());
        }
    }
}
