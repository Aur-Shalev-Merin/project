package organisms.plants;

import organisms.Plant;
import simulation.Position;

/**
 * Abstract base class for groundcover plants.
 * Groundcover grows fast, stays low, and regenerates quickly.
 */
public abstract class Groundcover extends Plant {
    protected double coverageRadius;  // Area covered by the plant

    /**
     * Creates a new groundcover plant at the specified position.
     * @param position The starting position
     * @param initialSize The starting size
     */
    public Groundcover(Position position, double initialSize) {
        super(position, initialSize);
        this.coverageRadius = 2.0;  // Default coverage radius
        this.regenerationRate = 1.0;  // Fast regeneration
    }

    /**
     * Gets the coverage radius.
     * @return The radius of area covered by this plant
     */
    public double getCoverageRadius() {
        return coverageRadius;
    }
}
