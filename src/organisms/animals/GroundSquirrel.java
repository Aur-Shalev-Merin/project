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
 * Small herbivore that prefers berries and wildflowers.
 */
public class GroundSquirrel extends Herbivore {

    /**
     * Creates a new ground squirrel at the specified position.
     * @param position The starting position
     * @param initialEnergy The starting energy level
     */
    public GroundSquirrel(Position position, double initialEnergy) {
        super(position, initialEnergy);

        // Set species-specific parameters
        this.maxEnergy = 20.0;
        this.metabolicRate = 0.3;  // Reduced from 0.5 for better survival
        this.reproductionAge = 3;  // Reduced from 4 for faster reproduction
        this.reproductionEnergyCost = 4.0;  // Reduced from 6.0 for easier reproduction
        this.reproductionEnergyThreshold = 11.0;  // Reduced from 13.0 for more frequent reproduction
        this.movementCost = 0.0;  // Removed - no longer moving
        this.visionRange = 6.0;

        // Set diet
        this.diet.add(Berries.class);
        this.diet.add(Wildflowers.class);
    }

    /**
     * Creates a new ground squirrel offspring.
     * @param position The position for the offspring
     * @return A new GroundSquirrel
     */
    @Override
    protected Animal createOffspring(Position position) {
        return new GroundSquirrel(position, 10.0);
    }

    /**
     * Gets the energy value when eaten by predators.
     * @return The energy value
     */
    @Override
    public double getEnergyValue() {
        return 8.0;
    }

    /**
     * Gets the species name.
     * @return "GroundSquirrel"
     */
    @Override
    public String getSpeciesName() {
        return "GroundSquirrel";
    }
}
