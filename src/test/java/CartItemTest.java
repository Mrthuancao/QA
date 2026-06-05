import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CartItemTest {

    private Item laptop;
    private Item mouse;
    private CartItem laptopCartItem;
    private CartItem mouseCartItem;

    @BeforeEach
    void setUp() {
        laptop = new Item("Laptop", 1200.00);
        mouse = new Item("Mouse", 25.50);
        laptopCartItem = new CartItem(laptop, 2);
        mouseCartItem = new CartItem(mouse, 3);
    }

    @Test
    @DisplayName("Constructor should correctly initialize item and quantity")
    void testConstructorAndGetters() {
        assertEquals(laptop, laptopCartItem.getItem());
        assertEquals(2, laptopCartItem.getQuantity());

        assertEquals(mouse, mouseCartItem.getItem());
        assertEquals(3, mouseCartItem.getQuantity());
    }

    @Test
    @DisplayName("setQuantity should update the quantity correctly")
    void testSetQuantity() {
        laptopCartItem.setQuantity(5);
        assertEquals(5, laptopCartItem.getQuantity());

        laptopCartItem.setQuantity(0); // Test setting to zero
        assertEquals(0, laptopCartItem.getQuantity());

        laptopCartItem.setQuantity(-1); // CartItem itself allows negative, ShoppingCart handles validation
        assertEquals(-1, laptopCartItem.getQuantity());
    }

    @Test
    @DisplayName("getTotalPrice should calculate total price correctly")
    void testGetTotalPrice() {
        assertEquals(2400.00, laptopCartItem.getTotalPrice(), 0.001); // 2 * 1200.00
        assertEquals(76.50, mouseCartItem.getTotalPrice(), 0.001);   // 3 * 25.50

        // Test with zero quantity
        laptopCartItem.setQuantity(0);
        assertEquals(0.00, laptopCartItem.getTotalPrice(), 0.001);

        // Test with item having zero price
        Item freeItem = new Item("Free Gift", 0.00);
        CartItem freeCartItem = new CartItem(freeItem, 5);
        assertEquals(0.00, freeCartItem.getTotalPrice(), 0.001);

        // Test with negative quantity (though usually prevented by ShoppingCart)
        laptopCartItem.setQuantity(-1);
        assertEquals(-1200.00, laptopCartItem.getTotalPrice(), 0.001);
    }

    @Test
    @DisplayName("toString should return correctly formatted string (content and flexible spacing)")
    void testToString() {
        // The CartItem.toString() is now simple: "%s x %d = $%.2f"

        String laptopCartItemString = laptopCartItem.toString();
        // Expected: "Laptop x 2 = $2400.00"
        assertTrue(laptopCartItemString.matches("Laptop\\s+x\\s+2\\s+=\\s+\\$2400\\.00"),
                "Laptop CartItem toString should match 'Laptop x 2 = $2400.00' with flexible spacing.");

        String mouseCartItemString = mouseCartItem.toString();
        // Expected: "Mouse x 3 = $76.50"
        assertTrue(mouseCartItemString.matches("Mouse\\s+x\\s+3\\s+=\\s+\\$76\\.50"),
                "Mouse CartItem toString should match 'Mouse x 3 = $76.50' with flexible spacing.");

        Item pen = new Item("Pen", 1.5);
        CartItem penCartItem = new CartItem(pen, 10);
        String penCartItemString = penCartItem.toString();
        // Expected: "Pen x 10 = $15.00"
        assertTrue(penCartItemString.matches("Pen\\s+x\\s+10\\s+=\\s+\\$15\\.00"),
                "Pen CartItem toString should match 'Pen x 10 = $15.00' with flexible spacing.");

        Item longNameItem = new Item("Super Long Item Name", 100.00);
        CartItem longNameCartItem = new CartItem(longNameItem, 1);
        String longNameCartItemString = longNameCartItem.toString();
        // Expected: "Super Long Item Name x 1 = $100.00"
        assertTrue(longNameCartItemString.matches("Super Long Item Name\\s+x\\s+1\\s+=\\s+\\$100\\.00"),
                "Long Name CartItem toString should match 'Super Long Item Name x 1 = $100.00' with flexible spacing.");
    }
}