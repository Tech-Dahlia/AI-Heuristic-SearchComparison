import java.io.FileNotFoundException;
import java.util.*;
import java.io.*;

public class IteratedLocalSearchAlgorithm {

    /* [ArrayList] - this holds our possible items in a bag.
    Items can be zero e.g [] or any number e.g [1, 0, 0, 1].
    [ArrayList] can grow or shrink depending on the number of items inside.
     */
    private List<Item> items = new ArrayList<>();

    // [maxCapicity] - this is the weight limit for the bag.
    private int maxCapicity;

    // [random] - this is like a dice the climber roles to fit random items in the bag.
    private Random random;

    public IteratedLocalSearchAlgorithm(long seed)
    {
        this.random = new Random(seed);
    }

    //file loading
    public void loadInstance(String fileName) {
        FileParser parser = new FileParser();
        parser.loadFile(fileName);

        this.items = parser.items;
        this.maxCapicity = parser.capacity;
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
                totalWeight += items.get(i).getWeight();
                totalValue += items.get(i).getValue();
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

}