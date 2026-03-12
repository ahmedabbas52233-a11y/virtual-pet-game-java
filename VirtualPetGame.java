import java.util.ArrayList;
import java.util.Scanner;

// ANSI color codes for better visuals
class Colors {
    public static final String RESET = "\u001B[0m";
    public static final String GREEN = "\u001B[32m";
    public static final String YELLOW = "\u001B[33m";
    public static final String RED = "\u001B[31m";
    public static final String CYAN = "\u001B[36m";
    public static final String BOLD = "\u001B[1m";
}

// User class to manage a user's virtual pets
class User {
    private final String username;
    private final ArrayList<VirtualPet> pets;
    public static final int MAX_PETS = 3;

    public User(String username) {
        this.username = username;
        this.pets = new ArrayList<>();
    }

    public String getUsername() {
        return username;
    }

    public ArrayList<VirtualPet> getPets() {
        return pets;
    }

    public void addPet(VirtualPet pet) {
        if (pets.size() < MAX_PETS) {
            pets.add(pet);
            System.out.println(Colors.GREEN + pet.getName() + " adopted by " + username + "!" + Colors.RESET);
        } else {
            System.out.println(Colors.RED + username + " already has the maximum number of pets." + Colors.RESET);
        }
    }

    public void removePet(VirtualPet pet) {
        pets.remove(pet);
    }

    public void displayUserPets() {
        System.out.println(Colors.CYAN + Colors.BOLD + "User: " + username + " | Pets: " + pets.size() + Colors.RESET);
        if (pets.isEmpty()) {
            System.out.println("   No pets yet.");
        }
        for (VirtualPet pet : pets) {
            pet.displayStatus();
        }
    }
}

public class VirtualPetGame {
    private static ArrayList<VirtualPet> availablePets;
    private static ArrayList<User> users;

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            // Welcome screen
            System.out.println(Colors.CYAN + Colors.BOLD + "==========================================" + Colors.RESET);
            System.out.println(Colors.BOLD + "     Welcome to Virtual Pet Simulator!     " + Colors.RESET);
            System.out.println(Colors.CYAN + Colors.BOLD + "==========================================" + Colors.RESET);
            System.out.println("Take care of your pets – feed, play, rest...");
            System.out.println("Neglect them and they might get sick or die!\n");

            // Initialize dummy data
            availablePets = DummyDataSetup.setupAvailablePets();
            users = DummyDataSetup.setupUsers(availablePets);

            boolean exit = false;

            while (!exit) {
                // Simulate time passing every loop (decay + random events)
                simulateTimePass();

                System.out.println(Colors.CYAN + "\n--- Main Menu ---" + Colors.RESET);
                System.out.println("1. Display available pets");
                System.out.println("2. Display users and their pets");
                System.out.println("3. Interact with a pet");
                System.out.println("4. Simulate damage to a pet");
                System.out.println("5. Exit");
                System.out.print("Enter choice: ");

                int choice;
                try {
                    choice = scanner.nextInt();
                    scanner.nextLine(); // consume newline
                } catch (Exception e) {
                    System.out.println(Colors.RED + "Invalid input. Please enter a number." + Colors.RESET);
                    scanner.nextLine();
                    continue;
                }

                switch (choice) {
                    case 1 -> displayAvailablePets();
                    case 2 -> displayUsers();
                    case 3 -> manageUserPetInteraction(scanner);
                    case 4 -> simulateDamage(scanner);
                    case 5 -> exit = true;
                    default -> System.out.println(Colors.YELLOW + "Invalid choice. Try again." + Colors.RESET);
                }
            }

