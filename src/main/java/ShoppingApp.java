import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class ShoppingApp {

    ShoppingCart cart;
    Customer customer;
    List<Item> availableItems;
    Scanner scanner;
    String shippingOption; // "STANDARD" or "NEXT_DAY"

    static final double MIN_PURCHASE_AMOUNT = 1.00;
    static final double MAX_PURCHASE_AMOUNT = 99999.99;
    static final double TAX_RATE = 0.06; // 6%
    static final String[] TAXABLE_STATES = {"IL", "CA", "NY"};
    static final double STANDARD_SHIPPING_COST = 10.00;
    static final double STANDARD_SHIPPING_FREE_THRESHOLD = 50.00;
    static final double NEXT_DAY_SHIPPING_COST = 25.00;

    public ShoppingApp(Scanner scanner) {
        this.scanner = scanner;
        this.cart = new ShoppingCart();
        this.availableItems = new ArrayList<>();
        this.availableItems.add(new Item("Laptop", 1200.00));
        this.availableItems.add(new Item("Mouse", 25.50));
        this.availableItems.add(new Item("Keyboard", 75.00));
        this.availableItems.add(new Item("Monitor", 300.00));
        this.availableItems.add(new Item("Webcam", 49.99));
        this.availableItems.add(new Item("Headphones", 150.00));
        this.availableItems.add(new Item("USB Drive", 15.99));
    }

    public ShoppingApp() {
        this(new Scanner(System.in));
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
                    running = false; 
                    break;
                case 0:
                    System.out.println("Exiting application. Goodbye!");
                    running = false;
                    break;
                default:
                    System.out.println("Invalid input. Please enter a number.");
            }
            System.out.print("\n------------------------------------\n");
        }
        scanner.close();
    }

    void getCustomerInfo() {
        String name;
        String state;

        System.out.print("Please enter your name: ");
        name = scanner.nextLine();

        while (true) {
            System.out.print("Please enter your state of residence (e.g., IL, CA, NY): ");
            state = scanner.nextLine().toUpperCase();
            if (state.matches("[A-Z]{2}")) { 
                break;
            } else {
                System.out.println("Invalid state format. Please enter a two-letter state code.");
            }
        }
        customer = new Customer(name, state);
        System.out.println("Welcome, " + customer.getName() + " from " + customer.getStateOfResidence() + "!");
    }

    void selectShippingOption() {
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

    void displayMenu() {
        System.out.println("Shopping Application Menu:");
        System.out.println("1. Add item to cart");
        System.out.println("2. See contents of shopping cart");
        System.out.println("3. Edit quantity of items in shopping cart");
        System.out.println("4. Remove items from shopping cart");
        System.out.println("5. Get current total");
        System.out.println("6. Checkout");
        System.out.println("0. Exit");
    }

    int getUserChoice() {
        System.out.print("Enter your choice: ");
        while (!scanner.hasNextInt()) {
            System.out.println("Invalid input. Please enter a number.");
            scanner.next(); 
            System.out.print("Enter your choice: ");
        }
        int choice = scanner.nextInt();
        scanner.nextLine(); 
        return choice;
    }

    void displayAvailableItems() {
        System.out.println("\nAvailable Items:");
        for (int i = 0; i < availableItems.size(); i++) {
            System.out.println((i + 1) + ". " + availableItems.get(i));
        }
    }

    Item getItemSelection() {
        displayAvailableItems();
        System.out.print("Enter the number of the item you want to select (or 0 to cancel): ");
        while (true) {
            if (scanner.hasNextInt()) {
                int itemIndex = scanner.nextInt();
                scanner.nextLine(); 
                if (itemIndex == 0) {
                    return null;
                }
                // Combined boundary checks to kill boundary mutants cleanly
                if (itemIndex >= 1 && itemIndex <= availableItems.size()) {
                    return availableItems.get(itemIndex - 1);
                } else {
                    System.out.println("Invalid item number. Please try again.");
                }
            } else {
                System.out.println("Invalid input. Please enter a number.");
                scanner.next(); 
            }
            System.out.print("Enter the number of the item: "); 
        }
    }

    int getQuantityInput() {
        System.out.print("Enter quantity: ");
        while (true) {
            if (scanner.hasNextInt()) {
                int quantity = scanner.nextInt();
                scanner.nextLine(); 
                if (quantity > 0) {
                    return quantity;
                } else {
                    System.out.println("Quantity must be greater than zero. Please try again.");
                }
            } else {
                System.out.println("Invalid input. Please enter a whole number for quantity.");
                scanner.next(); 
            }
            System.out.print("Enter quantity: ");
        }
    }

    void addItemToCart() {
        Item selectedItem = getItemSelection();
        if (selectedItem != null) {
            int quantity = getQuantityInput();
            cart.addItem(selectedItem, quantity);
            System.out.println(selectedItem.getName() + " added to cart. Current items in cart: " + cart.getItemCount());
        } else {
            System.out.println("Item selection cancelled.");
        }
    }

    void viewCartContents() {
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

    void editItemQuantity() {
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
            int newQuantity;
            while (true) {
                if (scanner.hasNextInt()) {
                    newQuantity = scanner.nextInt();
                    scanner.nextLine(); 
                    if (newQuantity < 0) {
                        System.out.println("Quantity must be greater than zero. Please try again.");
                    } else { 
                        break; 
                    }
                } else {
                    System.out.println("Invalid input. Please enter a whole number for quantity.");
                    scanner.next(); 
                }
                System.out.print("Enter new quantity: ");
            }

            if (cart.updateItemQuantity(cartItemToEdit.get().getItem(), newQuantity)) {
                if (newQuantity == 0) {
                    System.out.println(cartItemToEdit.get().getItem().getName() + " removed from cart.");
                } else {
                    System.out.println("Quantity for " + cartItemToEdit.get().getItem().getName() + " updated to " + newQuantity + ".");
                }
            } else {
                System.out.println("Failed to update quantity for " + cartItemToEdit.get().getItem().getName() + ". Please try again.");
            }
        } else {
            System.out.println("Item '" + itemName + "' not found in your cart.");
        }
    }

    void removeItemFromCart() {
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
            if (cart.removeItem(cartItemToRemove.get().getItem())) {
                System.out.println(cartItemToRemove.get().getItem().getName() + " removed from cart.");
            } else {
                System.out.println("Failed to remove " + cartItemToRemove.get().getItem().getName() + " from cart. Please try again.");
            }
        } else {
            System.out.println("Item '" + itemName + "' not found in your cart.");
        }
    }

    double calculateSalesTax(double subtotal) {
        if (customer != null && Arrays.asList(TAXABLE_STATES).contains(customer.getStateOfResidence())) {
            return subtotal * TAX_RATE;
        }
        return 0.0;
    }

    double calculateShippingCost(double subtotal) {
        if ("STANDARD".equals(shippingOption)) {
            if (subtotal >= STANDARD_SHIPPING_FREE_THRESHOLD) {
                return 0.0;
            } else {
                return STANDARD_SHIPPING_COST;
            }
        } else if ("NEXT_DAY".equals(shippingOption)) {
            return NEXT_DAY_SHIPPING_COST;
        }
        return 0.0; 
    }

    void calculateAndDisplayTotal() {
        if (cart.isEmpty()) {
            System.out.println("Your shopping cart is empty. No total to calculate.");
            return;
        }

        double subtotal = cart.calculateSubtotal();

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
        viewCartContents(); 
        System.out.println("Raw Purchase Price (Subtotal): $" + String.format("%.2f", subtotal));
        System.out.println("Sales Tax: $" + String.format("%.2f", tax));
        System.out.println("Shipping (" + shippingOption + "): $" + String.format("%.2f", shipping));
        System.out.println("Total: $" + String.format("%.2f", total));
        System.out.print("---------------------\n"); 
    }

    void checkout() {
        if (cart.isEmpty()) {
            System.out.println("Your shopping cart is empty. Cannot checkout.");
            return;
        }

        double subtotal = cart.calculateSubtotal();

        if (subtotal < MIN_PURCHASE_AMOUNT) {
            System.out.println("Checkout failed: Minimum acceptable purchase amount is $" + String.format("%.2f", MIN_PURCHASE_AMOUNT) + ". Your current subtotal is $" + String.format("%.2f", subtotal) + ".");
            return;
        }
        if (subtotal > MAX_PURCHASE_AMOUNT) {
            System.out.println("Checkout failed: Maximum acceptable purchase amount is $" + String.format("%.2f", MAX_PURCHASE_AMOUNT) + ". Your current subtotal is $" + String.format("%.2f", subtotal) + ".");
            return;
        }

        calculateAndDisplayTotal(); 
        System.out.println("\n--- Transaction completed! ---");
        cart.clearCart(); 
    }
}