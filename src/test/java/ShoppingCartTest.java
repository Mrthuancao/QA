import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ShoppingCartTest {

    private ShoppingCart cart;
    private Item laptop;
    private Item mouse;

    @BeforeEach
    void setUp() {
        cart = new ShoppingCart();
        laptop = new Item("Laptop", 1200.00);
        mouse = new Item("Mouse", 25.50);
    }

    @Test
    @DisplayName("A new cart should be empty")
    void testNewCartIsEmpty() {
        assertTrue(cart.isEmpty());
        assertEquals(0, cart.getItemCount());
        assertEquals(0, cart.getTotalItemCount()); // Added test for new method
        assertEquals(0.0, cart.calculateSubtotal(), 0.001);
    }

    @Test
    @DisplayName("Should add a new item to cart")
    void testAddItem_newItem() {
        cart.addItem(laptop, 1);
        assertFalse(cart.isEmpty());
        assertEquals(1, cart.getItemCount()); // Number of distinct items
        assertEquals(1, cart.getTotalItemCount()); // Total quantity of all items
        assertEquals(1200.00, cart.calculateSubtotal(), 0.001);
        assertEquals(1, cart.getCartContents().size());
        assertEquals(laptop, cart.getCartContents().get(0).getItem());
        assertEquals(1, cart.getCartContents().get(0).getQuantity());
    }

    @Test
    @DisplayName("Should increment quantity for an existing item")
    void testAddItem_existingItem() {
        cart.addItem(laptop, 1);
        cart.addItem(laptop, 2); // Add more of the same item
        assertFalse(cart.isEmpty());
        assertEquals(1, cart.getItemCount()); // Still 1 distinct item
        assertEquals(3, cart.getTotalItemCount()); // Total quantity is 1 + 2 = 3
        assertEquals(3600.00, cart.calculateSubtotal(), 0.001); // 3 * 1200.00
        assertEquals(1, cart.getCartContents().size());
        assertEquals(laptop, cart.getCartContents().get(0).getItem());
        assertEquals(3, cart.getCartContents().get(0).getQuantity());
    }

    @Test
    @DisplayName("Should add multiple distinct items")
    void testAddItem_multipleDistinctItems() {
        cart.addItem(laptop, 1);
        cart.addItem(mouse, 2);
        assertFalse(cart.isEmpty());
        assertEquals(2, cart.getItemCount()); // 2 distinct items
        assertEquals(3, cart.getTotalItemCount()); // Total quantity is 1 + 2 = 3
        assertEquals(1200.00 + (2 * 25.50), cart.calculateSubtotal(), 0.001);
        assertEquals(2, cart.getCartContents().size());
    }

    @Test
    @DisplayName("Should not add item with zero or negative quantity")
    void testAddItem_invalidQuantity() {
        cart.addItem(laptop, 0);
        cart.addItem(mouse, -1);
        assertTrue(cart.isEmpty());
        assertEquals(0, cart.getItemCount());
        assertEquals(0, cart.getTotalItemCount());
    }

    @Test
    @DisplayName("Should not add a null item")
    void testAddItem_nullItem() {
        cart.addItem(null, 1);
        assertTrue(cart.isEmpty());
        assertEquals(0, cart.getItemCount());
        assertEquals(0, cart.getTotalItemCount());
    }

    @Test
    @DisplayName("Should remove an existing item")
    void testRemoveItem_existing() {
        cart.addItem(laptop, 1);
        cart.addItem(mouse, 2);
        assertTrue(cart.removeItem(laptop));
        assertFalse(cart.isEmpty());
        assertEquals(1, cart.getItemCount());
        assertEquals(2, cart.getTotalItemCount());
        assertEquals(2 * 25.50, cart.calculateSubtotal(), 0.001);
        assertFalse(cart.getCartContents().stream().anyMatch(ci -> ci.getItem().equals(laptop)));
    }

    @Test
    @DisplayName("Should not remove a non-existent item")
    void testRemoveItem_nonExistent() {
        cart.addItem(laptop, 1);
        Item keyboard = new Item("Keyboard", 50.00);
        assertFalse(cart.removeItem(keyboard));
        assertFalse(cart.isEmpty());
        assertEquals(1, cart.getItemCount());
        assertEquals(1, cart.getTotalItemCount());
    }

    @Test
    @DisplayName("Removing the last item should make the cart empty")
    void testRemoveItem_lastItem() {
        cart.addItem(laptop, 1);
        assertTrue(cart.removeItem(laptop));
        assertTrue(cart.isEmpty());
        assertEquals(0, cart.getItemCount());
        assertEquals(0, cart.getTotalItemCount());
    }

    @Test
    @DisplayName("Should not remove a null item")
    void testRemoveItem_nullItem() {
        cart.addItem(laptop, 1);
        assertFalse(cart.removeItem(null));
        assertFalse(cart.isEmpty());
        assertEquals(1, cart.getItemCount());
        assertEquals(1, cart.getTotalItemCount());
    }

    @Test
    @DisplayName("Should update quantity of an existing item")
    void testUpdateItemQuantity_existing() {
        cart.addItem(laptop, 1);
        assertTrue(cart.updateItemQuantity(laptop, 5));
        assertEquals(1, cart.getItemCount());
        assertEquals(5, cart.getTotalItemCount());
        assertEquals(5 * 1200.00, cart.calculateSubtotal(), 0.001);
        assertEquals(5, cart.getCartContents().get(0).getQuantity());
    }

    @Test
    @DisplayName("Updating quantity to zero should remove the item")
    void testUpdateItemQuantity_toZero() {
        cart.addItem(laptop, 1);
        cart.addItem(mouse, 2);
        assertTrue(cart.updateItemQuantity(laptop, 0));
        assertFalse(cart.isEmpty());
        assertEquals(1, cart.getItemCount());
        assertEquals(2, cart.getTotalItemCount());
        assertEquals(2 * 25.50, cart.calculateSubtotal(), 0.001);
        assertFalse(cart.getCartContents().stream().anyMatch(ci -> ci.getItem().equals(laptop)));
    }

    @Test
    @DisplayName("Should not update quantity for a non-existent item")
    void testUpdateItemQuantity_nonExistent() {
        cart.addItem(laptop, 1);
        Item keyboard = new Item("Keyboard", 50.00);
        assertFalse(cart.updateItemQuantity(keyboard, 3));
        assertEquals(1, cart.getItemCount());
        assertEquals(1, cart.getTotalItemCount());
    }

    @Test
    @DisplayName("Should not update quantity for a null item")
    void testUpdateItemQuantity_nullItem() {
        cart.addItem(laptop, 1);
        assertFalse(cart.updateItemQuantity(null, 5));
        assertFalse(cart.isEmpty());
        assertEquals(1, cart.getItemCount());
        assertEquals(1, cart.getTotalItemCount());
    }

    @Test
    @DisplayName("Should clear all items from the cart")
    void testClearCart() {
        cart.addItem(laptop, 1);
        cart.addItem(mouse, 2);
        assertTrue(cart.clearCart());
        assertTrue(cart.isEmpty());
        assertEquals(0, cart.getItemCount());
        assertEquals(0, cart.getTotalItemCount());
        assertEquals(0.0, cart.calculateSubtotal(), 0.001);
    }

    @Test
    @DisplayName("Clearing an already empty cart should return false (or true depending on desired behavior)")
    void testClearCart_empty() {
        assertFalse(cart.clearCart()); // Assuming it returns false if already empty
        assertTrue(cart.isEmpty());
    }

    @Test
    @DisplayName("Calculate subtotal with multiple items")
    void testCalculateSubtotal_multipleItems() {
        cart.addItem(laptop, 1); // 1200.00
        cart.addItem(mouse, 2);  // 2 * 25.50 = 51.00
        assertEquals(1251.00, cart.calculateSubtotal(), 0.001);
    }

    @Test
    @DisplayName("Get cart contents should return a copy")
    void testGetCartContents_returnsCopy() {
        cart.addItem(laptop, 1);
        List<CartItem> contents = cart.getCartContents();
        assertEquals(1, contents.size());
        contents.clear(); // Clear the returned list
        assertFalse(cart.isEmpty()); // Original cart should still have the item
        assertEquals(1, cart.getItemCount());
    }

    @Test
    @DisplayName("getTotalItemCount should return 0 for an empty cart")
    void testGetTotalItemCount_emptyCart() {
        assertEquals(0, cart.getTotalItemCount());
    }

    @Test
    @DisplayName("getTotalItemCount should return correct sum of quantities for mixed items")
    void testGetTotalItemCount_mixedItems() {
        cart.addItem(laptop, 2);
        cart.addItem(mouse, 3);
        assertEquals(5, cart.getTotalItemCount()); // 2 + 3 = 5
    }

    @Test
    @DisplayName("getTotalItemCount should update correctly after item removal")
    void testGetTotalItemCount_afterRemoval() {
        cart.addItem(laptop, 2);
        cart.addItem(mouse, 3);
        cart.removeItem(laptop);
        assertEquals(3, cart.getTotalItemCount()); // Only mouse items remain
    }

    @Test
    @DisplayName("getTotalItemCount should update correctly after quantity update")
    void testGetTotalItemCount_afterQuantityUpdate() {
        cart.addItem(laptop, 2);
        cart.addItem(mouse, 3);
        cart.updateItemQuantity(laptop, 5); // Laptop quantity changes from 2 to 5
        assertEquals(8, cart.getTotalItemCount()); // 5 (laptop) + 3 (mouse) = 8
    }
}