            System.out.println(Colors.GREEN + "\nThank you for playing! Come back soon! 🐾" + Colors.RESET);
        }
    }

    // Time decay + random events
    private static void simulateTimePass() {
        for (User user : users) {
            for (VirtualPet pet : user.getPets()) {
                // Natural decay
                pet.setHunger(pet.getHunger() + 5);
                pet.setEnergy(pet.getEnergy() - 3);
                pet.setHealth(pet.getHealth() - 2);

                // Random event (30% chance)
                if (Math.random() < 0.30) {
                    if (Math.random() < 0.6) {
                        // Positive event
                        System.out.println(Colors.GREEN + "Random Event: " + pet.getName()
                                + " chased a butterfly! +12 Happiness" + Colors.RESET);
                        pet.setHappiness(pet.getHappiness() + 12);
                    } else {
                        // Negative event
                        System.out.println(Colors.YELLOW + "Random Event: " + pet.getName()
                                + " got startled by thunder! -8 Happiness" + Colors.RESET);
                        pet.setHappiness(pet.getHappiness() - 8);
                    }
                }

                // Check death
                if (pet.isDead()) {
                    System.out.println(Colors.RED + "Tragic! " + pet.getName() + " has passed away..." + Colors.RESET);
                    user.removePet(pet);
                    availablePets.add(pet);
                }
            }
        }
    }

    private static void displayAvailablePets() {
        System.out.println(Colors.CYAN + "\n--- Available Pets for Adoption ---" + Colors.RESET);
        if (availablePets.isEmpty()) {
            System.out.println("No pets left to adopt.");
            return;
        }
        for (VirtualPet pet : availablePets) {
            pet.displayStatus();
        }
    }

    private static void displayUsers() {
        System.out.println(Colors.CYAN + "\n--- All Users & Their Pets ---" + Colors.RESET);
        if (users.isEmpty()) {
            System.out.println("No users yet.");
            return;
        }
        for (User user : users) {
            user.displayUserPets();
        }
    }

    private static void manageUserPetInteraction(Scanner scanner) {
        if (users.isEmpty()) {
            System.out.println(Colors.YELLOW + "No users exist yet." + Colors.RESET);
            return;
        }

        System.out.println(Colors.CYAN + "\nSelect a user:" + Colors.RESET);
        for (int i = 0; i < users.size(); i++) {
            System.out.println((i + 1) + ". " + users.get(i).getUsername());
        }
        int userIndex = getValidInt(scanner, 1, users.size()) - 1;
        User user = users.get(userIndex);

        if (user.getPets().isEmpty()) {
            System.out.println(Colors.YELLOW + user.getUsername() + " has no pets yet." + Colors.RESET);
            return;
        }

        System.out.println(Colors.CYAN + "\nSelect a pet:" + Colors.RESET);
        ArrayList<VirtualPet> pets = user.getPets();
        for (int i = 0; i < pets.size(); i++) {
            System.out.println((i + 1) + ". " + pets.get(i).getName());
        }
        int petIndex = getValidInt(scanner, 1, pets.size()) - 1;
        VirtualPet pet = pets.get(petIndex);

        System.out.println(Colors.CYAN + "\nWhat would you like to do with " + pet.getName() + "?" + Colors.RESET);
        System.out.println("1. Feed");
        System.out.println("2. Play");
        System.out.println("3. Rest");
        System.out.println("4. Unique behavior");
        int action = getValidInt(scanner, 1, 4);

        switch (action) {
            case 1 -> {
                System.out.print("How much food? ");
                pet.feed(getValidInt(scanner, 1, 50));
            }
            case 2 -> {
                System.out.print("How much fun? ");
                pet.play(getValidInt(scanner, 1, 30));
            }
            case 3 -> {
                System.out.print("How long to rest? ");
                pet.rest(getValidInt(scanner, 1, 40));
            }
            case 4 -> pet.uniqueBehavior();
        }

        // Optional rename
        System.out.print("\nRename " + pet.getName() + "? (yes/no): ");
        String ans = scanner.nextLine().trim().toLowerCase();
        if (ans.startsWith("y")) {
            System.out.print("New name: ");
            String newName = scanner.nextLine().trim();
            if (!newName.isEmpty()) {
                pet.name = newName;
                System.out.println(Colors.GREEN + "Renamed to " + newName + "!" + Colors.RESET);
            }
        }

        if (pet.isSick()) {
            System.out.println(Colors.RED + "Warning: " + pet.getName() + " is feeling very unwell!" + Colors.RESET);
        }
        if (pet.isDead()) {
            System.out.println(Colors.RED + "Oh no... " + pet.getName() + " has passed away." + Colors.RESET);
            user.removePet(pet);
            availablePets.add(pet);
        }
    }

    private static void simulateDamage(Scanner scanner) {
        if (users.isEmpty()) {
            System.out.println(Colors.YELLOW + "No users to select." + Colors.RESET);
            return;
        }

        System.out.println(Colors.CYAN + "\nSelect user:" + Colors.RESET);
        for (int i = 0; i < users.size(); i++) {
            System.out.println((i + 1) + ". " + users.get(i).getUsername());
        }
        int userIndex = getValidInt(scanner, 1, users.size()) - 1;
        User user = users.get(userIndex);

        if (user.getPets().isEmpty()) {
            System.out.println(Colors.YELLOW + "This user has no pets." + Colors.RESET);
            return;
        }

        System.out.println(Colors.CYAN + "\nSelect pet:" + Colors.RESET);
        ArrayList<VirtualPet> pets = user.getPets();
        for (int i = 0; i < pets.size(); i++) {
            System.out.println((i + 1) + ". " + pets.get(i).getName());
        }
        int petIndex = getValidInt(scanner, 1, pets.size()) - 1;
        VirtualPet pet = pets.get(petIndex);

        System.out.print("Enter damage amount (1-100): ");
        int dmg = getValidInt(scanner, 1, 100);
        pet.setHealth(pet.getHealth() - dmg);

        if (pet.isSick()) {
            System.out.println(Colors.RED + pet.getName() + " is now sick!" + Colors.RESET);
        }
        if (pet.isDead()) {
            System.out.println(Colors.RED + pet.getName() + " could not survive the damage..." + Colors.RESET);
            user.removePet(pet);
            availablePets.add(pet);
        }
    }

    // Helper: get valid integer input in range
    private static int getValidInt(Scanner scanner, int min, int max) {
        while (true) {
            try {
                int val = scanner.nextInt();
                scanner.nextLine(); // consume newline
                if (val >= min && val <= max) {
                    return val;
                }
                System.out.println(
                        Colors.YELLOW + "Please enter a number between " + min + " and " + max + "." + Colors.RESET);
            } catch (Exception e) {
                System.out.println(Colors.RED + "Invalid input. Enter a number." + Colors.RESET);
                scanner.nextLine();
            }
        }
    }
}

