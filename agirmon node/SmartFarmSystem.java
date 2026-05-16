import java.io.*;
import java.util.*;

// ================
// 1. DATA MODELS 
// ================
class CropType {
    String cID;
    String cName;
    public CropType(String id, String name) { this.cID = id; this.cName = name; }
}

class Sensor {
    String sID;
    String type;
    String lastReading; 
    String alertStatus; 

    public Sensor(String id, String type) {
        this.sID = id;
        this.type = type;
        this.lastReading = generateRandomData(type);
        this.alertStatus = "NORMAL";
    }

    private String generateRandomData(String type) {
        return switch (type) {
            case "Temperature" -> String.format("%.1f°C", 25.0 + (Math.random() * 8));
            case "Moisture" -> (40 + (int)(Math.random() * 40)) + "%";
            case "Sunlight" -> (4 + (int)(Math.random() * 5)) + " hours";
            case "Wind" -> (10 + (int)(Math.random() * 40)) + " km/h";
            case "Pest" -> (int)(Math.random() * 25) + "%";
            case "Weed" -> (int)(Math.random() * 30) + "%";
            default -> "0";
        };
    }
}

class Plot {
    String pID;
    String cropType; 
    double health;
    List<Sensor> sensors = new ArrayList<>(); 

    public Plot(String id, String cropType) {
        this.pID = id;
        this.cropType = cropType;
        this.health = 80.0 + (Math.random() * 20);

        sensors.add(new Sensor("S-" + id.substring(2) + "-TEMP", "Temperature"));
        sensors.add(new Sensor("S-" + id.substring(2) + "-MOIS", "Moisture"));
        sensors.add(new Sensor("S-" + id.substring(2) + "-SUNL", "Sunlight"));
        sensors.add(new Sensor("S-" + id.substring(2) + "-WIND", "Wind"));
        sensors.add(new Sensor("S-" + id.substring(2) + "-PEST", "Pest"));
        sensors.add(new Sensor("S-" + id.substring(2) + "-WEED", "Weed"));
    }
}

class Farm {
    String fID;
    String fName;
    String cropType;
    double health;
    Plot[][] grid = new Plot[3][3];

    public Farm(String fID, String name, String defaultCrop) {
        this.fID = fID;
        this.fName = name;
        this.cropType = defaultCrop;
        this.health = 70 + (Math.random() * 30);
        
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                String pID = "P-" + fID.split("-")[1] + "-" + i + j;
                grid[i][j] = new Plot(pID, defaultCrop);
            }
        }
    }
    
    // 获取农场内所有的地块列表
    public List<Plot> getAllPlots() {
        List<Plot> all = new ArrayList<>();
        for(Plot[] row : grid) {
            for(Plot p : row) all.add(p);
        }
        return all;
    }
}

// BASE/PARENT CLASSES (Inheritance)
abstract class User {
    protected String name;
    protected String userID;
    protected String password;
    protected String profilePicture;
    protected String email; // (encapsulation)

    public User(String name, String id, String email) {
        this.name = name;
        this.userID = id;
        this.profilePicture = "default_avatar.png";
        this.email = email;
    }

    public abstract void showDashboard(); // （dynamic polymorphism-method overriding）
    public String getName() { return name; }
}

// child class 
class Farmer extends User {
    private List<Farm> myFarms = new ArrayList<>(); 
    private int farmCounter = 1;
    private Farm currentFocusedFarm = null;
    public List<Farm> getMyFarms() { 
    return this.myFarms;
    }

    public Farmer(String name, String id, String email) {
        super(name, id, email);
        addFarm("Farm 1", "Corn");
        addFarm("Farm 2", "Chili");
    }

    public void addFarm(String name, String crop) {
        String suffix = (userID.length() > 3) ? userID.substring(userID.length() - 3) : userID;
        String fID = "F-" + suffix + "-" + (farmCounter++); 
        myFarms.add(new Farm(fID, name, crop));
    }

    @Override 
    public void showDashboard() {
        System.out.println("\n🌿 [AGRIMON] FARMER DASHBOARD");
        System.out.println("User: " + name + " | Status: Active");
        System.out.println("------------------------------------");
        System.out.println("1. View Farm List & Real-time Sensors");
        System.out.println("2. Edit Farm Crops (Corn/Banana/Chili)");
        System.out.println("3. Personal Profile");
        System.out.println("4. System Info");
        System.out.println("0. Logout");
        System.out.print("Option: ");
    }

    public static void displayFarmList(List<Farm> farms) {
        System.out.println("\n 📋 FARM LIST");
        System.out.println(" -----------------------------------------------------------");
        System.out.println("  F ID  | LOCATION           | CROP TYPE    | STATUS ");
        System.out.println("  ----|--------------------|--------------|--------");
        if (farms.isEmpty()) {
            System.out.println("        [ No farms available ]");
        } else {
            for (int i = 0; i < farms.size(); i++) {
                Farm f = farms.get(i);
                String status = (f.health < 85) ? "🟡 ALERT " : "🟢 ONLINE";
                System.out.printf("  %03d | %-18s | %-12s | %s\n", (i+1), f.fName, f.cropType, status);
            }
        }
        System.out.println(" -----------------------------------------------------------");
    }

