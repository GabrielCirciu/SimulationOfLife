# Simulation of Life  

A simple, yet hardcore, plugin that adds an extra layer of survival mechanics by exhausting the player when performing certain actions such as mining, farming, and fightng. Alongside that it adds a specialization system that lets you get less exhausted in a field that you master. Intended for large population servers, as solo players can only do so much before their food levels run out.

All of this works in the backend, but allows players to view their specialization levels if they desire.

# Features

- **Exhaustion Management**: Players gain Exhaustion when doing certain activities such as mining, fighting, farming
- **Specializations**: Increase your efficieny by managing what you specialize in, be careful though, you can only be good at so many things
- **Configurable Settings**: Customize many parts of your experience, like the exhaustion amounts, cooldown, block exemptions, specialization points gain
- **Permission System**: Allows the bypassing of systems or altering for selected roles such as players or admins
- **Block Exemptions**: Exclude specific block types from exhaustion gain
- **Cooldown System**: Prevent over-exhaustion by modifying cooldowns between exhaustion checks
- **Debug Mode**: (somewhat) Detailed logging for troubleshooting
- **Admin Commands**: From basic functions like reloading the plugin, to advanced statistics to overview the servers specialization distribution

# Usage

### For Players

1. The plugin automatically activates when you place or destroy blocks, or hit entities in survival mode
2. You'll receive a message when you recieve exhaustion (if player messages are enabled)
3. Players with the `simulationoflife.bypass` permission won't gain exhaustion
4. The plugin may respect minimum hunger levels to prevent starvation

### For Server Administrators

1. **Installation**: Place the JAR file in your `plugins` folder and restart the server
2. **Configuration**: Edit `plugins/SimulationOfLife/config.yml` to customize settings
3. **Reloading**: Use `/simulationoflife reload` to apply configuration changes without restarting
4. **Monitoring**: Use `/simulationoflife status` to check plugin status and settings
5. **Debugging**: Enable debug mode in the config to see server-side logs

# Installation

### Download Pre-built JAR

1. Download the latest release JAR file from the releases page.
2. Place the JAR file in your server's `plugins` folder.
3. Start or restart your Folia server.

### Building from Source

#### Requirements for building

- **Minecraft Server**: Folia 1.20.4 or higher
- **Java**: Java 21 or higher
- **Maven**: For building the plugin

1. Clone this repository:
   ```bash
   git clone https://github.com/GabrielCirciu/SimulationOfLife
   cd SimulationOfLife
   ```

2. Build the plugin using Maven:
   ```bash
   mvn clean package
   ```

3. Copy the generated JAR file from `target/simulationoflife-x.x.x.jar` to your server's `plugins` folder.

4. Start or restart your Folia server.

# Configuration

The plugin creates a `config.yml` file in the `plugins/SimulationOfLife/` directory. Here's what each section does:

### General Settings

```yaml
general:
  enabled: true             # Enable or disable the plugin
  debug: false              # Debug mode for server-side logging
  player-messages: false    # Send player messages of their activity
  admin-messages: true      # Send admin messages for commands (you shouldn't set this to false :) )
```

### Exhaustion Settings

```yaml
exhaustion:
  place-block: 1.0          # Amount of exhaustion to gain when placing a block (0.0 - 20.0)
  mining-block: 1.0         # Amount of exhaustion to gain when destroying a block (0.0 - 20.0)
  farming-block: 1.0        # Amount of exhaustion to gain when farming a block (0.0 - 20.0)
  hit-entity: 1.0           # Amount of exhaustion to gain when hitting entities (0.0 - 20.0)
  default: 1.0              # Amount of exhaustion to reduce when doing a default action (0.0 - 20.0)
  minimum-food-level: 0.0   # Minimum food level before reduction happens (prevents starvation at >0)
  cooldown: 100             # Cooldown between exhaustions in milliseconds (0 = no cooldown)
```

### Specialization Settings

```yaml
specialization:
  building: 1               # Amount of points to gain when building
  fighting: 1               # Amount of points to gain when fighting
  athletics: 1              # Amount of points to gain when being athletic
  mining: 1                 # Amount of points to gain when mining
  farming: 1                # Amount of points to gain when farming
  max-points: 100           # Maximum points a player can have
```

### Athletics Settings

```yaml
run-speed:
  base-speed: 0.2           # Base run speed when sprinting (default Minecraft sprint speed is ~0.2)
  speed-increase-per-level: 0.005  # Speed increase per athletics level (0.01 = 1% increase per level)
```

### Block Exemptions

```yaml
blocks:
  place-exempt:             # Blocks that don't cause exhaustion when placed
    - AIR
    - CAVE_AIR
    - VOID_AIR
  destroy-exempt:           # Blocks that don't cause exhaustion when destroyed
    - AIR
    - CAVE_AIR
    - VOID_AIR
```

### Messages

```yaml
messages:
  prefix: "<dark_gray>[<red>Simulation of Life</red>]</dark_gray> "
  exhaustion-reduced: "<yellow>Exhaustion: <red>{exhaustion}</red>/<green>4</green></yellow>"
  plugin-reloaded: "<green>Plugin configuration reloaded!</green>"
  plugin-status: "<green>Plugin is <dark_green>enabled</dark_green>"
  plugin-disabled: "<red>Plugin is <dark_red>disabled</dark_red></red>" 
```

# Commands

### Admin Commands

- `/simulationoflife reload` - Reload the plugin configuration
- `/simulationoflife status` - Show plugin status and current settings
- `/simulationoflife perf`   - Show performance metrics of actions happening
- `/simulationoflife debug`  - Show if debug is enabled
- `/simulationoflife specs`  - Show overall server-wide specialization stats
- `/simulationoflife stats`  - Show your own player specialization stats

### Player Commands

- `/simulationoflife stats`  - Show your own player specialization stats

# In-Game Permissions

- `simulationoflife.admin`    - Access to admin commands (default: op)
- `simulationoflife.player`   - Access to only player commnands (default: true)
- `simulationoflife.bypass`  - Bypass hunger reduction from block actions (default: false)

# Usage

### For Players

1. The plugin automatically activates when you place or destroy blocks, or hit entities in survival mode
2. You'll receive a message when you recieve exhaustion (if player messages are enabled)
3. Players with the `simulationoflife.bypass` permission won't gain exhaustion
4. The plugin may respect minimum hunger levels to prevent starvation

### For Server Administrators

1. **Installation**: Place the JAR file in your `plugins` folder and restart the server
2. **Configuration**: Edit `plugins/SimulationOfLife/config.yml` to customize settings
3. **Reloading**: Use `/simulationoflife reload` to apply configuration changes without restarting
4. **Monitoring**: Use `/simulationoflife status` to check plugin status and settings
5. **Debugging**: Enable debug mode in the config to see server-side logs

## Troubleshooting

### Common Issues

Let me know of any issues.
