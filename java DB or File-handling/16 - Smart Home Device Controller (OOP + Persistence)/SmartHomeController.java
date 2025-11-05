import java.io.*;
import java.util.*;
import java.time.*;
import java.time.format.DateTimeFormatter;

// === Base Class ===
abstract class Device implements Serializable {
    private String deviceID;
    private String deviceName;
    private boolean status; // true = ON, false = OFF

    public Device(String deviceID, String deviceName) {
        this.deviceID = deviceID;
        this.deviceName = deviceName;
        this.status = false;
    }

    public String getDeviceID() { return deviceID; }
    public String getDeviceName() { return deviceName; }
    public boolean isOn() { return status; }

    public void turnOn() { status = true; }
    public void turnOff() { status = false; }

    public String getStatus() { return status ? "ON" : "OFF"; }

    public abstract String getDeviceDetails();
}

// === Derived Classes ===
class Light extends Device {
    private int brightness; // 0–100

    public Light(String id, String name, int brightness) {
        super(id, name);
        setBrightness(brightness);
    }

    public void setBrightness(int level) {
        if (level < 0 || level > 100)
            throw new IllegalArgumentException("Brightness must be 0-100");
        this.brightness = level;
    }

    public int getBrightness() { return brightness; }

    @Override
    public String getDeviceDetails() {
        return "[" + getDeviceID() + "] " + getDeviceName() + " - " + getStatus()
                + ", Brightness: " + brightness;
    }
}

class Fan extends Device {
    private int speed; // 1–5

    public Fan(String id, String name, int speed) {
        super(id, name);
        setSpeed(speed);
    }

    public void setSpeed(int level) {
        if (level < 1 || level > 5)
            throw new IllegalArgumentException("Speed must be 1-5");
        this.speed = level;
    }

    public int getSpeed() { return speed; }

    @Override
    public String getDeviceDetails() {
        return "[" + getDeviceID() + "] " + getDeviceName() + " - " + getStatus()
                + ", Speed: " + speed;
    }
}

class AC extends Device {
    private int temperature; // 16–30
    private String mode; // Cool, Heat, Fan

    public AC(String id, String name, int temperature, String mode) {
        super(id, name);
        setTemperature(temperature);
        setMode(mode);
    }

    public void setTemperature(int temp) {
        if (temp < 16 || temp > 30)
            throw new IllegalArgumentException("Temperature must be 16–30");
        this.temperature = temp;
    }

    public void setMode(String mode) {
        if (!mode.equalsIgnoreCase("Cool") && !mode.equalsIgnoreCase("Heat") && !mode.equalsIgnoreCase("Fan"))
            throw new IllegalArgumentException("Mode must be Cool, Heat, or Fan");
        this.mode = mode;
    }

    @Override
    public String getDeviceDetails() {
        return "[" + getDeviceID() + "] " + getDeviceName() + " - " + getStatus()
                + ", Temp: " + temperature + ", Mode: " + mode;
    }
}

// === Controller ===
public class SmartHomeController {
    private static List<Device> devices = new ArrayList<>();
    private static Scanner sc = new Scanner(System.in);
    private static final String FILE_NAME = "devices.txt";

    public static void main(String[] args) {
        int choice;
        do {
            System.out.println("\n=== Smart Home Device Controller ===");
            System.out.println("1. Add Device");
            System.out.println("2. Toggle Device ON/OFF");
            System.out.println("3. Adjust Device Settings");
            System.out.println("4. View All Devices");
            System.out.println("5. Save Devices to File");
            System.out.println("6. Load Devices from File");
            System.out.println("7. Schedule Device Action");
            System.out.println("8. Exit");
            System.out.print("Enter choice: ");
            choice = getIntInput();

            switch (choice) {
                case 1 -> addDevice();
                case 2 -> toggleDevice();
                case 3 -> adjustSettings();
                case 4 -> viewDevices();
                case 5 -> saveDevices();
                case 6 -> loadDevices();
                case 7 -> scheduleAction();
                case 8 -> System.out.println("Exiting system...");
                default -> System.out.println("Invalid choice. Try again.");
            }
        } while (choice != 8);
    }