    public void editFarmCrops(Scanner sc) {
        System.out.println("\n" + "=".repeat(45));
        System.out.println("\n--- 📝 EDIT FARM CROPS ---");
        System.out.println("=".repeat(45));
    
        Farm selectedFarm = null;

        if (this.currentFocusedFarm != null) {
            selectedFarm = this.currentFocusedFarm;
            System.out.println("\n[Linkage] You are currently managing: " + selectedFarm.fName);
            System.out.println("Current Planting  : " + selectedFarm.cropType);
            System.out.println("---------------------------------------------");

            System.out.println("\n        +-----------------------+");
            System.out.println("        |  [+] ADD / EDIT CROP  |");
            System.out.println("        +-----------------------+");
            System.out.print("\nClick the [+] button (Type '+' or '1' to click): ");
        
            String clickInput = sc.next();
            sc.nextLine(); 

            // 只有用户输入了 + 或者 1，才会继续以下步骤
            if (!clickInput.equals("+") && !clickInput.equals("1")) {
                System.out.println("❌ You didn't click the button. Returning to menu.");
                return;
            }
        
            System.out.println("\n Loading Crop Species Selection...");
        } else {
            System.out.println("No farm focused. Please choose a node to update:");
            for (int i = 0; i < myFarms.size(); i++) {
                System.out.println(" [" + (i + 1) + "] " + myFarms.get(i).fName + " (Current: " + myFarms.get(i).cropType + ")");
            }
            System.out.print("Select a farm: ");
            int farmChoice = sc.nextInt();
            sc.nextLine();
            if (farmChoice > 0 && farmChoice <= myFarms.size()) {
                selectedFarm = myFarms.get(farmChoice - 1);
            } else {
                System.out.println("❌ Operation cancelled.");
                return;
            }
        }

        System.out.println("\n┌───────────────────────────────────────────────┐");
        System.out.println("│          SELECT NEW CROP SPECIES              │");
        System.out.println("├───────────────────────────────────────────────┤");
        System.out.println("│ 1. 🌽 Corn      │ 2. 🌾 Paddy      │ 3. 🌶️ Chili   │");
        System.out.println("│ 4. 🍌 Banana    │ 5. 🍫 Cocoa      │ 6. 🌴 Oil Palm│");
        System.out.println("│ 7. 🍍 Pineapple │ 8. 🍈 Durian     │               │");
        System.out.println("└───────────────────────────────────────────────┘");

        System.out.print("Select crop index (1-8): ");
        int cropChoice = sc.nextInt();
        sc.nextLine(); 

        String newCrop = "";
        switch (cropChoice) {
            case 1: newCrop = "Corn"; break;
            case 2: newCrop = "Paddy"; break;
            case 3: newCrop = "Chili"; break;
            case 4: newCrop = "Banana"; break;
            case 5: newCrop = "Cocoa"; break;
            case 6: newCrop = "Oil Palm"; break;
            case 7: newCrop = "Pineapple"; break;
            case 8: newCrop = "Durian"; break;
            default:
                System.out.println("❌ Invalid choice. Returning to menu.");
                return;
        }

        System.out.println("\n      _________________________________");
        System.out.println("     |                                 |");
        System.out.println("     |  Do you want to save the change |");
        System.out.println("     |  to [" + newCrop.toUpperCase() + "] ?           |");
        System.out.println("     |_________________________________|");
        System.out.println("     |   [1] Save      [2] Don't Save  |");
        System.out.println("     |_________________________________|");
        System.out.print("\nYour choice: ");
        int saveChoice = sc.nextInt();
        sc.nextLine();

        if (saveChoice == 1) {
            selectedFarm.cropType = newCrop;
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    selectedFarm.grid[i][j].cropType = newCrop;
                }
            }

            SmartFarmSystem.saveDatabase();
        
            System.out.println("\n✅ [SUCCESS] Farm updated! " + selectedFarm.fName + " is now planting " + newCrop);
        } else {
            System.out.println("\n❌ [CANCELLED] Changes discarded.");
        }
    }

    public void viewTaskList(Farm f, double currentMoisture, double currentPest) {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("  📋 [ TASK LIST FOR: " + f.fName.toUpperCase() + " ]");
        System.out.println("=".repeat(60));

        System.out.println("\n📌 [Daily Tasks]");

        double targetMoisture = 70.0;
    if (currentMoisture < targetMoisture) {
        double waterNeeded = (targetMoisture - currentMoisture) * 0.5;
        System.out.printf("- Water %s: Current %.1f%%. Apply %.1fL to reach %.1f%%.\n", 
                          f.fName, currentMoisture, waterNeeded, targetMoisture);
    } else {
        System.out.println("- Water Status: Soil is sufficiently hydrated.");
    }

    if (currentPest > 5.0) {
        int row = (int)(Math.random() * 3);
        int col = (int)(Math.random() * 3);
        System.out.printf("- Pest Control: Detected at %.1f%% (Alert!) near Plot [%d, %d].\n", currentPest, row, col);
    }

        System.out.println("- General Inspection: Check growth status for all " + f.cropType + " plots.");

        System.out.println("\n📅 [Weekly Tasks]");
        System.out.println("- System Maintenance: Calibrate sensors for " + f.fName);
        System.out.println("- Fertilizer Application: Schedule for coming Sunday");

        System.out.println("\n" + "-".repeat(60));
        System.out.println("  0. Back to Farm Details");
        System.out.println("=".repeat(60));
    }

    public void selectAndMonitorFarm(Scanner sc) {
        System.out.println("\n--- 📋 MY FARM LIST ---");
        for (int i = 0; i < myFarms.size(); i++) {
            System.out.println((i + 1) + ". " + myFarms.get(i).fName);
        }
        System.out.print("Select a farm to view details (1 or 2): ");
        int choice = sc.nextInt();
        sc.nextLine(); 

        if (choice > 0 && choice <= myFarms.size()) {
            this.currentFocusedFarm = myFarms.get(choice - 1); 
            Farm f = this.currentFocusedFarm;

            System.out.println("\n===== 📍 MONITORING PAGE: " + f.fName + " =====");
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    System.out.print("[" + f.grid[i][j].cropType + "] ");
                }
                System.out.println();
            }

            System.out.println("-".repeat(50));
            System.out.println("📡 REAL-TIME DATA FOR: " + (f.cropType != null ? f.cropType.toUpperCase() : "CROP"));
            System.out.println("-".repeat(50));
            
            if (f.cropType.equalsIgnoreCase("Corn")) {
                System.out.println("ℹ️ Corn is a highly adaptable crop...");
            } else {
                System.out.println("ℹ️ Chili requires steady water supply...");
            }

            int growthDays = 75 + (int)(Math.random() * 15);     // 75-90天
            int sunlight = 4 + (int)(Math.random() * 5);        // 4-9小时
            double temp = 25.0 + (Math.random() * 8);           // 25.0-33.0°C
            int wind = 10 + (int)(Math.random() * 40);          // 10-50 km/h
            int moisture = 40 + (int)(Math.random() * 40);      // 40-80%
            int pest = (int)(Math.random() * 25);               // 0-25%
            int weed = (int)(Math.random() * 30);               // 0-30%
            
            String condition = (moisture < 45 || temp > 32) ? "Needs Attention" : "Healthy";

            System.out.printf("⏱️ Growth countdown: ~%d days   | ☀️ Sunlight hour: %d hours\n", growthDays, sunlight);
            System.out.printf("🌡️ Temperature: %.1f°C           | 💨 Wind speed: ~%dkm/h\n", temp, wind);
            System.out.printf("💧 Soil moisture: %d%%           | ✨ Condition: %s\n", moisture, condition);
            
            System.out.println("-".repeat(50));
            System.out.printf("⚠️  Pest level: ~%d%% (%s)      | ⚠️  Weed level: %d%% (%s)\n", 
                               pest, (pest > 15 ? "Alert" : "Normal"), 
                               weed, (weed > 20 ? "Alert" : "Normal"));
            System.out.println("-".repeat(50));
            
            if (condition.equals("Healthy") && pest < 15) {
                System.out.println("[ADVICE] All parameters within healthy range.");
            } else {
                System.out.println("[ADVICE] Warning: Check irrigation or pest control immediately.");
            }
            System.out.println("-".repeat(50));

            System.out.println("\n[ QUICK ACTIONS ]");
            System.out.println("1. View Daily/Weekly Tasks for " + f.fName);
            System.out.println("0. Back to Dashboard");
            System.out.print("Choice: ");
            int taskChoice = sc.nextInt();
            sc.nextLine();

            if (taskChoice == 1) {
                viewTaskList(f, (double)moisture, (double)pest); 
            }

        } else {
            System.out.println("❌ Invalid selection.");
        }
    }

    public void showProfile() {
        System.out.println("\n👤 ======= PERSONAL PROFILE =======");
        System.out.println("   _______ ");
        System.out.println("  /       \\");
        System.out.println(" |    👤    |  <-- [ AVATAR PLACEHOLDER ]");
        System.out.println("  \\_______/      (Linked to: " + this.profilePicture + ")");
        System.out.println("-----------------------------------");
        System.out.println("Name      : " + this.name);
        System.out.println("User ID   : " + this.userID);
        System.out.println("Email     : " + this.email);
        System.out.println("Role      : Professional Farmer");
        System.out.println("Bio       : Eco-farming enthusiast. Dedicated to smart agriculture.");
        System.out.println("-----------------------------------");
        System.out.println("📊 STATISTICS:");
        System.out.println("- Farms Managed: 2 (Farm 1, Farm 2)");
        System.out.println("- Total Plots  : 18 Plots");
        System.out.println("- Member Since : April 2026");
        System.out.println("-----------------------------------");
        System.out.println("0. Back to Dashboard");
    }

    public void showSystemInfo() {
        System.out.println("\n       .--------.       ");
        System.out.println("    . '  AGRI    ' .    ");
        System.out.println("   /     MON       \\   <-- [ SYSTEM LOGO: AGRIMON ]");
        System.out.println("   \\    (LEAF)     /    ");
        System.out.println("    ' .        . '      ");
        System.out.println("       '------'         ");
        System.out.println("\n🌱 ======= ABOUT AGRIMON v1.0 =======");
        System.out.println("AGRIMON is an Integrated Smart Farming System.");
        System.out.println("\n🚀 CORE FEATURES:");
        System.out.println("1. Real-time Sensor Monitoring (Sunlight, Temp, Moisture)");
        System.out.println("2. Smart Task Automation (AI-driven Advice)");
        System.out.println("3. Interactive Farm Management (Dynamic Crop Switching)");
        System.out.println("\n💻 DEVELOPED BY: [KwickCraft]");
        System.out.println("Technology: Java Object-Oriented Programming");
        System.out.println("-----------------------------------");
        System.out.println("0. Back to Dashboard");
    }
}

