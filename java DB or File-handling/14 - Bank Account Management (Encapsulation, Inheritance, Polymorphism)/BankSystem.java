import java.util.*;

abstract class Account {
    private String accountNumber;
    private String holderName;
    protected double balance;  // protected to allow subclass access

    public Account(String accountNumber, String holderName, double balance) {
        this.accountNumber = accountNumber;
        this.holderName = holderName;
        this.balance = balance;
    }

    // --- Encapsulation: getters and setters ---
    public String getAccountNumber() { return accountNumber; }
    public String getHolderName() { return holderName; }
    public double getBalance() { return balance; }

    public void deposit(double amount) {
        if (amount <= 0) {
            System.out.println("Error: Deposit amount must be positive.");
            return;
        }
        balance += amount;
        System.out.println("Deposit successful! New Balance: " + balance);
    }

    public abstract void withdraw(double amount);  // Polymorphic behavior

    public void displayAccountInfo() {
        System.out.println("----------------------------------------");
        System.out.println("Account Number: " + accountNumber);
        System.out.println("Holder Name:    " + holderName);
        System.out.println("Balance:        " + balance);
        System.out.println("----------------------------------------");
    }
}

// --- Derived Class: SavingsAccount ---
class SavingsAccount extends Account {
    private static final double MIN_BALANCE = 500.0;

    public SavingsAccount(String accountNumber, String holderName, double balance) {
        super(accountNumber, holderName, balance);
    }

    @Override
    public void withdraw(double amount) {
        if (amount <= 0) {
            System.out.println("Error: Withdrawal amount must be positive.");
            return;
        }

        if (balance - amount < MIN_BALANCE) {
            System.out.println("Error: Minimum balance of 500 must be maintained.");
        } else {
            balance -= amount;
            System.out.println("Withdrawal successful! New Balance: " + balance);
        }
    }
}

// --- Derived Class: CheckingAccount ---
class CheckingAccount extends Account {
    private static final double OVERDRAFT_LIMIT = -1000.0;

    public CheckingAccount(String accountNumber, String holderName, double balance) {
        super(accountNumber, holderName, balance);
    }

    @Override
    public void withdraw(double amount) {
        if (amount <= 0) {
            System.out.println("Error: Withdrawal amount must be positive.");
            return;
        }

        if (balance - amount < OVERDRAFT_LIMIT) {
            System.out.println("Error: Overdraft limit of -1000 exceeded.");
        } else {
            balance -= amount;
            System.out.println("Withdrawal successful! New Balance: " + balance);
        }
    }
}

// --- Main Menu-Driven System ---
public class BankSystem {
    private static final Scanner sc = new Scanner(System.in);
    private static final Map<String, Account> accounts = new HashMap<>();

    public static void main(String[] args) {
        int choice;
        do {
            System.out.println("\n===== Bank Account Management System =====");
            System.out.println("1. Create New Account");
            System.out.println("2. Deposit");
            System.out.println("3. Withdraw");
            System.out.println("4. Display Account Info");
            System.out.println("5. Exit");
            System.out.print("Enter choice: ");
            choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1 -> createAccount();
                case 2 -> deposit();
                case 3 -> withdraw();
                case 4 -> displayAccount();
                case 5 -> System.out.println("Exiting system...");
                default -> System.out.println("Invalid choice!");
            }
        } while (choice != 5);
    }

    private static void createAccount() {
        System.out.print("Enter Account Type (1-Savings, 2-Checking): ");
        int type = sc.nextInt();
        sc.nextLine();
        System.out.print("Enter Account Number: ");
        String accNo = sc.nextLine();

        if (accounts.containsKey(accNo)) {
            System.out.println("Error: Account number already exists.");
            return;
        }

        System.out.print("Enter Holder Name: ");
        String name = sc.nextLine();
        System.out.print("Enter Initial Balance: ");
        double bal = sc.nextDouble();

        Account acc = null;
        if (type == 1) {
            acc = new SavingsAccount(accNo, name, bal);
            System.out.println("Savings Account created successfully!");
        } else if (type == 2) {
            acc = new CheckingAccount(accNo, name, bal);
            System.out.println("Checking Account created successfully!");
        } else {
            System.out.println("Invalid account type!");
            return;
        }

        accounts.put(accNo, acc);
    }

    private static void deposit() {
        System.out.print("Enter Account Number: ");
        String accNo = sc.nextLine();
        Account acc = accounts.get(accNo);

        if (acc == null) {
            System.out.println("Account not found!");
            return;
        }

        System.out.print("Enter Deposit Amount: ");
        double amount = sc.nextDouble();
        acc.deposit(amount);
    }

    private static void withdraw() {
        System.out.print("Enter Account Number: ");
        String accNo = sc.nextLine();
        Account acc = accounts.get(accNo);

        if (acc == null) {
            System.out.println("Account not found!");
            return;
        }

        System.out.print("Enter Withdraw Amount: ");
        double amount = sc.nextDouble();
        acc.withdraw(amount); // <-- Polymorphic call
    }

    private static void displayAccount() {
        System.out.print("Enter Account Number: ");
        String accNo = sc.nextLine();
        Account acc = accounts.get(accNo);

        if (acc == null) {
            System.out.println("Account not found!");
        } else {
            acc.displayAccountInfo();
        }
    }
}
