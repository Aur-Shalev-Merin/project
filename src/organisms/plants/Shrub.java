package organisms.plants;

import organisms.Plant;
import simulation.Position;

/**
 * Abstract base class for shrub species.
 * Shrubs have medium growth rate and often produce berries.
 */
public abstract class Shrub extends Plant {
    protected boolean berryProducing;  // Whether this shrub produces edible berries

    /**
     * Creates a new shrub at the specified position.
     * @param position The starting position
     * @param initialSize The starting size
     */
    public Shrub(Position position, double initialSize) {
        super(position, initialSize);
        this.berryProducing = false;  // Default no berries
        this.regenerationRate = 0.5;  // Medium regeneration
    }

    /**
     * Gets the energy value, higher if berry-producing.
     * @return The energy value when consumed
     */
    @Override
    public double getEnergyValue() {
        double baseValue = Math.min(currentSize * 0.3, currentSize);
        return berryProducing ? baseValue * 1.5 : baseValue;  // Berries are more nutritious
    }

    /**
     * Checks if this shrub produces berries.
     * @return true if berry-producing
     */
    public boolean isBerryProducing() {
        return berryProducing;
    }
}
