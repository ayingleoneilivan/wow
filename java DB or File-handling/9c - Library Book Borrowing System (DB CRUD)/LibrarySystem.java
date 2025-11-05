import java.sql.*;
import java.util.Scanner;
import java.time.LocalDate;

public class LibrarySystem {
    static final String DB_URL = "jdbc:sqlite:library.db";
    static Connection conn;
    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        try {
            conn = DriverManager.getConnection(DB_URL);
            createTables();
            int choice;
            do {
                System.out.println("\n===== Library Book Borrowing System =====");
                System.out.println("1. Add Book");
                System.out.println("2. View Books");
                System.out.println("3. Update Book");
                System.out.println("4. Delete Book");
                System.out.println("5. Borrow Book");
                System.out.println("6. Return Book");
                System.out.println("7. View Borrowed Books");
                System.out.println("8. Exit");
                System.out.print("Choose an option: ");
                choice = sc.nextInt();
                sc.nextLine();

                switch (choice) {
                    case 1 -> addBook();
                    case 2 -> viewBooks();
                    case 3 -> updateBook();
                    case 4 -> deleteBook();
                    case 5 -> borrowBook();
                    case 6 -> returnBook();
                    case 7 -> viewBorrowedBooks();
                    case 8 -> System.out.println("Exiting...");
                    default -> System.out.println("Invalid choice!");
                }
            } while (choice != 8);

        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }

    // Create database tables if not exist
    static void createTables() throws SQLException {
        String booksTable = "CREATE TABLE IF NOT EXISTS Books (" +
                "BookID TEXT PRIMARY KEY, Title TEXT, Author TEXT, Year INTEGER, Status TEXT DEFAULT 'Available')";
        String transTable = "CREATE TABLE IF NOT EXISTS Transactions (" +
                "TransID INTEGER PRIMARY KEY AUTOINCREMENT, BookID TEXT, BorrowerName TEXT, " +
                "DateBorrowed TEXT, DateReturned TEXT, FOREIGN KEY(BookID) REFERENCES Books(BookID))";
        try (Statement stmt = conn.createStatement()) {
            stmt.execute(booksTable);
            stmt.execute(transTable);
        }
    }

