import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class ShoppingAppTest {

    private final InputStream originalIn = System.in;
    private final PrintStream originalOut = System.out;
    private ByteArrayOutputStream outputStreamCaptor;

    private ShoppingApp app;
    private ShoppingCart mockedCart; 
    private Customer testCustomer; 
    private Item laptop;
    private Item mouse;
    private Item keyboard; 
    private Item monitor; 
    private Item webcam; 
    private Item headphones; 
    private Item usbDrive; 

    private Scanner mockedScanner; 

    @BeforeEach
    void setUp() {
        mockedScanner = Mockito.mock(Scanner.class);
        app = new ShoppingApp(mockedScanner); 

        mockedCart = Mockito.mock(ShoppingCart.class);
        app.cart = mockedCart; 

        outputStreamCaptor = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outputStreamCaptor));

        laptop = new Item("Laptop", 1200.00);
        mouse = new Item("Mouse", 25.50);
        keyboard = new Item("Keyboard", 75.00);
        monitor = new Item("Monitor", 300.00);
        webcam = new Item("Webcam", 49.99);
        headphones = new Item("Headphones", 150.00);
        usbDrive = new Item("USB Drive", 15.99);

        app.availableItems.clear(); 
        app.availableItems.add(laptop);
        app.availableItems.add(mouse);
        app.availableItems.add(keyboard);
        app.availableItems.add(monitor);
        app.availableItems.add(webcam);
        app.availableItems.add(headphones);
        app.availableItems.add(usbDrive);

        testCustomer = new Customer("Test User", "IL");
        app.customer = testCustomer;
        app.shippingOption = "STANDARD"; 
    }

    @AfterEach
    void tearDown() {
        System.setIn(originalIn);
        System.setOut(originalOut);
        Mockito.reset(mockedScanner);
    }

    @Test
    @DisplayName("main method should initialize app and call start")
    void testMainMethod() {
        System.setIn(new ByteArrayInputStream("Main User\nNY\n2\n0\n".getBytes()));
        ShoppingApp.main(new String[]{});
        String output = outputStreamCaptor.toString();
        assertTrue(output.contains("Welcome to the CLI Shopping Application!"));
        assertTrue(output.contains("Welcome, Main User from NY!"));
        assertTrue(output.contains("Next Day Shipping selected."));
        assertTrue(output.contains("Exiting application. Goodbye!"));
    }

    @Test
    @DisplayName("Scanner should be closed when application exits")
    void testScannerIsClosedOnExit() {
        when(mockedScanner.nextLine())
                .thenReturn("Test User") 
                .thenReturn("IL")        
                .thenReturn("1")         
                .thenReturn("");         

        when(mockedScanner.hasNextInt()).thenReturn(true); 
        when(mockedScanner.nextInt()).thenReturn(0); 

        app.start();

        verify(mockedScanner, times(1)).close();
    }

    @Test
    @DisplayName("getCustomerInfo should set customer with valid input and print prompts")
    void testGetCustomerInfo_valid() {
        app.customer = null;
        when(mockedScanner.nextLine())
                .thenReturn("John Doe")
                .thenReturn("IL"); 

        app.getCustomerInfo();
        assertNotNull(app.customer);
        assertEquals("John Doe", app.customer.getName());
        assertEquals("IL", app.customer.getStateOfResidence());
        String output = outputStreamCaptor.toString();
        assertTrue(output.contains("Please enter your name: "));
        assertTrue(output.contains("Please enter your state of residence (e.g., IL, CA, NY): "));
        assertTrue(output.contains("Welcome, John Doe from IL!"));
    }

    @Test
    @DisplayName("getCustomerInfo should re-prompt for invalid state format then accept valid and print prompts")
    void testGetCustomerInfo_invalidStateThenValid() {
        app.customer = null;
        when(mockedScanner.nextLine())
                .thenReturn("Jane Doe")
                .thenReturn("INVALID") 
                .thenReturn("CA");     

        app.getCustomerInfo();
        assertNotNull(app.customer);
        assertEquals("Jane Doe", app.customer.getName());
        assertEquals("CA", app.customer.getStateOfResidence());
        String output = outputStreamCaptor.toString();
        assertTrue(output.contains("Please enter your name: "));
        assertTrue(output.contains("Please enter your state of residence (e.g., IL, CA, NY): "));
        assertTrue(output.contains("Invalid state format. Please enter a two-letter state code."));
        assertTrue(output.contains("Welcome, Jane Doe from CA!"));
    }

    @Test
    @DisplayName("selectShippingOption should set STANDARD for choice 1 and print prompts")
    void testSelectShippingOption_standard() {
        when(mockedScanner.nextLine()).thenReturn("1"); 
        app.selectShippingOption();
        assertEquals("STANDARD", app.shippingOption);
        String output = outputStreamCaptor.toString();
        assertTrue(output.contains("\nSelect a shipping option:"));
        assertTrue(output.contains("1. Standard Shipping"));
        assertTrue(output.contains("2. Next Day Shipping"));
        assertTrue(output.contains("Enter your choice (1 or 2): "));
        assertTrue(output.contains("Standard Shipping selected."));
    }

    @Test
    @DisplayName("selectShippingOption should set NEXT_DAY for choice 2 and print prompts")
    void testSelectShippingOption_nextDay() {
        when(mockedScanner.nextLine()).thenReturn("2"); 
        app.selectShippingOption();
        assertEquals("NEXT_DAY", app.shippingOption);
        String output = outputStreamCaptor.toString();
        assertTrue(output.contains("Next Day Shipping selected."));
    }

    @Test
    @DisplayName("selectShippingOption should re-prompt for invalid choice then accept valid and print prompts")
    void testSelectShippingOption_invalidThenValid() {
        when(mockedScanner.nextLine())
                .thenReturn("3") 
                .thenReturn("1"); 

        app.selectShippingOption();
        assertEquals("STANDARD", app.shippingOption);
        String output = outputStreamCaptor.toString();
        assertTrue(output.contains("Invalid shipping option. Please choose 1 or 2."));
        assertTrue(output.contains("Standard Shipping selected."));
        assertEquals(2, countOccurrences(output, "Enter your choice (1 or 2): "));
    }

    @Test
    @DisplayName("getUserChoice should return valid integer input and print prompt")
    void testGetUserChoice_valid() {
        Scanner realScanner = new Scanner(new ByteArrayInputStream("5\n".getBytes()));
        app.scanner = realScanner;

        assertEquals(5, app.getUserChoice());
        String output = outputStreamCaptor.toString();
        assertTrue(output.contains("Enter your choice: "));
    }

    @Test
    @DisplayName("getUserChoice should re-prompt for invalid non-integer input then return valid and print prompts")
    void testGetUserChoice_invalidThenValid() {
        Scanner realScanner = new Scanner(new ByteArrayInputStream("abc\n2\n".getBytes()));
        app.scanner = realScanner;

        assertEquals(2, app.getUserChoice());
        String output = outputStreamCaptor.toString();
        assertTrue(output.contains("Invalid input. Please enter a number."));
        assertEquals(2, countOccurrences(output, "Enter your choice: "));
    }

    @Test
    @DisplayName("getItemSelection should return selected item with valid input and check print statements")
    void testGetItemSelection_valid() {
        outputStreamCaptor.reset(); 
        Scanner realScanner = new Scanner(new ByteArrayInputStream("1\n".getBytes()));
        app.scanner = realScanner;

        Item selected = app.getItemSelection();
        assertEquals(laptop.getName(), selected.getName());
        String output = outputStreamCaptor.toString();
        assertTrue(output.contains("\nAvailable Items:"));
        assertTrue(output.contains("1. Laptop")); 
        assertTrue(output.contains("2. Mouse"));
        assertTrue(output.contains("Enter the number of the item you want to select (or 0 to cancel): "));
    }

    @Test
    @DisplayName("getItemSelection boundary check: selecting index equal to size must succeed and kill boundary mutant")
    void testGetItemSelection_boundaryCheck_exactlySize_killsMutant() {
        int lastIndex = app.availableItems.size(); // 7
        Item expectedLastItem = app.availableItems.get(lastIndex - 1);

        Scanner realScanner = new Scanner(new ByteArrayInputStream((lastIndex + "\n").getBytes()));
        app.scanner = realScanner;

        Item selected = app.getItemSelection();
        
        assertNotNull(selected, "Selecting the last index must return a valid item, not null!");
        assertEquals(expectedLastItem.getName(), selected.getName(), "The returned item must be the last item in the list.");
        assertFalse(outputStreamCaptor.toString().contains("Invalid item number. Please try again."), 
                "The last item index should be accepted without printing an error!");
    }

    @Test
    @DisplayName("getItemSelection boundary check: selecting index 1 must succeed")
    void testGetItemSelection_boundaryCheck_exactlyOne() {
        Scanner realScanner = new Scanner(new ByteArrayInputStream("1\n".getBytes()));
        app.scanner = realScanner;

        Item selected = app.getItemSelection();
        
        assertNotNull(selected);
        assertEquals(laptop.getName(), selected.getName(), "Selecting 1 should return the first item.");
        assertFalse(outputStreamCaptor.toString().contains("Invalid item number. Please try again."));
    }

    @Test
    @DisplayName("getItemSelection boundary check: selecting index size + 1 should fail and re-prompt")
    void testGetItemSelection_boundaryCheck_greaterThanSize() {
        int size = app.availableItems.size();
        Scanner realScanner = new Scanner(new ByteArrayInputStream(((size + 1) + "\n" + size + "\n").getBytes()));
        app.scanner = realScanner;

        Item selected = app.getItemSelection();
        assertEquals(usbDrive.getName(), selected.getName());
        String output = outputStreamCaptor.toString();
        assertTrue(output.contains("Invalid item number. Please try again."));
    }

    @Test
    @DisplayName("getItemSelection should re-prompt for invalid item number then valid and print prompts")
    void testGetItemSelection_invalidNumberThenValid() {
        Scanner realScanner = new Scanner(new ByteArrayInputStream("99\n1\n".getBytes()));
        app.scanner = realScanner;

        Item selected = app.getItemSelection();
        assertEquals(laptop.getName(), selected.getName());
        String output = outputStreamCaptor.toString();
        assertTrue(output.contains("Invalid item number. Please try again."));
        assertEquals(1, countOccurrences(output, "Enter the number of the item: "));
    }

    @Test
    @DisplayName("getItemSelection should re-prompt for invalid non-integer input then valid and print prompts")
    void testGetItemSelection_invalidNonIntegerThenValid() {
        Scanner realScanner = new Scanner(new ByteArrayInputStream("xyz\n1\n".getBytes()));
        app.scanner = realScanner;

        Item selected = app.getItemSelection();
        assertEquals(laptop.getName(), selected.getName());
        String output = outputStreamCaptor.toString();
        assertTrue(output.contains("Invalid input. Please enter a number."));
        assertEquals(1, countOccurrences(output, "Enter the number of the item: "));
    }

    @Test
    @DisplayName("getItemSelection should return null if user enters 0 to cancel and print prompt")
    void testGetItemSelection_cancelWithZero() {
        Scanner realScanner = new Scanner(new ByteArrayInputStream("0\n".getBytes()));
        app.scanner = realScanner;

        Item selected = app.getItemSelection();
        assertNull(selected);
        String output = outputStreamCaptor.toString();
        assertTrue(output.contains("Enter the number of the item you want to select (or 0 to cancel): "));
        assertFalse(output.contains("Invalid item number. Please try again.")); 
    }

    @Test
    @DisplayName("getItemSelection should re-prompt for negative item number then valid and print prompts")
    void testGetItemSelection_negativeNumberThenValid() {
        Scanner realScanner = new Scanner(new ByteArrayInputStream("-5\n1\n".getBytes()));
        app.scanner = realScanner;

        Item selected = app.getItemSelection();
        assertEquals(laptop.getName(), selected.getName());
        String output = outputStreamCaptor.toString();
        assertTrue(output.contains("Invalid item number. Please try again."));
        assertEquals(1, countOccurrences(output, "Enter the number of the item: "));
    }

    @Test
    @DisplayName("getQuantityInput should return valid quantity and print prompt")
    void testGetQuantityInput_valid() {
        Scanner realScanner = new Scanner(new ByteArrayInputStream("5\n".getBytes()));
        app.scanner = realScanner;

        assertEquals(5, app.getQuantityInput());
        String output = outputStreamCaptor.toString();
        assertTrue(output.contains("Enter quantity: "));
    }

    @Test
    @DisplayName("getQuantityInput should re-prompt for zero or negative quantity then valid and print prompts")
    void testGetQuantityInput_invalidQuantityThenValid() {
        Scanner realScanner = new Scanner(new ByteArrayInputStream("-1\n0\n3\n".getBytes()));
        app.scanner = realScanner;

        assertEquals(3, app.getQuantityInput());
        String output = outputStreamCaptor.toString();
        assertTrue(output.contains("Quantity must be greater than zero. Please try again."));
        assertEquals(3, countOccurrences(output, "Enter quantity: "));
    }

    @Test
    @DisplayName("getQuantityInput should re-prompt for invalid non-integer input then valid and print prompts")
    void testGetQuantityInput_invalidNonIntegerThenValid() {
        Scanner realScanner = new Scanner(new ByteArrayInputStream("abc\n2\n".getBytes()));
        app.scanner = realScanner;

        assertEquals(2, app.getQuantityInput());
        String output = outputStreamCaptor.toString();
        assertTrue(output.contains("Invalid input. Please enter a whole number for quantity."));
        assertEquals(2, countOccurrences(output, "Enter quantity: "));
    }

    @Test
    @DisplayName("calculateSalesTax should calculate tax for IL")
    void testCalculateSalesTax_IL() {
        app.customer = new Customer("Test", "IL");
        double subtotal = 100.00;
        double expectedTax = subtotal * 0.06;
        assertEquals(expectedTax, app.calculateSalesTax(subtotal), 0.001);
    }

    @Test
    @DisplayName("calculateSalesTax should calculate tax for CA")
    void testCalculateSalesTax_CA() {
        app.customer = new Customer("Test", "CA");
        double subtotal = 200.00;
        double expectedTax = subtotal * 0.06;
        assertEquals(expectedTax, app.calculateSalesTax(subtotal), 0.001);
    }

    @Test
    @DisplayName("calculateSalesTax should calculate tax for NY")
    void testCalculateSalesTax_NY() {
        app.customer = new Customer("Test", "NY");
        double subtotal = 300.00;
        double expectedTax = subtotal * 0.06;
        assertEquals(expectedTax, app.calculateSalesTax(subtotal), 0.001);
    }

    @Test
    @DisplayName("calculateSalesTax should return 0 for non-taxable state TX")
    void testCalculateSalesTax_TX() {
        app.customer = new Customer("Test", "TX");
        double subtotal = 150.00;
        assertEquals(0.0, app.calculateSalesTax(subtotal), 0.001);
    }

    @Test
    @DisplayName("calculateSalesTax should return 0 for subtotal 0")
    void testCalculateSalesTax_zeroSubtotal() {
        app.customer = new Customer("Test", "IL");
        double subtotal = 0.00;
        assertEquals(0.0, app.calculateSalesTax(subtotal), 0.001);
    }

    @Test
    @DisplayName("Standard shipping should be free above threshold")
    void testCalculateShippingCost_standardFree() {
        app.shippingOption = "STANDARD";
        double subtotal = 50.01; 
        assertEquals(0.0, app.calculateShippingCost(subtotal), 0.001);
    }

    @Test
    @DisplayName("Standard shipping should cost STANDARD_SHIPPING_COST below threshold")
    void testCalculateShippingCost_standardCost() {
        app.shippingOption = "STANDARD";
        double subtotal = 49.99; 
        assertEquals(10.00, app.calculateShippingCost(subtotal), 0.001);
    }

    @Test
    @DisplayName("Standard shipping should cost STANDARD_SHIPPING_COST at threshold")
    void testCalculateShippingCost_standardAtThreshold() {
        app.shippingOption = "STANDARD";
        double subtotal = 50.00; 
        assertEquals(0.0, app.calculateShippingCost(subtotal), 0.001);
    }

    @Test
    @DisplayName("Next day shipping should always cost NEXT_DAY_SHIPPING_COST")
    void testCalculateShippingCost_nextDay() {
        app.shippingOption = "NEXT_DAY";
        double subtotal = 10.00; 
        assertEquals(25.00, app.calculateShippingCost(subtotal), 0.001);

        subtotal = 1000.00;
        assertEquals(25.00, app.calculateShippingCost(subtotal), 0.001);
    }

    @Test
    @DisplayName("calculateShippingCost should return 0.0 for unknown shipping option")
    void testCalculateShippingCost_unknownOption() {
        app.shippingOption = "UNKNOWN"; 
        double subtotal = 100.00;
        assertEquals(0.0, app.calculateShippingCost(subtotal), 0.001);
    }

    @Test
    @DisplayName("addItemToCart should call cart.addItem with correct item and quantity")
    void testAddItemToCart() {
        Scanner realScanner = new Scanner(new ByteArrayInputStream("1\n2\n".getBytes()));
        app.scanner = realScanner;
        app.cart = mockedCart;

        doNothing().when(mockedCart).addItem(any(), eq(2));
        when(mockedCart.getItemCount()).thenReturn(2); 

        app.addItemToCart();

        verify(mockedCart).addItem(any(), eq(2));
        assertTrue(outputStreamCaptor.toString().contains("Laptop added to cart. Current items in cart: 2"));
    }

    @Test
    @DisplayName("addItemToCart should do nothing if item selection is cancelled (returns null)")
    void testAddItemToCart_itemSelectionCancelled() {
        Scanner realScanner = new Scanner(new ByteArrayInputStream("0\n".getBytes()));
        app.scanner = realScanner;
        app.cart = mockedCart;

        app.addItemToCart();

        verify(mockedCart, never()).addItem(any(Item.class), anyInt());
        assertTrue(outputStreamCaptor.toString().contains("Item selection cancelled."));
        assertFalse(outputStreamCaptor.toString().contains("added to cart."));
    }

    @Test
    @DisplayName("addItemToCart should handle invalid item selection then valid")
    void testAddItemToCart_invalidItemSelection() {
        Scanner realScanner = new Scanner(new ByteArrayInputStream("99\n1\n2\n".getBytes()));
        app.scanner = realScanner;
        app.cart = mockedCart;

        doNothing().when(mockedCart).addItem(any(), eq(2));
        when(mockedCart.getItemCount()).thenReturn(2);

        app.addItemToCart();

        verify(mockedCart).addItem(any(), eq(2));
        assertTrue(outputStreamCaptor.toString().contains("Invalid item number. Please try again."));
        assertTrue(outputStreamCaptor.toString().contains("Laptop added to cart. Current items in cart: 2"));
    }

    @Test
    @DisplayName("addItemToCart should handle invalid quantity input then valid")
    void testAddItemToCart_invalidQuantity() {
        Scanner realScanner = new Scanner(new ByteArrayInputStream("1\n-1\n2\n".getBytes()));
        app.scanner = realScanner;
        app.cart = mockedCart;

        doNothing().when(mockedCart).addItem(any(), eq(2));
        when(mockedCart.getItemCount()).thenReturn(2);

        app.addItemToCart();

        verify(mockedCart).addItem(any(), eq(2));
        assertTrue(outputStreamCaptor.toString().contains("Quantity must be greater than zero"));
        assertTrue(outputStreamCaptor.toString().contains("Laptop added to cart. Current items in cart: 2"));
    }

    @Test
    @DisplayName("viewCartContents should print empty message if cart is empty")
    void testViewCartContents_empty() {
        when(mockedCart.isEmpty()).thenReturn(true);
        app.viewCartContents();
        assertTrue(outputStreamCaptor.toString().contains("Your shopping cart is empty."));
        verify(mockedCart, never()).getCartContents();
    }

    @Test
    @DisplayName("viewCartContents should print cart contents if not empty and check separator")
    void testViewCartContents_notEmpty() {
        when(mockedCart.isEmpty()).thenReturn(false);
        CartItem cartItem1 = new CartItem(laptop, 1);
        CartItem cartItem2 = new CartItem(mouse, 2);
        when(mockedCart.getCartContents()).thenReturn(List.of(cartItem1, cartItem2));

        app.viewCartContents();

        String output = outputStreamCaptor.toString();
        assertTrue(output.contains("--- Your Shopping Cart ---"));
        assertTrue(output.contains(cartItem1.toString()));
        assertTrue(output.contains(cartItem2.toString()));
        assertTrue(output.contains("--------------------------"));
        verify(mockedCart).getCartContents();
    }

    @Test
    @DisplayName("editItemQuantity should show message for empty cart")
    void testEditItemQuantity_emptyCart() {
        when(mockedCart.isEmpty()).thenReturn(true);
        app.editItemQuantity();
        assertTrue(outputStreamCaptor.toString().contains("Your shopping cart is empty. Nothing to edit."));
        verify(mockedCart, never()).getCartContents();
    }

    @Test
    @DisplayName("editItemQuantity should update quantity for existing item and print prompts")
    void testEditItemQuantity_existingItem() {
        Scanner realScanner = new Scanner(new ByteArrayInputStream("Laptop\n5\n".getBytes()));
        app.scanner = realScanner;
        app.cart = mockedCart;

        when(mockedCart.isEmpty()).thenReturn(false);
        CartItem laptopInCart = new CartItem(laptop, 1);
        when(mockedCart.getCartContents()).thenReturn(List.of(laptopInCart));
        when(mockedCart.updateItemQuantity(any(), eq(5))).thenReturn(true);

        app.editItemQuantity();

        verify(mockedCart).updateItemQuantity(any(), eq(5));
        String output = outputStreamCaptor.toString();
        assertTrue(output.contains("--- Your Shopping Cart ---")); 
        assertTrue(output.contains("Enter the name of the item you wish to edit:"));
        assertTrue(output.contains("Enter new quantity for Laptop (enter 0 to remove): "));
        assertTrue(output.contains("Quantity for Laptop updated to 5."));
    }

    @Test
    @DisplayName("editItemQuantity should remove item if new quantity is 0")
    void testEditItemQuantity_toZero() {
        Scanner realScanner = new Scanner(new ByteArrayInputStream("Laptop\n0\n".getBytes()));
        app.scanner = realScanner;
        app.cart = mockedCart;

        when(mockedCart.isEmpty()).thenReturn(false);
        CartItem laptopInCart = new CartItem(laptop, 1);
        when(mockedCart.getCartContents()).thenReturn(List.of(laptopInCart));
        when(mockedCart.updateItemQuantity(any(), eq(0))).thenReturn(true);

        app.editItemQuantity();

        verify(mockedCart).updateItemQuantity(any(), eq(0));
        assertTrue(outputStreamCaptor.toString().contains("Laptop removed from cart."));
    }

    @Test
    @DisplayName("editItemQuantity should show message for non-existent item")
    void testEditItemQuantity_nonExistent() {
        Scanner realScanner = new Scanner(new ByteArrayInputStream("Keyboard\n".getBytes()));
        app.scanner = realScanner;
        app.cart = mockedCart;

        when(mockedCart.isEmpty()).thenReturn(false);
        CartItem laptopInCart = new CartItem(laptop, 1);
        when(mockedCart.getCartContents()).thenReturn(List.of(laptopInCart));

        app.editItemQuantity();

        assertTrue(outputStreamCaptor.toString().contains("Item 'Keyboard' not found in your cart."));
        verify(mockedCart, never()).updateItemQuantity(any(), anyInt());
    }

    @Test
    @DisplayName("editItemQuantity should re-prompt for negative quantity then accept valid and print prompts")
    void testEditItemQuantity_negativeThenValid() {
        Scanner realScanner = new Scanner(new ByteArrayInputStream("Laptop\n-1\n3\n".getBytes()));
        app.scanner = realScanner;
        app.cart = mockedCart;

        when(mockedCart.isEmpty()).thenReturn(false);
        CartItem laptopInCart = new CartItem(laptop, 1);
        when(mockedCart.getCartContents()).thenReturn(List.of(laptopInCart));
        when(mockedCart.updateItemQuantity(any(), eq(3))).thenReturn(true);

        app.editItemQuantity();

        String output = outputStreamCaptor.toString();
        assertTrue(output.contains("Quantity must be greater than zero. Please try again."));
        assertEquals(1, countOccurrences(output, "Enter new quantity: "));
        verify(mockedCart).updateItemQuantity(any(), eq(3));
        assertTrue(output.contains("Quantity for Laptop updated to 3."));
    }

    @Test
    @DisplayName("editItemQuantity should re-prompt for invalid non-integer quantity then valid")
    void testEditItemQuantity_invalidNonIntegerQuantityThenValid() {
        Scanner realScanner = new Scanner(new ByteArrayInputStream("Laptop\nabc\n2\n".getBytes()));
        app.scanner = realScanner;
        app.cart = mockedCart;

        when(mockedCart.isEmpty()).thenReturn(false);
        CartItem laptopInCart = new CartItem(laptop, 1);
        when(mockedCart.getCartContents()).thenReturn(List.of(laptopInCart));
        when(mockedCart.updateItemQuantity(any(), eq(2))).thenReturn(true);

        app.editItemQuantity();

        String output = outputStreamCaptor.toString();
        assertTrue(output.contains("Invalid input. Please enter a whole number for quantity."));
        assertEquals(1, countOccurrences(output, "Enter new quantity: "));
        verify(mockedCart).updateItemQuantity(any(), eq(2));
        assertTrue(output.contains("Quantity for Laptop updated to 2."));
    }

    @Test
    @DisplayName("editItemQuantity should show failure message if cart.updateItemQuantity returns false")
    void testEditItemQuantity_updateFails() {
        Scanner realScanner = new Scanner(new ByteArrayInputStream("Laptop\n5\n".getBytes()));
        app.scanner = realScanner;
        app.cart = mockedCart;

        when(mockedCart.isEmpty()).thenReturn(false);
        CartItem laptopInCart = new CartItem(laptop, 1);
        when(mockedCart.getCartContents()).thenReturn(List.of(laptopInCart));
        when(mockedCart.updateItemQuantity(any(), eq(5))).thenReturn(false); 

        app.editItemQuantity();

        verify(mockedCart).updateItemQuantity(any(), eq(5));
        assertTrue(outputStreamCaptor.toString().contains("Failed to update quantity for Laptop. Please try again."));
    }

    @Test
    @DisplayName("editItemQuantity lambda validation: should edit correct item when multiple exist (kills line 244 mutant)")
    void testEditItemQuantity_killsLambdaMutant() {
        Scanner realScanner = new Scanner(new ByteArrayInputStream("Mouse\n5\n".getBytes()));
        app.scanner = realScanner;
        app.cart = mockedCart;

        when(mockedCart.isEmpty()).thenReturn(false);
        CartItem laptopInCart = new CartItem(laptop, 1);
        CartItem mouseInCart = new CartItem(mouse, 2);
        
        when(mockedCart.getCartContents()).thenReturn(List.of(laptopInCart, mouseInCart));
        when(mockedCart.updateItemQuantity(any(), eq(5))).thenReturn(true);

        app.editItemQuantity();

        verify(mockedCart).updateItemQuantity(argThat(item -> item.getName().equals("Mouse")), eq(5));
        verify(mockedCart, never()).updateItemQuantity(argThat(item -> item.getName().equals("Laptop")), anyInt());
        
        String output = outputStreamCaptor.toString();
        assertTrue(output.contains("Quantity for Mouse updated to 5."));
    }

    @Test
    @DisplayName("removeItemFromCart should show message for empty cart")
    void testRemoveItemFromCart_emptyCart() {
        when(mockedCart.isEmpty()).thenReturn(true);
        app.removeItemFromCart();
        assertTrue(outputStreamCaptor.toString().contains("Your shopping cart is empty. Nothing to remove."));
        verify(mockedCart, never()).getCartContents();
    }

    @Test
    @DisplayName("removeItemFromCart should call cart.removeItem for existing item and print prompts")
    void testRemoveItemFromCart_existing() {
        Scanner realScanner = new Scanner(new ByteArrayInputStream("Laptop\n".getBytes()));
        app.scanner = realScanner;
        app.cart = mockedCart;

        when(mockedCart.isEmpty()).thenReturn(false);
        CartItem laptopInCart = new CartItem(laptop, 1);
        when(mockedCart.getCartContents()).thenReturn(List.of(laptopInCart));
        when(mockedCart.removeItem(any())).thenReturn(true);

        app.removeItemFromCart();

        verify(mockedCart).removeItem(any());
        String output = outputStreamCaptor.toString();
        assertTrue(output.contains("--- Your Shopping Cart ---")); 
        assertTrue(output.contains("Enter the name of the item you wish to remove:"));
        assertTrue(output.contains("Laptop removed from cart."));
    }

    @Test
    @DisplayName("removeItemFromCart should show message for non-existent item")
    void testRemoveItemFromCart_nonExistent() {
        Scanner realScanner = new Scanner(new ByteArrayInputStream("Keyboard\n".getBytes()));
        app.scanner = realScanner;
        app.cart = mockedCart;

        when(mockedCart.isEmpty()).thenReturn(false);
        CartItem laptopInCart = new CartItem(laptop, 1);
        when(mockedCart.getCartContents()).thenReturn(List.of(laptopInCart));

        app.removeItemFromCart();

        assertTrue(outputStreamCaptor.toString().contains("Item 'Keyboard' not found in your cart."));
        verify(mockedCart, never()).removeItem(any());
    }

    @Test
    @DisplayName("removeItemFromCart should show failure message if cart.removeItem returns false")
    void testRemoveItemFromCart_removeFails() {
        Scanner realScanner = new Scanner(new ByteArrayInputStream("Laptop\n".getBytes()));
        app.scanner = realScanner;
        app.cart = mockedCart;

        when(mockedCart.isEmpty()).thenReturn(false);
        CartItem laptopInCart = new CartItem(laptop, 1);
        when(mockedCart.getCartContents()).thenReturn(List.of(laptopInCart));
        when(mockedCart.removeItem(any())).thenReturn(false); 

        app.removeItemFromCart();

        verify(mockedCart).removeItem(any());
        assertTrue(outputStreamCaptor.toString().contains("Failed to remove Laptop from cart. Please try again."));
    }

    @Test
    @DisplayName("calculateAndDisplayTotal should display error for empty cart")
    void testCalculateAndDisplayTotal_emptyCart() {
        when(mockedCart.isEmpty()).thenReturn(true);
        app.calculateAndDisplayTotal();
        assertTrue(outputStreamCaptor.toString().contains("Your shopping cart is empty. No total to calculate."));
    }

    @Test
    @DisplayName("calculateAndDisplayTotal should display error for subtotal below min purchase amount")
    void testCalculateAndDisplayTotal_belowMinPurchase() {
        when(mockedCart.isEmpty()).thenReturn(false);
        when(mockedCart.calculateSubtotal()).thenReturn(0.50); 
        app.calculateAndDisplayTotal();
        assertTrue(outputStreamCaptor.toString().contains("Error: Minimum acceptable purchase amount is $1.00."));
    }

    @Test
    @DisplayName("calculateAndDisplayTotal should display error for subtotal above max purchase amount")
    void testCalculateAndDisplayTotal_aboveMaxPurchase() {
        when(mockedCart.isEmpty()).thenReturn(false);
        when(mockedCart.calculateSubtotal()).thenReturn(100000.00); 
        app.calculateAndDisplayTotal();
        assertTrue(outputStreamCaptor.toString().contains("Error: Maximum acceptable purchase amount is $99999.99."));
    }

    @Test
    @DisplayName("calculateAndDisplayTotal should display correct summary and kill line 355 mutant")
    void testCalculateAndDisplayTotal_validOrder_taxableStandard() {
        app.customer = new Customer("Test", "IL");
        app.shippingOption = "STANDARD";

        when(mockedCart.isEmpty()).thenReturn(false);
        when(mockedCart.calculateSubtotal()).thenReturn(100.00); 
        CartItem testCartItem = new CartItem(new Item("TestItem", 100.00), 1);
        when(mockedCart.getCartContents()).thenReturn(List.of(testCartItem));

        app.calculateAndDisplayTotal();

        String output = outputStreamCaptor.toString();
        assertTrue(output.contains("\n--- Order Summary ---"));
        assertTrue(output.contains(testCartItem.toString())); 
        assertTrue(output.contains("Raw Purchase Price (Subtotal): $100.00"));
        assertTrue(output.contains("Sales Tax: $6.00"));
        assertTrue(output.contains("Shipping (STANDARD): $0.00"));
        assertTrue(output.contains("Total: $106.00"));
        
        String[] lines = output.split("\\r?\\n");
        boolean foundExactLine355Separator = false;
        for (String line : lines) {
            if (line.equals("---------------------")) {
                foundExactLine355Separator = true;
                break;
            }
        }
        assertTrue(foundExactLine355Separator, "Must print exactly 21 hyphens inside calculateAndDisplayTotal!");
    }

    @Test
    @DisplayName("calculateAndDisplayTotal should display correct summary for valid order (non-taxable, next day shipping)")
    void testCalculateAndDisplayTotal_validOrder_nonTaxableNextDay() {
        app.customer = new Customer("Test", "TX");
        app.shippingOption = "NEXT_DAY";

        when(mockedCart.isEmpty()).thenReturn(false);
        when(mockedCart.calculateSubtotal()).thenReturn(30.00); 
        when(mockedCart.getCartContents()).thenReturn(List.of(new CartItem(new Item("TestItem", 30.00), 1)));

        app.calculateAndDisplayTotal();

        String output = outputStreamCaptor.toString();
        assertTrue(output.contains("Raw Purchase Price (Subtotal): $30.00"));
        assertTrue(output.contains("Sales Tax: $0.00"));
        assertTrue(output.contains("Shipping (NEXT_DAY): $25.00"));
        assertTrue(output.contains("Total: $55.00"));
    }

    @Test
    @DisplayName("checkout should fail for empty cart")
    void testCheckout_emptyCart() {
        when(mockedCart.isEmpty()).thenReturn(true);
        app.checkout();
        assertTrue(outputStreamCaptor.toString().contains("Your shopping cart is empty. Cannot checkout."));
        verify(mockedCart, never()).clearCart();
    }

    @Test
    @DisplayName("checkout should fail for subtotal below min purchase amount")
    void testCheckout_belowMinPurchase() {
        when(mockedCart.isEmpty()).thenReturn(false);
        when(mockedCart.calculateSubtotal()).thenReturn(0.50);
        app.checkout();
        assertTrue(outputStreamCaptor.toString().contains("Checkout failed: Minimum acceptable purchase amount is $1.00."));
        verify(mockedCart, never()).clearCart();
    }

    @Test
    @DisplayName("checkout should fail for subtotal above max purchase amount")
    void testCheckout_aboveMaxPurchase() {
        when(mockedCart.isEmpty()).thenReturn(false);
        when(mockedCart.calculateSubtotal()).thenReturn(100000.00);
        app.checkout();
        assertTrue(outputStreamCaptor.toString().contains("Checkout failed: Maximum acceptable purchase amount is $99999.99."));
        verify(mockedCart, never()).clearCart();
    }

    @Test
    @DisplayName("checkout should complete successfully and clear cart for valid order")
    void testCheckout_success() {
        app.customer = new Customer("Test", "IL");
        app.shippingOption = "STANDARD";

        when(mockedCart.isEmpty()).thenReturn(false);
        when(mockedCart.calculateSubtotal()).thenReturn(100.00);
        when(mockedCart.getCartContents()).thenReturn(List.of(new CartItem(new Item("TestItem", 100.00), 1)));
        when(mockedCart.clearCart()).thenReturn(true);

        app.checkout();

        String output = outputStreamCaptor.toString();
        assertTrue(output.contains("--- Transaction completed! ---"));
        assertTrue(output.contains("Total: $106.00"));
        verify(mockedCart).clearCart();
    }

    @Test
    @DisplayName("checkout should complete successfully for subtotal exactly at MIN_PURCHASE_AMOUNT")
    void testCheckout_atMinPurchase() {
        app.customer = new Customer("Test", "IL");
        app.shippingOption = "STANDARD";

        when(mockedCart.isEmpty()).thenReturn(false);
        when(mockedCart.calculateSubtotal()).thenReturn(1.00); 
        when(mockedCart.getCartContents()).thenReturn(List.of(new CartItem(new Item("TestItem", 1.00), 1)));
        when(mockedCart.clearCart()).thenReturn(true);

        app.checkout();

        String output = outputStreamCaptor.toString();
        assertFalse(output.contains("Checkout failed: Minimum acceptable purchase amount is $1.00."));
        assertTrue(output.contains("--- Transaction completed! ---"));
        assertTrue(output.contains("Total: $11.06"));
        verify(mockedCart).clearCart();
    }

    @Test
    @DisplayName("checkout should complete successfully for subtotal exactly at MAX_PURCHASE_AMOUNT")
    void testCheckout_atMaxPurchase() {
        app.customer = new Customer("Test", "IL");
        app.shippingOption = "STANDARD";

        when(mockedCart.isEmpty()).thenReturn(false);
        when(mockedCart.calculateSubtotal()).thenReturn(99999.99); 
        when(mockedCart.getCartContents()).thenReturn(List.of(new CartItem(new Item("TestItem", 99999.99), 1)));
        when(mockedCart.clearCart()).thenReturn(true);

        app.checkout();

        String output = outputStreamCaptor.toString();
        assertFalse(output.contains("Checkout failed: Maximum acceptable purchase amount is $99999.99."));
        assertTrue(output.contains("--- Transaction completed! ---"));
        assertTrue(output.contains("Total: $105999.99"));
        verify(mockedCart).clearCart();
    }

    @Test
    @DisplayName("Start method should exit gracefully on '0' input and print separator")
    void testStart_exit() {
        when(mockedScanner.nextLine())
                .thenReturn("Test User")
                .thenReturn("IL")
                .thenReturn("1") 
                .thenReturn(""); 

        when(mockedScanner.hasNextInt()).thenReturn(true); 
        when(mockedScanner.nextInt()).thenReturn(0); 

        app.start();

        String output = outputStreamCaptor.toString();
        assertTrue(output.contains("Welcome to the CLI Shopping Application!"));
        assertTrue(output.contains("Exiting application. Goodbye!"));
        assertTrue(output.contains("\n------------------------------------\n"));
        verify(mockedCart, never()).clearCart(); 
    }

    @Test
    @DisplayName("Start method should allow adding item and then exiting")
    void testStart_addItemThenExit() {
        when(mockedScanner.nextLine())
                .thenReturn("Test User")
                .thenReturn("IL")
                .thenReturn("1") 
                .thenReturn("") 
                .thenReturn("") 
                .thenReturn("") 
                .thenReturn("") 
                .thenReturn(""); 
        when(mockedScanner.hasNextInt()).thenReturn(true, true, true, true, true); 
        when(mockedScanner.nextInt())
                .thenReturn(1)  
                .thenReturn(1)  
                .thenReturn(1)  
                .thenReturn(1)  
                .thenReturn(0); 
        when(mockedScanner.next()).thenReturn(""); 

        doNothing().when(mockedCart).addItem(eq(laptop), eq(1));
        when(mockedCart.getItemCount()).thenReturn(1);

        app.start();

        String output = outputStreamCaptor.toString();
        assertTrue(output.contains("Laptop added to cart. Current items in cart: 1"));
        assertTrue(output.contains("Exiting application. Goodbye!"));
        verify(mockedCart).addItem(eq(laptop), eq(1));
    }

    @Test
    @DisplayName("Start method should handle all menu options and an invalid choice before checkout")
    void testStart_allMenuOptionsAndInvalidChoice() {
        Item laptop = new Item("Laptop", 1200.0);
        CartItem laptopInCart = new CartItem(laptop, 2);

        when(mockedCart.isEmpty()).thenReturn(
            true,  
            false, 
            false, 
            false, 
            false, 
            true,  
            true   
        );

        when(mockedCart.getCartContents()).thenReturn(List.of(laptopInCart));
        when(mockedCart.updateItemQuantity(any(), anyInt())).thenReturn(true);
        when(mockedCart.removeItem(any())).thenReturn(true);

        when(mockedScanner.nextLine()).thenReturn(
            "Test User", 
            "IL",        
            "1",         
            "",          
            "",          
            "",          
            "",          
            "",          
            "",          
            "Laptop",    
            "",          
            "",          
            "Laptop",    
            "",          
            "",          
            ""           
        );

        when(mockedScanner.hasNextInt()).thenReturn(
            true,  
            true,  
            true,  
            true,  
            true,  
            true,  
            true,  
            true,  
            true,  
            false, 
            true,  
            true   
        );

        when(mockedScanner.nextInt()).thenReturn(
            2,  
            1,  
            1,  
            2,  
            2,  
            3,  
            3,  
            4,  
            5,  
            99, 
            6   
        );

        when(mockedScanner.next()).thenReturn("invalid_text");

        app.start();

        String output = outputStreamCaptor.toString();
        assertTrue(output.contains("Welcome, Test User from IL!"));
        assertTrue(output.contains("Your shopping cart is empty.")); 
        assertTrue(output.contains("Laptop added to cart."));
        assertTrue(output.contains("Quantity for Laptop updated to 3."));
        assertTrue(output.contains("Laptop removed from cart."));
        
        // Assert that the invalid input warning is printed EXACTLY twice:
        // Once from getUserChoice() when "invalid_text" is passed,
        // and once from the start() switch default branch when menu choice "99" is passed.
        assertEquals(2, countOccurrences(output, "Invalid input. Please enter a number."));
        
        assertTrue(output.contains("Your shopping cart is empty. Cannot checkout."));
    }           

    @Test
    @DisplayName("displayMenu should print all menu items correctly")
    void testDisplayMenu_printsAllLines() {
        app.displayMenu();
        String output = outputStreamCaptor.toString();
        assertTrue(output.contains("Shopping Application Menu:"));
        assertTrue(output.contains("1. Add item to cart"));
        assertTrue(output.contains("2. See contents of shopping cart"));
        assertTrue(output.contains("3. Edit quantity of items in shopping cart"));
        assertTrue(output.contains("4. Remove items from shopping cart"));
        assertTrue(output.contains("5. Get current total"));
        assertTrue(output.contains("6. Checkout"));
        assertTrue(output.contains("0. Exit"));
    }

    @Test
    @DisplayName("Start method loop should show menu and handle calculateAndDisplayTotal (option 5)")
    void testStart_calculateTotalOption() {
        when(mockedScanner.nextLine())
                .thenReturn("Test User")
                .thenReturn("IL")
                .thenReturn("1") 
                .thenReturn("")  
                .thenReturn(""); 

        when(mockedScanner.hasNextInt()).thenReturn(true, true);
        when(mockedScanner.nextInt())
                .thenReturn(5) 
                .thenReturn(0); 

        when(mockedCart.isEmpty()).thenReturn(true); 

        app.start();

        String output = outputStreamCaptor.toString();
        assertTrue(output.contains("Shopping Application Menu:")); 
        assertTrue(output.contains("Your shopping cart is empty. No total to calculate.")); 
        
        assertEquals(2, countOccurrences(output, "\n------------------------------------\n"));
    }

    @Test
    @DisplayName("Verify that the loop separator is printed exactly as expected inside start() and kill line 84 mutant")
    void testStart_loopSeparatorPrinted_strict_killsMutant() {
        outputStreamCaptor.reset();

        when(mockedScanner.nextLine())
                .thenReturn("Test User")
                .thenReturn("IL")
                .thenReturn("1") 
                .thenReturn(""); 

        when(mockedScanner.hasNextInt()).thenReturn(true);
        when(mockedScanner.nextInt()).thenReturn(0); 

        app.start();

        String output = outputStreamCaptor.toString();
        
        assertTrue(output.contains("\n------------------------------------\n"), 
                "The exact separator string with newlines must be printed!");
    }

    @Test
    @DisplayName("Integration test to kill Line 84 mutant - removed call to PrintStream::println")
    void testMainMethodExitMessageAndSeparator_KillsLine84() {
        String simulatedInput = "Test User\nIL\n1\n0\n";
        System.setIn(new ByteArrayInputStream(simulatedInput.getBytes()));

        outputStreamCaptor.reset();
        ShoppingApp.main(new String[]{});

        String output = outputStreamCaptor.toString();
        
        assertTrue(output.contains("Exiting application. Goodbye!"));
        assertTrue(output.contains("\n------------------------------------\n"), 
                "Line 84 loop separator must be printed during app shutdown!");
    }

    private int countOccurrences(String text, String searchString) {
        int count = 0;
        int lastIndex = 0;
        while (lastIndex != -1) {
            lastIndex = text.indexOf(searchString, lastIndex);
            if (lastIndex != -1) {
                count++;
                lastIndex += searchString.length();
            }
        }
        return count;
    }

    @Test
    @DisplayName("calculateSalesTax should return 0.0 when customer is null")
    void testCalculateSalesTax_nullCustomer() {
        app.customer = null; // Force customer to be null
        double subtotal = 100.00;
        assertEquals(0.0, app.calculateSalesTax(subtotal), 0.001);
    }
}