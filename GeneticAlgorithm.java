import java.io.File;

/**
 * COS314 Artificial Intelligence - Assignment 2
 * ---------------------------------------------------------
 * GENETIC ALGORITHM SPECIALIST
 * * This class contains the logic for the GA. It is called by Main.java.
 **/
public class GeneticAlgorithm {

    private long seed;

    public GeneticAlgorithm(long seed) {
        this.seed = seed;
    }

    // This is the main "Work" method that Main.java calls
    public void executeGA(String fileName, double knownOpt) {
        long startTime = System.nanoTime();

        /* --- GA LOGIC GOES HERE ---
        For now, I'll keep your simulation logic.
        When you finish the actual GA, I'll just replace this part.
        */
        try { Thread.sleep(45); } catch (InterruptedException e) {}
        double result = knownOpt * 0.98;
        // ---------------------------

        long endTime = System.nanoTime();
        double runtime = (endTime - startTime) / 1_000_000_000.0;

        System.out.printf("%-25s | %-10s | %-15d | %-15.4f | %-15.4f | %-15.6f\n",
                fileName, "GA", seed, result, knownOpt, runtime);
    }
}