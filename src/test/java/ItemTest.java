import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ItemTest {

    private Item laptop;
    private Item anotherLaptop;
    private Item mouse;
    private Item zeroPriceItem;
    private Item onePointFivePriceItem;

    @BeforeEach
    void setUp() {
        laptop = new Item("Laptop", 1200.00);
        anotherLaptop = new Item("Laptop", 1200.00); // Same as laptop
        mouse = new Item("Mouse", 25.50);
        zeroPriceItem = new Item("Freebie", 0.00);
        onePointFivePriceItem = new Item("Pen", 1.5);
    }

    @Test
    @DisplayName("Constructor should correctly initialize name and price")
    void testConstructorAndGetters() {
        assertEquals("Laptop", laptop.getName());
        assertEquals(1200.00, laptop.getPrice(), 0.001); // Use delta for double comparison
        assertEquals("Mouse", mouse.getName());
        assertEquals(25.50, mouse.getPrice(), 0.001);
    }

    @Test
    @DisplayName("toString should return correctly formatted string")
    void testToString() {
        assertEquals("Laptop ($1200.00)", laptop.toString());
        assertEquals("Mouse ($25.50)", mouse.toString());
        assertEquals("Freebie ($0.00)", zeroPriceItem.toString());
        assertEquals("Pen ($1.50)", onePointFivePriceItem.toString());
    }

    @Test
    @DisplayName("equals should return true for identical items")
    void testEquals_identicalItems() {
        assertTrue(laptop.equals(anotherLaptop));
        assertTrue(anotherLaptop.equals(laptop));
    }

    // NEW TEST CASE: To cover 'this == o' branch
    @Test
    @DisplayName("equals should return true for same object reference")
    void testEquals_sameObjectReference() {
        assertTrue(laptop.equals(laptop));
    }

    @Test
    @DisplayName("equals should return false for items with different names")
    void testEquals_differentNames() {
        assertFalse(laptop.equals(mouse));
        assertFalse(mouse.equals(laptop));
    }

    @Test
    @DisplayName("equals should return false for items with different prices")
    void testEquals_differentPrices() {
        Item cheaperLaptop = new Item("Laptop", 1000.00);
        assertFalse(laptop.equals(cheaperLaptop));
        assertFalse(cheaperLaptop.equals(laptop));
    }

    // NEW TEST CASE: To cover 'same price, different names' branch
    @Test
    @DisplayName("equals should return false for items with same price but different names")
    void testEquals_samePriceDifferentNames() {
        Item itemA = new Item("Book A", 15.99);
        Item itemB = new Item("Book B", 15.99);
        assertFalse(itemA.equals(itemB));
    }

    @Test
    @DisplayName("equals should return false for null object")
    void testEquals_nullObject() {
        assertFalse(laptop.equals(null));
    }

    @Test
    @DisplayName("equals should return false for object of different class")
    void testEquals_differentClass() {
        assertFalse(laptop.equals(new Object()));
    }

    @Test
    @DisplayName("hashCode should be consistent for equal objects")
    void testHashCode_consistentForEqualObjects() {
        assertEquals(laptop.hashCode(), anotherLaptop.hashCode());
    }

    @Test
    @DisplayName("hashCode should ideally be different for unequal objects")
    void testHashCode_differentForUnequalObjects() {
        // While not strictly required, good hash codes are different for different objects
        assertNotEquals(laptop.hashCode(), mouse.hashCode());
        Item cheaperLaptop = new Item("Laptop", 1000.00);
        assertNotEquals(laptop.hashCode(), cheaperLaptop.hashCode());
    }
}