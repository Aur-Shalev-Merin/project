package organisms.animals;

import organisms.Animal;
import organisms.Organism;
import organisms.Plant;
import organisms.plants.Wildflowers;
import organisms.plants.Aspen;
import organisms.plants.Spruce;
import organisms.plants.Berries;
import simulation.Ecosystem;
import simulation.Position;
import java.util.List;

/**
 * Large herbivore with good predator detection and varied plant diet.
 */
public class Deer extends Herbivore {

    /**
     * Creates a new deer at the specified position.
     * @param position The starting position
     * @param initialEnergy The starting energy level
     */
    public Deer(Position position, double initialEnergy) {
        super(position, initialEnergy);

        // Set species-specific parameters
        this.maxEnergy = 80.0;
        this.metabolicRate = 0.6;  // Reduced from 1.5 (was 7.5x higher than Bunny!)
        this.reproductionAge = 12;  // Reduced from 18 for faster reproduction
        this.reproductionEnergyCost = 18.0;  // Reduced from 25.0 for easier reproduction
        this.reproductionEnergyThreshold = 45.0;  // Reduced from 55.0 for more frequent reproduction
        this.movementCost = 0.0;  // Removed - no longer moving
        this.visionRange = 20.0;  // Good predator detection

        // Set diet - various plants
        this.diet.add(Wildflowers.class);
        this.diet.add(Aspen.class);
        this.diet.add(Spruce.class);
        this.diet.add(Berries.class);
    }

    /**
     * Creates a new deer offspring.
     * @param position The position for the offspring
     * @return A new Deer
     */
    @Override
    protected Animal createOffspring(Position position) {
        return new Deer(position, 40.0);  // Offspring start with half max energy
    }

    /**
     * Gets the energy value when eaten by predators.
     * @return The energy value
     */
    @Override
    public double getEnergyValue() {
        return 40.0;  // Large prey, high energy value
    }

    /**
     * Gets the species name.
     * @return "Deer"
     */
    @Override
    public String getSpeciesName() {
        return "Deer";
    }
}
