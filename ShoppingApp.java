// ShoppingApp.java
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class ShoppingApp {

    private ShoppingCart cart;
    private Customer customer;
    private List<Item> availableItems;
    private Scanner scanner;
    private String shippingOption; // "STANDARD" or "NEXT_DAY"

    private static final double MIN_PURCHASE_AMOUNT = 1.00;
    private static final double MAX_PURCHASE_AMOUNT = 99999.99;
    private static final double TAX_RATE = 0.06; // 6%
    private static final String[] TAXABLE_STATES = {"IL", "CA", "NY"};
    private static final double STANDARD_SHIPPING_COST = 10.00;
    private static final double STANDARD_SHIPPING_FREE_THRESHOLD = 50.00;
    private static final double NEXT_DAY_SHIPPING_COST = 25.00;

    public ShoppingApp() {
        cart = new ShoppingCart();
        scanner = new Scanner(System.in);
        availableItems = new ArrayList<>();
        // Populate some sample items
        availableItems.add(new Item("Laptop", 1200.00));
        availableItems.add(new Item("Mouse", 25.50));
        availableItems.add(new Item("Keyboard", 75.00));
        availableItems.add(new Item("Monitor", 300.00));
        availableItems.add(new Item("Webcam", 49.99));
        availableItems.add(new Item("Headphones", 150.00));
        availableItems.add(new Item("USB Drive", 15.99));
    }

    public static void main(String[] args) {
        ShoppingApp app = new ShoppingApp();
        app.start();
    }

    public void start() {
        System.out.println("Welcome to the CLI Shopping Application!");
        getCustomerInfo();
        selectShippingOption();

        boolean running = true;
        while (running) {
            displayMenu();
            int choice = getUserChoice();

            switch (choice) {
                case 1:
                    addItemToCart();
                    break;
                case 2:
                    viewCartContents();
                    break;
                case 3:
                    editItemQuantity();
                    break;
                case 4:
                    removeItemFromCart();
                    break;
                case 5:
                    calculateAndDisplayTotal();
                    break;
                case 6:
                    checkout();
                    running = false; // Exit after checkout
                    break;
                case 0:
                    System.out.println("Exiting application. Goodbye!");
                    running = false;
                    break;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
            System.out.println("\n------------------------------------\n");
        }
        scanner.close();
    }

    private void getCustomerInfo() {
        String name;
        String state;

        System.out.print("Please enter your name: ");
        name = scanner.nextLine();

        while (true) {
            System.out.print("Please enter your state of residence (e.g., IL, CA, NY): ");
            state = scanner.nextLine().toUpperCase();
            if (state.matches("[A-Z]{2}")) { // Basic validation for two uppercase letters
                break;
            } else {
                System.out.println("Invalid state format. Please enter a two-letter state code.");
            }
        }
        customer = new Customer(name, state);
        System.out.println("Welcome, " + customer.getName() + " from " + customer.getStateOfResidence() + "!");
    }

    private void selectShippingOption() {
        while (true) {
            System.out.println("\nSelect a shipping option:");
            System.out.println("1. Standard Shipping");
            System.out.println("2. Next Day Shipping");
            System.out.print("Enter your choice (1 or 2): ");
            String choice = scanner.nextLine();

            if ("1".equals(choice)) {
                shippingOption = "STANDARD";
                System.out.println("Standard Shipping selected.");
                break;
            } else if ("2".equals(choice)) {
                shippingOption = "NEXT_DAY";
                System.out.println("Next Day Shipping selected.");
                break;
            } else {
                System.out.println("Invalid shipping option. Please choose 1 or 2.");
            }
        }
    }

    private void displayMenu() {
        System.out.println("Shopping Application Menu:");
        System.out.println("1. Add item to cart");
        System.out.println("2. See contents of shopping cart");
        System.out.println("3. Edit quantity of items in shopping cart");
        System.out.println("4. Remove items from shopping cart");
        System.out.println("5. Get current total");
        System.out.println("6. Checkout");
        System.out.println("0. Exit");
        System.out.print("Enter your choice: ");
    }

    private int getUserChoice() {
        while (!scanner.hasNextInt()) {
            System.out.println("Invalid input. Please enter a number.");
            scanner.next(); // consume the invalid input
            System.out.print("Enter your choice: ");
        }
        int choice = scanner.nextInt();
        scanner.nextLine(); // consume newline
        return choice;
    }

    private void displayAvailableItems() {
        System.out.println("\nAvailable Items:");
        for (int i = 0; i < availableItems.size(); i++) {
            System.out.println((i + 1) + ". " + availableItems.get(i));
        }
    }

    private Item getItemSelection() {
        displayAvailableItems();
        System.out.print("Enter the number of the item you want to select: ");
        while (true) {
            if (scanner.hasNextInt()) {
                int itemIndex = scanner.nextInt();
                scanner.nextLine(); // consume newline
                if (itemIndex > 0 && itemIndex <= availableItems.size()) {
                    return availableItems.get(itemIndex - 1);
                } else {
                    System.out.println("Invalid item number. Please try again.");
                }
            } else {
                System.out.println("Invalid input. Please enter a number.");
                scanner.next(); // consume invalid input
            }
            System.out.print("Enter the number of the item: ");
        }
    }

    private int getQuantityInput() {
        System.out.print("Enter quantity: ");
        while (true) {
            if (scanner.hasNextInt()) {
                int quantity = scanner.nextInt();
                scanner.nextLine(); // consume newline
                if (quantity > 0) {
                    return quantity;
                } else {
                    System.out.println("Quantity must be greater than zero. Please try again.");
                }
            } else {
                System.out.println("Invalid input. Please enter a whole number for quantity.");
                scanner.next(); // consume invalid input
            }
            System.out.print("Enter quantity: ");
        }
    }

    private void addItemToCart() {
        Item selectedItem = getItemSelection();
        if (selectedItem != null) {
            int quantity = getQuantityInput();
            cart.addItem(selectedItem, quantity);
        }
    }

    private void viewCartContents() {
        if (cart.isEmpty()) {
            System.out.println("Your shopping cart is empty.");
            return;
        }
        System.out.println("\n--- Your Shopping Cart ---");
        for (CartItem ci : cart.getCartContents()) {
            System.out.println(ci);
        }
        System.out.println("--------------------------");
    }

    private void editItemQuantity() {
        if (cart.isEmpty()) {
            System.out.println("Your shopping cart is empty. Nothing to edit.");
            return;
        }
        viewCartContents();
        System.out.println("\nEnter the name of the item you wish to edit:");
        String itemName = scanner.nextLine();

        Optional<CartItem> cartItemToEdit = cart.getCartContents().stream()
                .filter(ci -> ci.getItem().getName().equalsIgnoreCase(itemName))
                .findFirst();

        if (cartItemToEdit.isPresent()) {
            System.out.print("Enter new quantity for " + cartItemToEdit.get().getItem().getName() + " (enter 0 to remove): ");
            while (true) {
                if (scanner.hasNextInt()) {
                    int newQuantity = scanner.nextInt();
                    scanner.nextLine(); // consume newline
                    if (newQuantity >= 0) {
                        cart.updateItemQuantity(cartItemToEdit.get().getItem(), newQuantity);
                        break;
                    } else {
                        System.out.println("Quantity cannot be negative. Please enter a non-negative number.");
                    }
                } else {
                    System.out.println("Invalid input. Please enter a whole number for quantity.");
                    scanner.next(); // consume invalid input
                }
                System.out.print("Enter new quantity: ");
            }
        } else {
            System.out.println("Item '" + itemName + "' not found in your cart.");
        }
    }

    private void removeItemFromCart() {
        if (cart.isEmpty()) {
            System.out.println("Your shopping cart is empty. Nothing to remove.");
            return;
        }
        viewCartContents();
        System.out.println("\nEnter the name of the item you wish to remove:");
        String itemName = scanner.nextLine();

        Optional<CartItem> cartItemToRemove = cart.getCartContents().stream()
                .filter(ci -> ci.getItem().getName().equalsIgnoreCase(itemName))
                .findFirst();

        if (cartItemToRemove.isPresent()) {
            cart.removeItem(cartItemToRemove.get().getItem());
        } else {
            System.out.println("Item '" + itemName + "' not found in your cart.");
        }
    }


    private double calculateSalesTax(double subtotal) {
        if (Arrays.asList(TAXABLE_STATES).contains(customer.getStateOfResidence())) {
            return subtotal * TAX_RATE;
        }
        return 0.0;
    }

    private double calculateShippingCost(double subtotal) {
        if ("STANDARD".equals(shippingOption)) {
            return (subtotal > STANDARD_SHIPPING_FREE_THRESHOLD) ? 0.0 : STANDARD_SHIPPING_COST;
        } else if ("NEXT_DAY".equals(shippingOption)) {
            return NEXT_DAY_SHIPPING_COST;
        }
        return 0.0; // Should not happen if shippingOption is always set
    }

    private void calculateAndDisplayTotal() {
        if (cart.isEmpty()) {
            System.out.println("Your shopping cart is empty. No total to calculate.");
            return;
        }

        double subtotal = cart.calculateSubtotal();

        // Validate purchase amount
        if (subtotal < MIN_PURCHASE_AMOUNT) {
            System.out.println("Error: Minimum acceptable purchase amount is $" + String.format("%.2f", MIN_PURCHASE_AMOUNT) + ". Your current subtotal is $" + String.format("%.2f", subtotal) + ".");
            return;
        }
        if (subtotal > MAX_PURCHASE_AMOUNT) {
            System.out.println("Error: Maximum acceptable purchase amount is $" + String.format("%.2f", MAX_PURCHASE_AMOUNT) + ". Your current subtotal is $" + String.format("%.2f", subtotal) + ".");
            return;
        }

        double tax = calculateSalesTax(subtotal);
        double shipping = calculateShippingCost(subtotal);
        double total = subtotal + tax + shipping;

        System.out.println("\n--- Order Summary ---");
        viewCartContents(); // Show cart contents again
        System.out.println("Raw Purchase Price (Subtotal): $" + String.format("%.2f", subtotal));
        System.out.println("Sales Tax (" + (TAX_RATE * 100) + "% for " + customer.getStateOfResidence() + "): $" + String.format("%.2f", tax));
        System.out.println("Shipping (" + shippingOption + "): $" + String.format("%.2f", shipping));
        System.out.println("Total: $" + String.format("%.2f", total));
        System.out.println("---------------------");
    }

    private void checkout() {
        if (cart.isEmpty()) {
            System.out.println("Your shopping cart is empty. Cannot checkout.");
            return;
        }

        double subtotal = cart.calculateSubtotal();

        // Re-validate purchase amount before final checkout
        if (subtotal < MIN_PURCHASE_AMOUNT) {
            System.out.println("Checkout failed: Minimum acceptable purchase amount is $" + String.format("%.2f", MIN_PURCHASE_AMOUNT) + ". Your current subtotal is $" + String.format("%.2f", subtotal) + ".");
            return;
        }
        if (subtotal > MAX_PURCHASE_AMOUNT) {
            System.out.println("Checkout failed: Maximum acceptable purchase amount is $" + String.format("%.2f", MAX_PURCHASE_AMOUNT) + ". Your current subtotal is $" + String.format("%.2f", subtotal) + ".");
            return;
        }

        calculateAndDisplayTotal(); // Display final summary
        System.out.println("\n--- Transaction completed! ---");
        cart.clearCart(); // Clear cart after successful checkout
    }
}