import java.io.FileNotFoundException;
import java.util.*;
import java.io.*;

public class ILSKnapsack {
    // [class] - created an object called 'Item'.
    static class Item{
        // [attributes] - this is what each Item has inside of it.
        double weight;
        double value;

        // [constructor]
        Item(double weight, double value)
        {
            this.weight = weight;
            this.value = value;
        }
    }

    /* [ArrayList] - this holds our possible items in a bag.
    Items can be zero e.g [] or any number e.g [1, 0, 0, 1].
    [ArrayList] can grow or shrink depending on the number of items inside.
     */
    private List<Item> items = new ArrayList<>();

    // [maxCapicity] - this is the weight limit for the bag.
    private int maxCapicity;

    // [random] - this is like a dice the climber roles to fit random items in the bag.
    private Random random;

    public ILSKnapsack(long seed)
    {
        this.random = new Random(seed);
    }

    //file loading
    public void loadInstance(String fileName){
        File file = new File(fileName);
        if(!file.exists())
        {
            System.out.println("current dir: "+System.getProperty("user.dir"));
            System.out.println("not found at: "+file.getAbsolutePath());
            return;
        }

        // clear the previous items list so we don't carry items from the last file.
        items.clear();

        /* [Scanner] - primarily used to obtain user input.
        In our case the input/s is the file Knapsack Instances e.g. f1_l-d_kp_10_269.
        We use [nextDouble()] just in case the file has decimal values.
         */
        try (Scanner fileScanner = new Scanner(new File(fileName))){

            // The format that Java reads a file is (Number of items), (Capacity)
            int numItems = (int) fileScanner.nextDouble();
            this.maxCapicity = (int) fileScanner.nextDouble();

            while(fileScanner.hasNextDouble())
            {
                double value = (int) fileScanner.nextDouble();
                double weight = (int) fileScanner.nextDouble();

                items.add(new Item(weight, value));
            }
        }catch (FileNotFoundException e)
        {
            System.out.println("File not found.");
        }
    }

    // Calculate the total value of the solution.
    public double evaluate(int[] solution)
    {
        double totalWeight = 0;
        double totalValue = 0;

        for(int i = 0; i < solution.length; i++  )
        {
            if(solution[i] == 1)
            {
                totalWeight += items.get(i).weight;
                totalValue += items.get(i).value;
            }
        }
        return (totalWeight <= maxCapicity) ? totalValue : -1;
    }

    /*
    The Local Climb - This is the "Hill Climbing" logic.
    [int[] current] - This is like the climber's map,
    1 means the item is in the bag; a 0 means it's left behind.
    */
    public int[] localSearch(int[] current)
    {
        boolean improved = true;
        int[] best = current.clone();

        /*
        This is the climb.
        The climber tries to add more items in the bag, flipping 0 to a 1,
        if the bag is not too heavy they keep the item.
        They keep doing this until they can't find a single way to make the bag better(Local Optimum).
         */
        while (improved)
        {
            improved = false;
            for (int i = 0; i < best.length; i++)
            {
                // flip bit
                best[i] = 1 -  best[i];

                if(evaluate(best) > evaluate(current))
                {
                    current = best.clone();
                    improved = true;
                }
                else
                {
                    // Revert if not better
                    best[i] = 1 - best[i];
                }
            }
        }
        return current;
    }

    /*
    Perturbation
    The climber needs to jump to another mountain to find new items.
    [strength] - this is the percentage with which the climber changes the items in the bag,
    e.g. 0.10 means 10% of the items are flipped in the bag.
    * */
    public int[] perturb (int[] solution, double strength)
    {
        if(solution.length == 0) return solution;

        int[] copy = solution.clone();
        int numFlips = (int) Math.max(1, solution.length * strength);

        for(int i = 0; i < numFlips; i++)
        {
            int index = random.nextInt(solution.length);
            copy[index] = 1 - copy[index];

            copy[index] = 1 - copy[index];
        }
        return copy;
    }

    /* Run the Climb and the Perturbation.
    This runs the climb and perturbation n number of times e.g. 1000.
    It keeps a record [sStar] of the best bag (best value) out of the runs.
     */
    public int[] solve(int iterations, double strength)
    {
        // Start counter
        long startTime = System.currentTimeMillis();

        // Initial solution
        int[] s = new int[items.size()];
        int[] sStar = localSearch(s);

        for(int i = 0; i < iterations; i++)
        {
            // Perturbation - we run the "jump to new mountain".
            int[] sPrime = perturb(sStar, strength);

            // Local search - we find the best bag in that mountain.
            int[] sPrimeStar = localSearch(sPrime);

            // Acceptance criterion - we check if the new bag is better than the current bag.
            if(evaluate(sPrimeStar) > evaluate(sStar))
            {
                sStar = sPrimeStar;
            }
        }

        //see the array
        //System.out.println("Items selected: " + Arrays.toString(sStar));

        return sStar;
    }

    //main
    public static void main(String[] args)
    {
        Scanner scanner = new Scanner(System.in);

        // We need a seed (random) value.
        System.out.println("Enter seed value: ");
        long seed = scanner.nextLong();

        // List of the files
        String[] files = {
                "f1_l-d_kp_10_269", "f2_l-d_kp_20_878", "f3_l-d_kp_4_20",
                "f4_l-d_kp_4_11", "f5_l-d_kp_15_375", "f6_l-d_kp_10_60",
                "f7_l-d_kp_7_50", "f8_l-d_kp_23_10000", "f9_l-d_kp_5_80",
                "f10_l-d_kp_20_879", "knapPI_1_100_1000_1"
        };

        // Table formatting
        System.out.println("\n" + "=".repeat(85));
        System.out.printf("%-25s | %-10s | %-10s | %-15s | %-15s%n",
                "Problem Instance", "Algorithm", "Seed", "Best Value", "Runtime (s)");
        System.out.println("-".repeat(85));

        for(String fileName : files)
        {
            ILSKnapsack app = new ILSKnapsack(seed);
            app.loadInstance("src/" + fileName);

            long start = System.currentTimeMillis();
            int[] result = app.solve(1000, 0.10);
            double time = (System.currentTimeMillis() - start) / 1000.0;

            /* print the rows
            %.2f tells Java to show the best value with 2 decimal places
             */
            System.out.printf("%-25s | %-10s | %-10s | %-15.0f | %-15.3f%n",
                    fileName, "ILS", seed, app.evaluate(result), time);
        }
        //table footer
        System.out.println("=".repeat(85));
    }
}