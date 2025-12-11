package organisms.animals;

import organisms.Animal;
import organisms.Organism;
import organisms.Plant;
import simulation.Ecosystem;
import simulation.Position;
import java.util.ArrayList;
import java.util.List;

/**
 * Abstract base class for animals that eat both plants and animals.
 */
public abstract class Omnivore extends Animal {

    /**
     * Creates a new omnivore at the specified position.
     * @param position The starting position
     * @param initialEnergy The starting energy level
     */
    public Omnivore(Position position, double initialEnergy) {
        super(position, initialEnergy);
    }

    /**
     * Filters diet to get only animal prey.
     * @return List of animal types this omnivore can hunt
     */
    protected List<Class<?>> getAnimalDiet() {
        List<Class<?>> animalDiet = new ArrayList<>();
        for (Class<?> foodType : diet) {
            if (Animal.class.isAssignableFrom(foodType)) {
                animalDiet.add(foodType);
            }
        }
        return animalDiet;
    }

    /**
     * Filters diet to get only plant food.
     * @return List of plant types this omnivore can eat
     */
    protected List<Class<?>> getPlantDiet() {
        List<Class<?>> plantDiet = new ArrayList<>();
        for (Class<?> foodType : diet) {
            if (Plant.class.isAssignableFrom(foodType)) {
                plantDiet.add(foodType);
            }
        }
        return plantDiet;
    }

    /**
     * Attempts to hunt prey using probability-based encounters.
     * @param ecosystem The ecosystem containing this omnivore
     * @return true if hunting was successful
     */
    @Override
    protected boolean hunt(Ecosystem ecosystem) {
        List<Class<?>> animalDiet = getAnimalDiet();
        if (animalDiet.isEmpty()) return false;

        // Count available prey in animal diet
        int totalPrey = ecosystem.getPopulationCount(animalDiet);
        if (totalPrey == 0) return false;

        // Calculate encounter probability based on prey density and carnivore competition
        int totalCarnivores = ecosystem.getTotalCarnivores();
        double carnivoreCompetition = Math.max(1, totalCarnivores + 1); // +1 to include this omnivore

        // Encounter rate: Omnivores hunt less efficiently than pure carnivores
        // Base rate of 0.015 (slightly lower than carnivore's 0.02)
        double encounterRate = (totalPrey / carnivoreCompetition) * 0.015;
        encounterRate = Math.min(1.0, encounterRate); // Cap at 100%

        // Random encounter check
        if (ecosystem.getRandom().nextDouble() < encounterRate) {
            // Successfully encountered prey - get random prey of edible type
            Organism prey = ecosystem.getRandomOrganismOfType(animalDiet);

            if (prey instanceof Animal && prey.isAlive()) {
                // Hunt success probability based on relative energy
                double huntSuccess = calculateHuntSuccess((Animal) prey);

                if (ecosystem.getRandom().nextDouble() < huntSuccess) {
                    eat(prey);
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Attempts to graze on plants using probability-based encounters.
     * @param ecosystem The ecosystem containing this omnivore
     * @return true if grazing was successful
     */
    @Override
    protected boolean graze(Ecosystem ecosystem) {
        List<Class<?>> plantDiet = getPlantDiet();
        if (plantDiet.isEmpty()) return false;

        // Count available plants in plant diet
        int totalPlants = ecosystem.getPopulationCount(plantDiet);
        if (totalPlants == 0) return false;

        // Calculate encounter probability based on plant density and herbivore competition
        int totalHerbivores = ecosystem.getTotalHerbivores();
        double herbivoreCompetition = Math.max(1, totalHerbivores + 1); // +1 to include this omnivore

        // Encounter rate: Omnivores are less efficient grazers than pure herbivores
        // Base rate of 0.15 (slightly lower than herbivore's 0.20)
        double encounterRate = (totalPlants / herbivoreCompetition) * 0.15;
        encounterRate = Math.min(1.0, encounterRate); // Cap at 100%

        // Random encounter check
        if (ecosystem.getRandom().nextDouble() < encounterRate) {
            // Successfully encountered food - get random plant of edible type
            Organism food = ecosystem.getRandomOrganismOfType(plantDiet);

            if (food instanceof Plant && food.isAlive()) {
                eat(food);
                return true;
            }
        }

        return false;
    }

    /**
     * Calculates the probability of a successful hunt based on relative size/energy.
     * Small prey are harder to catch (agile), giving them better escape chance.
     * @param prey The target prey animal
     * @return Probability of successful hunt (0.0 to 1.0)
     */
    protected double calculateHuntSuccess(Animal prey) {
        // Success based on relative energy/size
        double sizeRatio = this.energy / prey.getEnergy();

        // Small prey (energy < 15) are more agile - reduce success rate
        if (prey.getEnergy() < 15.0) {
            return Math.min(0.6, 0.2 + sizeRatio * 0.2);  // Reduced for small prey
        }

        // Normal success for medium/large prey
        return Math.min(0.9, 0.3 + sizeRatio * 0.3);
    }

    /**
     * Main update loop for omnivores.
     * Preferentially grazes on plants but will hunt when hungry.
     * @param ecosystem The ecosystem containing this omnivore
     */
    @Override
    public void update(Ecosystem ecosystem) {
        if (!isAlive) return;

        incrementAge();
        timeSinceLastReproduction++;
        metabolize();
        // Movement removed - probability-based encounters don't require spatial movement

        // Omnivore feeding strategy: plants first if available, hunt if desperate
        boolean foundFood = false;
        if (energy < maxEnergy * 0.6) {
            foundFood = hunt(ecosystem);  // Hunt when moderately hungry
        }
        if (!foundFood) {
            foundFood = graze(ecosystem);  // Prefer foraging
        }
        if (!foundFood && energy < maxEnergy * 0.4) {
            hunt(ecosystem);  // Hunt again if desperate
        }

        checkSurvival();

        if (shouldReproduce()) {
            reproduce(ecosystem);
        }
    }
}
