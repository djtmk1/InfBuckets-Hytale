# InfiniteBuckets

InfiniteBuckets is a Hytale server plugin that adds infinite buckets for all liquid types to the game.

## Features

- **Infinite Buckets for All Placeable Liquids**: Water, Lava, Poison, Slime, Red Slime, and Tar buckets never run out.
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

1. Use the command `/infb give <your_name> <liquid_type>` to receive an infinite bucket.
2. Use the bucket as normal. It will automatically refill after use and never run out.

## Asset Pack

This plugin includes an integrated asset pack with custom bucket items:
- `Container_Bucket.json` - Main infinite bucket with all liquid states
- `Deco_Bucket.json` - Decorative bucket variant

## Development

This project is built for the Hytale Server API.

- **Group ID**: `net.busybee.infbuckets`
- **Artifact ID**: `InfiniteBuckets`
- **Version**: `2026.1.1`
- **Main Class**: `net.busybee.infbuckets.InfBucketsPlugin`
