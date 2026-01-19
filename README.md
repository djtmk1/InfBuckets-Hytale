# InfiniteBuckets

InfiniteBuckets is a Hytale server plugin that adds infinite water buckets to the game.

## Features

- **Infinite Water Buckets**: Place water continuously without refilling.
- **Command Support**: Easily give infinite buckets to players via commands.
- **Seamless Integration**: Works with the standard Hytale bucket item (`Container_Bucket`).

## Commands

### `/infb give <player> water`
Gives an infinite water bucket to the specified player.

- **Permission**: `infbuckets.give`
- **Usage**: `/infb give <player> water`

## Installation

1. Build the project using Maven:
   ```sh
   mvn clean package
   ```
2. Place the generated JAR file into your server's `plugins` folder.
3. Restart the server.

## Usage

1. Use the command `/infb give <your_name> water` to receive an infinite bucket.
2. Use the bucket as normal. It will automatically refill after use.

## Development

This project is built for the Hytale Server API.

- **Group ID**: `net.busybee.infbuckets`
- **Artifact ID**: `InfiniteBuckets`
- **Version**: `1.0`
