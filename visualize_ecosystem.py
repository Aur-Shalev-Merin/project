#!/usr/bin/env python3
"""
Visualization script for ecosystem population dynamics.
Generates plots showing population trends over time.
"""

import pandas as pd
import matplotlib.pyplot as plt
import sys
import os

def load_data(filename='ecosystem_data.csv'):
    """Load the ecosystem CSV data."""
    if not os.path.exists(filename):
        print(f"Error: {filename} not found")
        sys.exit(1)

    data = pd.read_csv(filename)
    print(f"Loaded {len(data)} timesteps with {len(data.columns)-1} species")
    return data

def plot_all_populations(data, output='population_dynamics.png'):
    """Plot all species populations on one graph."""
    plt.close('all')  # Clear any existing figures
    plt.figure(figsize=(14, 8))

    for column in data.columns:
        if column != 'TimeStep':
            plt.plot(data['TimeStep'], data[column], label=column, linewidth=2)

    plt.title('Ecosystem Population Dynamics (All Species)', fontsize=16, fontweight='bold')
    plt.xlabel('Time Step', fontsize=12)
    plt.ylabel('Population Count', fontsize=12)
    plt.legend(bbox_to_anchor=(1.05, 1), loc='upper left', fontsize=10)
    plt.grid(True, alpha=0.3)
    plt.tight_layout()
    plt.savefig(output, dpi=150)
    plt.close()  # Close figure after saving
    print(f"Saved: {output}")

def plot_trophic_levels(data, output='trophic_levels.png'):
    """Plot populations separated by trophic level."""
    plt.close('all')  # Clear any existing figures
    fig, axes = plt.subplots(3, 1, figsize=(12, 10))

    # Producers (Plants)
    producers = ['Wildflowers', 'Berries', 'Aspen', 'Spruce']
    for plant in producers:
        if plant in data.columns:
            axes[0].plot(data['TimeStep'], data[plant], label=plant, linewidth=2)
    axes[0].set_title('Producers (Plants)', fontsize=14, fontweight='bold')
    axes[0].set_ylabel('Population', fontsize=11)
    axes[0].legend(loc='upper left')
    axes[0].grid(True, alpha=0.3)

    # Herbivores
    herbivores = ['Deer', 'Bunny', 'FieldMouse', 'GroundSquirrel', 'Chipmunk']
    for herbivore in herbivores:
        if herbivore in data.columns:
            axes[1].plot(data['TimeStep'], data[herbivore], label=herbivore, linewidth=2)
    axes[1].set_title('Primary Consumers (Herbivores)', fontsize=14, fontweight='bold')
    axes[1].set_ylabel('Population', fontsize=11)
    axes[1].legend(loc='upper left')
    axes[1].grid(True, alpha=0.3)

    # Carnivores/Omnivores
    predators = ['Fox', 'Coyote', 'BlackBear']
    for predator in predators:
        if predator in data.columns:
            axes[2].plot(data['TimeStep'], data[predator], label=predator, linewidth=2)
    axes[2].set_title('Predators (Carnivores & Omnivores)', fontsize=14, fontweight='bold')
    axes[2].set_xlabel('Time Step', fontsize=11)
    axes[2].set_ylabel('Population', fontsize=11)
    axes[2].legend(loc='upper left')
    axes[2].grid(True, alpha=0.3)

    plt.tight_layout()
    plt.savefig(output, dpi=150)
    plt.close()  # Close figure after saving
    print(f"Saved: {output}")

def print_statistics(data):
    """Print summary statistics for all species."""
    print("\n" + "="*80)
    print("ECOSYSTEM STATISTICS SUMMARY")
    print("="*80)

    print(f"\n{'Species':<20} {'Initial':<10} {'Final':<10} {'Mean':<10} {'Max':<10} {'Min':<10}")
    print("-"*80)

    for column in data.columns:
        if column != 'TimeStep':
            initial = data[column].iloc[0]
            final = data[column].iloc[-1]
            mean = data[column].mean()
            maximum = data[column].max()
            minimum = data[column].min()

            print(f"{column:<20} {initial:<10} {final:<10} {mean:<10.1f} {maximum:<10} {minimum:<10}")

    print("\n" + "="*80)
    print(f"Total timesteps: {len(data)}")
    print(f"Initial total population: {data.iloc[0].sum() - data['TimeStep'].iloc[0]:.0f}")
    print(f"Final total population: {data.iloc[-1].sum() - data['TimeStep'].iloc[-1]:.0f}")
    print("="*80 + "\n")

def main():
    """Main execution function."""
    print("Ecosystem Data Visualization Tool")
    print("=" * 50)

    # Load data
    data = load_data('ecosystem_data.csv')

    # Print statistics
    print_statistics(data)

    # Generate plots with timestamp to avoid cache issues
    import time
    timestamp = int(time.time())
    print("Generating visualizations...")
    plot_all_populations(data, f'population_dynamics_{timestamp}.png')
    plot_trophic_levels(data, f'trophic_levels_{timestamp}.png')

    print("\nVisualization complete!")
    print("Check the generated PNG files for plots.")

if __name__ == '__main__':
    main()
