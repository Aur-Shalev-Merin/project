package organisms;

import simulation.Ecosystem;
import simulation.Position;

/**
 * Abstract base class for all living entities (plants and animals).
 */
public abstract class Organism {
    protected static int nextId = 0;  // Static counter for unique IDs
    protected int id;                 // Unique identifier
    protected int age;                // Age in time steps
    protected double energy;          // Current energy level
    protected boolean isAlive;        // Survival status
    protected Position position;      // Location in ecosystem

    /**
     * Creates a new organism at the specified position with initial energy.
     * @param position The starting position
     * @param initialEnergy The starting energy level
     */
    public Organism(Position position, double initialEnergy) {
        this.id = nextId++;
        this.position = position;
        this.energy = initialEnergy;
        this.age = 0;
        this.isAlive = true;
    }

    // Abstract methods to be implemented by subclasses

    /**
     * Updates the organism's state for one time step.
     * @param ecosystem The ecosystem containing this organism
     */
    public abstract void update(Ecosystem ecosystem);

    /**
     * Attempts to reproduce.
     * @return true if reproduction was successful, false otherwise
     */
    public abstract boolean reproduce();

    /**
     * Handles the organism's death.
     */
    public abstract void die();

    /**
     * @return The energy value this organism provides when consumed
     */
    public abstract double getEnergyValue();

    /**
     * @return The species name of this organism
     */
    public abstract String getSpeciesName();

    // Concrete methods

    /**
     * @return The unique ID of this organism
     */
    public int getId() {
        return id;
    }

    /**
     * @return The age of this organism in time steps
     */
    public int getAge() {
        return age;
    }

    /**
     * @return The current energy level
     */
    public double getEnergy() {
        return energy;
    }

    /**
     * @return true if the organism is alive, false otherwise
     */
    public boolean isAlive() {
        return isAlive;
    }

    /**
     * @return The current position
     */
    public Position getPosition() {
        return position;
    }

    /**
     * Sets the organism's position.
     * @param p The new position
     */
    public void setPosition(Position p) {
        this.position = p;
    }

    /**
     * Increments the organism's age by 1.
     */
    protected void incrementAge() {
        this.age++;
    }

    /**
     * Modifies the organism's energy by the specified amount.
     * Can be positive (gain energy) or negative (lose energy).
     * @param amount The amount to add or subtract
     */
    protected void modifyEnergy(double amount) {
        this.energy += amount;
    }

    /**
     * @return A string representation of this organism
     */
    @Override
    public String toString() {
        return String.format("%s[%d] at %s", getSpeciesName(), id, position);
    }
}
