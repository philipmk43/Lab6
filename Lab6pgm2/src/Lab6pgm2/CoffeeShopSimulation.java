package Lab6pgm2;
import java.util.Scanner;

class CoffeeShop {
    private int coffeeCounter = 0; // Counter for coffee stock
    private final int capacity;   // Maximum capacity of the coffee counter

    public CoffeeShop(int capacity) {
        this.capacity = capacity;
    }

    // Barista prepares coffee (Producer)
    public synchronized void prepareCoffee(String baristaName) throws InterruptedException {
        while (coffeeCounter == capacity) {
            System.out.println(baristaName + " is waiting. Counter is full.");
            wait(); // Wait if the counter is full
        }
        coffeeCounter++;
        System.out.println(baristaName + " prepared coffee. Counter: " + coffeeCounter);
        notifyAll(); // Notify all waiting threads
    }

    // Customer picks up coffee (Consumer)
    public synchronized void pickUpCoffee(String customerName, int amount) throws InterruptedException {
        while (coffeeCounter < amount) {
            wait(); // Wait if there aren't enough coffees
        }
        coffeeCounter -= amount;
        System.out.println(customerName + " picked up " + amount + " coffee(s). Counter: " + coffeeCounter);
        notifyAll(); // Notify all waiting threads
    }

    // Coffee reviewer samples coffee (Observer)
    public synchronized void sampleCoffee(String reviewerName) throws InterruptedException {
        while (coffeeCounter == 0) {
            wait(); // Wait if no coffee is available
        }
        coffeeCounter--;
        System.out.println(reviewerName + " sampled coffee. Counter: " + coffeeCounter);
        notifyAll(); // Notify all waiting threads
    }
}

// Barista class for preparing coffee
class Barista extends Thread {
    private final CoffeeShop shop;
    private final String name;
    private final int coffeeCount;

    public Barista(CoffeeShop shop, String name, int coffeeCount) {
        this.shop = shop;
        this.name = name;
        this.coffeeCount = coffeeCount;
    }

    @Override
    public void run() {
        try {
            for (int i = 0; i < coffeeCount; i++) {
                shop.prepareCoffee(name);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

// Customer class for picking up coffee
class Customer extends Thread {
    private final CoffeeShop shop;
    private final String name;
    private final int coffeeCount;

    public Customer(CoffeeShop shop, String name, int coffeeCount) {
        this.shop = shop;
        this.name = name;
        this.coffeeCount = coffeeCount;
    }

    @Override
    public void run() {
        try {
            shop.pickUpCoffee(name, coffeeCount);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

// Coffee Reviewer class for sampling coffee
class CoffeeReviewer extends Thread {
    private final CoffeeShop shop;
    private final String name;

    public CoffeeReviewer(CoffeeShop shop, String name) {
        this.shop = shop;
        this.name = name;
    }

    @Override
    public void run() {
        try {
            shop.sampleCoffee(name);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

// Main Class
public class CoffeeShopSimulation {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter the counter capacity: ");
        int capacity = scanner.nextInt();
        CoffeeShop shop = new CoffeeShop(capacity);

        System.out.print("Enter the number of baristas: ");
        int baristaCount = scanner.nextInt();
        for (int i = 1; i <= baristaCount; i++) {
            System.out.print("Enter the number of coffees prepared by Barista " + i + ": ");
            int coffeePrepared = scanner.nextInt();
            new Barista(shop, "Barista " + i, coffeePrepared).start();
        }

        System.out.print("Enter the number of customers: ");
        int customerCount = scanner.nextInt();
        for (int i = 1; i <= customerCount; i++) {
            System.out.print("Enter the number of coffees picked up by Customer " + i + ": ");
            int coffeePicked = scanner.nextInt();
            new Customer(shop, "Customer " + i, coffeePicked).start();
        }

        System.out.print("Enter the number of coffee reviewers: ");
        int reviewerCount = scanner.nextInt();
        for (int i = 1; i <= reviewerCount; i++) {
            new CoffeeReviewer(shop, "Reviewer " + i).start();
        }

        scanner.close();
    }
}
