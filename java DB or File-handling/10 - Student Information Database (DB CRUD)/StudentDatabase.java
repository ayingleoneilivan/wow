import java.sql.*;
import java.util.Scanner;

public class StudentDatabase {
    private static final String DB_URL = "jdbc:sqlite:students.db";
    private static Connection conn;
    private static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        try {
            // Load driver
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection(DB_URL);
            createTable();

            int choice;
            do {
                System.out.println("\n===== Student Information System =====");
                System.out.println("1. Add Student");
                System.out.println("2. View Students");
                System.out.println("3. Search Student");
                System.out.println("4. Update Student");
                System.out.println("5. Delete Student");
                System.out.println("6. Reports");
                System.out.println("7. Exit");
                System.out.print("Enter choice: ");
                choice = sc.nextInt();
                sc.nextLine();

                switch (choice) {
                    case 1 -> addStudent();
                    case 2 -> viewStudents();
                    case 3 -> searchStudent();
                    case 4 -> updateStudent();
                    case 5 -> deleteStudent();
                    case 6 -> showReports();
                    case 7 -> System.out.println("Exiting...");
                    default -> System.out.println("Invalid choice.");
                }
            } while (choice != 7);

            conn.close();
        } catch (Exception e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }

    private static void createTable() throws SQLException {
      String sql = "CREATE TABLE IF NOT EXISTS Students (" +
                    "student_id VARCHAR(20) PRIMARY KEY," +
                    "first_name VARCHAR(50)," +
                    "last_name VARCHAR(50)," +
                    "course VARCHAR(50)," +
                    "year_level INT" +
                    ");";
        conn.createStatement().execute(sql);
    }

    private static void addStudent() throws SQLException {
        System.out.print("Enter Student ID: ");
        String id = sc.nextLine();
        System.out.print("Enter First Name: ");
        String first = sc.nextLine();
        System.out.print("Enter Last Name: ");
        String last = sc.nextLine();
        System.out.print("Enter Course: ");
        String course = sc.nextLine();
        System.out.print("Enter Year Level (1-5): ");
        int year = sc.nextInt();
        sc.nextLine();

        if (year < 1 || year > 5) {
            System.out.println("Year level must be between 1 and 5!");
            return;
        }

        String checkSql = "SELECT * FROM Students WHERE student_id = ?";
        PreparedStatement checkStmt = conn.prepareStatement(checkSql);
        checkStmt.setString(1, id);
        ResultSet rs = checkStmt.executeQuery();
        if (rs.next()) {
            System.out.println("Error: Student ID already exists!");
            return;
        }

        String sql = "INSERT INTO Students VALUES (?, ?, ?, ?, ?)";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, id);
        pstmt.setString(2, first);
        pstmt.setString(3, last);
        pstmt.setString(4, course);
        pstmt.setInt(5, year);
        pstmt.executeUpdate();
        System.out.println("Student successfully added!");
    }

    private static void viewStudents() throws SQLException {
        String sql = "SELECT * FROM Students";
        ResultSet rs = conn.createStatement().executeQuery(sql);

        System.out.println("\nStudentID    First Name      Last Name       Course      Year");
        System.out.println("------------------------------------------------------------");
        while (rs.next()) {
            System.out.printf("%-12s%-15s%-15s%-12s%-5d\n",
                    rs.getString("student_id"),
                    rs.getString("first_name"),
                    rs.getString("last_name"),
                    rs.getString("course"),
                    rs.getInt("year_level"));
        }
    }

    private static void searchStudent() throws SQLException {
        System.out.print("Enter Student ID to search: ");
        String id = sc.nextLine();

        String sql = "SELECT * FROM Students WHERE student_id = ?";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, id);
        ResultSet rs = pstmt.executeQuery();

        if (rs.next()) {
            System.out.println("Student found:");
            System.out.printf("%s - %s %s (%s, Year %d)\n",
                    rs.getString("student_id"),
                    rs.getString("first_name"),
                    rs.getString("last_name"),
                    rs.getString("course"),
                    rs.getInt("year_level"));
        } else {
            System.out.println("No student found with ID " + id);
        }
    }

    private static void updateStudent() throws SQLException {
        System.out.print("Enter Student ID to update: ");
        String id = sc.nextLine();

        System.out.print("Enter new Course: ");
        String course = sc.nextLine();
        System.out.print("Enter new Year Level (1–5): ");
        int year = sc.nextInt();
        sc.nextLine();

        String sql = "UPDATE Students SET course = ?, year_level = ? WHERE student_id = ?";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, course);
        pstmt.setInt(2, year);
        pstmt.setString(3, id);

        int rows = pstmt.executeUpdate();
        if (rows > 0)
            System.out.println("Record updated successfully!");
        else
            System.out.println("Student not found.");
    }

    private static void deleteStudent() throws SQLException {
        System.out.print("Enter Student ID to delete: ");
        String id = sc.nextLine();

        String sql = "DELETE FROM Students WHERE student_id = ?";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, id);
        int rows = pstmt.executeUpdate();
        if (rows > 0)
            System.out.println("Record deleted successfully!");
        else
            System.out.println("Student not found.");
    }

    private static void showReports() throws SQLException {
        System.out.println("\nReports Menu:");
        System.out.println("1. Count students per course");
        System.out.println("2. List students per year level");
        System.out.print("Choice: ");
        int ch = sc.nextInt();
        sc.nextLine();

        switch (ch) {
            case 1 -> {
                String sql = "SELECT course, COUNT(*) as count FROM Students GROUP BY course";
                ResultSet rs = conn.createStatement().executeQuery(sql);
                System.out.println("\nCourse         Count");
                System.out.println("----------------------");
                while (rs.next()) {
                    System.out.printf("%-15s%-5d\n", rs.getString("course"), rs.getInt("count"));
                }
            }
            case 2 -> {
                String sql = "SELECT year_level, student_id, first_name, last_name, course FROM Students ORDER BY year_level";
                ResultSet rs = conn.createStatement().executeQuery(sql);
                System.out.println("\nYear    StudentID    Name                Course");
                System.out.println("-----------------------------------------------");
                while (rs.next()) {
                    System.out.printf("%-8d%-12s%-20s%-12s\n",
                            rs.getInt("year_level"),
                            rs.getString("student_id"),
                            rs.getString("first_name") + " " + rs.getString("last_name"),
                            rs.getString("course"));
                }
            }
            default -> System.out.println("Invalid choice.");
        }
    }
}
