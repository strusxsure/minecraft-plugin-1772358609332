# FireMaze Plugin

A Minecraft Paper plugin that creates an immersive fire-themed maze experience with heat damage, lava traps, fire mob spawns, and timed flame walls.

## Features

- **Heat Damage Over Time**: Players take damage while in heat zones
- **Lava Trap Triggers**: Stepping on lava causes damage and fire
- **Fire Mob Spawn Zones**: Random fire mobs spawn in designated areas
- **Timed Flame Wall Sections**: Flame walls activate and deactivate on triggers
- **Area-based Heat Damage System**: Configurable damage rates
- **Trigger Blocks**: Redstone blocks activate special events
- **Mob Spawn Control**: Configurable spawn rates and zones

## Installation

1. Build the plugin using Maven: `mvn clean package`
2. Copy the generated JAR file from `target/` to your server's `plugins/` directory
3. Restart your Minecraft server

## Commands

- `/firemaze create` - Create a new maze at your location
- `/firemaze info` - Show maze information
- `/firemaze setdamage <rate>` - Set the damage rate per tick

## Permissions

- `firemaze.admin` - Access to all admin commands (default: op)

## Configuration

The plugin automatically creates a `config.yml` file with configurable options including damage rate.

## Compatibility

- Minecraft: 1.21+
- API: Paper
- Java: 21