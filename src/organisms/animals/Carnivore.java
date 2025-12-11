package organisms.animals;

import organisms.Animal;
import organisms.Organism;
import simulation.Ecosystem;
import simulation.Position;
import java.util.List;

/**
 * Abstract base class for meat-eating animals.
 */
public abstract class Carnivore extends Animal {

    /**
     * Creates a new carnivore at the specified position.
     * @param position The starting position
     * @param initialEnergy The starting energy level
     */
    public Carnivore(Position position, double initialEnergy) {
        super(position, initialEnergy);
    }

    /**
     * Carnivores don't eat plants.
     * @param ecosystem The ecosystem containing this carnivore
     * @return false (carnivores don't graze)
     */
    @Override
    protected boolean graze(Ecosystem ecosystem) {
        return false;  // Carnivores don't eat plants
    }

    /**
     * Attempts to hunt prey using probability-based encounters.
     * @param ecosystem The ecosystem containing this carnivore
     * @return true if hunting was successful
     */
    @Override
    protected boolean hunt(Ecosystem ecosystem) {
        // Count available prey in diet
        int totalPrey = ecosystem.getPopulationCount(diet);
        if (totalPrey == 0) return false;

        // Calculate encounter probability based on prey density and carnivore competition
        int totalCarnivores = ecosystem.getTotalCarnivores();
        double carnivoreCompetition = Math.max(1, totalCarnivores);

        // Encounter rate: more prey = higher chance, more carnivores = lower chance
        // Base rate of 0.02 means 2% chance per carnivore per prey at 1:1 ratio
        // Much lower than herbivores (0.20) because hunting is harder than grazing
        // Further reduced from 0.05 to allow herbivore biodiversity
        double encounterRate = (totalPrey / carnivoreCompetition) * 0.02;
        encounterRate = Math.min(1.0, encounterRate); // Cap at 100%

        // Random encounter check
        if (ecosystem.getRandom().nextDouble() < encounterRate) {
            // Successfully encountered prey - get random prey of edible type
            Organism prey = ecosystem.getRandomOrganismOfType(diet);

            if (prey instanceof Animal && prey.isAlive()) {
                // Hunt success probability based on relative energy
                double huntSuccess = calculateHuntSuccess((Animal) prey);

                if (ecosystem.getRandom().nextDouble() < huntSuccess) {
                    eat(prey);
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Calculates the probability of a successful hunt based on relative size/energy.
     * @param prey The target prey animal
     * @return Probability of successful hunt (0.0 to 1.0)
     */
    protected double calculateHuntSuccess(Animal prey) {
        // Success based on relative energy/size
        double sizeRatio = this.energy / prey.getEnergy();
        return Math.min(0.9, 0.4 + sizeRatio * 0.3);  // Increased base from 0.3 to 0.4
    }
}