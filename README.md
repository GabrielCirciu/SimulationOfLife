# Simulation of Life  

A simple, yet hardcore, plugin that adds an extra layer of survival mechanics by exhausting the player when performing certain actions such as mining, farming, building, and fightng. Alongside that it adds a specialization system that lets you get less exhausted in a field that you master. Intended for large population servers, as solo players can only do so much before their food levels run out.

# Features

### Main Features:
- **Exhaustion Management**: Players gain Exhaustion when doing certain activities such as mining, fighting, building, and farming
- **Specializations**: Increase your efficieny by managing what you specialize in, be careful though, you can only be good at so many things

### Optional:
- **Configurable**: Customize many parts of your experience, like the exhaustion amounts, cooldown, block exemptions, specialization points gain, and run speed values
- **Permissions**: Allows the bypassing of systems or altering for selected roles such as players or admins
- **Exemptions**: Exclude specific block types from exhaustion gain
- **Cooldowns**: Prevent over-exhaustion by modifying cooldowns between exhaustion checks
- **Debug Mode**: (somewhat) Detailed logging for troubleshooting
- **Commands**: From basic functions like reloading the plugin, to advanced statistics to overview the servers specialization distribution

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
  enabled: true                 # Enable or disable the plugin
  debug: true                   # Debug mode for server-side logging
  player-notifications: false   # Send player notifications of their activity
  admin-notifications: true     # Send admin notifications for commands (you shouldn't set this to false :) )
```

### Exhaustion Settings

```yaml
exhaustion:
  place-block: 1.0           # Amount of exhaustion when placing a block (4 points = 1 hunger or saturation)
  mining-block: 1.0          # Amount of exhaustion when destroying a block (4 points = 1 hunger or saturation)
  farming-block: 1.0         # Amount of exhaustion when farming a block (4 points = 1 hunger or saturation)
  hit-entity: 1.0            # Amount of exhaustion when hitting entities (4 points = 1 hunger or saturation)
  default: 1.0               # Amount of exhaustion when doing a default action (4 points = 1 hunger or saturation)
  minimum-food-level: 0.0    # Minimum food level before reducing (prevents starvation if above 1)
  cooldown: 100              # Cooldown between exhaustion in milliseconds (0 = no cooldown)
```

### Specialization Settings

```yaml
specialization:
  mining: 1                 # Amount of points to gain when mining
  farming: 1                # Amount of points to gain when farming
  building: 1               # Amount of points to gain when building
  fighting: 1               # Amount of points to gain when fighting
  athletics: 1              # Amount of points to gain when being athletic
  max-points: 1000          # Maximum points a player can have
  auto-save-interval: 10    # Auto-save interval in minutes (0 = disabled, only save on server shutdown)
```

### Athletics Settings

```yaml
run-speed:
  base-speed: 0.25  # Base run speed when sprinting
  max-speed: 0.9    # Maximum run speed (minecraft max sprint speed is 1.0, going beyond will break it)
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

### Notification Messages

```yaml
admin-notification-messages:
  prefix: "<dark_gray>[<red>Simulation of Life</red>]</dark_gray> "
  exhaustion-reduced: "<yellow>Exhaustion: <red>{exhaustion}</red>/<green>4</green></yellow>"
  plugin-reloaded: "<green>Plugin configuration reloaded!</green>"
  plugin-status: "<green>Plugin is <dark_green>enabled</dark_green>"
  plugin-disabled: "<red>Plugin is <dark_red>disabled</dark_red></red>" 
```

# Commands

### Admin Commands

- `/simulationoflife stats`             - Show your specialization statistics
- `/simulationoflife status`            - Show plugin status and settings
- `/simulationoflife perf`              - Show performance statistics
- `/simulationoflife debug`             - Show debug information
- `/simulationoflife spec-all`          - Show specialization statistics
- `/simulationoflife spec-set`          - Set a player's specialization level
- `/simulationoflife spec-reset`        - Reset a player's every specialization to 0
- `/simulationoflife spec-reset-all`    - Reset ALL players' every specialization to 0
- `/simulationoflife save`              - Save all players' specializations to file
- `/simulationoflife reload`            - Reload the plugin configuration

### Player Commands

- `/simulationoflife stats`  - Show your specialization statistics

# In-Game Permissions

- `simulationoflife.admin`    - Access to admin commands (default: op)
- `simulationoflife.player`   - Access to only player commnands (default: true)
- `simulationoflife.bypass`   - Bypass hunger reduction from block actions (default: false)

# Troubleshooting

### Common Issues
- Specialization only saves to file if player has at least 1 point in something (This could lead to a problem if specializations are reset for a player as it may not update)
- Some blocks like Short and Tall Grass still being incorrectly detected

# Upcoming features

- More extensive statistics tracking and saving to file - My data science desires must be fulfilled
- Weight system - can't have you carrying all those stacks of wood ;)
- Difficulty setting - so you can play either solo, with few friends, or on large servers in a balanced way
