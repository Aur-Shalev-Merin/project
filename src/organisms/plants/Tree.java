package organisms.plants;

import organisms.Plant;
import simulation.Position;

/**
 * Abstract base class for tree species.
 * Trees are slow-growing, long-lived, and have high energy value.
 */
public abstract class Tree extends Plant {
    protected double woodDensity;  // Affects energy value and growth

    /**
     * Creates a new tree at the specified position.
     * @param position The starting position
     * @param initialSize The starting size
     */
    public Tree(Position position, double initialSize) {
        super(position, initialSize);
        this.woodDensity = 0.6;  // Default wood density
        this.regenerationRate = 0.2;  // Slow regeneration
    }

    /**
     * Gets the energy value based on wood density and size.
     * Trees provide more concentrated energy.
     * @return The energy value when consumed
     */
    @Override
    public double getEnergyValue() {
        return Math.min(currentSize * 0.3 * woodDensity, currentSize);
    }
}
