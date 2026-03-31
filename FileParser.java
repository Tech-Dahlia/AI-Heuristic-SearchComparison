import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

public class FileParser {
    public int numItems;
    public int capacity;
    public ArrayList<Item> items;

    public void loadFile(String filename) {
        try {
            File file = new File(filename);
            Scanner scanner = new Scanner(file);
            scanner.useDelimiter("\\s+");

            this.items = new ArrayList<>();

            if (scanner.hasNextInt()) this.numItems = scanner.nextInt();
            if (scanner.hasNextInt()) this.capacity = scanner.nextInt();

            while (scanner.hasNextInt()) {
                int val = scanner.nextInt();
                if (scanner.hasNextInt()) {
                    int weight = scanner.nextInt();
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