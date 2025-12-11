package organisms.animals;

import organisms.Animal;
import organisms.Organism;
import organisms.Plant;
import organisms.plants.Wildflowers;
import organisms.plants.Berries;
import simulation.Ecosystem;
import simulation.Position;
import java.util.List;

/**
 * Smallest herbivore with fastest reproduction rate.
 */
public class FieldMouse extends Herbivore {

    /**
     * Creates a new field mouse at the specified position.
     * @param position The starting position
     * @param initialEnergy The starting energy level
     */
    public FieldMouse(Position position, double initialEnergy) {
        super(position, initialEnergy);

        // Set species-specific parameters
        this.maxEnergy = 12.0;
        this.metabolicRate = 0.25;  // Reduced from 0.3 for better survival
        this.reproductionAge = 2;  // Reduced from 3 for fastest reproduction
        this.reproductionEnergyCost = 3.0;  // Reduced from 4.0 for easier reproduction
        this.reproductionEnergyThreshold = 7.0;  // Reduced from 8.0 for more frequent reproduction
        this.movementCost = 0.0;  // Removed - no longer moving
        this.visionRange = 4.0;

        // Set diet
        this.diet.add(Wildflowers.class);
        this.diet.add(Berries.class);
    }

    /**
     * Creates a new field mouse offspring.
     * @param position The position for the offspring
     * @return A new FieldMouse
     */
    @Override
    protected Animal createOffspring(Position position) {
        return new FieldMouse(position, 6.0);
    }

    /**
     * Gets the energy value when eaten by predators.
     * @return The energy value
     */
    @Override
    public double getEnergyValue() {
        return 5.0;
    }

    /**
     * Gets the species name.
     * @return "FieldMouse"
     */
    @Override
    public String getSpeciesName() {
        return "FieldMouse";
    }
}