    static void addBook() {
        try {
            System.out.print("Enter BookID: ");
            String id = sc.nextLine();
            System.out.print("Enter Title: ");
            String title = sc.nextLine();
            System.out.print("Enter Author: ");
            String author = sc.nextLine();
            System.out.print("Enter Year: ");
            int year = sc.nextInt();
            sc.nextLine();

            String sql = "INSERT INTO Books (BookID, Title, Author, Year, Status) VALUES (?, ?, ?, ?, 'Available')";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, id);
            ps.setString(2, title);
            ps.setString(3, author);
            ps.setInt(4, year);
            ps.executeUpdate();

            System.out.println("Book added successfully!");
        } catch (SQLException e) {
            System.out.println("Error adding book: " + e.getMessage());
        }
    }

    static void viewBooks() {
        try {
            String sql = "SELECT * FROM Books";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            System.out.printf("%-10s %-30s %-20s %-6s %-10s%n", "BookID", "Title", "Author", "Year", "Status");
            System.out.println("--------------------------------------------------------------------------");
            while (rs.next()) {
                System.out.printf("%-10s %-30s %-20s %-6d %-10s%n",
                        rs.getString("BookID"), rs.getString("Title"), rs.getString("Author"),
                        rs.getInt("Year"), rs.getString("Status"));
            }
        } catch (SQLException e) {
            System.out.println("Error viewing books: " + e.getMessage());
        }
    }

    static void updateBook() {
        try {
            System.out.print("Enter BookID to update: ");
            String id = sc.nextLine();
            System.out.print("Enter new Title: ");
            String title = sc.nextLine();
            System.out.print("Enter new Author: ");
            String author = sc.nextLine();
            System.out.print("Enter new Year: ");
            int year = sc.nextInt();
            sc.nextLine();

            String sql = "UPDATE Books SET Title=?, Author=?, Year=? WHERE BookID=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, title);
            ps.setString(2, author);
            ps.setInt(3, year);
            ps.setString(4, id);
            int rows = ps.executeUpdate();

            if (rows > 0)
                System.out.println("Book updated successfully!");
            else
                System.out.println("Book not found.");
        } catch (SQLException e) {
            System.out.println("Error updating book: " + e.getMessage());
        }
    }

    static void deleteBook() {
        try {
            System.out.print("Enter BookID to delete: ");
            String id = sc.nextLine();
            String sql = "DELETE FROM Books WHERE BookID=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, id);
            int rows = ps.executeUpdate();

            if (rows > 0)
                System.out.println("Book deleted successfully!");
            else
                System.out.println("Book not found.");
        } catch (SQLException e) {
            System.out.println("Error deleting book: " + e.getMessage());
        }
    }

    static void borrowBook() {
        try {
            System.out.print("Enter BookID to borrow: ");
            String id = sc.nextLine();
            System.out.print("Enter Borrower Name: ");
            String borrower = sc.nextLine();

            String check = "SELECT Status FROM Books WHERE BookID=?";
            PreparedStatement psCheck = conn.prepareStatement(check);
            psCheck.setString(1, id);
            ResultSet rs = psCheck.executeQuery();

            if (!rs.next()) {
                System.out.println("Book not found.");
                return;
            }
            if (rs.getString("Status").equalsIgnoreCase("Borrowed")) {
                System.out.println("Book already borrowed!");
                return;
            }

            String borrowSQL = "INSERT INTO Transactions (BookID, BorrowerName, DateBorrowed) VALUES (?, ?, ?)";
            PreparedStatement psBorrow = conn.prepareStatement(borrowSQL);
            psBorrow.setString(1, id);
            psBorrow.setString(2, borrower);
            psBorrow.setString(3, LocalDate.now().toString());
            psBorrow.executeUpdate();

            String updateStatus = "UPDATE Books SET Status='Borrowed' WHERE BookID=?";
            PreparedStatement psUpdate = conn.prepareStatement(updateStatus);
            psUpdate.setString(1, id);
            psUpdate.executeUpdate();

            System.out.println("Book borrowed successfully!");
        } catch (SQLException e) {
            System.out.println("Error borrowing book: " + e.getMessage());
        }
    }

    static void returnBook() {
        try {
            System.out.print("Enter BookID to return: ");
            String id = sc.nextLine();

            String sql = "SELECT Status FROM Books WHERE BookID=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();

            if (!rs.next()) {
                System.out.println("Book not found.");
                return;
            }
            if (rs.getString("Status").equalsIgnoreCase("Available")) {
                System.out.println("Book is not currently borrowed.");
                return;
            }

            String updateTrans = "UPDATE Transactions SET DateReturned=? WHERE BookID=? AND DateReturned IS NULL";
            PreparedStatement psTrans = conn.prepareStatement(updateTrans);
            psTrans.setString(1, LocalDate.now().toString());
            psTrans.setString(2, id);
            psTrans.executeUpdate();

            String updateBook = "UPDATE Books SET Status='Available' WHERE BookID=?";
            PreparedStatement psBook = conn.prepareStatement(updateBook);
            psBook.setString(1, id);
            psBook.executeUpdate();

            System.out.println("Book returned successfully!");
        } catch (SQLException e) {
            System.out.println("Error returning book: " + e.getMessage());
        }
    }

    static void viewBorrowedBooks() {
        try {
            String sql = "SELECT b.BookID, b.Title, t.BorrowerName, t.DateBorrowed " +
                    "FROM Books b JOIN Transactions t ON b.BookID = t.BookID WHERE b.Status='Borrowed'";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            System.out.printf("%-10s %-30s %-20s %-12s%n", "BookID", "Title", "Borrower", "DateBorrowed");
            System.out.println("--------------------------------------------------------------------------");
            while (rs.next()) {
                System.out.printf("%-10s %-30s %-20s %-12s%n",
                        rs.getString("BookID"), rs.getString("Title"),
                        rs.getString("BorrowerName"), rs.getString("DateBorrowed"));
            }
        } catch (SQLException e) {
            System.out.println("Error viewing borrowed books: " + e.getMessage());
        }
    }
}
