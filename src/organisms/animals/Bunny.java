package organisms.animals;

import organisms.Animal;
import organisms.Organism;
import organisms.Plant;
import organisms.plants.Wildflowers;
import simulation.Ecosystem;
import simulation.Position;
import java.util.List;

/**
 * Small, fast-reproducing herbivore that feeds on wildflowers.
 */
public class Bunny extends Herbivore {

    /**
     * Creates a new bunny at the specified position.
     * @param position The starting position
     * @param initialEnergy The starting energy level
     */
    public Bunny(Position position, double initialEnergy) {
        super(position, initialEnergy);

        // Set species-specific parameters
        this.maxEnergy = 40.0;
        this.metabolicRate = 0.2;  // Reduced from 0.3 to help survival
        this.reproductionAge = 4;  // Reduced from 6 for faster reproduction
        this.reproductionEnergyCost = 8.0;  // Reduced from 10.0 for easier reproduction
        this.reproductionEnergyThreshold = 18.0;  // Reduced from 22.0 for more frequent reproduction
        this.movementCost = 0.0;  // Removed - no longer moving
        this.visionRange = 15.0;

        // Set diet - only Wildflowers for Phase 2
        this.diet.add(Wildflowers.class);
    }

    /**
     * Creates a new bunny offspring.
     * @param position The position for the offspring
     * @return A new Bunny
     */
    @Override
    protected Animal createOffspring(Position position) {
        return new Bunny(position, 20.0);  // Offspring start with half max energy
    }

    /**
     * Gets the energy value when eaten by predators.
     * @return The energy value
     */
    @Override
    public double getEnergyValue() {
        return 25.0;  // Energy provided when eaten by predators
    }

    /**
     * Gets the species name.
     * @return "Bunny"
     */
    @Override
    public String getSpeciesName() {
        return "Bunny";
    }
}