// Here, admin class also acts as a child class that inherits from User class. 
// “class Admin extends User” show the same meaning like the farmer class just now.
// It extends the functionality of system-wide monitoring and permissions like runGlobalDiagnostic scan across all farm nodes.
class Admin extends User {
    private int totalFarms = 12;
    private double systemHealth = 98.5;
    private String topCrop = "Corn";

    public Admin(String name, String id,String email) { super(name, id, email); }
    public int getTotalFarms() { return totalFarms; }
    public double getSystemHealth() { return systemHealth; }
    public String getTopCrop() { return topCrop; }

    public void showDashboard(Map<String, String> roles) {
        refreshSystemAnalytics();

        int activeCount = 0;
        for (String role : roles.values()) {
            if (role.equalsIgnoreCase("Farmer")) {
                activeCount++;
            }
        }

        showAdminInitialDashboard(activeCount, roles.size());

        System.out.println("\n🛡️ [AGRIMON] ADMIN CONTROL PANEL");
        System.out.println("------------------------------------");
        System.out.println("1. User Management (Check User List)"); // 这里去掉了死板的数字
        System.out.println("2. Farm Management (Total: " + totalFarms + " Farms)");
        System.out.println("3. Monitoring & Data (Health: " + String.format("%.1f", systemHealth) + "%)");
        System.out.println("0. Logout");
        System.out.print("Option: ");
    }

    @Override // Similarly,  admin also use the @override to provide thier own showDashboard() which display platform statistic and health reports.
    public void showDashboard() {
        System.out.println("\n🛡️ [AGRIMON] ADMIN CONTROL PANEL");
        System.out.println("------------------------------------");
        System.out.println("1. User Management");
        System.out.println("2. Farm Management");
        System.out.println("3. Monitoring & Data Center");
        System.out.println("4. Persona Profile");
        System.out.println("5. System Info");
        System.out.println("0. Logout");
        System.out.print("Option: ");
    }

    private void refreshSystemAnalytics() {
        this.systemHealth = 95 + (Math.random() * 5); 
    }

    private void showAdminInitialDashboard(int activeCount, int totalUsers) {
        System.out.println("\n" + "=".repeat(55));
        System.out.println(" [NAV] User Management | Farm Management | Monitoring");
        System.out.println("=".repeat(55));
        
        System.out.println("\n +-------------------+   +--------------------------------+");
        System.out.println(" |  👤 Active        |   |  📊 Data Summary               |");
        System.out.println(" |     Farmers       |   |                                |");
        System.out.println(" |                   |   |  > System Health : " + String.format("%.1f", systemHealth) + "%      |");
        System.out.println(" |        " + activeCount + "          |   |  > Total Users   : " + totalUsers + "           |");
        System.out.println(" |      persons      |   |  > Total Farms   : " + totalFarms + "           |");
        System.out.println(" |                   |   |  > Most Planted  : " + topCrop + "         |");
        System.out.println(" +-------------------+   +--------------------------------+");
        System.out.println("\n [STATUS] All nodes responding. No critical errors.");
        System.out.println("-".repeat(55));
    }

