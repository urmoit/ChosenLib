# ChosenLib v1.4.0 – Changelog

## New in this release

### Major New Utility Classes

- **WorldUtils** - World and block manipulation utilities:
  - Block operations: `getBlockState()`, `setBlockState()`, `isPositionLoaded()`
  - Area queries: `getBlocksInCube()`, `getBlocksInSphere()`, `getBlocksInArea()`
  - Block finding: `findBlocks()`, `findNearestBlock()`, `replaceBlocks()`
  - World properties: `getBiome()`, `getLightLevel()`, `canSeeSky()`
  - Chunk utilities: `getChunk()`, `isChunkLoaded()`, `getChunkPos()`
  - Fluid operations: `getFluidState()`, `hasFluid()`
  - Entity queries: `getEntitiesInBox()`, `getEntitiesAround()`, `getNearestEntity()`

- **EntityUtils** - Entity-related helper functions:
  - Health management: `getHealth()`, `setHealth()`, `heal()`, `damage()`
  - Status effects: `hasStatusEffect()`, `addStatusEffect()`, `removeStatusEffect()`
  - Attributes: `getAttributeValue()`, `setAttributeBaseValue()`
  - Teleportation: `teleport()`, `teleportToEntity()`
  - Distance utilities: `getDistance()`, `isWithinDistance()`
  - Entity finding: `getEntitiesInRadius()`, `findNearestPlayer()`
  - Physics: `setVelocity()`, `launchTowards()`, `knockback()`
  - Player operations: `sendMessage()`, `giveItem()`

- **NetworkUtils** - Networking and packet utilities:
  - Packet creation: `createBuffer()`, `createSimplePacket()`, `createPositionPacket()`
  - Player targeting: `sendToPlayer()`, `sendToPlayers()`, `sendToWorld()`
  - Radius sending: `sendToPlayersInRadius()`
  - Data serialization: `writeString()`, `writeBlockPos()`, `writeVec3d()`, `writeUuid()`
  - Array support: `writeBooleanArray()`, `writeIntArray()`, `writeStringArray()`
  - Safe operations: `safeBufferOperation()`, `hasReadableBytes()`

### Enhanced Existing Utilities

- **GuiUtils** - Now server-side compatible with major enhancements:
  - Server-side utilities: `centerX()`, `centerY()`, `rgba()`, `rgb()`, `withAlpha()`
  - Color operations: `getRed()`, `getGreen()`, `getBlue()`, `getAlpha()`
  - Mathematical: `isPointInRect()`, `rectanglesOverlap()`, `distance()`
  - Color manipulation: `hsvToRgb()`, `adjustBrightness()`, `interpolateColor()`
  - Progress utilities: `calculateProgressWidth()`
  - Clamping: `clamp()` for int, float, and double

- **ClientGuiUtils** - Client-side rendering extensions:
  - Screen utilities: `centerXOnScreen()`, `centerYOnScreen()`, `getScreenWidth()`
  - Drawing: `drawRect()`, `drawBorderedRect()`, `drawGradient()`, `drawTexture()`
  - Text rendering: `drawText()`, `drawCenteredText()`, `drawTooltip()`
  - UI components: `drawButton()`, `drawCheckbox()`, `drawProgressBar()`
  - Text utilities: `getTextWidth()`, `getTextHeight()`

- **ItemUtils** - Performance and feature enhancements:
  - Enhanced validation: `canMerge()`, `isDamageable()`, `isEnchanted()`
  - Item lookup: `getItemById()` for Identifier and String
  - Durability: `getDurabilityPercentage()`, `damageItem()`, `repairItem()`, `fullyRepairItem()`
  - NBT operations: `getNbt()`, `setNbt()`, `hasNbt()`
  - Enchantments: `getEnchantmentLevel()`, `hasEnchantment()`
  - Inventory: `findFirst()`, `findAll()`, `countItem()`, `consumeItem()`, `insertStack()`
  - Performance: Caching for `getItemId()` and `getMaxStackSize()`

- **TextUtils** - Advanced text operations with performance improvements:
  - Interactive text: `clickableCommand()`, `withTooltip()`, `interactiveText()`
  - Text wrapping: `wrapText()`, `createMultiLineTextBox()`
  - Validation: `isBlank()`, `matches()` with pattern caching
  - Formatting: `formatNumber()`, `formatDecimal()`, `formatDuration()`
  - Enhanced utilities: `removeSpecialCharsKeepSpaces()`, `join()` for Lists
  - Progress bars: `progressBar()` with percentage support
  - Performance: Cached regex patterns and formatting styles

### Performance Optimizations

- **Caching Systems**:
  - ItemUtils: Concurrent caching for item IDs and stack sizes
  - TextUtils: Compiled regex pattern caching and pre-computed styles
  - Thread-safe concurrent hash maps for all caches
  - Cache statistics and management: `getCacheStats()`, `clearCache()`

### Command Improvements

- **/chosenlib Command Enhancements**:
  - Updated to showcase all new v1.4.0 features
  - Feature highlights for WorldUtils, EntityUtils, and NetworkUtils
  - Improved formatting with success/info message types
  - Enhanced user experience with comprehensive feature overview

### General

- Internal version updated to **1.4.0**
- All utility classes fully documented with comprehensive JavaDoc
- Extensive null safety checks and error handling
- Thread-safe operations across all utilities
- Backward compatibility maintained - no breaking changes

---

_This is a major feature release introducing comprehensive world, entity, and networking utilities with significant performance improvements. All existing code continues to work unchanged and automatically benefits from performance enhancements!_