# ChosenLib v1.5.0

A comprehensive utility library for Fabric that streamlines common modding tasks with advanced features. Use it to bootstrap new mods faster and share powerful reusable helpers across projects.

- **Modern toolchain**: Fabric Loader, Fabric API, Java 21, Yarn mappings
- **Works on client and server** unless noted
- **Open-source and developer-friendly**
- **Production-ready** with extensive error handling and performance optimization

## 🚀 New in v1.5.0

### Major New Utility Classes
- **🔧 AdvancedBlockOps** - Enhanced block manipulation with safety checks and transactional editing
- **🤖 EntityAIUtils** - AI utilities for custom mobs and advanced entity behaviors  
- **💾 DataPersistence** - World, player, and global data storage with compression
- **✨ EffectsUtils** - Sound & particle effect utilities with timing control
- **🌐 AdvancedNetworking** - Sophisticated packet handling with encryption and compression
- **🌍 DimensionUtils** - Custom dimension management and portal systems
- **🔴 RedstoneUtils** - Circuit analysis, automation, and logic gates
- **📊 PerformanceMonitor** - Built-in profiling and optimization tools

### Enhanced Existing Utilities
- **WorldUtils** - Transactional editing, undo system, pattern analysis
- **EntityUtils** - AI integration, enhanced pathfinding, memory system
- **NetworkUtils** - Reliable packets, compression, encryption
- **ItemUtils** - Data persistence, advanced validation, custom items
- **TextUtils** - Rich formatting, performance optimization, localization

### Performance Improvements
- **Multi-level caching** (L1/L2/L3) with intelligent invalidation
- **Batch operations** for blocks, entities, packets, and items
- **Smart object pooling** and memory leak detection

Read Full Changelog here: https://urmoit.github.io/ChosenLib/changelog.html or https://github.com/urmoit/ChosenLib/blob/main/Changelogs/CHANGELOG_v1.5.0.md

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
    modImplementation "com.yourgroup:chosenlib:1.5.0"
}
```

## Usage

### Command System
- `/chosenlib` - Main command with feature showcase
- `/chosenlib demo` - Interactive demonstrations  
- `/chosenlib perf` - Real-time performance monitoring
- `/chosenlib stats` - Statistics overview
- `/chosenlib report` - Detailed performance analysis

### Basic Utilities
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
```

### Advanced Features (v1.5.0)
```java
import com.chosen.lib.util.*;

// Advanced Block Operations
AdvancedBlockOps.TransactionResult result = AdvancedBlockOps.fillAreaSafely(
    world, area, blockState, "my_operation");
if (result.success) {
    // Operation completed successfully
}

// Entity AI Management
EntityAIUtils.setBehaviorState(entity, BehaviorState.HUNTING);
EntityAIUtils.triggerBehavior(entity, BehaviorState.FOLLOWING, target);

// Data Persistence
JsonObject data = new JsonObject();
data.addProperty("key", "value");
DataPersistence.saveWorldData(world, "my_data", data, true); // compress=true

// Sound & Particle Effects
EffectsUtils.createExplosionEffect(world, pos, 3.0, 1.0f);
EffectsUtils.spawnParticleSphere(world, center, radius, ParticleTypes.ENCHANT, 50, 0.3);

// Performance Monitoring
PerformanceMonitor.ProfileData profile = PerformanceMonitor.startProfiling("my_operation");
// ... do work ...
long elapsedTime = PerformanceMonitor.stopProfiling("my_operation");
```

### Entry Points
- Mod entrypoint: `com.chosen.lib.ChosenLib`
- Client entrypoint: `com.chosen.lib.client.ChosenClient`

## Building from source
```bash
./gradlew clean build
```
Artifacts are in `build/libs/`:
- `<name>-1.5.0.jar` (use this)
- `<name>-1.5.0-sources.jar` (sources)

## 📋 Version History and raodmap: https://urmoit.github.io/ChosenLib/changelog.html

## 📄 License

MIT License - see [LICENSE](LICENSE) file for details.

## 🤝 Contributing

Contributions are welcome! Please read our contributing guidelines and submit pull requests to our GitHub repository.

## 📞 Support

- **GitHub Issues:** [Report bugs and request features](https://github.com/urmoit/ChosenLib/issues)
- **Discord:** [Join our community](https://discord.gg/CFwkrnFD4P)
- **Documentation:** [Full API documentation](https://urmoit.github.io/ChosenLib/index.html)
