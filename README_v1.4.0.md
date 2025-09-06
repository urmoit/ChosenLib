# ChosenLib v1.4.0 - Release Notes

## 🎉 What's New in v1.4.0

ChosenLib v1.4.0 introduces major new utility classes and significant performance improvements, making it the most comprehensive utility library for Fabric mods.

### 🌍 WorldUtils - World and Block Manipulation
A comprehensive utility class for world and block operations:

- **Block Operations**: Safe block getting/setting with chunk loading checks
- **Area Operations**: Get blocks in cubes, spheres, and rectangular areas
- **Block Finding**: Find specific blocks, nearest blocks, and replace blocks in areas
- **World Queries**: Get biomes, light levels, check sky visibility
- **Chunk Operations**: Chunk loading checks and chunk position utilities
- **Fluid Operations**: Fluid state checking and validation
- **Entity Queries**: Get entities in areas with filtering support

```java
// Example usage
List<BlockPos> blocks = WorldUtils.getBlocksInSphere(center, 5.0);
BlockPos nearest = WorldUtils.findNearestBlock(world, pos, 10, state -> state.isOf(Blocks.DIAMOND_ORE));
boolean canPlace = WorldUtils.canPlaceBlock(world, pos, newState);
```

### 👤 EntityUtils - Entity-Related Operations
Comprehensive entity manipulation and query utilities:

- **Health Management**: Get, set, heal, and damage entities safely
- **Status Effects**: Add, remove, and check status effects
- **Attributes**: Get and modify entity attributes
- **Teleportation**: Teleport entities with validation
- **Distance Calculations**: Efficient distance and proximity checks
- **Entity Finding**: Find entities by type, nearest entities, players in radius
- **Entity Properties**: Check alive/dead status, invulnerability, fire status
- **Inventory Operations**: Give items to players, send messages
- **Physics**: Set velocity, launch entities, knockback effects

```java
// Example usage
EntityUtils.heal(entity, 10.0f);
EntityUtils.teleport(entity, targetPos);
List<PlayerEntity> nearbyPlayers = EntityUtils.getPlayersInRadius(world, center, 10.0);
EntityUtils.launchTowards(entity, target, 2.0);
```

### 🌐 NetworkUtils - Networking and Packet Utilities
Advanced networking utilities for client-server communication:

- **Packet Creation**: Easy packet buffer creation and management
- **Player Targeting**: Send packets to specific players, groups, or areas
- **Data Serialization**: Safe reading/writing of common data types
- **Bulk Operations**: Send to multiple players, worlds, or radius-based
- **Error Handling**: Safe operations with exception handling
- **Array Support**: Built-in support for arrays of primitives and strings
- **Utility Packets**: Pre-built packet types for common operations

```java
// Example usage
PacketByteBuf buffer = NetworkUtils.createBuffer(buf -> {
    NetworkUtils.writeString(buf, "Hello World");
    NetworkUtils.writeBlockPos(buf, pos);
});
NetworkUtils.sendToPlayersInRadius(world, center, 50.0, channelId, buffer);
```

### 🎨 GuiUtils - Now Server-Side Compatible!
GuiUtils has been redesigned to work on both server and client:

- **Server-Side**: Color utilities, positioning calculations, mathematical operations
- **Client-Side**: Extended with `ClientGuiUtils` for rendering operations
- **Performance**: Cached color operations and optimized calculations
- **New Features**: Color interpolation, rectangle overlap detection, distance calculations

```java
// Server-side usage
int color = GuiUtils.rgb(255, 128, 0);
int centered = GuiUtils.centerX(elementWidth, containerWidth);
boolean overlaps = GuiUtils.rectanglesOverlap(x1, y1, w1, h1, x2, y2, w2, h2);

// Client-side usage (extends server-side)
ClientGuiUtils.drawProgressBar(context, x, y, width, height, progress, bgColor, fgColor);
ClientGuiUtils.drawCenteredTextWithShadow(context, text, centerX, y, color);
```

### ⚡ Performance Optimizations

#### ItemUtils Enhancements
- **Caching**: Item ID and max stack size caching for frequently accessed properties
- **Enhanced Operations**: New methods for item manipulation, enchantment checking, NBT handling
- **Inventory Operations**: Advanced inventory management with insertion, consumption, and searching
- **Durability Management**: Comprehensive durability and repair operations

```java
// New features
ItemStack result = ItemUtils.insertStack(inventory, stack);
int consumed = ItemUtils.consumeItem(inventory, Items.DIAMOND, 5);
boolean hasEnchant = ItemUtils.hasEnchantment(stack, enchantment);
ItemUtils.repairItem(stack, 100);
```