    // === Add Device ===
    private static void addDevice() {
        System.out.println("Select Device Type: ");
        System.out.println("1. Light");
        System.out.println("2. Fan");
        System.out.println("3. Air Conditioner");
        System.out.print("Enter choice: ");
        int type = getIntInput();

        System.out.print("Enter Device ID: ");
        String id = sc.nextLine();
        if (findDevice(id) != null) {
            System.out.println("Error: Device ID already exists!");
            return;
        }

        System.out.print("Enter Device Name: ");
        String name = sc.nextLine();

        try {
            switch (type) {
                case 1 -> {
                    System.out.print("Enter Brightness (0-100): ");
                    int b = getIntInput();
                    devices.add(new Light(id, name, b));
                }
                case 2 -> {
                    System.out.print("Enter Speed (1-5): ");
                    int s = getIntInput();
                    devices.add(new Fan(id, name, s));
                }
                case 3 -> {
                    System.out.print("Enter Temperature (16-30): ");
                    int t = getIntInput();
                    System.out.print("Enter Mode (Cool/Heat/Fan): ");
                    String m = sc.nextLine();
                    devices.add(new AC(id, name, t, m));
                }
                default -> System.out.println("Invalid device type.");
            }
            System.out.println("Device added successfully!");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // === Toggle Device ===
    private static void toggleDevice() {
        System.out.print("Enter Device ID: ");
        String id = sc.nextLine();
        Device d = findDevice(id);
        if (d == null) {
            System.out.println("Device not found!");
            return;
        }

        System.out.print("Action (Turn ON, Turn OFF): ");
        String action = sc.nextLine().toLowerCase();
        if (action.equals("turn on")) {
            d.turnOn();
        } else if (action.equals("turn off")) {
            d.turnOff();
        } else {
            System.out.println("Invalid action.");
            return;
        }
        System.out.println(d.getDeviceName() + " is now " + d.getStatus());
    }

    // === Adjust Settings ===
    private static void adjustSettings() {
        System.out.print("Enter Device ID: ");
        String id = sc.nextLine();
        Device d = findDevice(id);
        if (d == null) {
            System.out.println("Device not found!");
            return;
        }

        try {
            if (d instanceof Light l) {
                System.out.print("Enter new Brightness (0-100): ");
                l.setBrightness(getIntInput());
            } else if (d instanceof Fan f) {
                System.out.print("Enter new Speed (1-5): ");
                f.setSpeed(getIntInput());
            } else if (d instanceof AC a) {
                System.out.print("Enter new Temperature (16-30): ");
                a.setTemperature(getIntInput());
                System.out.print("Enter new Mode (Cool/Heat/Fan): ");
                a.setMode(sc.nextLine());
            }
            System.out.println("Settings updated successfully!");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // === View Devices ===
    private static void viewDevices() {
        if (devices.isEmpty()) {
            System.out.println("No devices added yet.");
            return;
        }
        System.out.println("\n--- Device List ---");
        for (Device d : devices) {
            System.out.println(d.getDeviceDetails());
        }
    }

    // === Save Devices ===
    private static void saveDevices() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FILE_NAME))) {
            oos.writeObject(devices);
            System.out.println("Devices saved successfully to " + FILE_NAME);
        } catch (IOException e) {
            System.out.println("Error saving devices: " + e.getMessage());
        }
    }

    // === Load Devices ===
    private static void loadDevices() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FILE_NAME))) {
            devices = (ArrayList<Device>) ois.readObject();
            System.out.println("Devices loaded successfully from " + FILE_NAME);
        } catch (Exception e) {
            System.out.println("Error loading devices: " + e.getMessage());
        }
    }

    // === Schedule Action ===
    private static void scheduleAction() {
        System.out.print("Enter Device ID: ");
        String id = sc.nextLine();
        Device d = findDevice(id);
        if (d == null) {
            System.out.println("Device not found!");
            return;
        }

        System.out.print("Schedule (Turn ON/OFF at hh:mm AM/PM): ");
        String scheduleInput = sc.nextLine();
        String[] parts = scheduleInput.split(" at ");
        if (parts.length != 2) {
            System.out.println("Invalid schedule format.");
            return;
        }

        String action = parts[0].trim().toLowerCase();
        String time = parts[1].trim().toUpperCase();

        LocalTime target;
        try {
            target = LocalTime.parse(time, DateTimeFormatter.ofPattern("h:mm a"));
        } catch (Exception e) {
            System.out.println("Invalid time format. Use hh:mm AM/PM.");
            return;
        }

        LocalTime now = LocalTime.now();
        long delay = Duration.between(now, target).toMillis();
        if (delay < 0) delay += 24 * 60 * 60 * 1000; // next day

        if (action.equals("turn on")) {
            System.out.println(d.getDeviceName() + " will be turned ON at " + time);
            new Timer().schedule(new TimerTask() {
                public void run() {
                    d.turnOn();
                    System.out.println("\n[Scheduled] " + d.getDeviceName() + " is now ON");
                }
            }, delay);
        } else if (action.equals("turn off")) {
            System.out.println(d.getDeviceName() + " will be turned OFF at " + time);
            new Timer().schedule(new TimerTask() {
                public void run() {
                    d.turnOff();
                    System.out.println("\n[Scheduled] " + d.getDeviceName() + " is now OFF");
                }
            }, delay);
        } else {
            System.out.println("Invalid action. Use Turn ON or Turn OFF.");
        }
    }

    // === Utility Methods ===
    private static Device findDevice(String id) {
        for (Device d : devices)
            if (d.getDeviceID().equalsIgnoreCase(id))
                return d;
        return null;
    }

    private static int getIntInput() {
        while (true) {
            try {
                return Integer.parseInt(sc.nextLine());
            } catch (NumberFormatException e) {
                System.out.print("Invalid number. Try again: ");
            }
        }
    }
}
