import java.util.ArrayList;
import java.util.Random;

public class GeneticAlgorithm {
    private ProblemInstance instance;

    // Total individuals in the 'system'
    private int populationSize = 100;

    // Convergence limit
    private int generations = 1000;

    // Probability of stochastic genetic drift (0.10 = 10%)
    private double mutationRate = 0.10;

    // Selective pressure intensity
    private int tournamentSize = 5;
    private Random rand;

    public GeneticAlgorithm(ProblemInstance instance, long seed) {
        this.instance = instance;
        this.rand = new Random(seed);
    }

    // Start the primary evolutionary cycle
    public double solve() {

        // Create an initial diverse population of random chromosomes
        Chromosome[] population = new Chromosome[populationSize];
        for (int i = 0; i < populationSize; i++) {
            population[i] = new Chromosome(instance.numItems, rand);
            population[i].calculateFitness(instance);
        }

        // Evolution loop
        for (int g = 0; g < generations; g++) {
            Chromosome[] nextGeneration = new Chromosome[populationSize];

            // ELITISM: Preservation of the absolute best individual.
            nextGeneration[0] = getBest(population).copy();

            // Reproduction
            for (int i = 1; i < populationSize; i++) {

                // Choose parents based on fitness competition
                Chromosome parent1 = tournamentSelection(population);
                Chromosome parent2 = tournamentSelection(population);

                // Create offspring using single-point crossover
                Chromosome child = crossover(parent1, parent2);

                // Apply mutation to introduce new genetic material
                mutate(child);

                // Map the genes to the knapsack value
                child.calculateFitness(instance);

                nextGeneration[i] = child;
            }
            // Generational replacement
            population = nextGeneration;
        }
        return getBest(population).fitness;
    }

    // Selects an individual by comparing a random subset of the population
    private Chromosome tournamentSelection(Chromosome[] population) {
        Chromosome best = null;
        for (int i = 0; i < tournamentSize; i++) {
            Chromosome contestant = population[rand.nextInt(population.length)];
            if (best == null || contestant.fitness > best.fitness) {
                best = contestant;
            }
        }
        return best;
    }

    // Single-Point Crossover: Swaps genetic segments between two parents
    private Chromosome crossover(Chromosome p1, Chromosome p2) {
        Chromosome child = new Chromosome(instance.numItems, rand);
        int cutPoint = (instance.numItems <= 1) ? 0 : rand.nextInt(instance.numItems);
        for (int i = 0; i < instance.numItems; i++) {
            child.genes[i] = (i < cutPoint) ? p1.genes[i] : p2.genes[i];
        }
        return child;
    }

    // Randomly alters genes to maintain population diversity
    private void mutate(Chromosome c) {
        for (int i = 0; i < c.genes.length; i++) {
            if (rand.nextDouble() < mutationRate) {
                c.genes[i] = (c.genes[i] == 0) ? 1 : 0;
            }
        }
    }

    // Acceptance criterion
    private Chromosome getBest(Chromosome[] population) {
        Chromosome best = population[0];
        for (Chromosome c : population) {
            if (c.fitness > best.fitness) best = c;
        }
        return best;
    }
}

/**
 * Helper Class: ProblemInstance
 */
class ProblemInstance {
    public ArrayList<Item> items;
    public int capacity;
    public int numItems;

    public ProblemInstance(ArrayList<Item> items, int capacity) {
        this.items = items;
        this.capacity = capacity;
        this.numItems = items.size();
    }
}

/**
 * Helper Class: Chromosome
 */
class Chromosome {
    public int[] genes;
    public double fitness;
    private Random rand;

    public Chromosome(int numItems, Random rand) {
        this.rand = rand;
        this.genes = new int[numItems];
        for (int i = 0; i < numItems; i++) {
            this.genes[i] = (rand.nextDouble() < 0.1) ? 1 : 0;
        }
    }

    public void calculateFitness(ProblemInstance instance) {
        double totalValue = 0;
        int totalWeight = 0;

        for (int i = 0; i < genes.length; i++) {
            if (genes[i] == 1) {
                totalValue += instance.items.get(i).getValue();
                totalWeight += instance.items.get(i).getWeight();
            }
        }

        // If the solution is invalid (overweight), randomly prune items until it satisfies the capacity constraint
        // It highlights that you aren't just "killing" bad solutions, but "fixing" them
        while (totalWeight > instance.capacity) {
            int dropIndex = rand.nextInt(genes.length);
            if (genes[dropIndex] == 1) {
                totalWeight -= instance.items.get(dropIndex).getWeight();
                totalValue -= instance.items.get(dropIndex).getValue();
                genes[dropIndex] = 0;
            }
        }
        this.fitness = totalValue;
    }

    public Chromosome copy() {
        Chromosome copy = new Chromosome(this.genes.length, this.rand);
        System.arraycopy(this.genes, 0, copy.genes, 0, this.genes.length);
        copy.fitness = this.fitness;
        return copy;
    }
}