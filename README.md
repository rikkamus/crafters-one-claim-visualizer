# Crafters.one Claim Visualizer
A client-side mod for the [Crafters.one](https://crafters.one/) Minecraft server that renders land claim boundaries in-game.

## Usage
Claim rendering can be toggled using commands:

| Command                      | Usage                                                           |
|------------------------------|-----------------------------------------------------------------|
| <code>/claims show</code>    | Enables claim boundary rendering and shows claim info overlay.  |
| <code>/claims hide</code>    | Disables claim boundary rendering and hides claim info overlay. |
| <code>/claims refresh</code> | Reloads claim data from the API.                                |

## Configuration
Claim boundaries and the claim info overlay can be customized using [Cloth Config](https://www.curseforge.com/minecraft/mc-mods/cloth-config).

## Dependencies
### NeoForge
* [Cloth Config (optional)](https://www.curseforge.com/minecraft/mc-mods/cloth-config)

### Fabric
* [**Fabric API (REQUIRED)**](https://www.curseforge.com/minecraft/mc-mods/fabric-api)
* [Cloth Config (optional)](https://www.curseforge.com/minecraft/mc-mods/cloth-config)
* [Mod Menu (optional)](https://www.curseforge.com/minecraft/mc-mods/modmenu)

## Building
To build the project, run `gradlew build` in the project's root directory.
