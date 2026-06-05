import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// Assuming Item and CartItem classes exist and are correctly defined.
// This is a placeholder implementation for ShoppingCart based on the needs of ShoppingAppTest.
public class ShoppingCart {
    private List<CartItem> items;

    public ShoppingCart() {
        this.items = new ArrayList<>();
    }

    /**
     * Adds an item to the cart or updates its quantity if already present.
     * This method is assumed to be void based on the Mockito error in ShoppingAppTest.
     *
     * @param item The item to add.
     * @param quantity The quantity of the item to add.
     */
    public void addItem(Item item, int quantity) {
        if (item == null || quantity <= 0) {
            // In a real application, you might throw an IllegalArgumentException
            // or return a boolean indicating success/failure.
            // For now, based on the void method signature implied by the tests,
            // we'll just return if input is invalid.
            return;
        }

        Optional<CartItem> existingItem = items.stream()
                .filter(ci -> ci.getItem().equals(item))
                .findFirst();

        if (existingItem.isPresent()) {
            existingItem.get().setQuantity(existingItem.get().getQuantity() + quantity);
        } else {
            items.add(new CartItem(item, quantity));
        }
    }

    /**
     * Updates the quantity of an existing item in the cart.
     * If newQuantity is 0 or less, the item is removed.
     *
     * @param item The item to update.
     * @param newQuantity The new quantity for the item.
     * @return true if the item was found and updated/removed, false otherwise.
     */
    public boolean updateItemQuantity(Item item, int newQuantity) {
        if (item == null) {
            return false;
        }
        Optional<CartItem> existingItem = items.stream()
                .filter(ci -> ci.getItem().equals(item))
                .findFirst();

        if (existingItem.isPresent()) {
            if (newQuantity <= 0) {
                items.remove(existingItem.get());
            } else {
                existingItem.get().setQuantity(newQuantity);
            }
            return true;
        }
        return false;
    }

    /**
     * Removes an item completely from the cart.
     *
     * @param item The item to remove.
     * @return true if the item was found and removed, false otherwise.
     */
    public boolean removeItem(Item item) {
        if (item == null) {
            return false;
        }
        return items.removeIf(ci -> ci.getItem().equals(item));
    }

    /**
     * Returns a list of all items currently in the cart.
     *
     * @return A new list containing the cart items.
     */
    public List<CartItem> getCartContents() {
        return new ArrayList<>(items); // Return a copy to prevent external modification
    }

    /**
     * Checks if the shopping cart is empty.
     *
     * @return true if the cart contains no items, false otherwise.
     */
    public boolean isEmpty() {
        return items.isEmpty();
    }

    /**
     * Calculates the subtotal of all items in the cart before tax and shipping.
     *
     * @return The total price of all items.
     */
    public double calculateSubtotal() {
        return items.stream()
                .mapToDouble(CartItem::getTotalPrice)
                .sum();
    }

    /**
     * Clears all items from the shopping cart.
     *
     * @return true if the cart was cleared, false if it was already empty (or an error occurred).
     */
    public boolean clearCart() {
        if (items.isEmpty()) {
            return false; // Or simply true, depending on desired behavior for already empty cart
        }
        items.clear();
        return true;
    }

    /**
     * Returns the number of distinct items in the cart.
     * This method was added to resolve the "getItemCount() is undefined" error in ShoppingAppTest.
     *
     * @return The count of distinct items.
     */
    public int getItemCount() {
        return items.size();
    }

    /**
     * Returns the total quantity of all items in the cart (sum of quantities).
     * This method was added to resolve the "getTotalItemCount() is undefined" error in ShoppingCartTest.
     *
     * @return The total quantity of all items.
     */
    public int getTotalItemCount() {
        return items.stream()
                .mapToInt(CartItem::getQuantity)
                .sum();
    }
}