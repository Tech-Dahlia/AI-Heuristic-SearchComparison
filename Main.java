import java.io.File;
import java.util.Arrays;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * COS314 Artificial Intelligence - Assignment 2
 * ---------------------------------------------------------
 * UNIVERSAL Main Entry Point for the Knapsack Comparison.
 *
 * This class serves as the controller for the experimental
 * evaluation of Metaheuristic algorithms (GA and ILS).
 * It handles dynamic file I/O, natural sorting of instances,
 * and formatted tabular output for comparative analysis.
 *
 * Team member 1: Nthabiseng Mphalo
 * Team member 2: Allen Mzobe
 **/

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // --- 1. FOLDER SETUP ---
        // Locate the "Knapsack Instances" folder with all the instances
        File dataFolder = new File("Knapsack Instances");
        if (!dataFolder.exists()) {
            System.out.println("Folder not found! Please ensure 'Knapsack Instances' exists.");
            return;
        }

        // --- 2. FILE FILTERING & NATURAL SORTING ---
        // Checks if the knapsack instances files contain the prefix "f" or "knap"
        File[] instanceFiles = dataFolder.listFiles((dir, name) ->
                name.toLowerCase().contains("f") || name.toLowerCase().contains("knap")
        );

        // Ensures files are processed in logical numerical order (f1, f2... f10)
        Arrays.sort(instanceFiles, (f1, f2) -> extractInt(f1.getName()) - extractInt(f2.getName()));

        // --- 3. USER INTERFACE ---
        System.out.println("==========================================================================");
        System.out.println("                COS314 Assignment 2: Knapsack Solver                      ");
        System.out.println("==========================================================================");
        System.out.println("Select Metaheuristic Engine:");
        System.out.println("1. GA: Genetic Algorithm (Population-Based)");
        System.out.println("2. ILS: Iterated Local Search (Trajectory-Based)");
        System.out.println("3. Run Both (Comparison)");
        System.out.print("Choice: ");
        int mode = scanner.nextInt();

        // The seed ensures all stochastic decisions (mutations, kicks) are reproducible
        System.out.print("Enter seed value: ");
        long seed = scanner.nextLong();

        printHeader();

        // --- 4. THE EXECUTION LOOP ---
        // Iterates through each problem instance to perform the heuristic search
        for (File file : instanceFiles) {
            String fileName = file.getName();
            String filePath = file.getPath();

            // Get the target optimum to calculate the 'Optimality Gap' during evaluation.
            double opt = getOptimumForInstance(fileName);

            // GENETIC ALGORITHM (GA) logic
            if (mode == 1 || mode == 3) {
                // Prepare the data for the GA
                FileParser parser = new FileParser();
                parser.loadFile(filePath);
                ProblemInstance instance = new ProblemInstance(parser.items, parser.capacity);

                // Start and run the GA
                GeneticAlgorithm ga = new GeneticAlgorithm(instance, seed);

                long start = System.nanoTime();
                double result = ga.solve();
                double runtime = (System.nanoTime() - start) / 1_000_000_000.0;

                System.out.printf("%-25s | %-10s | %-15d | %-15.4f | %-15.4f | %-15.6f\n",
                        fileName, "GA", seed, result, opt, runtime);
                //System.out.println();
            }

            // Iterated Local Search (ILS) logic
            if (mode == 2 || mode == 3) {
                // Start and run the ILS
                IteratedLocalSearchAlgorithm ils = new IteratedLocalSearchAlgorithm(seed);
                ils.loadInstance(filePath);

                long start = System.nanoTime();
                int[] result = ils.solve(1000, 0.10); // 1000 iterations, 10% kick
                double bestValue = ils.evaluate(result);
                double runtime = (System.nanoTime() - start) / 1_000_000_000.0;

                System.out.printf("%-25s | %-10s | %-15d | %-15.4f | %-15.4f | %-15.6f\n",
                        fileName, "ILS", seed, bestValue, opt, runtime);
            }
            // Prints a blank line after each problem instance
            System.out.println();
        }
        // Table width
        System.out.println("-".repeat(110));
    }

    // Filenames for correct sorting using Regex
    private static int extractInt(String s) {
        Pattern p = Pattern.compile("f(\\d+)");
        Matcher m = p.matcher(s.toLowerCase());
        if (m.find()) return Integer.parseInt(m.group(1));
        // Non-prefixed files (like knapPI) are pushed to the end.
        return 999;
    }

    // Format the table header
    private static void printHeader() {
        System.out.println("\n" + "-".repeat(110));
        System.out.printf("%-25s | %-10s | %-15s | %-15s | %-15s | %-15s\n",
                "Problem Instance", "Algorithm", "Seed Value", "Best Solution", "Known Optimum", "Runtime (s)");
        System.out.println("-".repeat(110));
    }

    // Truth-Table containing known optima for performance verification
    private static double getOptimumForInstance(String instanceName) {
        String name = instanceName.toLowerCase().trim();
        if (name.startsWith("f1"))  return 295.0;
        if (name.startsWith("f2"))  return 1024.0;
        if (name.startsWith("f3"))  return 35.0;
        if (name.startsWith("f4"))  return 23.0;
        if (name.startsWith("f5"))  return 481.0694;
        if (name.startsWith("f6"))  return 52.0;
        if (name.startsWith("f7"))  return 107.0;
        if (name.startsWith("f8"))  return 9767.0;
        if (name.startsWith("f9"))  return 130.0;
        if (name.startsWith("f10")) return 1025.0;
        if (name.contains("knappi")) return 9147.0;
        return 0.0;
    }
}