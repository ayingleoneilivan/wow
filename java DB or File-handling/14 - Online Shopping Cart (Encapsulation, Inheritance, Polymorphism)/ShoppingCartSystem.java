import java.util.ArrayList;
import java.util.Scanner;

// Base Class: Product
abstract class Product {
    private String productId;
    private String productName;
    private double price;

    public Product(String productId, String productName, double price) {
        this.productId = productId;
        this.productName = productName;
        this.price = price;
    }

    public String getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public double getPrice() {
        return price;
    }

    public abstract double getFinalPrice();

    public void displayProductInfo() {
        System.out.printf("%s - %s - Original: %.2f | Final Price: %.2f%n",
                productId, productName, price, getFinalPrice());
    }
}

// Derived Class: Electronics (10% discount)
class Electronics extends Product {
    public Electronics(String productId, String productName, double price) {
        super(productId, productName, price);
    }

    @Override
    public double getFinalPrice() {
        return getPrice() * 0.90; // 10% off
    }
}

// Derived Class: Clothing (20% off if price > 1000)
class Clothing extends Product {
    public Clothing(String productId, String productName, double price) {
        super(productId, productName, price);
    }

    @Override
    public double getFinalPrice() {
        if (getPrice() > 1000) {
            return getPrice() * 0.80;
        } else {
            return getPrice();
        }
    }
}

// Derived Class: Grocery (no discount)
class Grocery extends Product {
    public Grocery(String productId, String productName, double price) {
        super(productId, productName, price);
    }

    @Override
    public double getFinalPrice() {
        return getPrice();
    }
}

// Shopping Cart Class
class ShoppingCart {
    private ArrayList<Product> products = new ArrayList<>();

    public void addProduct(Product product) {
        // Prevent duplicate Product IDs
        for (Product p : products) {
            if (p.getProductId().equalsIgnoreCase(product.getProductId())) {
                System.out.println("Error: Product ID already exists!");
                return;
            }
        }
        products.add(product);
        System.out.println("Product added: " + product.getProductName());
    }

    public void removeProduct(String productId) {
        for (Product p : products) {
            if (p.getProductId().equalsIgnoreCase(productId)) {
                products.remove(p);
                System.out.println("Product removed successfully!");
                return;
            }
        }
        System.out.println("Error: Product not found.");
    }

    public void displayCart() {
        if (products.isEmpty()) {
            System.out.println("Cart is empty!");
            return;
        }
        System.out.println("Cart Contents:");
        for (Product p : products) {
            p.displayProductInfo();
        }
    }

    public double calculateTotal() {
        double total = 0;
        for (Product p : products) {
            total += p.getFinalPrice();
        }
        return total;
    }
}

// Main Program
public class ShoppingCartSystem {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        ShoppingCart cart = new ShoppingCart();

        while (true) {
            System.out.println("\n===== Online Shopping Cart =====");
            System.out.println("1. Add Product");
            System.out.println("2. Remove Product");
            System.out.println("3. Display Cart");
            System.out.println("4. Checkout");
            System.out.println("5. Exit");
            System.out.print("Enter choice: ");
            int choice = sc.nextInt();
            sc.nextLine(); // consume newline

            switch (choice) {
                case 1:
                    System.out.print("Enter Product Type (1-Electronics, 2-Clothing, 3-Grocery): ");
                    int type = sc.nextInt();
                    sc.nextLine();
                    System.out.print("Enter Product ID: ");
                    String id = sc.nextLine();
                    System.out.print("Enter Product Name: ");
                    String name = sc.nextLine();
                    System.out.print("Enter Price: ");
                    double price = sc.nextDouble();
                    sc.nextLine();

                    if (price <= 0) {
                        System.out.println("Error: Price must be positive!");
                        break;
                    }

                    Product product = null;
                    switch (type) {
                        case 1:
                            product = new Electronics(id, name, price);
                            break;
                        case 2:
                            product = new Clothing(id, name, price);
                            break;
                        case 3:
                            product = new Grocery(id, name, price);
                            break;
                        default:
                            System.out.println("Invalid product type!");
                    }

                    if (product != null) {
                        cart.addProduct(product);
                    }
                    break;

                case 2:
                    System.out.print("Enter Product ID to remove: ");
                    String removeId = sc.nextLine();
                    cart.removeProduct(removeId);
                    break;

                case 3:
                    cart.displayCart();
                    break;

                case 4:
                    double total = cart.calculateTotal();
                    System.out.printf("Total Amount to Pay: %.2f%n", total);
                    break;

                case 5:
                    System.out.println("Thank you for shopping!");
                    sc.close();
                    return;

                default:
                    System.out.println("Invalid choice. Try again.");
            }
        }
    }
}
