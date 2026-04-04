// ShoppingCart.java
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ShoppingCart {
    private List<CartItem> items;

    public ShoppingCart() {
        this.items = new ArrayList<>();
    }

    public void addItem(Item item, int quantity) {
        if (quantity <= 0) {
            System.out.println("Quantity must be greater than zero.");
            return;
        }

        Optional<CartItem> existingCartItem = items.stream()
                .filter(ci -> ci.getItem().equals(item))
                .findFirst();

        if (existingCartItem.isPresent()) {
            existingCartItem.get().setQuantity(existingCartItem.get().getQuantity() + quantity);
        } else {
            items.add(new CartItem(item, quantity));
        }
        System.out.println(item.getName() + " added to cart. Current items in cart: " + getTotalItemCount());
    }

    public void updateItemQuantity(Item item, int newQuantity) {
        if (newQuantity < 0) {
            System.out.println("Quantity cannot be negative.");
            return;
        }
        
        Optional<CartItem> existingCartItem = items.stream()
                .filter(ci -> ci.getItem().equals(item))
                .findFirst();

        if (existingCartItem.isPresent()) {
            if (newQuantity == 0) {
                removeItem(item); // If new quantity is 0, remove the item
            } else {
                existingCartItem.get().setQuantity(newQuantity);
                System.out.println("Quantity for " + item.getName() + " updated to " + newQuantity + ".");
            }
        } else {
            System.out.println(item.getName() + " is not in your cart to update.");
        }
    }

    public void removeItem(Item item) {
        boolean removed = items.removeIf(ci -> ci.getItem().equals(item));
        if (removed) {
            System.out.println(item.getName() + " removed from cart.");
        } else {
            System.out.println(item.getName() + " was not found in your cart.");
        }
    }

    public List<CartItem> getCartContents() {
        return new ArrayList<>(items); // Return a copy to prevent external modification
    }

    public double calculateSubtotal() {
        return items.stream().mapToDouble(CartItem::getTotalPrice).sum();
    }

    public int getTotalItemCount() {
        return items.stream().mapToInt(CartItem::getQuantity).sum();
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    public void clearCart() {
        items.clear();
    }
}