    public void runGlobalDiagnostic(List<Farm> farms) {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("🛰️  AGRIMON REAL-TIME SYSTEM DIAGNOSTIC");
        System.out.println("=".repeat(50));
        
        if (farms.isEmpty()) {
            System.out.println(" ⚠️ No active farm nodes detected for scanning.");
        } else {
            for (Farm f : farms) {
                String statusIcon;
                String diagnosticResult;
                String alertLevel;

                if (f.health >= 90) {
                    statusIcon = "🟢";
                    diagnosticResult = "OPTIMAL (All sensors nominal)";
                    alertLevel = "NORMAL";
                } else if (f.health >= 75) {
                    statusIcon = "🟡";
                    diagnosticResult = "STABLE (Minor fluctuations detected)";
                    alertLevel = "STABLE";
                } else {
                    statusIcon = "🔴";
                    diagnosticResult = "CRITICAL (Immediate attention required)";
                    alertLevel = "CRITICAL";
                }
                for (Plot p : f.getAllPlots()) {
                    for (Sensor s : p.sensors) {
                        s.alertStatus = alertLevel; 
                    }
                }

                System.out.printf(" - Node: %-18s | Health: %5.1f%% | %s %s\n", 
                                  f.fName, f.health, statusIcon, diagnosticResult);
            }
        }
        
        System.out.println("=".repeat(50));
        System.out.println("✅ Global scan completed.");
        SmartFarmSystem.saveDatabase();
    }

    public void showProfile() {
        int totalUsers = SmartFarmSystem.getTotalUserCount();
        int totalFarms = SmartFarmSystem.getTotalFarmCount();
        String currentTime = java.time.LocalTime.now().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss"));
        System.out.println("\n" + "=".repeat(45));
        System.out.println("      _______ ");
        System.out.println("     /       \\    [ NAME: " + this.name + " ]");
        System.out.println("    |   👤     |   [ ROLE: Administrator ]");
        System.out.println("     \\_______/    [ ID  : " + this.userID + " ]");
        System.out.println("-".repeat(45));
        System.out.println(" 📝 ACCOUNT DETAILS:");
        System.out.println(" > Email: " + this.email);
        System.out.println(" > Bio: Managing the core of AgriMon System.");
        System.out.println(" > Region: Malaysia HQ");
        System.out.println("-".repeat(45));
        System.out.println(" [📊 Stats Block]           [⚙️ Settings Block]");
        System.out.printf(" Total Farms: %-14d Last Backup: %s\n", totalFarms, currentTime);
        System.out.printf(" Total Users: %-14d Status: %s\n", totalUsers, "Active");
        System.out.println("-".repeat(45));
        System.out.println(" 0. Back to Dashboard");
    }

    public void showSystemInfo() {
        System.out.println("\n       .--------.       ");
        System.out.println("    . '  AGRI    ' .    ");
        System.out.println("   /     MON       \\   <-- [ SYSTEM LOGO: AGRIMON ]");
        System.out.println("   \\    (LEAF)     /    ");
        System.out.println("    ' .        . '      ");
        System.out.println("       '------'         ");
        System.out.println("\n🌱 ======= ABOUT AGRIMON v1.0 =======");
        System.out.println("AGRIMON is an Integrated Smart Farming System.");
        System.out.println("\n🚀 CORE FEATURES:");
        System.out.println("1. Real-time Sensor Monitoring (Sunlight, Temp, Moisture)");
        System.out.println("2. Smart Task Automation (AI-driven Advice)");
        System.out.println("3. Interactive Farm Management (Dynamic Crop Switching)");
        System.out.println("\n💻 DEVELOPED BY: [KwickCraft]");
        System.out.println("Technology: Java Object-Oriented Programming");
        System.out.println("-----------------------------------");
        System.out.println("0. Back to Dashboard");
    }
}

// ==========================================
// 3. MAIN SYSTEM ENGINE
// ==========================================
public class SmartFarmSystem {
    public static List<CropType> globalCrops = Arrays.asList(
        new CropType("C-001", "Corn"),
        new CropType("C-002", "Paddy"),
        new CropType("C-003", "Chili"),
        new CropType("C-004", "Banana"),
        new CropType("C-005", "Cocoa"),
        new CropType("C-006", "Oil Palm"),
        new CropType("C-007", "Pineapple"),
        new CropType("C-008", "Durian")
    );

    private static Map<String, String> userDatabase = new HashMap<>();
    private static Map<String, String> roleDatabase = new HashMap<>();
    private static Map<String, String> userIDDatabase = new HashMap<>();
    private static Map<String, String> emailDatabase = new HashMap<>();
    public static Map<String, Farmer> farmerRegistry = new HashMap<>();

    public static List<String> farms = new ArrayList<>(Arrays.asList("Johor Farm", "Kedah Plantation"));

    public static int getTotalUserCount() {
        int farmerCount = 0;
        for (String role : roleDatabase.values()) {
            if (role.equalsIgnoreCase("Farmer")) {
                farmerCount++;
            }
        }
        return farmerCount;
    }

    public static int getTotalFarmCount() {
        int farmerCount = 0;
        for (String role : roleDatabase.values()) {
            if (role.equalsIgnoreCase("Farmer")) {
                farmerCount++;
            }
        }
        return farmerCount * 2; 
    }