// Dummy data setup
class DummyDataSetup {
    public static ArrayList<VirtualPet> setupAvailablePets() {
        ArrayList<VirtualPet> pets = new ArrayList<>();
        for (int i = 1; i <= 10; i++)
            pets.add(new Dog("Dog" + i));
        for (int i = 1; i <= 7; i++)
            pets.add(new Cat("Cat" + i));
        for (int i = 1; i <= 3; i++)
            pets.add(new Fish("Fish" + i));
        return pets;
    }

    public static ArrayList<User> setupUsers(ArrayList<VirtualPet> availablePets) {
        ArrayList<User> users = new ArrayList<>();
        String[] names = { "Alice", "Bob", "Charlie", "Diana", "Eve" };
        for (String name : names) {
            User user = new User(name);
            while (user.getPets().size() < User.MAX_PETS && !availablePets.isEmpty()) {
                VirtualPet pet = availablePets.remove(0);
                user.addPet(pet);
            }
            users.add(user);
        }
        return users;
    }
}

// Abstract VirtualPet
abstract class VirtualPet {
    protected String name;
    protected int health, happiness, hunger, energy;

    public static final int MAX_HEALTH = 100;
    public static final int MAX_HAPPINESS = 70;
    public static final int MAX_HUNGER = 100;
    public static final int MAX_ENERGY = 70;

