package miniProject;


import java.util.*;

// ======================== ITEM CLASSES ========================
class Items {
    String location, description, dateFound;
    boolean isClaimed;

    Items(String location, String description, String dateFound) {
        this.location = location;
        this.description = description;
        this.dateFound = dateFound;
        this.isClaimed = false;
    }

    void display() {
        System.out.println("Location Found: " + location);
        System.out.println("Description: " + description);
        System.out.println("Date Found: " + dateFound);
        System.out.println("Claimed: " + (isClaimed ? "Yes" : "No"));
    }
}

// Category classes
class Electronics extends Items {
    String name, color, brandModel;
    Electronics(String name, String color, String brandModel, String location, String description, String dateFound) {
        super(location, description, dateFound);
        this.name = name;
        this.color = color;
        this.brandModel = brandModel;
    }

    void display() {
        System.out.println("\n--- Electronic Item ---");
        System.out.println("Name: " + name);
        System.out.println("Color: " + color);
        System.out.println("Brand/Model: " + brandModel);
        super.display();
    }
}

class DailyUse extends Items {
    String name, color;
    DailyUse(String name, String color, String location, String description, String dateFound) {
        super(location, description, dateFound);
        this.name = name;
        this.color = color;
    }

    void display() {
        System.out.println("\n--- Daily Use Item ---");
        System.out.println("Name: " + name);
        System.out.println("Color: " + color);
        super.display();
    }
}

class Stationary extends Items {
    String name, color;
    Stationary(String name, String color, String location, String description, String dateFound) {
        super(location, description, dateFound);
        this.name = name;
        this.color = color;
    }

    void display() {
        System.out.println("\n--- Stationary Item ---");
        System.out.println("Name: " + name);
        System.out.println("Color: " + color);
        super.display();
    }
}

class Accessories extends Items {
    String name, color;
    Accessories(String name, String color, String location, String description, String dateFound) {
        super(location, description, dateFound);
        this.name = name;
        this.color = color;
    }

    void display() {
        System.out.println("\n--- Accessory ---");
        System.out.println("Name: " + name);
        System.out.println("Color: " + color);
        super.display();
    }
}

class Miscellaneous extends Items {
    String name;
    Miscellaneous(String name, String location, String description, String dateFound) {
        super(location, description, dateFound);
        this.name = name;
    }

    void display() {
        System.out.println("\n--- Miscellaneous Item ---");
        System.out.println("Name: " + name);
        super.display();
    }
}

class IDCards extends Items {
    String department, uNumber, year;
    IDCards(String department, String uNumber, String year, String location, String description, String dateFound) {
        super(location, description, dateFound);
        this.department = department;
        this.uNumber = uNumber;
        this.year = year;
    }

    void display() {
        System.out.println("\n--- ID Card ---");
        System.out.println("Department: " + department);
        System.out.println("U-Number: " + uNumber);
        System.out.println("Year: " + year);
        super.display();
    }
}

// ======================== DATABASE CLASS ========================
class Database {
    private HashMap<String, HashMap<String, ArrayList<Items>>> itemList = new HashMap<>();
    private Scanner sc = new Scanner(System.in);

    private String getInput(String prompt) {
        System.out.print(prompt);
        return sc.nextLine().trim();
    }

    void registerFound() {
        System.out.println("\nSelect the type of item found:");
        System.out.println("1. Electronics\n2. Daily Use\n3. Stationary\n4. Accessories\n5. Miscellaneous\n6. ID Card");
        String choice = getInput("Enter choice: ");

        Items item = null;
        String category = "", subKey = "";

        switch (choice) {
            case "1":
                category = "electronics";
                String eName = getInput("Enter device name: ");
                String eColor = getInput("Enter color: ");
                String brand = getInput("Enter brand/model: ");
                String eLoc = getInput("Enter location found: ");
                String eDesc = getInput("Enter description: ");
                String eDate = getInput("Enter date found: ");
                item = new Electronics(eName, eColor, brand, eLoc, eDesc, eDate);
                subKey = eName.toLowerCase();
                break;

            case "2":
                category = "dailyuse";
                String dName = getInput("Enter item name: ");
                String dColor = getInput("Enter color: ");
                String dLoc = getInput("Enter location found: ");
                String dDesc = getInput("Enter description: ");
                String dDate = getInput("Enter date found: ");
                item = new DailyUse(dName, dColor, dLoc, dDesc, dDate);
                subKey = dName.toLowerCase();
                break;

            case "3":
                category = "stationary";
                String sName = getInput("Enter item name: ");
                String sColor = getInput("Enter color: ");
                String sLoc = getInput("Enter location found: ");
                String sDesc = getInput("Enter description: ");
                String sDate = getInput("Enter date found: ");
                item = new Stationary(sName, sColor, sLoc, sDesc, sDate);
                subKey = sName.toLowerCase();
                break;

            case "4":
                category = "accessories";
                String aName = getInput("Enter item name: ");
                String aColor = getInput("Enter color: ");
                String aLoc = getInput("Enter location found: ");
                String aDesc = getInput("Enter description: ");
                String aDate = getInput("Enter date found: ");
                item = new Accessories(aName, aColor, aLoc, aDesc, aDate);
                subKey = aName.toLowerCase();
                break;

            case "5":
                category = "miscellaneous";
                String mName = getInput("Enter item name: ");
                String mLoc = getInput("Enter location found: ");
                String mDesc = getInput("Enter description: ");
                String mDate = getInput("Enter date found: ");
                item = new Miscellaneous(mName, mLoc, mDesc, mDate);
                subKey = mName.toLowerCase();
                break;

            case "6":
                category = "idcards";
                String dept = getInput("Enter department: ");
                String uNo = getInput("Enter U-Number: ");
                String year = getInput("Enter year: ");
                String iLoc = getInput("Enter location found: ");
                String iDesc = getInput("Enter description: ");
                String iDate = getInput("Enter date found: ");
                item = new IDCards(dept, uNo, year, iLoc, iDesc, iDate);
                subKey = dept.toLowerCase();
                break;

            default:
                System.out.println("Invalid choice.");
                return;
        }

        itemList.putIfAbsent(category, new HashMap<>());
        itemList.get(category).putIfAbsent(subKey, new ArrayList<>());
        itemList.get(category).get(subKey).add(item);
        System.out.println("\n✅ Item registered successfully!");
    }

