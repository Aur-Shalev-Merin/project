package organisms.animals;

import organisms.Animal;
import organisms.Organism;
import organisms.Plant;
import simulation.Ecosystem;
import simulation.Position;
import java.util.List;

/**
 * Abstract base class for plant-eating animals.
 */
public abstract class Herbivore extends Animal {

    /**
     * Creates a new herbivore at the specified position.
     * @param position The starting position
     * @param initialEnergy The starting energy level
     */
    public Herbivore(Position position, double initialEnergy) {
        super(position, initialEnergy);
    }

    /**
     * Herbivores don't hunt animals.
     * @param ecosystem The ecosystem containing this herbivore
     * @return false (herbivores don't hunt)
     */
    @Override
    protected boolean hunt(Ecosystem ecosystem) {
        return false;  // Herbivores don't hunt
    }

    /**
     * Attempts to graze on plants using probability-based encounters.
     * @param ecosystem The ecosystem containing this herbivore
     * @return true if grazing was successful
     */
    @Override
    protected boolean graze(Ecosystem ecosystem) {
        // Count available plants in diet
        int totalPlants = ecosystem.getPopulationCount(diet);
        if (totalPlants == 0) return false;

        // Calculate encounter probability based on plant density and herbivore competition
        int totalHerbivores = ecosystem.getTotalHerbivores();
        double herbivoreCompetition = Math.max(1, totalHerbivores);

        // Encounter rate: more plants = higher chance, more herbivores = lower chance
        // Base rate of 0.20 means 20% chance per herbivore per plant at 1:1 ratio
        // Increased from 0.15 to help herbivores survive better
        double encounterRate = (totalPlants / herbivoreCompetition) * 0.20;
        encounterRate = Math.min(1.0, encounterRate); // Cap at 100%

        // Random encounter check
        if (ecosystem.getRandom().nextDouble() < encounterRate) {
            // Successfully encountered food - get random plant of edible type
            Organism food = ecosystem.getRandomOrganismOfType(diet);

            if (food instanceof Plant && food.isAlive()) {
                eat(food);
                return true;
            }
        }

        return false;
    }
}
