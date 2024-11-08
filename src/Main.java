import java.io.*;
import java.util.Scanner;

class CoffeeMachineApp {
    public static void main(String[] args) {
        CoffeeMachine coffeeMachine = CoffeeMachine.loadState();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.print("Write action (buy, login, exit): ");
            String action = scanner.next();

            switch (action) {
                case "buy":
                    coffeeMachine.buyCoffee(scanner);
                    break;
                case "login":
                    coffeeMachine.login(scanner);
                    break;
                case "exit":
                    coffeeMachine.saveState();
                    System.out.println("Shutting down. State saved.");
                    return;
                default:
                    System.out.println("Unknown action");
            }
        }
    }
}

class CoffeeMachine {
    private int water;
    private int milk;
    private int coffeeBeans;
    private int cups;
    private int money;
    private final String adminUsername = "admin";
    private final String adminPassword = "admin123";

    public CoffeeMachine(int water, int milk, int coffeeBeans, int cups, int money) {
        this.water = water;
        this.milk = milk;
        this.coffeeBeans = coffeeBeans;
        this.cups = cups;
        this.money = money;
    }

    public static CoffeeMachine loadState() {
        try (BufferedReader reader = new BufferedReader(new FileReader("coffee_machine_status.txt"))) {
            String[] machineState = reader.readLine().split(";");
            int water = Integer.parseInt(machineState[0].trim());
            int milk = Integer.parseInt(machineState[1].trim());
            int coffeeBeans = Integer.parseInt(machineState[2].trim());
            int cups = Integer.parseInt(machineState[3].trim());
            int money = Integer.parseInt(machineState[4].trim());
            return new CoffeeMachine(water, milk, coffeeBeans, cups, money);
        } catch (IOException e) {
            System.out.println("Error loading machine state. Starting with default values.");
            return new CoffeeMachine(400, 540, 120, 9, 550);
        }
    }

    public void saveState() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("coffee_machine_status.txt"))) {
            writer.write(water + ";" + milk + ";" + coffeeBeans + ";" + cups + ";" + money + "\n");
            writer.write(adminUsername + ";" + adminPassword + "\n");
        } catch (IOException e) {
            System.out.println("Error saving machine state.");
        }
    }

    public void buyCoffee(Scanner scanner) {
        System.out.print("What do you want to buy? 1 - espresso, 2 - latte, 3 - cappuccino, back - to main menu: ");
        String choice = scanner.next();

        switch (choice) {
            case "1":
                makeCoffee(250, 0, 16, 4, "Espresso");
                break;
            case "2":
                makeCoffee(350, 75, 20, 7, "Latte");
                break;
            case "3":
                makeCoffee(200, 100, 12, 6, "Cappuccino");
                break;
            case "back":
                return;
            default:
                System.out.println("Invalid choice");
        }
    }

    private void makeCoffee(int waterNeeded, int milkNeeded, int coffeeBeansNeeded, int price, String coffeeType) {
        if (checkResources(waterNeeded, milkNeeded, coffeeBeansNeeded)) {
            water -= waterNeeded;
            milk -= milkNeeded;
            coffeeBeans -= coffeeBeansNeeded;
            cups -= 1;
            money += price;
            System.out.println("I have enough resources, making you a " + coffeeType + "!");
        }
    }

    private boolean checkResources(int waterNeeded, int milkNeeded, int coffeeBeansNeeded) {
        if (water < waterNeeded) {
            System.out.println("Sorry, not enough water!");
            return false;
        }
        if (milk < milkNeeded) {
            System.out.println("Sorry, not enough milk!");
            return false;
        }
        if (coffeeBeans < coffeeBeansNeeded) {
            System.out.println("Sorry, not enough coffee beans!");
            return false;
        }
        if (cups < 1) {
            System.out.println("Sorry, not enough disposable cups!");
            return false;
        }
        return true;
    }

    public void login(Scanner scanner) {
        System.out.print("Enter username: ");
        String username = scanner.next();
        System.out.print("Enter password: ");
        String password = scanner.next();

        if (username.equals(adminUsername) && password.equals(adminPassword)) {
            adminMenu(scanner);
        } else {
            System.out.println("Wrong username or password");
        }
    }

    private void adminMenu(Scanner scanner) {
        while (true) {
            System.out.print("Write action (fill, remaining, take, exit): ");
            String action = scanner.next();

            switch (action) {
                case "fill":
                    fillSupplies(scanner);
                    break;
                case "remaining":
                    displayMachineState();
                    break;
                case "take":
                    takeMoney();
                    break;
                case "exit":
                    return;
                default:
                    System.out.println("Unknown action");
            }
        }
    }

    private void fillSupplies(Scanner scanner) {
        System.out.print("Write how many ml of water you want to add: ");
        water += scanner.nextInt();
        System.out.print("Write how many ml of milk you want to add: ");
        milk += scanner.nextInt();
        System.out.print("Write how many grams of coffee beans you want to add: ");
        coffeeBeans += scanner.nextInt();
        System.out.print("Write how many disposable cups you want to add: ");
        cups += scanner.nextInt();
    }

    private void displayMachineState() {
        System.out.println("CoffeeMachine{ water=" + water + ", milk=" + milk + ", coffeeBeans=" + coffeeBeans + ", cups=" + cups + ", money=" + money + " }");
    }

    private void takeMoney() {
        System.out.println("I gave you $" + money);
        money = 0;
    }
}
