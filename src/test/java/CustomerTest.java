// src/test/java/your/package/name/CustomerTest.java
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CustomerTest {

    private Customer customer1;
    private Customer customer2;

    @BeforeEach
    void setUp() {
        customer1 = new Customer("Alice Smith", "CA");
        customer2 = new Customer("Bob Johnson", "TX");
    }

    @Test
    @DisplayName("Constructor should correctly initialize name and stateOfResidence")
    void testConstructorAndGetters() {
        assertEquals("Alice Smith", customer1.getName());
        assertEquals("CA", customer1.getStateOfResidence());

        assertEquals("Bob Johnson", customer2.getName());
        assertEquals("TX", customer2.getStateOfResidence());
    }
}