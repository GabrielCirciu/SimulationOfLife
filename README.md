# MCSurvivalFolia

A Minecraft plugin for Folia that adds survival mechanics by reducing player hunger when placing or destroying blocks.

## Features

- **Hunger Reduction**: Players lose hunger when placing or destroying blocks
- **Configurable Settings**: Customize hunger reduction amounts, cooldowns, and exempt blocks
- **Permission System**: Bypass permissions for admins or specific players
- **Block Exemptions**: Exclude specific block types from hunger reduction
- **Cooldown System**: Prevent spam by implementing cooldowns between hunger reductions
- **Debug Mode**: Detailed logging for troubleshooting
- **Admin Commands**: Reload configuration and check plugin status

## Requirements

- **Minecraft Server**: Folia 1.20.4 or higher
- **Java**: Java 17 or higher
- **Maven**: For building the plugin

## Installation

### Building from Source

1. Clone this repository:
   ```bash
   git clone https://github.com/yourusername/mc-survival-folia.git
   cd mc-survival-folia
   ```

2. Build the plugin using Maven:
   ```bash
   mvn clean package
   ```

3. Copy the generated JAR file from `target/mc-survival-folia-1.0.0.jar` to your server's `plugins` folder.

4. Start or restart your Folia server.

### Download Pre-built JAR

1. Download the latest release JAR file from the releases page.
2. Place the JAR file in your server's `plugins` folder.
3. Start or restart your Folia server.

## Configuration

The plugin creates a `config.yml` file in the `plugins/MCSurvivalFolia/` directory. Here's what each section does:

### General Settings

```yaml
general:
  enabled: true    # Enable or disable the plugin
  debug: false     # Enable debug logging
```

### Hunger Settings

```yaml
hunger:
  place-block: 0.5      # Hunger reduction when placing blocks (0.0 - 20.0)
  destroy-block: 0.3    # Hunger reduction when destroying blocks (0.0 - 20.0)
  minimum-hunger: 2.0   # Minimum hunger level (prevents starvation)
  cooldown: 1000        # Cooldown between hunger reductions in milliseconds
```

### Block Exemptions

```yaml
blocks:
  place-exempt:         # Blocks that don't cause hunger when placed
    - AIR
    - CAVE_AIR
    - VOID_AIR
    - WATER
    - LAVA
    - FIRE
  
  destroy-exempt:       # Blocks that don't cause hunger when destroyed
    - AIR
    - CAVE_AIR
    - VOID_AIR
    - WATER
    - LAVA
    - FIRE
    - GRASS
    - TALL_GRASS
    - DEAD_BUSH
    - FERN
    - LARGE_FERN
```

### Messages

```yaml
messages:
  prefix: "&8[&cMCSurvival&8] &r"  # Message prefix
  hunger-reduced: "&eYou feel tired from your work. Hunger: &c{hunger}&e/&a20"
  plugin-reloaded: "&aPlugin configuration reloaded!"
  plugin-status: "&aPlugin is &2enabled&a. Hunger reduction: &e{place} &7(place), &e{destroy} &7(destroy)"
  plugin-disabled: "&cPlugin is &4disabled&c."
```

## Commands

### Admin Commands

- `/mcsurvival reload` - Reload the plugin configuration
- `/mcsurvival status` - Show plugin status and current settings

### Permissions

- `mcsurvival.admin` - Access to admin commands (default: op)
- `mcsurvival.bypass` - Bypass hunger reduction from block actions (default: false)

## Usage

### For Players

1. The plugin automatically activates when you place or destroy blocks in survival mode
2. You'll receive a message when your hunger is reduced
3. Players with the `mcsurvival.bypass` permission won't lose hunger
4. The plugin respects minimum hunger levels to prevent starvation

### For Server Administrators

1. **Installation**: Place the JAR file in your `plugins` folder and restart the server
2. **Configuration**: Edit `plugins/MCSurvivalFolia/config.yml` to customize settings
3. **Reloading**: Use `/mcsurvival reload` to apply configuration changes without restarting
4. **Monitoring**: Use `/mcsurvival status` to check plugin status and settings
5. **Debugging**: Enable debug mode in the config to see detailed logs

## Customization Examples

### More Realistic Hunger System

```yaml
hunger:
  place-block: 0.2      # Less hunger for placing
  destroy-block: 0.4    # More hunger for breaking
  minimum-hunger: 3.0   # Higher minimum to prevent starvation
  cooldown: 2000        # 2-second cooldown
```

### Hardcore Mode

```yaml
hunger:
  place-block: 1.0      # High hunger cost for placing
  destroy-block: 1.5    # Very high hunger cost for breaking
  minimum-hunger: 1.0   # Lower minimum for challenge
  cooldown: 500         # Short cooldown for frequent actions
```

### Exempting More Blocks

```yaml
blocks:
  place-exempt:
    - AIR
    - CAVE_AIR
    - VOID_AIR
    - WATER
    - LAVA
    - FIRE
    - TORCH
    - REDSTONE_TORCH
    - LEVER
    - BUTTON
    - PRESSURE_PLATE
```

## Troubleshooting

### Common Issues

1. **Plugin not working**: Check if the plugin is enabled in the config
2. **No hunger reduction**: Verify the player is in survival mode and doesn't have bypass permission
3. **Configuration not loading**: Use `/mcsurvival reload` to reload the config
4. **Debug information**: Enable debug mode in the config to see detailed logs

### Logs

Check your server logs for messages starting with `[MCSurvivalFolia]` for plugin-related information.

## Development

### Building

```bash
mvn clean package
```

### Testing

1. Build the plugin
2. Place the JAR in a test server's plugins folder
3. Test block placement and destruction
4. Verify hunger reduction and cooldowns work correctly

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Test thoroughly
5. Submit a pull request

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Support

If you encounter any issues or have questions:

1. Check the troubleshooting section above
2. Enable debug mode and check server logs
3. Create an issue on the GitHub repository
4. Include your server version, plugin version, and relevant logs

## Changelog

### Version 1.0.0
- Initial release
- Basic hunger reduction system
- Configurable settings
- Block exemptions
- Cooldown system
- Admin commands
- Permission system 