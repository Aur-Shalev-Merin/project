package organisms.plants;

import simulation.Position;

/**
 * Aspen tree - fast-growing deciduous tree common in temperate forests.
 */
public class Aspen extends Tree {

    /**
     * Creates a new aspen tree at the specified position.
     * @param position The starting position
     * @param initialSize The starting size
     */
    public Aspen(Position position, double initialSize) {
        super(position, initialSize);
        this.maxSize = 100.0;
        this.growthRate = 2.0;
        this.reproductionAge = 50;
        this.seedDispersalRadius = 10;
        this.reproductionEnergyThreshold = 70.0;
        this.woodDensity = 0.5;  // Lower density (softer wood)
    }

    /**
     * Aspen photosynthesizes efficiently in full sun.
     * @param sunlight The sunlight intensity
     */
    @Override
    public void photosynthesize(double sunlight) {
        if (currentSize < maxSize) {
            double energyGain = sunlight * 1.5;  // Good sun efficiency
            currentSize += energyGain;
            currentSize = Math.min(currentSize, maxSize);
            energy = currentSize;
        }
    }

    /**
     * Creates an aspen offspring.
     * @param position The position for the offspring
     * @return A new Aspen seedling
     */
    @Override
    protected Aspen createOffspring(Position position) {
        return new Aspen(position, 5.0);  // Start as seedling
    }

    /**
     * Gets the energy value when consumed.
     * @return Energy value based on size and wood density
     */
    @Override
    public double getEnergyValue() {
        return 25.0;  // Moderate energy value
    }

    /**
     * Gets the species name.
     * @return "Aspen"
     */
    @Override
    public String getSpeciesName() {
        return "Aspen";
    }
}
