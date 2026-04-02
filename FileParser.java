import java.io.File;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Scanner;

public class FileParser {
    public int numItems;
    public int capacity;
    public ArrayList<Item> items;

    public void loadFile(String filename) {
        try {
            File file = new File(filename);
            Scanner scanner = new Scanner(file);

            // It tells the scanner: "Ignore my computer's settings, use DOTS for decimals."
            scanner.useLocale(java.util.Locale.US);

            this.items = new ArrayList<>();

            // Read the first two values in a File (Number of Items and Capacity)
            if (scanner.hasNext())
            {
                this.numItems = (int) scanner.nextDouble();
            }
            if (scanner.hasNext())
            {
                this.capacity = (int) scanner.nextDouble();
            }

            // Read the Values and Weights
            while (scanner.hasNext()) {
                double val = scanner.nextDouble();

                if (scanner.hasNext()) {
                    double weight = scanner.nextDouble();

                    // Matches Item(weight, value)
                    this.items.add(new Item(weight, val));
                }
            }
            scanner.close();
        } catch (Exception e) {
            System.out.println("ERROR: Could not read " + filename);
            this.items = new ArrayList<>();
        }
    }
}