#### TextUtils Improvements
- **Pattern Caching**: Compiled regex patterns are cached for better performance
- **Style Caching**: Pre-computed formatting styles for faster text creation
- **New Features**: Text wrapping, duration formatting, number formatting, interactive text
- **Enhanced Validation**: Improved email validation, pattern matching with caching

```java
// New features
List<String> wrapped = TextUtils.wrapText(longText, 40);
String duration = TextUtils.formatDuration(milliseconds);
Text interactive = TextUtils.interactiveText("Click me", "Tooltip", "https://example.com");
Text progress = TextUtils.progressBar(0.75, 20);
```

## 🔧 Technical Improvements

### Memory Optimization
- Concurrent hash maps for thread-safe caching
- Lazy initialization of expensive operations
- Efficient data structures for common operations

### Error Handling
- Comprehensive null safety checks
- Graceful degradation when operations fail
- Detailed error logging for debugging

### API Design
- Consistent method naming and parameter ordering
- Comprehensive JavaDoc documentation
- Backward compatibility maintained

## 📚 Usage Examples

### Complete World Manipulation Example
```java
// Find and replace all stone with cobblestone in a 10-block radius
List<BlockPos> area = WorldUtils.getBlocksInSphere(playerPos, 10.0);
int replaced = WorldUtils.replaceBlocks(world, area, 
    state -> state.isOf(Blocks.STONE), 
    Blocks.COBBLESTONE.getDefaultState(), 
    Block.NOTIFY_ALL);
```

### Advanced Entity Management
```java
// Heal all players within 20 blocks and give them speed
List<PlayerEntity> players = EntityUtils.getPlayersInRadius(world, center, 20.0);
for (PlayerEntity player : players) {
    EntityUtils.heal(player, 20.0f);
    EntityUtils.addStatusEffect(player, new StatusEffectInstance(StatusEffects.SPEED, 200, 1));
    EntityUtils.sendMessage(player, TextUtils.success("You have been blessed!"));
}
```

### Network Communication
```java
// Send custom data to all players in the world
PacketByteBuf data = NetworkUtils.createBuffer(buffer -> {
    NetworkUtils.writeString(buffer, "server_event");
    NetworkUtils.writeVec3d(buffer, eventLocation);
    NetworkUtils.writeIntArray(buffer, new int[]{1, 2, 3, 4, 5});
});
NetworkUtils.sendToWorld(serverWorld, CUSTOM_PACKET_ID, data);
```

## 🚀 Migration Guide

### From v1.3.x to v1.4.0

1. **GuiUtils**: If you were using client-side GuiUtils, import `ClientGuiUtils` instead
2. **New Dependencies**: The new utility classes have no breaking changes to existing APIs
3. **Performance**: Existing code will automatically benefit from caching improvements

### Recommended Updates
- Replace manual world/entity operations with the new utility methods
- Use the new networking utilities for cleaner packet handling
- Take advantage of the enhanced ItemUtils for inventory management

## 📋 Complete Feature List

### WorldUtils (New)
- Block state operations with safety checks
- Area-based block operations (cube, sphere, rectangle)
- Block finding and replacement utilities
- World property queries (biome, light, sky visibility)
- Chunk management utilities
- Fluid state operations
- Entity area queries

### EntityUtils (New)
- Health and damage management
- Status effect operations
- Entity attribute manipulation
- Teleportation with validation
- Distance and proximity utilities
- Entity finding and filtering
- Physics operations (velocity, knockback)
- Player-specific operations

### NetworkUtils (New)
- Packet buffer creation and management
- Multi-target packet sending
- Safe data serialization/deserialization
- Array and collection support
- Error-resistant operations
- Pre-built packet utilities

### GuiUtils (Enhanced)
- Server-side compatibility
- Advanced color operations
- Mathematical utilities
- Client-side rendering (ClientGuiUtils)
- Performance optimizations

### ItemUtils (Enhanced)
- Performance caching
- Enhanced item operations
- Inventory management
- Enchantment utilities
- NBT data handling
- Durability management

### TextUtils (Enhanced)
- Pattern and style caching
- Text wrapping and formatting
- Interactive text components
- Duration and number formatting
- Enhanced validation

## 🎯 What's Next

ChosenLib v1.4.0 establishes a solid foundation for comprehensive mod development. Future versions will focus on:

- Recipe and crafting utilities
- Advanced GUI framework
- Data persistence utilities
- Integration helpers for popular mods
- Performance profiling tools

---

**ChosenLib v1.4.0** - Making Fabric mod development easier, faster, and more reliable! 🚀