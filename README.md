<p align="center">
  <img src="src/main/resources/assets/postman/textures/postman-logo-transparent.png" alt="Postman" width="175" />
</p>

# Postman — Fabric 1.21.11

[![Version](https://img.shields.io/badge/version-0.6.0-79C1FF)](CHANGELOG.md)
[![Minecraft](https://img.shields.io/badge/Minecraft-1.21.11-79C1FF)](https://www.minecraft.net/)
[![Java](https://img.shields.io/badge/Java-21-79C1FF)](https://adoptium.net/)
[![License](https://img.shields.io/badge/license-MIT-79C1FF)](LICENSE)

**Maintained by Sq33.** An unofficial, open-source continuation of Postman for Minecraft 1.21.11 using Fabric. The goal is to restore and modernize features from the original Forge 1.12.2 client and the upstream Fabric project, while keeping the original ClickGUI and feel. Use only in singleplayer or on servers that permit client modifications.

> **Development release:** 0.6.0. This is an evolving port, not an official upstream release. Some rendering and legacy features still need in-game testing.

## Features

- Original-style ClickGUI and customizable HUD, including full-text RGB watermark.
- Render modules: Nametags, ESP, HoleESP, Tracers, Peek, FullBright, Freecam, CameraClip, DamageTilt, LowOffHand, ViewModel, World, NoRender, NewChunks and Xray.
- Local PlayerClone support, including equipment and custom nametags.
- Config saving, keybinds and client commands.

NewChunks marks chunks received during the current session; it does not detect when terrain was generated. Xray highlights ores rather than hiding terrain. ESP uses a projected overlay, not a depth-tested world renderer.

## Requirements

| Dependency | Version |
| --- | --- |
| Minecraft | 1.21.11 |
| Java | 21 |
| Fabric Loader | 0.19.2 or newer |
| Fabric API | 0.141.4+1.21.11 |
| Fabric Loom (development) | 1.14.10 |

## Installation

1. Install [Fabric Loader](https://fabricmc.net/use/installer/) for Minecraft 1.21.11.
2. Download a compatible [Fabric API](https://modrinth.com/mod/fabric-api) and the Postman release JAR from this fork's Releases page once published.
3. Place both JARs in `.minecraft/mods/` and launch your Fabric profile.

Right Shift opens the ClickGUI. Left-click a module to toggle it; right-click to open its settings. The default command prefix is `,`.

## Build from source

Install JDK 21. Open a terminal in the repository root.

Windows (PowerShell):

```powershell
.\gradlew.bat runClient
.\gradlew.bat clean build
```

Linux / macOS:

```bash
chmod +x gradlew
./gradlew runClient
./gradlew clean build
```

The distributable JAR is `build/libs/postman-0.6.0.jar`. Do not install the `-sources.jar`. To change the release version, edit `mod_version` in `gradle.properties`; Fabric metadata, the JAR and the in-game watermark use this version automatically. The README badge and changelog should also be updated manually.

GitHub Actions is configured to run a Java 21 build on pushes and pull requests and archive the JAR when the build succeeds. It does not publish releases automatically.

## Credits

- **Sq33** — maintainer of this continuation, Minecraft 1.21.11 port, module restoration, and ongoing development.
- **SrgantMooMoo** — original creator of Postman and the upstream source code.
- **TechAle, RECIOR, fluffery, theforecaster, cattyngmd/cattyn, MWA** — upstream and legacy contributions.
- **lukflug** — PanelStudio and upstream support.
- The Fabric, Yarn and Minecraft modding communities, and all other original contributors.

Original project: [Postman](https://github.com/srgantmoomoo/postman). The original README is preserved in [README.upstream.md](README.upstream.md).

## License

[MIT](LICENSE). Original copyright and license notices are retained. This continuation is not affiliated with Mojang, Microsoft or the original Postman maintainers.
