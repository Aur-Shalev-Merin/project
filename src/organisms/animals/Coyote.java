package organisms.animals;

import organisms.Animal;
import organisms.Organism;
import simulation.Ecosystem;
import simulation.Position;
import java.util.List;

/**
 * Medium-sized predator that hunts deer and small mammals.
 */
public class Coyote extends Carnivore {

    /**
     * Creates a new coyote at the specified position.
     * @param position The starting position
     * @param initialEnergy The starting energy level
     */
    public Coyote(Position position, double initialEnergy) {
        super(position, initialEnergy);

        // Set species-specific parameters
        this.maxEnergy = 100.0;
        this.metabolicRate = 0.5;  // Increased from 0.3 to control population
        this.reproductionAge = 24;
        this.reproductionEnergyCost = 35.0;  // Increased from 30.0
        this.reproductionEnergyThreshold = 75.0;  // Increased from 65.0 to slow reproduction
        this.movementCost = 0.0;  // Removed - no longer moving
        this.visionRange = 20.0;  // Increased from 15.0 to help find prey

        // Set diet - hunts deer, rabbits, and small mammals
        this.diet.add(Deer.class);
        this.diet.add(Bunny.class);
        this.diet.add(FieldMouse.class);
        this.diet.add(GroundSquirrel.class);
    }

    /**
     * Calculates hunt success based on prey type.
     * Higher success rate for small prey, lower for deer.
     * @param prey The target prey animal
     * @return Probability of successful hunt (0.0 to 1.0)
     */
    @Override
    protected double calculateHuntSuccess(Animal prey) {
        // Higher success for small prey, lower for deer
        if (prey instanceof Deer) {
            return 0.3;  // Deer are harder to catch
        } else {
            return 0.7;  // Small mammals are easier
        }
    }

    /**
     * Creates a new coyote offspring.
     * @param position The position for the offspring
     * @return A new Coyote
     */
    @Override
    protected Animal createOffspring(Position position) {
        return new Coyote(position, 50.0);  // Offspring start with half max energy
    }

    /**
     * Gets the energy value when eaten by larger predators.
     * @return The energy value
     */
    @Override
    public double getEnergyValue() {
        return 50.0;  // Energy provided when eaten by larger predators
    }

    /**
     * Gets the species name.
     * @return "Coyote"
     */
    @Override
    public String getSpeciesName() {
        return "Coyote";
    }
}
