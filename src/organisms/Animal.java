package organisms;

import simulation.Ecosystem;
import simulation.Position;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Abstract base class for all mobile consumers.
 */
public abstract class Animal extends Organism {
    protected double maxEnergy;                          // Maximum energy capacity
    protected double metabolicRate;                      // Energy lost per time step
    protected int reproductionAge;                       // Minimum age to reproduce
    protected double reproductionEnergyCost;             // Energy cost to reproduce
    protected double reproductionEnergyThreshold;        // Minimum energy to reproduce
    protected double movementCost;                       // Energy cost per move
    protected double visionRange;                        // Detection radius
    protected List<Class<?>> diet;                       // What this animal can eat
    protected int reproductionCooldown;                  // Time steps between reproductions
    protected int timeSinceLastReproduction;             // Time since last reproduction

    /**
     * Creates a new animal at the specified position with initial energy.
     * @param position The starting position
     * @param initialEnergy The starting energy level
     */
    public Animal(Position position, double initialEnergy) {
        super(position, initialEnergy);
        this.diet = new ArrayList<>();
        this.reproductionCooldown = 25;  // Default cooldown
        this.timeSinceLastReproduction = 25;  // Start ready to reproduce
    }

    /**
     * Moves the animal based on its behavior.
     * No longer used in probability-based system, but kept for legacy compatibility.
     * @param ecosystem The ecosystem containing this animal
     */
    protected void move(Ecosystem ecosystem) {
        // Empty default implementation - movement no longer needed
    }

    /**
     * Attempts to hunt prey (for carnivores and omnivores).
     * @param ecosystem The ecosystem containing this animal
     * @return true if hunting was successful
     */
    protected abstract boolean hunt(Ecosystem ecosystem);

    /**
     * Attempts to graze on plants (for herbivores and omnivores).
     * @param ecosystem The ecosystem containing this animal
     * @return true if grazing was successful
     */
    protected abstract boolean graze(Ecosystem ecosystem);

    /**
     * Creates an offspring of the same species.
     * @param position The position for the offspring
     * @return A new Animal offspring
     */
    protected abstract Animal createOffspring(Position position);

    /**
     * Main update loop for animals.
     * @param ecosystem The ecosystem containing this animal
     */
    @Override
    public void update(Ecosystem ecosystem) {
        if (!isAlive) return;

        incrementAge();
        timeSinceLastReproduction++;
        metabolize();
        // Movement removed - probability-based encounters don't require spatial movement

        // Attempt to find food
        boolean foundFood = hunt(ecosystem);
        if (!foundFood) {
            graze(ecosystem);
        }

        checkSurvival();

        if (shouldReproduce()) {
            reproduce(ecosystem);
        }
    }

    /**
     * Loses energy due to metabolism.
     */
    protected void metabolize() {
        energy -= metabolicRate;
    }

    /**
     * Checks if animal has enough energy to survive.
     * Dies if energy is depleted.
     */
    protected void checkSurvival() {
        if (energy <= 0) {
            die();
        }
    }

    /**
     * Checks if animal is ready to reproduce.
     * @return true if animal meets reproduction conditions
     */
    protected boolean shouldReproduce() {
        return age >= reproductionAge &&
               energy >= reproductionEnergyThreshold &&
               timeSinceLastReproduction >= reproductionCooldown;
    }

    /**
     * Creates offspring at the current position.
     * @param ecosystem The ecosystem to add offspring to
     * @return true if reproduction was successful
     */
    public boolean reproduce(Ecosystem ecosystem) {
        if (!shouldReproduce()) return false;

        // Only 25% chance of reproduction per time step when conditions are met
        if (ecosystem.getRandom().nextDouble() > 0.25) return false;

        // Create offspring at current position
        Animal offspring = createOffspring(position);
        ecosystem.addOrganism(offspring);

        // Pay energy cost
        modifyEnergy(-reproductionEnergyCost);

        // Reset reproduction timer
        timeSinceLastReproduction = 0;

        return true;
    }

    /**
     * Wrapper for abstract reproduce method compatibility.
     * @return true if reproduction was successful
     */
    @Override
    public boolean reproduce() {
        // This should not be called directly for animals
        // Use reproduce(Ecosystem ecosystem) instead
        return false;
    }

    /**
     * Consumes food and gains energy.
     * @param food The organism to consume
     */
    protected void eat(Organism food) {
        double energyGained = food.getEnergyValue();

        // Partial consumption for plants
        if (food instanceof Plant) {
            Plant plant = (Plant) food;
            plant.getConsumed(energyGained);
        } else {
            food.die();
        }

        // Gain energy (with conversion efficiency)
        modifyEnergy(energyGained * 0.7);  // 70% efficiency

        // Cap at max energy
        energy = Math.min(energy, maxEnergy);
    }

    /**
     * Handles death of the animal.
     */
    @Override
    public void die() {
        isAlive = false;
    }

    /**
     * Generates a random nearby position for movement.
     * @param ecosystem The ecosystem (for bounds checking)
     * @return A new random position within bounds
     */
    protected Position randomMove(Ecosystem ecosystem) {
        Random rand = ecosystem.getRandom();
        double angle = rand.nextDouble() * 2 * Math.PI;
        double distance = rand.nextDouble() * 2.0;  // 0-2 units

        double newX = position.getX() + distance * Math.cos(angle);
        double newY = position.getY() + distance * Math.sin(angle);

        // Keep in bounds
        newX = Math.max(0, Math.min(newX, ecosystem.getGridWidth()));
        newY = Math.max(0, Math.min(newY, ecosystem.getGridHeight()));

        return new Position(newX, newY);
    }
}
