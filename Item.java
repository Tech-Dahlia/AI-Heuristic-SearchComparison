/**
 * Represents a single item in the 0/1 Knapsack Problem.
 * Without this file, our program is going to hit a wall when we execute. 
 * Its only job is to hold the data for one item (weight and value).
 */
public class Item {
    private final double weight;
    private final double value;

    /**
     * Constructor to initialize an item with its weight and value.
     * @param weight The weight (w_i) of the item.
     * @param value The value (v_i) of the item to be maximized.
     */
    public Item(double weight, double value) {
        this.weight = weight;
        this.value = value;
    }

    /**
     * @return The weight of this specific item.
     */
    public double getWeight() {
        return weight;
    }

    /**
     * @return The value of this specific item.
     */
    public double getValue() {
        return value;
    }

    /**
     * Overriding toString() allows us to see the actual data during debugging.
     * Instead of seeing "Item@7a81197d", we will see the weight and value.
     */
    @Override
    public String toString() {
        return "Item{w=" + weight + ", v=" + value + "}";
    }
}