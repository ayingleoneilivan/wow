import java.sql.*;
import java.util.Scanner;

public class InventorySystem {
    private static final String DB_URL = "jdbc:sqlite:inventory.db";
    private static final Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        createTable();
        int choice;
        do {
            System.out.println("\n===== Inventory Management System =====");
            System.out.println("1. Add Product");
            System.out.println("2. View Products");
            System.out.println("3. Search Product");
            System.out.println("4. Update Product");
            System.out.println("5. Delete Product");
            System.out.println("6. Reports");
            System.out.println("7. Exit");
            System.out.print("Enter choice: ");
            choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1 -> addProduct();
                case 2 -> viewProducts();
                case 3 -> searchProduct();
                case 4 -> updateProduct();
                case 5 -> deleteProduct();
                case 6 -> showReports();
                case 7 -> System.out.println("Exiting...");
                default -> System.out.println("Invalid choice!");
            }
        } while (choice != 7);
    }

    private static void createTable() {
        String sql = """
            CREATE TABLE IF NOT EXISTS Products (
                product_id VARCHAR(20) PRIMARY KEY,
                product_name VARCHAR(100),
                category VARCHAR(50),
                quantity INT CHECK(quantity >= 0),
                unit_price DECIMAL(10,2) CHECK(unit_price > 0)
            );
        """;
        try (Connection conn = DriverManager.getConnection(DB_URL);
            Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println("Error creating table: " + e.getMessage());
        }
    }

    private static void addProduct() {
        System.out.print("Enter Product ID: ");
        String id = sc.nextLine();
        System.out.print("Enter Product Name: ");
        String name = sc.nextLine();
        System.out.print("Enter Category: ");
        String category = sc.nextLine();
        System.out.print("Enter Quantity: ");
        int qty = sc.nextInt();
        System.out.print("Enter Unit Price: ");
        double price = sc.nextDouble();
        sc.nextLine();

        if (qty < 0 || price <= 0) {
            System.out.println("Invalid quantity or price.");
            return;
        }

        String sql = "INSERT INTO Products VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL);
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            pstmt.setString(2, name);
            pstmt.setString(3, category);
            pstmt.setInt(4, qty);
            pstmt.setDouble(5, price);
            pstmt.executeUpdate();
            System.out.println("Product successfully added!");
        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
        }
    }

    private static void viewProducts() {
        String sql = "SELECT * FROM Products";
        try (Connection conn = DriverManager.getConnection(DB_URL);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql)) {

            System.out.printf("%-10s %-20s %-15s %-10s %-10s%n",
                    "ProductID", "Name", "Category", "Qty", "Price");
            System.out.println("--------------------------------------------------------------");

            while (rs.next()) {
                System.out.printf("%-10s %-20s %-15s %-10d %-10.2f%n",
                        rs.getString("product_id"),
                        rs.getString("product_name"),
                        rs.getString("category"),
                        rs.getInt("quantity"),
                        rs.getDouble("unit_price"));
            }

        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void searchProduct() {
        System.out.print("Enter Product ID or Name: ");
        String input = sc.nextLine();
        String sql = "SELECT * FROM Products WHERE product_id = ? OR product_name LIKE ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, input);
            pstmt.setString(2, "%" + input + "%");
            ResultSet rs = pstmt.executeQuery();

            if (!rs.isBeforeFirst()) {
                System.out.println("No product found!");
                return;
            }

            while (rs.next()) {
                System.out.printf("ID: %s | Name: %s | Category: %s | Qty: %d | Price: %.2f%n",
                        rs.getString("product_id"),
                        rs.getString("product_name"),
                        rs.getString("category"),
                        rs.getInt("quantity"),
                        rs.getDouble("unit_price"));
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void updateProduct() {
        System.out.print("Enter Product ID to update: ");
        String id = sc.nextLine();

        System.out.print("Enter new Quantity: ");
        int qty = sc.nextInt();
        System.out.print("Enter new Unit Price: ");
        double price = sc.nextDouble();
        sc.nextLine();

        String sql = "UPDATE Products SET quantity = ?, unit_price = ? WHERE product_id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, qty);
            pstmt.setDouble(2, price);
            pstmt.setString(3, id);
            int rows = pstmt.executeUpdate();

            if (rows > 0)
                System.out.println("Product updated successfully!");
            else
                System.out.println("Product not found!");

        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void deleteProduct() {
        System.out.print("Enter Product ID to delete: ");
        String id = sc.nextLine();
        String sql = "DELETE FROM Products WHERE product_id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            int rows = pstmt.executeUpdate();
            if (rows > 0)
                System.out.println("Product deleted successfully!");
            else
                System.out.println("Product not found!");
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void showReports() {
        System.out.println("\nReports Menu:");
        System.out.println("1. Low Stock Report");
        System.out.println("2. Total Inventory Value");
        System.out.print("Enter choice: ");
        int choice = sc.nextInt();
        sc.nextLine();

        switch (choice) {
            case 1 -> {
                System.out.println("Low Stock Products (<10):");
                String sql = "SELECT * FROM Products WHERE quantity < 10";
                try (Connection conn = DriverManager.getConnection(DB_URL);
                    Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery(sql)) {

                    System.out.printf("%-10s %-20s %-10s%n", "ID", "Name", "Qty");
                    System.out.println("------------------------------------");
                    while (rs.next()) {
                        System.out.printf("%-10s %-20s %-10d%n",
                                rs.getString("product_id"),
                                rs.getString("product_name"),
                                rs.getInt("quantity"));
                    }
                } catch (SQLException e) {
                    System.out.println("Error: " + e.getMessage());
                }
            }
            case 2 -> {
                String sql = "SELECT SUM(quantity * unit_price) AS total_value FROM Products";
                try (Connection conn = DriverManager.getConnection(DB_URL);
                    Statement stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery(sql)) {

                    if (rs.next())
                        System.out.printf("Total Inventory Value: %.2f%n", rs.getDouble("total_value"));
                } catch (SQLException e) {
                    System.out.println("Error: " + e.getMessage());
                }
            }
            default -> System.out.println("Invalid choice!");
        }
    }
}
