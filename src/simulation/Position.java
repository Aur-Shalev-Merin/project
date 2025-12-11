package simulation;

/**
 * Represents a 2D coordinate in the ecosystem with spatial operations.
 */
public class Position {
    private double x;
    private double y;

    /**
     * Creates a new position with specified coordinates.
     * @param x The x coordinate
     * @param y The y coordinate
     */
    public Position(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * @return The x coordinate
     */
    public double getX() {
        return x;
    }

    /**
     * @return The y coordinate
     */
    public double getY() {
        return y;
    }

    /**
     * Sets the x coordinate.
     * @param x The new x coordinate
     */
    public void setX(double x) {
        this.x = x;
    }

    /**
     * Sets the y coordinate.
     * @param y The new y coordinate
     */
    public void setY(double y) {
        this.y = y;
    }

    /**
     * Calculates the Euclidean distance to another position.
     * @param other The other position
     * @return The distance to the other position
     */
    public double distanceTo(Position other) {
        double dx = this.x - other.x;
        double dy = this.y - other.y;
        return Math.sqrt(dx * dx + dy * dy);
    }

    /**
     * Returns a new position moved toward a target position by a specified distance.
     * If the distance is greater than the distance to the target, returns the target position.
     * @param target The target position to move toward
     * @param distance The distance to move
     * @return A new Position moved toward the target
     */
    public Position moveToward(Position target, double distance) {
        double currentDistance = this.distanceTo(target);

        // If already at target or distance is 0, return current position
        if (currentDistance == 0 || distance == 0) {
            return new Position(this.x, this.y);
        }

        // If distance would overshoot target, return target position
        if (distance >= currentDistance) {
            return new Position(target.x, target.y);
        }

        // Calculate unit vector toward target
        double dx = target.x - this.x;
        double dy = target.y - this.y;
        double ratio = distance / currentDistance;

        // Move by distance in direction of target
        return new Position(this.x + dx * ratio, this.y + dy * ratio);
    }

    /**
     * Checks if another position is within a specified range.
     * @param other The other position
     * @param range The range to check
     * @return true if the other position is within range, false otherwise
     */
    public boolean isWithinRange(Position other, double range) {
        return this.distanceTo(other) <= range;
    }

    /**
     * Creates a deep copy of this position.
     * @return A new Position with the same coordinates
     */
    @Override
    public Position clone() {
        return new Position(this.x, this.y);
    }

    /**
     * Returns a string representation of this position.
     * @return A formatted string "(x, y)"
     */
    @Override
    public String toString() {
        return String.format("(%.2f, %.2f)", x, y);
    }
}
