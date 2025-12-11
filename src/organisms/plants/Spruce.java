package organisms.plants;

import simulation.Position;

/**
 * Spruce tree - slow-growing conifer, shade-tolerant.
 */
public class Spruce extends Tree {

    /**
     * Creates a new spruce tree at the specified position.
     * @param position The starting position
     * @param initialSize The starting size
     */
    public Spruce(Position position, double initialSize) {
        super(position, initialSize);
        this.maxSize = 120.0;
        this.growthRate = 1.5;
        this.reproductionAge = 60;
        this.seedDispersalRadius = 8;
        this.reproductionEnergyThreshold = 80.0;
        this.woodDensity = 0.7;  // Higher density (harder wood)
    }

    /**
     * Spruce is shade-tolerant but grows slower.
     * @param sunlight The sunlight intensity
     */
    @Override
    public void photosynthesize(double sunlight) {
        if (currentSize < maxSize) {
            double energyGain = sunlight * 1.2;  // Shade tolerant
            currentSize += energyGain;
            currentSize = Math.min(currentSize, maxSize);
            energy = currentSize;
        }
    }

    /**
     * Creates a spruce offspring.
     * @param position The position for the offspring
     * @return A new Spruce seedling
     */
    @Override
    protected Spruce createOffspring(Position position) {
        return new Spruce(position, 5.0);  // Start as seedling
    }

    /**
     * Gets the energy value when consumed.
     * @return Energy value based on size and wood density
     */
    @Override
    public double getEnergyValue() {
        return 20.0;  // Lower palatability (evergreen)
    }

    /**
     * Gets the species name.
     * @return "Spruce"
     */
    @Override
    public String getSpeciesName() {
        return "Spruce";
    }
}
