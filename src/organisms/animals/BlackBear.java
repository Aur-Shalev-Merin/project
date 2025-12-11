package organisms.animals;

import organisms.Animal;
import organisms.Organism;
import organisms.Plant;
import organisms.plants.Berries;
import organisms.plants.Wildflowers;
import simulation.Ecosystem;
import simulation.Position;
import java.util.Arrays;
import java.util.List;

/**
 * Large omnivore that strongly prefers berries but will hunt when necessary.
 */
public class BlackBear extends Omnivore {

    /**
     * Creates a new black bear at the specified position.
     * @param position The starting position
     * @param initialEnergy The starting energy level
     */
    public BlackBear(Position position, double initialEnergy) {
        super(position, initialEnergy);

        // Set species-specific parameters
        this.maxEnergy = 150.0;
        this.metabolicRate = 0.8;  // Reduced from 2.5 (was unsustainably high!)
        this.reproductionAge = 36;
        this.reproductionEnergyCost = 45.0;
        this.reproductionEnergyThreshold = 100.0;
        this.movementCost = 0.0;  // Removed - no longer moving
        this.visionRange = 10.0;

        // Set diet - APEX PREDATOR: eats everything!
        // Plants
        this.diet.add(Berries.class);
        this.diet.add(Wildflowers.class);
        this.diet.add(organisms.plants.Aspen.class);
        this.diet.add(organisms.plants.Spruce.class);

        // Herbivores
        this.diet.add(Bunny.class);
        this.diet.add(Deer.class);
        this.diet.add(FieldMouse.class);
        this.diet.add(GroundSquirrel.class);
        this.diet.add(Chipmunk.class);

        // Carnivores (apex predator!)
        this.diet.add(Fox.class);
        this.diet.add(Coyote.class);
        this.diet.add(BlackBear.class);  // Even cannibalism (realistic for bears!)
    }

    /**
     * Creates a new black bear offspring.
     * @param position The position for the offspring
     * @return A new BlackBear
     */
    @Override
    protected Animal createOffspring(Position position) {
        return new BlackBear(position, 75.0);  // Offspring start with half max energy
    }

    /**
     * Gets the energy value. Black bears are large and provide much energy.
     * @return The energy value
     */
    @Override
    public double getEnergyValue() {
        return 80.0;  // High energy value
    }

    /**
     * Gets the species name.
     * @return "BlackBear"
     */
    @Override
    public String getSpeciesName() {
        return "BlackBear";
    }
}
