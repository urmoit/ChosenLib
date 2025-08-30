## ChosenLib

A lightweight utility library for Fabric that streamlines common modding tasks. Use it to bootstrap new mods faster and share reusable helpers across projects.

- Modern toolchain: Fabric Loader, Fabric API, Java 21, Yarn mappings
- Works on client and server unless noted
- Open-source and developer-friendly

## Features
- Utility helpers: `TextUtils` for clean, localized messages
- General-purpose helpers: `ChosenLib` static utilities (random, clamp, lerp, etc.)
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
    modImplementation "com.yourgroup:chosenlib:1.1.0"
}
```

## Usage
- Mod entrypoint: `com.chosen.lib.ChosenLib`
- Client entrypoint: `com.chosen.lib.client.ChosenClient`
- Utilities:
```java
import com.chosen.lib.util.TextUtils;
import com.chosen.lib.ChosenLib;

// TextUtils examples:
TextUtils.success("Operation successful!");
TextUtils.error("Something went wrong!");
TextUtils.capitalize("hello world");
TextUtils.repeat("abc", 3); // "abcabcabc"
TextUtils.isNullOrEmpty(""); // true
TextUtils.join(", ", "a", "b", "c"); // "a, b, c"

// ChosenLib general utilities:
ChosenLib.randomInt(1, 10); // random int between 1 and 10
ChosenLib.clamp(15, 0, 10); // 10
ChosenLib.lerp(0.0, 10.0, 0.5); // 5.0
ChosenLib.isBetween(5, 1, 10); // true
ChosenLib.safeEquals("a", "a"); // true
ChosenLib.debugLog("Debug message");
```
- See `src/main/java` for examples and mixins.

## Building from source
```bash
./gradlew clean build
```
Artifacts are in `build/libs/`:
- `<name>-1.1.0.jar` (use this)
- `<name>-1.1.0-sources.jar` (sources)

## Publishing (manual)
- Upload the remapped JAR (no `-dev`/`