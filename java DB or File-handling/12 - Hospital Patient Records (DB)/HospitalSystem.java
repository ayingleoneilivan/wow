import java.sql.*;
import java.util.Scanner;

public class HospitalSystem {
    private static final String DB_URL = "jdbc:sqlite:hospital.db";
    private static final Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        createTable();
        int choice;
        do {
            System.out.println("\n===== Hospital Patient Records System =====");
            System.out.println("1. Add Patient");
            System.out.println("2. View Patients");
            System.out.println("3. Search Patient");
            System.out.println("4. Update Patient");
            System.out.println("5. Delete Patient");
            System.out.println("6. Reports");
            System.out.println("7. Exit");
            System.out.print("Enter choice: ");
            choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1 -> addPatient();
                case 2 -> viewPatients();
                case 3 -> searchPatient();
                case 4 -> updatePatient();
                case 5 -> deletePatient();
                case 6 -> reportsMenu();
                case 7 -> System.out.println("Exiting...");
                default -> System.out.println("Invalid choice!");
            }
        } while (choice != 7);
    }

    private static void createTable() {
        String sql = """
            CREATE TABLE IF NOT EXISTS Patients (
                patient_id VARCHAR(20) PRIMARY KEY,
                full_name VARCHAR(100),
                age INT,
                gender VARCHAR(10),
                diagnosis VARCHAR(100),
                admission_date DATE,
                discharge_date DATE
            );
        """;
        try (Connection conn = DriverManager.getConnection(DB_URL);
            Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println("Error creating table: " + e.getMessage());
        }
    }

    private static void addPatient() {
        System.out.print("Enter Patient ID: ");
        String id = sc.nextLine();
        System.out.print("Enter Full Name: ");
        String name = sc.nextLine();
        System.out.print("Enter Age: ");
        int age = sc.nextInt();
        sc.nextLine();
        if (age <= 0) {
            System.out.println("Error: Age must be positive.");
            return;
        }
        System.out.print("Enter Gender (Male/Female): ");
        String gender = sc.nextLine();
        if (!gender.equalsIgnoreCase("Male") && !gender.equalsIgnoreCase("Female")) {
            System.out.println("Error: Invalid gender.");
            return;
        }
        System.out.print("Enter Diagnosis: ");
        String diagnosis = sc.nextLine();
        System.out.print("Enter Admission Date (YYYY-MM-DD): ");
        String admit = sc.nextLine();
        System.out.print("Enter Discharge Date (YYYY-MM-DD or blank if still admitted): ");
        String discharge = sc.nextLine();
        if (discharge.isEmpty()) discharge = null;

        String sql = "INSERT INTO Patients VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL);
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            pstmt.setString(2, name);
            pstmt.setInt(3, age);
            pstmt.setString(4, gender);
            pstmt.setString(5, diagnosis);
            pstmt.setString(6, admit);
            pstmt.setString(7, discharge);
            pstmt.executeUpdate();
            System.out.println("Patient record successfully added!");
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }

    private static void viewPatients() {
        String sql = "SELECT * FROM Patients";
        try (Connection conn = DriverManager.getConnection(DB_URL);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)) {

            System.out.printf("%-10s %-20s %-5s %-8s %-15s %-15s %-15s%n",
                    "PatientID", "Name", "Age", "Gender", "Diagnosis", "AdmitDate", "DischargeDate");
            System.out.println("-------------------------------------------------------------------------------------------");

            while (rs.next()) {
                System.out.printf("%-10s %-20s %-5d %-8s %-15s %-15s %-15s%n",
                        rs.getString("patient_id"),
                        rs.getString("full_name"),
                        rs.getInt("age"),
                        rs.getString("gender"),
                        rs.getString("diagnosis"),
                        rs.getString("admission_date"),
                        rs.getString("discharge_date") == null ? "—" : rs.getString("discharge_date"));
            }

        } catch (SQLException e) {
            System.out.println("Error retrieving records: " + e.getMessage());
        }
    }

    private static void searchPatient() {
        System.out.print("Enter Patient ID or Name: ");
        String input = sc.nextLine();
        String sql = "SELECT * FROM Patients WHERE patient_id = ? OR full_name LIKE ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, input);
            pstmt.setString(2, "%" + input + "%");
            ResultSet rs = pstmt.executeQuery();

            if (!rs.isBeforeFirst()) {
                System.out.println("No patient found!");
                return;
            }

            while (rs.next()) {
                System.out.printf("""
                    Patient ID: %s
                    Name: %s
                    Age: %d
                    Gender: %s
                    Diagnosis: %s
                    Admission Date: %s
                    Discharge Date: %s
                    ------------------------------------------
                    """,
                        rs.getString("patient_id"),
                        rs.getString("full_name"),
                        rs.getInt("age"),
                        rs.getString("gender"),
                        rs.getString("diagnosis"),
                        rs.getString("admission_date"),
                        rs.getString("discharge_date") == null ? "Still Admitted" : rs.getString("discharge_date"));
            }
        } catch (SQLException e) {
            System.out.println("Error searching patient: " + e.getMessage());
        }
    }

    private static void updatePatient() {
        System.out.print("Enter Patient ID to update: ");
        String id = sc.nextLine();
        System.out.print("Enter new Diagnosis: ");
        String diagnosis = sc.nextLine();
        System.out.print("Enter new Discharge Date (YYYY-MM-DD or blank if still admitted): ");
        String discharge = sc.nextLine();
        if (discharge.isEmpty()) discharge = null;

        String sql = "UPDATE Patients SET diagnosis = ?, discharge_date = ? WHERE patient_id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, diagnosis);
            pstmt.setString(2, discharge);
            pstmt.setString(3, id);
            int rows = pstmt.executeUpdate();
            if (rows > 0)
                System.out.println("Patient record updated successfully!");
            else
                System.out.println("Patient not found.");
        } catch (SQLException e) {
            System.out.println("Error updating record: " + e.getMessage());
        }
    }

    private static void deletePatient() {
        System.out.print("Enter Patient ID to delete: ");
        String id = sc.nextLine();
        String sql = "DELETE FROM Patients WHERE patient_id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            int rows = pstmt.executeUpdate();
            if (rows > 0)
                System.out.println("Patient record deleted successfully!");
            else
                System.out.println("Patient not found!");
        } catch (SQLException e) {
            System.out.println("Error deleting record: " + e.getMessage());
        }
    }

    private static void reportsMenu() {
        System.out.println("\nReports Menu:");
        System.out.println("1. Currently Admitted Patients");
        System.out.println("2. Count Patients per Diagnosis");
        System.out.print("Enter choice: ");
        int choice = sc.nextInt();
        sc.nextLine();

        switch (choice) {
            case 1 -> showAdmittedPatients();
            case 2 -> countPatientsByDiagnosis();
            default -> System.out.println("Invalid choice!");
        }
    }

    private static void showAdmittedPatients() {
        String sql = "SELECT patient_id, full_name, diagnosis FROM Patients WHERE discharge_date IS NULL";
        try (Connection conn = DriverManager.getConnection(DB_URL);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)) {

            System.out.printf("%-10s %-20s %-20s%n", "PatientID", "Name", "Diagnosis");
            System.out.println("----------------------------------------------------------");
            while (rs.next()) {
                System.out.printf("%-10s %-20s %-20s%n",
                        rs.getString("patient_id"),
                        rs.getString("full_name"),
                        rs.getString("diagnosis"));
            }

        } catch (SQLException e) {
            System.out.println("Error generating report: " + e.getMessage());
        }
    }

    private static void countPatientsByDiagnosis() {
        String sql = "SELECT diagnosis, COUNT(*) AS total FROM Patients GROUP BY diagnosis";
        try (Connection conn = DriverManager.getConnection(DB_URL);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)) {

            System.out.printf("%-20s %-10s%n", "Diagnosis", "Count");
            System.out.println("------------------------------------");
            while (rs.next()) {
                System.out.printf("%-20s %-10d%n",
                        rs.getString("diagnosis"),
                        rs.getInt("total"));
            }

        } catch (SQLException e) {
            System.out.println("Error generating report: " + e.getMessage());
        }
    }
}

