## ChosenLib

A lightweight utility library for Fabric that streamlines common modding tasks. Use it to bootstrap new mods faster and share reusable helpers across projects.

- Modern toolchain: Fabric Loader, Fabric API, Java 21, Yarn mappings
- Works on client and server unless noted
- Open-source and developer-friendly

## Features
- Utility helpers: `TextUtils` for clean, localized messages
- Client conveniences: sample keybinding and tick hook
- Example mixin to safely start patching behavior
- Sensible Gradle setup with sources JAR and per-env source sets

## Requirements
- Fabric Loader
- Fabric API
- Java 21

## Installation (as a dependency)
1. Download the latest release JAR from Modrinth/CurseForge/GitHub Releases.
2. Drop it into your `mods/` folder alongside Fabric API.

Or as a Gradle dependency (example):
```gradle
repositories {
    mavenCentral()
    maven { url = 'https://maven.fabricmc.net/' }
}

dependencies {
    modImplementation "com.yourgroup:chosenlib:<version>"
}
```

## Usage
- Mod entrypoint: `com.chosen.lib.ChosenLib`
- Client entrypoint: `com.chosen.lib.client.ChosenClient`
- Utilities:
```java
import com.chosen.lib.util.TextUtils;
// Example: TextUtils.withPrefix("Hello")
```
- See `src/main/java` for examples and mixins.

## Building from source
```bash
./gradlew clean build
```
Artifacts are in `build/libs/`:
- `<name>-<version>.jar` (use this)
- `<name>-<version>-sources.jar` (sources)

## Publishing (manual)
- Upload the remapped JAR (no `-dev`/`-sources`) to Modrinth/CurseForge.
- Mark Fabric API as a required dependency.
- Provide a changelog and set release type.

## Roadmap
- More utility classes (math, NBT, data)
- Optional integrations without hard Fabric API requirement
- Versioned releases for multiple Minecraft versions

## Contributing
Issues and PRs are welcome. Please keep code readable and match the existing style.

## License
MIT. See `[LICENSE](https://github.com/urmoit/ChosenLib/blob/main/LICENSE)`.

## Links
- Fabric: https://fabricmc.net/
- Yarn mappings: https://github.com/FabricMC/yarn

