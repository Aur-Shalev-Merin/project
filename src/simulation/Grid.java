package simulation;

import organisms.Organism;
import java.util.*;

/**
 * Spatial indexing structure for efficient organism lookups by location.
 * Uses spatial hashing to organize organisms into cells for fast queries.
 */
public class Grid {
    private Map<String, List<Organism>> cells;  // Cell key -> organisms in that cell
    private int width;                          // Grid width in units
    private int height;                         // Grid height in units
    private double cellSize;                    // Size of each cell (for hashing)

    /**
     * Creates a new grid with specified dimensions.
     * @param width Grid width in units
     * @param height Grid height in units
     * @param cellSize Size of each cell for spatial hashing
     */
    public Grid(int width, int height, double cellSize) {
        this.cells = new HashMap<>();
        this.width = width;
        this.height = height;
        this.cellSize = cellSize;
    }

    /**
     * Adds an organism to the appropriate cell.
     * @param o The organism to add
     */
    public void addOrganism(Organism o) {
        String key = getCellKey(o.getPosition());
        cells.computeIfAbsent(key, k -> new ArrayList<>()).add(o);
    }

    /**
     * Removes an organism from its cell.
     * @param o The organism to remove
     */
    public void removeOrganism(Organism o) {
        String key = getCellKey(o.getPosition());
        List<Organism> cellOrganisms = cells.get(key);
        if (cellOrganisms != null) {
            cellOrganisms.remove(o);
            if (cellOrganisms.isEmpty()) {
                cells.remove(key);
            }
        }
    }

    /**
     * Updates an organism's position in the grid.
     * Moves the organism from its old cell to its new cell.
     * @param o The organism that moved
     * @param oldPos The organism's previous position
     */
    public void updateOrganismPosition(Organism o, Position oldPos) {
        String oldKey = getCellKey(oldPos);
        String newKey = getCellKey(o.getPosition());

        // Only update if the cell changed
        if (!oldKey.equals(newKey)) {
            // Remove from old cell
            List<Organism> oldCellOrganisms = cells.get(oldKey);
            if (oldCellOrganisms != null) {
                oldCellOrganisms.remove(o);
                if (oldCellOrganisms.isEmpty()) {
                    cells.remove(oldKey);
                }
            }

            // Add to new cell
            cells.computeIfAbsent(newKey, k -> new ArrayList<>()).add(o);
        }
    }

    /**
     * Returns all organisms within a specified radius of a center position.
     * @param center The center position
     * @param radius The search radius
     * @return List of organisms within the radius
     */
    public List<Organism> getOrganismsInRadius(Position center, double radius) {
        List<Organism> result = new ArrayList<>();

        // Calculate which cells might contain organisms within radius
        int cellRadiusX = (int) Math.ceil(radius / cellSize);
        int cellRadiusY = (int) Math.ceil(radius / cellSize);

        int centerCellX = (int) (center.getX() / cellSize);
        int centerCellY = (int) (center.getY() / cellSize);

        // Check all cells that might overlap with the search radius
        for (int dx = -cellRadiusX; dx <= cellRadiusX; dx++) {
            for (int dy = -cellRadiusY; dy <= cellRadiusY; dy++) {
                String key = (centerCellX + dx) + "_" + (centerCellY + dy);
                List<Organism> cellOrganisms = cells.get(key);

                if (cellOrganisms != null) {
                    // Check each organism in the cell for actual distance
                    for (Organism o : cellOrganisms) {
                        if (o.isAlive() && center.distanceTo(o.getPosition()) <= radius) {
                            result.add(o);
                        }
                    }
                }
            }
        }

        return result;
    }

    /**
     * Returns all organisms in the grid.
     * @return List of all organisms
     */
    public List<Organism> getAllOrganisms() {
        List<Organism> result = new ArrayList<>();
        for (List<Organism> cellOrganisms : cells.values()) {
            result.addAll(cellOrganisms);
        }
        return result;
    }

    /**
     * Calculates the cell key for a given position using spatial hashing.
     * @param pos The position
     * @return The cell key as "x_y"
     */
    private String getCellKey(Position pos) {
        int x = (int) (pos.getX() / cellSize);
        int y = (int) (pos.getY() / cellSize);
        return x + "_" + y;
    }

    /**
     * @return The grid width
     */
    public int getWidth() {
        return width;
    }

    /**
     * @return The grid height
     */
    public int getHeight() {
        return height;
    }

    /**
     * @return The cell size
     */
    public double getCellSize() {
        return cellSize;
    }
}
