public class CartItem {
    private Item item;
    private int quantity;

    public CartItem(Item item, int quantity) {
        this.item = item;
        this.quantity = quantity;
    }

    public Item getItem() {
        return item;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getTotalPrice() {
        return item.getPrice() * quantity;
    }

    @Override
    public String toString() {
        // Simple output without explicit padding for alignment.
        // This will produce strings like: "Laptop x 2 = $2400.00"
        return String.format("%s x %d = $%.2f", item.getName(), quantity, getTotalPrice());
    }
}