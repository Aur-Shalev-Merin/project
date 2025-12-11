package organisms.plants;

import simulation.Position;

/**
 * Berry shrubs - fast-growing, highly nutritious, preferred by omnivores.
 */
public class Berries extends Shrub {

    /**
     * Creates a new berry shrub at the specified position.
     * @param position The starting position
     * @param initialSize The starting size
     */
    public Berries(Position position, double initialSize) {
        super(position, initialSize);
        this.maxSize = 30.0;
        this.growthRate = 0.2;  // Further reduced from 0.3
        this.reproductionAge = 12;  // Increased from 10
        this.seedDispersalRadius = 15;  // Seeds spread by animals
        this.reproductionEnergyThreshold = 22.0;  // Increased from 20.0
        this.berryProducing = true;
    }

    /**
     * Berry shrubs photosynthesize efficiently.
     * @param sunlight The sunlight intensity
     */
    @Override
    public void photosynthesize(double sunlight) {
        if (currentSize < maxSize) {
            double energyGain = sunlight * 0.3;  // Further reduced from 0.4
            currentSize += energyGain;
            currentSize = Math.min(currentSize, maxSize);
            energy = currentSize;
        }
    }

    /**
     * Creates a berry shrub offspring.
     * @param position The position for the offspring
     * @return A new Berries plant
     */
    @Override
    protected Berries createOffspring(Position position) {
        return new Berries(position, 3.0);  // Start small
    }

    /**
     * Gets the energy value when consumed.
     * Berries are highly nutritious.
     * @return Energy value
     */
    @Override
    public double getEnergyValue() {
        return 15.0;  // High energy value for size
    }

    /**
     * Gets the species name.
     * @return "Berries"
     */
    @Override
    public String getSpeciesName() {
        return "Berries";
    }
}