    public VirtualPet(String name) {
        this.name = name;
        this.health = 100;
        this.happiness = 35;
        this.hunger = 50;
        this.energy = 35;
    }

    public String getName() {
        return name;
    }

    public int getHealth() {
        return health;
    }

    public int getHappiness() {
        return happiness;
    }

    public int getHunger() {
        return hunger;
    }

    public int getEnergy() {
        return energy;
    }

    public void setHealth(int v) {
        health = Math.max(0, Math.min(MAX_HEALTH, v));
    }

    public void setHappiness(int v) {
        happiness = Math.max(0, Math.min(MAX_HAPPINESS, v));
    }

    public void setHunger(int v) {
        hunger = Math.max(0, Math.min(MAX_HUNGER, v));
    }

    public void setEnergy(int v) {
        energy = Math.max(0, Math.min(MAX_ENERGY, v));
    }

    public void feed(int amount) {
        hunger = Math.max(0, hunger - amount);
        System.out.println(Colors.GREEN + name + " ate happily! Hunger: " + hunger + Colors.RESET);
    }

    public void play(int amount) {
        happiness = Math.min(MAX_HAPPINESS, happiness + amount);
        energy = Math.max(0, energy - amount / 2);
        System.out.println(
                Colors.GREEN + name + " had fun! Happiness: " + happiness + ", Energy: " + energy + Colors.RESET);
    }

    public void rest(int amount) {
        energy = Math.min(MAX_ENERGY, energy + amount);
        health = Math.min(MAX_HEALTH, health + amount / 3);
        System.out.println(
                Colors.GREEN + name + " rested well! Energy: " + energy + ", Health: " + health + Colors.RESET);
    }

    public boolean isSick() {
        return health > 0 && health < 20;
    }

    public boolean isDead() {
        return health <= 0;
    }

    public abstract void uniqueBehavior();

    public void displayStatus() {
        System.out.println(Colors.YELLOW + "┌──────────────────────────────┐" + Colors.RESET);
        System.out.println("  " + Colors.BOLD + name + Colors.RESET);
        System.out.println(
                "  Health   : " + (health < 20 ? Colors.RED : Colors.GREEN) + health + Colors.RESET + " / 100");
        System.out.println("  Happiness: " + happiness + " / " + MAX_HAPPINESS);
        System.out.println(
                "  Hunger   : " + (hunger > 70 ? Colors.RED : Colors.YELLOW) + hunger + Colors.RESET + " / 100");
        System.out.println("  Energy   : " + energy + " / " + MAX_ENERGY);
        if (isSick())
            System.out.println(Colors.RED + "  Status   : SICK!" + Colors.RESET);
        if (isDead())
            System.out.println(Colors.RED + "  Status   : DEAD" + Colors.RESET);
        System.out.println(Colors.YELLOW + "└──────────────────────────────┘" + Colors.RESET);
    }
}

class Dog extends VirtualPet {
    public Dog(String name) {
        super(name);
    }

    @Override
    public void uniqueBehavior() {
        System.out.println(Colors.GREEN + name + " wags its tail excitedly and barks happily!" + Colors.RESET);
    }
}

class Cat extends VirtualPet {
    public Cat(String name) {
        super(name);
    }

    @Override
    public void uniqueBehavior() {
        System.out.println(Colors.GREEN + name + " purrs contentedly and rubs against your leg." + Colors.RESET);
    }
}

class Fish extends VirtualPet {
    public Fish(String name) {
        super(name);
    }

    @Override
    public void uniqueBehavior() {
        System.out.println(Colors.GREEN + name + " swims in graceful circles around the tank." + Colors.RESET);
    }
}