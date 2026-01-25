# InfiniteBuckets

InfiniteBuckets is a Hytale server plugin that adds infinite buckets for all liquid types to the game.

## Features

- **Infinite Buckets for All Placeable Liquids**: Water, Lava, Poison, Slime, Red Slime, and Tar buckets never run out.
- **Progression-Based Crafting System**: Craft infinite buckets through a tiered progression system.
- **Command Support**: Easily give infinite buckets to players via commands.
- **Custom Assets**: Includes custom bucket items with infinite liquid mechanics.
- **Seamless Integration**: Works with standard Hytale bucket items.

## Supported Liquid Types

- Water
- Lava
- Poison
- Slime
- Red Slime
- Tar

## Crafting Recipes

Infinite buckets follow a progression system where each tier requires the previous bucket:

### Tier 1: Infinite Water Bucket
- **Workbench**: Basic Workbench
- **Recipe**:
  - Regular Bucket (1)
  - Iron Ingots (3)
- **Crafting Time**: 3.0 seconds

### Tier 2: Infinite Lava Bucket
- **Workbench**: Forge
- **Recipe**:
  - Infinite Water Bucket (1)
  - Iron Ingots (4)
  - Stone Blocks (8)
- **Crafting Time**: 5.0 seconds

### Tier 3: Infinite Poison Bucket
- **Workbench**: Advanced Workbench
- **Recipe**:
  - Infinite Lava Bucket (1)
  - Gold Ingots (3)
  - Copper Ingots (5)
  - Bones (6)
- **Crafting Time**: 6.0 seconds

### Tier 4: Infinite Slime Bucket
- **Workbench**: Advanced Workbench
- **Recipe**:
  - Infinite Poison Bucket (1)
  - Gold Ingots (4)
  - Silver Ingots (6)
  - Feathers (10)
- **Crafting Time**: 7.0 seconds

### Tier 5: Infinite Red Slime Bucket
- **Workbench**: Forge
- **Recipe**:
  - Infinite Slime Bucket (1)
  - Titanium Ingots (4)
  - Gold Ingots (6)
  - Ruby/Red Crystals (3)
- **Crafting Time**: 8.0 seconds

### Tier 6: Infinite Tar Bucket (Hardest)
- **Workbench**: Forge
- **Recipe**:
  - Infinite Red Slime Bucket (1)
  - Titanium Ingots (6)
  - Mithril Ingots (4)
  - Ancient Stone (8)
- **Crafting Time**: 10.0 seconds

## Commands

### `/infb give <player> <liquid_type>`
Gives an infinite bucket of the specified liquid type to the player.

- **Permission**: `infbuckets.give`
- **Usage**: `/infb give <player> <liquid_type>`
- **Examples**:
  - `/infb give Steve water`
  - `/infb give Alex lava`

## Installation

1. Build the project using Maven:
   ```sh
   mvn clean package
   ```
2. Place the generated JAR file (`InfiniteBuckets-2026.1.1.jar`) into your server's `mods` folder.
3. Restart the server.

## Usage

### Crafting Method (Recommended)
1. Progress through the crafting tiers starting with the Infinite Water Bucket at a Basic Workbench.
2. Each subsequent bucket requires the previous tier bucket as an ingredient.
3. Use the buckets as normal - they will automatically refill after use and never run out.

### Command Method (Admin/Testing)
1. Use the command `/infb give <your_name> <liquid_type>` to receive an infinite bucket directly.
2. Use the bucket as normal. It will automatically refill after use and never run out.

## Asset Pack

This plugin includes an integrated asset pack with custom bucket items:
- `InfiniteBucket_Water.json` - Infinite Water Bucket
- `InfiniteBucket_Lava.json` - Infinite Lava Bucket
- `InfiniteBucket_Poison.json` - Infinite Poison Bucket
- `InfiniteBucket_Slime.json` - Infinite Slime Bucket
- `InfiniteBucket_Red_Slime.json` - Infinite Red Slime Bucket
- `InfiniteBucket_Tar.json` - Infinite Tar Bucket
- `Container_Bucket.json` - Base bucket with all liquid states
- `Deco_Bucket.json` - Decorative bucket variant

## Development

This project is built for the Hytale Server API.

- **Group ID**: `net.busybee.infbuckets`
- **Artifact ID**: `InfiniteBuckets`
- **Version**: `2026.1.1`
- **Main Class**: `net.busybee.infbuckets.InfBucketsPlugin`