    public static void saveDatabase() {
        try (PrintWriter writer = new PrintWriter(new FileWriter("database.txt"))) {
            // --- 1. [USERS] ---
            writer.println("[USERS]");
            for (String name : userDatabase.keySet()) {
                writer.println(userIDDatabase.get(name) + "|" + name + "|" + 
                            userDatabase.get(name) + "|" + emailDatabase.get(name) + "|" + 
                            roleDatabase.get(name));
            }

            // --- 2. [FARMS] ---
            writer.println("\n[FARMS]");
            for (Farmer f : farmerRegistry.values()) {
                for (Farm farm : f.getMyFarms()) {
                    writer.println(farm.fID + "|" + farm.fName + "|" + f.userID);
                }
            }

            // --- 3. [PLOTS] ---
            writer.println("\n[PLOTS]");
            for (Farmer f : farmerRegistry.values()) {
                for (Farm farm : f.getMyFarms()) {
                    for (Plot p : farm.getAllPlots()) {
                        writer.println(p.pID + "|" + "Plot_Grid" + "|" + farm.fID);
                    }
                }
            }

            // --- 4. [CROP_TYPE] ---
            writer.println("\n[CROP_TYPE]");
            for (CropType ct : globalCrops) {
                writer.println(ct.cID + "|" + ct.cName);
            }

            // --- 5. [CROP_LINK] ---
            writer.println("\n[CROP_LINK]");
            for (Farmer f : farmerRegistry.values()) {
                for (Farm farm : f.getMyFarms()) {
                    for (Plot p : farm.getAllPlots()) {
                        // 找到对应的 CropID
                        String cID = "C-001"; // 默认
                        for(CropType ct : globalCrops) if(ct.cName.equals(p.cropType)) cID = ct.cID;
                        writer.println(p.pID + "|" + cID);
                    }
                }
            }

            // --- 6. [SENSORS] ---
            writer.println("\n[SENSORS]");
            for (Farmer f : farmerRegistry.values()) {
                for (Farm farm : f.getMyFarms()) {
                    for (Plot p : farm.getAllPlots()) {
                        for (Sensor s : p.sensors) {
                            writer.println(s.sID + "|" + s.type + "|" + p.pID);
                        }
                    }
                }
            }

            // --- 7. [SENSOR_DATA] & [ALERTS] ---
            writer.println("\n[SENSOR_DATA]");
            for (Farmer f : farmerRegistry.values()) {
                for (Farm farm : f.getMyFarms()) {
                    for (Plot p : farm.getAllPlots()) {
                        for (Sensor s : p.sensors) {
                            writer.println("R-" + s.sID + "|" + s.lastReading + "|" + s.sID);
                        }
                    }
                }
            }

            writer.println("\n[ALERTS]");
            for (Farmer f : farmerRegistry.values()) {
                for (Farm farm : f.getMyFarms()) {
                    for (Plot p : farm.getAllPlots()) {
                        for (Sensor s : p.sensors) {
                            if(!s.alertStatus.equals("NORMAL")) {
                                writer.println("A-" + s.sID + "|" + s.alertStatus + "|R-" + s.sID);
                            }
                        }
                    }
                }
            }
            System.out.println("✅ Relational database.txt updated!");
        } catch (IOException e) {
            System.out.println("❌ Critical: Save failed.");
        }
    }

