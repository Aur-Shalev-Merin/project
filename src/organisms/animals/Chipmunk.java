package organisms.animals;

import organisms.Animal;
import organisms.Organism;
import organisms.Plant;
import organisms.plants.Berries;
import organisms.plants.Wildflowers;
import simulation.Ecosystem;
import simulation.Position;
import java.util.List;

/**
 * Small, fast herbivore that feeds on berries and wildflowers.
 */
public class Chipmunk extends Herbivore {

    /**
     * Creates a new chipmunk at the specified position.
     * @param position The starting position
     * @param initialEnergy The starting energy level
     */
    public Chipmunk(Position position, double initialEnergy) {
        super(position, initialEnergy);

        // Set species-specific parameters
        this.maxEnergy = 15.0;
        this.metabolicRate = 0.3;  // Reduced from 0.6 (was unsustainably high for probability-based system!)
        this.reproductionAge = 4;  // Reduced from 6 to allow faster reproduction
        this.reproductionEnergyCost = 5.0;  // Reduced from 6.0
        this.reproductionEnergyThreshold = 10.0;  // Reduced from 12.0 to make reproduction achievable
        this.movementCost = 0.0;  // Removed - no longer moving
        this.visionRange = 5.0;

        // Set diet
        this.diet.add(Berries.class);
        this.diet.add(Wildflowers.class);
    }

    /**
     * Creates a new chipmunk offspring.
     * @param position The position for the offspring
     * @return A new Chipmunk
     */
    @Override
    protected Animal createOffspring(Position position) {
        return new Chipmunk(position, 8.0);
    }

    /**
     * Gets the energy value when eaten by predators.
     * @return The energy value
     */
    @Override
    public double getEnergyValue() {
        return 6.0;
    }

    /**
     * Gets the species name.
     * @return "Chipmunk"
     */
    @Override
    public String getSpeciesName() {
        return "Chipmunk";
    }
}