    void searchLost() {
        System.out.println("\nSelect category to search:");
        System.out.println("1. Electronics\n2. Daily Use\n3. Stationary\n4. Accessories\n5. Miscellaneous\n6. ID Card");
        String choice = getInput("Enter choice: ");

        String category = switch (choice) {
            case "1" -> "electronics";
            case "2" -> "dailyuse";
            case "3" -> "stationary";
            case "4" -> "accessories";
            case "5" -> "miscellaneous";
            case "6" -> "idcards";
            default -> null;
        };

        if (category == null) {
            System.out.println("Invalid category.");
            return;
        }

        if (!itemList.containsKey(category) || itemList.get(category).isEmpty()) {
            System.out.println("\n⚠️ No items found in this category yet.");
            return;
        }

        String nameKey = getInput("Enter name or keyword to search: ").toLowerCase();
        if (!itemList.get(category).containsKey(nameKey)) {
            System.out.println("\n⚠️ No items with that name found.");
            return;
        }

        ArrayList<Items> possibleMatches = itemList.get(category).get(nameKey);
        ArrayList<Items> verified = new ArrayList<>();

        if (category.equals("electronics")) verified = verifyElectronics(possibleMatches);
        else if (category.equals("idcards")) verified = verifyIDCards(possibleMatches);
        else verified = verifyGeneric(possibleMatches);

        if (verified.isEmpty()) {
            System.out.println("\n⚠️ No matching items found for your description.");
            return;
        }

        System.out.println("\nPossible matches:");
        for (int i = 0; i < verified.size(); i++) {
            System.out.println("\n[" + (i + 1) + "]");
            verified.get(i).display();
        }

        String claim = getInput("\nEnter item number to claim (0 to cancel): ");
        try {
            int index = Integer.parseInt(claim);
            if (index > 0 && index <= verified.size()) {
                verified.get(index - 1).isClaimed = true;
                System.out.println("✅ Item successfully claimed!");
            } else {
                System.out.println("Cancelled or invalid number.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input.");
        }
    }

    // ---------- Verification methods ----------
    private ArrayList<Items> verifyElectronics(ArrayList<Items> list) {
        ArrayList<Items> verified = new ArrayList<>();
        String color = getInput("Enter color: ").toLowerCase();
        String brand = getInput("Enter brand/model: ").toLowerCase();
        String loc = getInput("Enter location found: ").toLowerCase();

        for (Items item : list) {
            if (item instanceof Electronics e) {
                int score = 0;
                if (e.color.equalsIgnoreCase(color)) score += 30;
                if (e.brandModel.equalsIgnoreCase(brand)) score += 40;
                if (e.location.equalsIgnoreCase(loc)) score += 30;
                if (score >= 70) verified.add(e);
            }
        }
        return verified;
    }

    private ArrayList<Items> verifyGeneric(ArrayList<Items> list) {
        ArrayList<Items> verified = new ArrayList<>();
        String color = getInput("Enter color: ").toLowerCase();
        String loc = getInput("Enter location found: ").toLowerCase();

        for (Items item : list) {
            if (item.location.equalsIgnoreCase(loc)) {
                if (item instanceof DailyUse d && d.color.equalsIgnoreCase(color)) verified.add(d);
                else if (item instanceof Stationary s && s.color.equalsIgnoreCase(color)) verified.add(s);
                else if (item instanceof Accessories a && a.color.equalsIgnoreCase(color)) verified.add(a);
            }
        }
        return verified;
    }

    private ArrayList<Items> verifyIDCards(ArrayList<Items> list) {
        ArrayList<Items> verified = new ArrayList<>();
        String year = getInput("Enter year: ").toLowerCase();
        String uNum = getInput("Enter U-Number: ").toLowerCase();
        String loc = getInput("Enter location found: ").toLowerCase();

        for (Items item : list) {
            if (item instanceof IDCards id) {
                int score = 0;
                if (id.year.equalsIgnoreCase(year)) score += 30;
                if (id.uNumber.equalsIgnoreCase(uNum)) score += 40;
                if (id.location.equalsIgnoreCase(loc)) score += 30;
                if (score >= 70) verified.add(id);
            }
        }
        return verified;
    }
}

// ======================== MAIN CLASS ========================
public class LostAndFoundSystem {
    public static void main(String[] args) {
        Database db = new Database();
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println("\n--- Lost and Found Management System ---");
            System.out.println("1. Register Found Item");
            System.out.println("2. Search Lost Item");
            System.out.println("3. Exit");
            System.out.print("Enter choice: ");
            String choice = sc.nextLine();

            switch (choice) {
                case "1" -> db.registerFound();
                case "2" -> db.searchLost();
                case "3" -> {
                    System.out.println("Exiting... Goodbye!");
                    sc.close();
                    return;
                }
                default -> System.out.println("Invalid choice. Try again.");
            }
        }
    }
}
