import java.util.*;

// Base class
abstract class Person {
    private String id;
    private String name;
    private int age;

    public Person(String id, String name, int age) {
        this.id = id;
        this.name = name;
        this.age = age;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public int getAge() { return age; }

    public abstract void introduce();
}

// Derived class: Teacher
class Teacher extends Person {
    private String subject;

    public Teacher(String id, String name, int age, String subject) {
        super(id, name, age);
        this.subject = subject;
    }

    @Override
    public void introduce() {
        System.out.println("ID: " + getId() + " - I am " + getName() + ", a Teacher of " + subject + ".");
    }

    public void gradeStudent() {
        System.out.println(getName() + " has graded a student.");
    }
}

// Derived class: Student
class Student extends Person {
    private String course;
    private int yearLevel;

    public Student(String id, String name, int age, String course, int yearLevel) {
        super(id, name, age);
        this.course = course;
        this.yearLevel = yearLevel;
    }

    @Override
    public void introduce() {
        String yearSuffix;
        switch (yearLevel) {
            case 1 -> yearSuffix = "1st";
            case 2 -> yearSuffix = "2nd";
            case 3 -> yearSuffix = "3rd";
            default -> yearSuffix = yearLevel + "th";
        }
        System.out.println("ID: " + getId() + " - I am " + getName() + ", a " + yearSuffix + " year student of " + course + ".");
    }

    public void submitAssignment() {
        System.out.println(getName() + " has submitted an assignment.");
    }
}

// Derived class: AdminStaff
class AdminStaff extends Person {
    private String department;

    public AdminStaff(String id, String name, int age, String department) {
        super(id, name, age);
        this.department = department;
    }

    @Override
    public void introduce() {
        System.out.println("ID: " + getId() + " - I am " + getName() + ", working in the " + department + " department.");
    }

    public void processDocument() {
        System.out.println(getName() + " has processed a document.");
    }
}

// PersonnelManager class
public class SchoolPersonnelSystem {
    private static List<Person> personnelList = new ArrayList<>();
    private static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        int choice;

        do {
            System.out.println("\n=== School Personnel Management System ===");
            System.out.println("1. Add Teacher");
            System.out.println("2. Add Student");
            System.out.println("3. Add Admin Staff");
            System.out.println("4. View All Personnel");
            System.out.println("5. Role-Specific Action");
            System.out.println("6. Exit");
            System.out.print("Enter choice: ");
            choice = getIntInput();

            switch (choice) {
                case 1 -> addTeacher();
                case 2 -> addStudent();
                case 3 -> addAdmin();
                case 4 -> viewAll();
                case 5 -> roleAction();
                case 6 -> System.out.println("Exiting program...");
                default -> System.out.println("Invalid choice. Try again.");
            }
        } while (choice != 6);
    }

    // Helper: safe int input
    private static int getIntInput() {
        while (true) {
            try {
                return Integer.parseInt(sc.nextLine());
            } catch (NumberFormatException e) {
                System.out.print("Invalid input. Please enter a number: ");
            }
        }
    }

    // Helper: check unique ID
    private static boolean idExists(String id) {
        for (Person p : personnelList) {
            if (p.getId().equalsIgnoreCase(id)) {
                return true;
            }
        }
        return false;
    }

    private static void addTeacher() {
        System.out.println("Add Teacher");
        System.out.print("Enter ID: ");
        String id = sc.nextLine();
        if (idExists(id)) {
            System.out.println("Error: ID already exists!");
            return;
        }

        System.out.print("Enter Name: ");
        String name = sc.nextLine();
        System.out.print("Enter Age: ");
        int age = getIntInput();
        if (age <= 0) {
            System.out.println("Error: Age must be positive.");
            return;
        }

        System.out.print("Enter Subject: ");
        String subject = sc.nextLine();

        personnelList.add(new Teacher(id, name, age, subject));
        System.out.println("Teacher added successfully!");
    }

    private static void addStudent() {
        System.out.println("Add Student");
        System.out.print("Enter ID: ");
        String id = sc.nextLine();
        if (idExists(id)) {
            System.out.println("Error: ID already exists!");
            return;
        }

        System.out.print("Enter Name: ");
        String name = sc.nextLine();
        System.out.print("Enter Age: ");
        int age = getIntInput();
        if (age <= 0) {
            System.out.println("Error: Age must be positive.");
            return;
        }

        System.out.print("Enter Course: ");
        String course = sc.nextLine();
        System.out.print("Enter Year Level: ");
        int year = getIntInput();

        personnelList.add(new Student(id, name, age, course, year));
        System.out.println("Student added successfully!");
    }

    private static void addAdmin() {
        System.out.print("Enter ID: ");
        String id = sc.nextLine();
        if (idExists(id)) {
            System.out.println("Error: ID already exists!");
            return;
        }

        System.out.print("Enter Name: ");
        String name = sc.nextLine();
        System.out.print("Enter Age: ");
        int age = getIntInput();
        if (age <= 0) {
            System.out.println("Error: Age must be positive.");
            return;
        }

        System.out.print("Enter Department: ");
        String dept = sc.nextLine();

        personnelList.add(new AdminStaff(id, name, age, dept));
        System.out.println("Admin Staff added successfully!");
    }

    private static void viewAll() {
        if (personnelList.isEmpty()) {
            System.out.println("No personnel added yet.");
            return;
        }
        System.out.println("\n--- All Personnel ---");
        for (Person p : personnelList) {
            p.introduce();
        }
    }

    private static void roleAction() {
        if (personnelList.isEmpty()) {
            System.out.println("No personnel available.");
            return;
        }

        System.out.print("Enter Personnel ID: ");
        String id = sc.nextLine();
        for (Person p : personnelList) {
            if (p.getId().equalsIgnoreCase(id)) {
                if (p instanceof Teacher t) {
                    t.gradeStudent();
                } else if (p instanceof Student s) {
                    s.submitAssignment();
                } else if (p instanceof AdminStaff a) {
                    a.processDocument();
                }
                return;
            }
        }
        System.out.println("No personnel found with that ID.");
    }
}