    // --- 📂 从文本文件加载数据 ---
    public static void loadDatabase() {
        File file = new File("database.txt");
        if (!file.exists()) return; 

        try (Scanner reader = new Scanner(file)) {
            String currentSection = ""; 
        
            while (reader.hasNextLine()) {
                String line = reader.nextLine().trim();
                if (line.isEmpty()) continue; 

                if (line.startsWith("[")) {
                    currentSection = line;
                    continue;
                }

                String[] data = line.split("\\|"); 

                if (currentSection.equals("[USERS]")) {
                    if (data.length >= 5) {
                        String uID = data[0];
                        String uName = data[1];
                        String uPass = data[2];
                        String uEmail = data[3];
                        String uRole = data[4];

                        userDatabase.put(uName, uPass);
                        roleDatabase.put(uName, uRole);
                        userIDDatabase.put(uName, uID);
                        emailDatabase.put(uName, uEmail);

                        if (uRole.equalsIgnoreCase("Farmer")) {
                            farmerRegistry.put(uName, new Farmer(uName, uID, uEmail));
                        }
                    }
                }
            }
            System.out.println("📂 System: Relational tables loaded successfully.");
        } catch (Exception e) {
            System.out.println("⚠️ Database load warning: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        loadDatabase();
        if (!farmerRegistry.isEmpty()) {
            saveDatabase(); 
            System.out.println("✅ database.txt 已经根据现有用户补全了所有关联表资料！");
        }
        Scanner sc = new Scanner(System.in);
        User currentUser = null; // （dynamic polymorphism）here is a variable of the parent class = parent class的变量
        boolean exitSystem = false;

        while (!exitSystem) {
            System.out.println("\n======= 🌱 AGRIMON ENTRY SYSTEM =======");
            System.out.println("[1] Sign in  [2] Sign Up  [3] Exit");
            System.out.print("Entry > ");
            int entry = sc.nextInt(); sc.nextLine();

            if (entry == 2) {
                System.out.print("Set Username: ");
                String regName = sc.nextLine();
                if (userDatabase.containsKey(regName)) {
                    System.out.println("❌ Username already exists!");
                    continue; 
                }
                System.out.print("Enter Email: ");
                String regEmail = sc.nextLine();
                System.out.print("Set Password: ");
                String regPass = sc.nextLine();
                System.out.print("Confirm Password: ");
                String confirmPass = sc.nextLine();
                if (!regPass.equals(confirmPass)) {
                    System.out.println("❌ Passwords do not match! Registration failed.");
                    continue;
                }
                System.out.println("Select Role: [1] Farmer  [2] Admin");
                int regRole = sc.nextInt(); sc.nextLine();
                String roleStr = (regRole == 1) ? "Farmer" : "Admin";
                String prefix = roleStr.equalsIgnoreCase("Admin") ? "ADM" : "FAR";
                String newID = prefix + "-" + String.format("%03d", roleDatabase.size() + 1);
                userDatabase.put(regName, regPass);
                roleDatabase.put(regName, roleStr);
                userIDDatabase.put(regName, newID);
                emailDatabase.put(regName, regEmail);
                if (roleStr.equalsIgnoreCase("Farmer")) {
                    // 这样会触发 Farmer 的构造函数，自动生成初始的 2 个农场和 18 个地块
                    farmerRegistry.put(regName, new Farmer(regName, newID, regEmail));
                }
                saveDatabase();
                System.out.println("✅ Registration Successful! Your ID is: " + newID);
            } else if (entry == 1) {
                System.out.print("Username: ");
                String inputName = sc.nextLine();
                System.out.print("👁️ Show password while typing? (y/n): ");
                String eyeTrigger = sc.nextLine();
    
                if (eyeTrigger.equalsIgnoreCase("y")) {
                    System.out.print("Enter Password (VISIBLE): ");
                } else {
                    System.out.print("Enter Password (HIDDEN): ");
                }
                String inputPass = sc.nextLine();

                System.out.print("[\u2713] Remember me? (y/n): "); 
                String remember = sc.nextLine();

                if (userDatabase.containsKey(inputName) && userDatabase.get(inputName).equals(inputPass)) {
                    System.out.println("\n✅ Login Successful!");
                    if (remember.equalsIgnoreCase("y")) System.out.println(" (Account remembered) ");
                    
                    String role = roleDatabase.get(inputName);
                    String actualID = userIDDatabase.get(inputName);
                    String actualEmail = emailDatabase.get(inputName);
                    if (role.equals("Farmer")) { // Here means we will know the “current user” variable is farmer or admin until the program is rinning. 
                                                            // Based on the login role, we will know either it is a Farmer object or an Admin object to this variable.
                        if (!farmerRegistry.containsKey(inputName)) {
                            farmerRegistry.put(inputName, new Farmer(inputName, actualID, actualEmail));
                        }
        
                        currentUser = farmerRegistry.get(inputName); // If the user is farmer, the system will show the dashboard of farmer.
        
                    } else {
                        currentUser = new Admin(inputName, actualID, actualEmail); //Similarly, If the user is admin, the system will show the dashboard of admin.
                    }
    
                    runDashboard(currentUser, sc);
                } else {
                    System.out.println("❌ Login Failed!");
                    System.out.println("Need help? [1] Forgot Username [2] Forgot Password [3] Try Again");
                    System.out.print("Choice > ");
                    int helpChoice = sc.nextInt(); sc.nextLine();

                    if (helpChoice == 1 || helpChoice == 2) {
                        System.out.print("Enter your Registered Email: ");
                        String searchEmail = sc.nextLine();

                        String foundName = null;
                        for (Map.Entry<String, String> entrySet : emailDatabase.entrySet()) {
                            if (entrySet.getValue().equalsIgnoreCase(searchEmail)) {
                                foundName = entrySet.getKey();
                                break;
                            }
                        }

                        if (foundName != null) {
                            if (helpChoice == 1) {
                                System.out.println("💡 Your Username is: " + foundName);
                            } else {
                                System.out.print("Set New Password: ");
                                String newP = sc.nextLine();
                                System.out.print("Confirm New Password: ");
                                String confirmP = sc.nextLine();
                                if (newP.equals(confirmP)) {
                                    userDatabase.put(foundName, newP);
                                    saveDatabase();
                                    System.out.println("✅ Password updated successfully!");
                                } else {
                                    System.out.println("❌ Mismatch! Password reset failed.");
                                }
                            }
                        } else {
                            System.out.println("❌ Error: ID not found.");
                        }
                    }
                }
            } else if (entry == 3) {
                System.out.println("Exiting... See you next time!");
                exitSystem = true;
            }
        }
        sc.close();
    } // main 结束

    private static void runDashboard(User user, Scanner sc) {
        boolean session = true;
        boolean isFirstLogin = true; 

        while (session) {
            if (user instanceof Admin) {
                Admin adm = (Admin) user;
                if (isFirstLogin) {
                    showAdminInitialDashboard(adm, roleDatabase);
                    isFirstLogin = false; 
                }
                adm.showDashboard(); 
            } else {
                user.showDashboard();
            }
            if (!sc.hasNextInt()) {
                sc.nextLine(); 
                continue;
            }

            int action = sc.nextInt(); sc.nextLine();
            if (action == 0) {
                System.out.println("\n[SYSTEM] Logging out from Session...");
                System.out.println("Returning to Region Selection Screen...\n");
                session = false; 
            } 
            else if (user instanceof Farmer) {
                handleFarmerActions((Farmer)user, action, sc);
            } 
            else if (user instanceof Admin) {
                handleAdminActions((Admin)user, action, roleDatabase, sc, user);
            }
        }
    } // runDashboard 结束

    private static void showAdminInitialDashboard(Admin admin, Map<String, String> roles) {
        int activeCount = 0;
        for (String role : roles.values()) if (role.equalsIgnoreCase("Farmer")) activeCount++;

        System.out.println("\n" + "=".repeat(55));
        System.out.println(" [NAV] User Management | Farm Management | Monitoring");
        System.out.println("=".repeat(55));
        
        System.out.println("\n +-------------------+   +--------------------------------+");
        System.out.println(" |  👤 Active         |   |  📊 Data Summary                |");
        System.out.println(" |     Farmers       |   |                                |");
        System.out.println(" |                   |   |  > System Health : " + String.format("%.1f", admin.getSystemHealth()) + "%      |");
        System.out.println(" |        " + String.format("%-2d", activeCount) + "         |   |  > Total Users   : " + String.format("%-11d", roles.size()) + " |");
        System.out.println(" |      persons      |   |  > Total Farms   : " + String.format("%-11d", admin.getTotalFarms()) + " |");
        System.out.println(" |                   |   |  > Most Planted  : Corn        |");
        System.out.println(" +-------------------+   +--------------------------------+");
        System.out.println("\n [STATUS] All nodes responding. No critical errors.");
        System.out.println("-".repeat(55));
    }

    private static void handleFarmerActions(Farmer f, int act, Scanner sc) {
        switch (act) {
            case 1: 
                f.selectAndMonitorFarm(sc); 
                break;
            case 2: 
                f.editFarmCrops(sc); 
                break;
            case 3: 
                f.showProfile();
                break;
            case 4:
                f.showSystemInfo(); 
                break;
            default: 
                System.out.println("Invalid Option.");
        }
    } // handleFarmerActions 结束
    
    private static void handleAdminActions(Admin admin, int act, Map<String, String> roles, Scanner sc, User currentUser) {
        switch (act) {
            case 1:
                System.out.println("\n" + "=".repeat(45));
                System.out.println(" 📊 [ SYSTEM STATISTICS ]");
                System.out.println(" Total Members : " + roles.size());
                System.out.println(" Status        : 🟢 Online & Synced");
                System.out.println("=".repeat(45));

                System.out.println("\n===== 👥 USER MANAGEMENT LIST =====");
                System.out.printf("%-12s | %-15s | %-10s\n", "User ID", "Username", "Role");
                System.out.println("-".repeat(45));

                for (String username : roleDatabase.keySet()) {
                    String role = roleDatabase.get(username);
                    String id = userIDDatabase.get(username); 

                    System.out.printf("%-12s | %-15s | %-10s\n", id, username, role);
                }

                System.out.println("-".repeat(45));
                System.out.println("✅ All data pulled from Central Database.");
                break;
            case 2: 
                System.out.println("\n" + "=".repeat(60));
                System.out.println("            🏢 GLOBAL FARM MANAGEMENT CENTER");
                System.out.println("=".repeat(60));
                System.out.println("\n 📋 GLOBAL FARM LIST (All Registered Farmers)");
                System.out.println(" -----------------------------------------------------------");
                System.out.println("  OWNER      | F ID  | LOCATION           | CROP      | STATUS ");
                System.out.println(" ------------|-------|--------------------|-----------|--------");

                List<Farm> allSystemFarms = new ArrayList<>();
                boolean foundFarmer = false;
                int globalFarmCount = 1;

                for (String username : roleDatabase.keySet()) {
                    if (roleDatabase.get(username).equalsIgnoreCase("Farmer")) {
            
                        Farmer realFarmer = SmartFarmSystem.farmerRegistry.get(username);

                        if (realFarmer == null) {
                            String id = userIDDatabase.get(username);
                            String email = emailDatabase.get(username);
                            realFarmer = new Farmer(username, id, email);
                            SmartFarmSystem.farmerRegistry.put(username, realFarmer);
                        }

                        foundFarmer = true;
                        List<Farm> userFarms = realFarmer.getMyFarms();
            
                        for (int i = 0; i < userFarms.size(); i++) {
                            Farm f = userFarms.get(i);
                            allSystemFarms.add(f); 
                
                            String displayID = String.format("%03d", globalFarmCount++);
                            String status = (f.health < 80) ? "🟡 ALERT " : "🟢 ONLINE";
            
                            System.out.printf(" %-11s | %-5s | %-18s | %-9s | %s\n", 
                                            username, displayID, f.fName, f.cropType, status);
                        }
                    }
                }

                if (!foundFarmer) {
                    System.out.println("        [ No registered farmers found in database ]");
                }
            
                System.out.println(" -----------------------------------------------------------");
                System.out.println("\n [1] 🔍 Run Global System Diagnostic");
                System.out.println(" [0] 🔙 Back to Dashboard");
                System.out.print("\n Choice: ");

                String adminInput = sc.nextLine();
                if (adminInput.equals("1") && !allSystemFarms.isEmpty()) {
                    admin.runGlobalDiagnostic(allSystemFarms);
                } else if (adminInput.equals("1")) {
                    System.out.println("❌ No farms available to diagnose.");
                }
                break;
            case 3: 
                boolean backToDashboard = false; 
                
                while (!backToDashboard) { 
                    System.out.println("\n" + "=".repeat(60));
                    System.out.println(" [NAV] User Management | Farm Management | > Monitoring & Data");
                    System.out.println("=".repeat(60));
                
                    System.out.println("\n🌍 SELECT REGION TO MONITOR:");
                    String[] regions = {"Johor", "Kedah", "Kelantan", "Melaka", "Negeri Sembilan", 
                                        "Pahang", "Penang", "Perak", "Perlis", "Sabah", "Sarawak", "Selangor", "Terengganu"};
                
                    for (int i = 0; i < regions.length; i++) {
                        System.out.printf("%d. %-20s", (i + 1), regions[i]);
                        if ((i + 1) % 2 == 0) System.out.println();
                    }
                    if (regions.length % 2 != 0) System.out.println(); 
                    
                    System.out.println("0. Back to Dashboard");
                    System.out.print("\nSelect Region: ");
                    
                    int regChoice = sc.nextInt(); sc.nextLine();

                    if (regChoice == 0) {
                        backToDashboard = true; 
                    } 
                    else if (regChoice > 0 && regChoice <= regions.length) {
                        String selectedRegion = regions[regChoice - 1];
                    
                        boolean inRegionMenu = true;
                        while (inRegionMenu) { 
                            System.out.println("\n" + "-".repeat(60));
                            System.out.println("📍 CURRENT LOCATION: " + selectedRegion.toUpperCase());
                            System.out.println("-".repeat(60));
                            System.out.println("\n [1] ⚠️ Alert List       [2] 🔍 Real-Time Data");
                            System.out.println("\n [3] 📊 Charts           [4] 📄 Reports");
                            System.out.println("\n [0] Return to Region Selection");
                            System.out.print("\nAction: ");
                            
                            int featureChoice = sc.nextInt(); sc.nextLine();

                            if (featureChoice == 0) {
                                inRegionMenu = false; 
                            } else {
                                String title = switch(featureChoice) {
                                    case 1 -> "⚠️ ALERT LIST";
                                    case 2 -> "🔍 REAL-TIME DATA";
                                    case 3 -> "📊 CHARTS";
                                    case 4 -> "📄 REPORTS";
                                    default -> "UNKNOWN";
                                };
                            
                                if (!title.equals("UNKNOWN")) {
                                    showDetailPlaceholder(title, selectedRegion, sc, admin);
                                } else {
                                    System.out.println("Invalid selection.");
                                }
                            }
                        }
                    } else {
                        System.out.println("Invalid region selection. Please try again.");
                    }
                } 
                break;
            case 4: 
                boolean inProfile = true;
                while (inProfile) {
                    admin.showProfile(); 
                    
                    System.out.print("\nAction: ");
                    String profileChoice = sc.nextLine();
                    
                    if (profileChoice.equals("0")) {
                        inProfile = false; 
                    } else {
                        System.out.println("Invalid choice. Press 0 to back.");
                    }
                }
                break;

            case 5: 
                boolean inSysInfo = true;
                while (inSysInfo) {
                    admin.showSystemInfo();
                    
                    System.out.print("\nAction: ");
                    String sysChoice = sc.nextLine();
                    
                    if (sysChoice.equals("0")) {
                        inSysInfo = false; 
                    } else {
                        System.out.println("Invalid choice. Press 0 to back.");
                    }
                }
                break;
            default: 
                System.out.println("Invalid Option.");
        } // 这里是 switch 的结束
    } // 这里是 handleAdminActions 方法的结束

    private static void showDetailPlaceholder(String title, String region, Scanner sc, Admin admin) {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("       " + title);
        System.out.println("       Location: " + region);
        System.out.println("=".repeat(50));
        java.time.LocalTime now = java.time.LocalTime.now();

        if (title.contains("ALERT LIST")) {
            String currentTime = now.withNano(0).toString();
            String earlierTime = now.minusMinutes(15).withNano(0).toString();
            System.out.println("\n[ ID ] | [ LEVEL ]  | [ DESCRIPTION ]             | [ TIME ]");
            System.out.println("-------|------------|-----------------------------|----------");

            int randomMoisture = 10 + (int)(Math.random() * 15); // 10-25% 
            int randomTemp = 35 + (int)(Math.random() * 5);     // 35-40°C

            String criticalDesc = "Low Soil Moisture (" + randomMoisture + "%) in " + region;
            String warningDesc = "High Temp Alert (" + randomTemp + "°C) " + region + " Node";

            System.out.printf("A001   | CRITICAL   | %-27s | %s\n", criticalDesc, currentTime);
            System.out.printf("A005   | WARNING    | %-27s | %s\n", warningDesc, earlierTime);
            System.out.printf("A009   | INFO       | %-27s | %s\n", "System Health Check Pass", now.withNano(0));

        } else if (title.contains("REAL-TIME DATA")) {
            System.out.println("\n--- 📍 LIVE SENSOR TELEMETRY ---");
            double dynamicRate = 1.0 + (Math.random() * 0.5);
            System.out.printf("Status: 🟢 CONNECTED | Sampling Rate: %.1fs\n", dynamicRate);
            System.out.println("-".repeat(40));

            double temp = 27.0 + (Math.random() * 4); // 27-31度
            int soil = 45 + (int)(Math.random() * 15); // 45-60% 正常湿度
            double ph = 6.2 + (Math.random() * 0.8);   // 6.2-7.0 理想pH
        
            System.out.printf("🌡️ Air Temperature : %.1f °C\n", temp);
            System.out.printf("💧 Soil Moisture    : %d %%\n", soil);
            System.out.printf("🧪 Soil pH Level    : %.2f\n", ph);
            System.out.printf("☀️ Light Exposure   : %d Lux\n", 15000 + (int)(Math.random() * 2000));
            System.out.println("-".repeat(40));
            System.out.println("Latest Sync: " + now.withNano(0));

        } else if (title.contains("CHARTS")) {
            int currentHour = java.time.LocalTime.now().getHour();

            double hourlyBaseTemp;
            if (currentHour >= 6 && currentHour <= 10) hourlyBaseTemp = 26.0;      // 早晨
            else if (currentHour > 10 && currentHour <= 16) hourlyBaseTemp = 32.0; // 下午高温
            else if (currentHour > 16 && currentHour <= 21) hourlyBaseTemp = 28.0; // 傍晚
            else hourlyBaseTemp = 24.0;                                           // 深夜/凌晨

            double realTimeTemp = hourlyBaseTemp + (Math.random() * 2.0);

            System.out.println("\n📊 24H TEMP TREND (°C) - [LOCAL SYNC]");
            System.out.printf("Current Device Time: %02d:00 | Local Temp: %.1f°C\n", currentHour, realTimeTemp);

            int gapSize = Math.max(0, (int)(realTimeTemp - 20) / 2);
            String trendGap = " ".repeat(gapSize);
            System.out.println(" 35| " + trendGap + "        _ ");
            System.out.println(" 30| " + trendGap + "  _  __/ \\_ ");
            System.out.println(" 25| " + trendGap + "_/ \\/      \\_ ");
            System.out.println("   +----------------> Time");

            System.out.println("\n💧 WEEKLY WATER USAGE (Litre)");
            String[] days = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
            for (String day : days) {
                int usage = (int)(realTimeTemp * 0.5) + (int)(Math.random() * 8);
                System.out.printf("%-3s | %-25s (%d L)\n", day, "█".repeat(usage), usage);
            }

        } else if (title.contains("REPORTS")) {
            String fileName = "SMART_FARM_" + region.toUpperCase() + "_APR2026.txt";
            System.out.println("\n📊 ANALYZING MONTHLY DATA FOR: " + region.toUpperCase());
            System.out.println("Processing period: 1st April 2026 - 18th April 2026");

            System.out.print("\nGenerating Report: [");
            for(int i=0; i<20; i++) {
                try { Thread.sleep(50); } catch(Exception e) {} // 模拟小小的延迟
                System.out.print("■");
            }
            System.out.println("] 100%");

            double avgTemp = 26.0 + Math.random() * 4;
            int waterUsage = 1500 + (int)(Math.random() * 500);
            String activeSensor = String.format("Soil_Node_%03d", (int)(Math.random() * 100));

            try (PrintWriter writer = new PrintWriter(new FileWriter(fileName))) {
                writer.println("==========================================");
                writer.println("        AGRIMON SYSTEM EXECUTIVE REPORT    ");
                writer.println("==========================================");
                writer.println("Region    : " + region.toUpperCase());
                writer.println("Gen Date  : " + new java.util.Date());
                writer.println("------------------------------------------");
                writer.printf("Avg. Temperature    : %.2f °C\n", avgTemp);
                writer.printf("Total Water Usage   : %d Liters\n", waterUsage);
                writer.println("System Uptime       : 99.8%");
                writer.println("Most Active Sensor  : " + activeSensor);
                writer.println("\n--- RECOMMENDATIONS ---");
                writer.println("1. Soil moisture levels are stable.");
                writer.println("2. Replace battery for Sensor Node #09.");
                writer.println("------------------------------------------");
                writer.println("Report Digital Signature: " + java.util.UUID.randomUUID());
            } catch (IOException e) {
                System.out.println("❌ System Error: Physical storage not accessible.");
            }

            System.out.println("\n--- 📑 EXECUTIVE SUMMARY ---");
            System.out.printf("🔹 Avg. Temperature    : %.2f °C\n", avgTemp);
            System.out.printf("🔹 Total Water Usage   : %d Liters\n", waterUsage);
            System.out.printf("🔹 System Uptime       : 99.8%%\n");
            System.out.printf("🔹 Most Active Sensor  : %s\n", activeSensor);

            System.out.println("\n--- 📝 RECOMMENDATIONS ---");
            System.out.println("1. Soil moisture levels are stable; maintain current irrigation.");
            System.out.println("2. Warning: Sensor Node #09 battery is at 15%, replace soon.");

            System.out.println("\n" + "-".repeat(40));
            System.out.println("✅ Report successfully generated and saved.");
            System.out.println("📂 Path: ./" + fileName);
        }
    }
} // SmartFarmSystem 类的结束。

// ==========================================
// 4. AUXILIARY TOOLS (必须在 SmartFarmSystem 类外面)
// ==========================================
class FarmSystem {
    public static void showSensors() {
        double moisture = 15.0 + (Math.random() * 20);
        double water = (moisture < 30.0) ? (30.0 - moisture) * 5.5 : 0;
        
        System.out.printf("\n[Sensor] Soil Moisture: %.1f%%\n", moisture);
        
        if (water > 0) {
            System.out.printf("⚠️ Figma Alert: Suggesting %.2fL water.\n", water);
        } else {
            System.out.println("✅ Status: Optimal.");
        }
    }
}