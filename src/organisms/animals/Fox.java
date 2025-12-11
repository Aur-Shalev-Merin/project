package organisms.animals;

import organisms.Animal;
import organisms.Organism;
import simulation.Ecosystem;
import simulation.Position;
import java.util.List;

/**
 * Small predator that hunts bunnies and other small herbivores.
 */
public class Fox extends Carnivore {

    /**
     * Creates a new fox at the specified position.
     * @param position The starting position
     * @param initialEnergy The starting energy level
     */
    public Fox(Position position, double initialEnergy) {
        super(position, initialEnergy);

        // Set species-specific parameters
        this.maxEnergy = 60.0;
        this.metabolicRate = 0.15;  // Reduced from 0.4 to help survival
        this.reproductionAge = 12;  // Reduced from 15 to enable faster reproduction
        this.reproductionEnergyCost = 20.0;
        this.reproductionEnergyThreshold = 45.0;  // Reduced from 50.0 to enable reproduction
        this.movementCost = 0.0;  // Removed - no longer moving
        this.visionRange = 20.0;  // Increased from 15.0 to help find prey

        // Set diet - hunts small herbivores
        this.diet.add(Bunny.class);
        this.diet.add(FieldMouse.class);
        this.diet.add(Chipmunk.class);
    }

    /**
     * Creates a new fox offspring.
     * @param position The position for the offspring
     * @return A new Fox
     */
    @Override
    protected Animal createOffspring(Position position) {
        return new Fox(position, 35.0);  // Offspring start with more energy to survive
    }

    /**
     * Gets the energy value when eaten by larger predators.
     * @return The energy value
     */
    @Override
    public double getEnergyValue() {
        return 30.0;  // Energy provided when eaten by larger predators
    }

    /**
     * Gets the species name.
     * @return "Fox"
     */
    @Override
    public String getSpeciesName() {
        return "Fox";
    }
}