package com.chosen.lib.util;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Utility class for item-related operations in Minecraft mods.
 * Provides helpers for item validation, manipulation, and common item patterns.
 * Includes performance optimizations and caching for frequently used operations.
 */
public class ItemUtils {
    
    // Performance optimization: Cache frequently accessed item properties
    private static final Map<Item, String> ITEM_ID_CACHE = new ConcurrentHashMap<>();
    private static final Map<Item, Integer> MAX_STACK_SIZE_CACHE = new ConcurrentHashMap<>();
    
    /**
     * Checks if an ItemStack is empty or null.
     * @param stack The ItemStack to check.
     * @return True if the stack is empty or null.
     */
    public static boolean isEmpty(ItemStack stack) {
        return stack == null || stack.isEmpty();
    }
    
    /**
     * Checks if an ItemStack is not empty.
     * @param stack The ItemStack to check.
     * @return True if the stack is not empty.
     */
    public static boolean isNotEmpty(ItemStack stack) {
        return !isEmpty(stack);
    }
    
    /**
     * Gets the item ID as a string with caching for performance.
     * @param item The item.
     * @return The item ID string.
     */
    public static String getItemId(Item item) {
        if (item == null) return "null";
        return ITEM_ID_CACHE.computeIfAbsent(item, i -> Registries.ITEM.getId(i).toString());
    }
    
    /**
     * Gets the item ID as a string from an ItemStack.
     * @param stack The ItemStack.
     * @return The item ID string.
     */
    public static String getItemId(ItemStack stack) {
        if (isEmpty(stack)) return "null";
        return getItemId(stack.getItem());
    }
    
    /**
     * Gets an item by its identifier.
     * @param identifier The item identifier.
     * @return The item, or Items.AIR if not found.
     */
    public static Item getItemById(Identifier identifier) {
        return Registries.ITEM.getOrEmpty(identifier).orElse(Items.AIR);
    }
    
    /**
     * Gets an item by its string identifier.
     * @param id The item identifier string.
     * @return The item, or Items.AIR if not found.
     */
    public static Item getItemById(String id) {
        try {
            return getItemById(Identifier.of(id));
        } catch (Exception e) {
            return Items.AIR;
        }
    }
    
    /**
     * Copies an ItemStack with a new count.
     * @param stack The original ItemStack.
     * @param count The new count.
     * @return A new ItemStack with the specified count.
     */
    public static ItemStack copyWithCount(ItemStack stack, int count) {
        if (isEmpty(stack)) return ItemStack.EMPTY;
        
        ItemStack copy = stack.copy();
        copy.setCount(Math.max(0, count));
        return copy;
    }
    
    /**
     * Checks if two ItemStacks are the same item (ignoring count and NBT).
     * @param stack1 The first ItemStack.
     * @param stack2 The second ItemStack.
     * @return True if they are the same item.
     */
    public static boolean isSameItem(ItemStack stack1, ItemStack stack2) {
        if (isEmpty(stack1) || isEmpty(stack2)) return false;
        return stack1.getItem() == stack2.getItem();
    }
    
    /**
     * Checks if two ItemStacks are exactly the same (including count and NBT).
     * @param stack1 The first ItemStack.
     * @param stack2 The second ItemStack.
     * @return True if they are exactly the same.
     */
    public static boolean isExactMatch(ItemStack stack1, ItemStack stack2) {
        if (isEmpty(stack1) && isEmpty(stack2)) return true;
        if (isEmpty(stack1) || isEmpty(stack2)) return false;
        return ItemStack.areEqual(stack1, stack2);
    }
    
    /**
     * Checks if two ItemStacks can be merged (same item and NBT, ignoring count).
     * @param stack1 The first ItemStack.
     * @param stack2 The second ItemStack.
     * @return True if they can be merged.
     */
    public static boolean canMerge(ItemStack stack1, ItemStack stack2) {
        if (isEmpty(stack1) || isEmpty(stack2)) return false;
        return ItemStack.areItemsEqual(stack1, stack2) && ItemStack.areEqual(stack1, stack2);
    }
    
    /**
     * Gets the display name of an ItemStack.
     * @param stack The ItemStack.
     * @return The display name.
     */
    public static String getDisplayName(ItemStack stack) {
        if (isEmpty(stack)) return "Empty";
        return stack.getName().getString();
    }
    
    /**
     * Gets the display name as a Text component.
     * @param stack The ItemStack.
     * @return The display name as Text.
     */
    public static Text getDisplayNameText(ItemStack stack) {
        if (isEmpty(stack)) return Text.literal("Empty");
        return stack.getName();
    }
    
    /**
     * Gets the maximum stack size for an item with caching.
     * @param item The item.
     * @return The maximum stack size.
     */
    public static int getMaxStackSize(Item item) {
        if (item == null) return 0;
        return MAX_STACK_SIZE_CACHE.computeIfAbsent(item, Item::getMaxCount);
    }
    
    /**
     * Gets the maximum stack size for an ItemStack.
     * @param stack The ItemStack.
     * @return The maximum stack size.
     */
    public static int getMaxStackSize(ItemStack stack) {
        if (isEmpty(stack)) return 0;
        return getMaxStackSize(stack.getItem());
    }
    
    /**
     * Checks if an item can be stacked.
     * @param item The item.
     * @return True if the item can be stacked.
     */
    public static boolean isStackable(Item item) {
        return getMaxStackSize(item) > 1;
    }
    
    /**
     * Checks if an ItemStack can be stacked.
     * @param stack The ItemStack.
     * @return True if the stack can be stacked.
     */
    public static boolean isStackable(ItemStack stack) {
        return getMaxStackSize(stack) > 1;
    }
    
    /**
     * Gets the durability of an ItemStack.
     * @param stack The ItemStack.
     * @return The current durability, or -1 if not damageable.
     */
    public static int getDurability(ItemStack stack) {
        if (isEmpty(stack) || !stack.isDamageable()) return -1;
        return stack.getMaxDamage() - stack.getDamage();
    }
    
    /**
     * Gets the maximum durability of an ItemStack.
     * @param stack The ItemStack.
     * @return The maximum durability, or -1 if not damageable.
     */
    public static int getMaxDurability(ItemStack stack) {
        if (isEmpty(stack) || !stack.isDamageable()) return -1;
        return stack.getMaxDamage();
    }
    
    /**
     * Checks if an ItemStack is damaged.
     * @param stack The ItemStack.
     * @return True if the stack is damaged.
     */
    public static boolean isDamaged(ItemStack stack) {
        if (isEmpty(stack)) return false;
        return stack.isDamaged();
    }
    
    /**
     * Checks if an ItemStack is damageable.
     * @param stack The ItemStack.
     * @return True if the stack is damageable.
     */
    public static boolean isDamageable(ItemStack stack) {
        if (isEmpty(stack)) return false;
        return stack.isDamageable();
    }
    
    /**
     * Gets the damage percentage of an ItemStack.
     * @param stack The ItemStack.
     * @return The damage percentage (0.0 to 1.0), or -1 if not damageable.
     */
    public static double getDamagePercentage(ItemStack stack) {
        if (isEmpty(stack) || !stack.isDamageable()) return -1;
        
        int maxDamage = stack.getMaxDamage();
        if (maxDamage <= 0) return -1;
        
        return (double) stack.getDamage() / maxDamage;
    }
    
    /**
     * Gets the durability percentage of an ItemStack.
     * @param stack The ItemStack.
     * @return The durability percentage (0.0 to 1.0), or -1 if not damageable.
     */
    public static double getDurabilityPercentage(ItemStack stack) {
        double damagePercentage = getDamagePercentage(stack);
        return damagePercentage == -1 ? -1 : 1.0 - damagePercentage;
    }
    
    /**
     * Damages an ItemStack by a specified amount.
     * @param stack The ItemStack to damage.
     * @param amount The damage amount.
     * @param player The player using the item (can be null).
     * @return True if the item was damaged successfully.
     */
    public static boolean damageItem(ItemStack stack, int amount, PlayerEntity player) {
        if (isEmpty(stack) || !stack.isDamageable() || amount <= 0) return false;
        
        stack.damage(amount, player, net.minecraft.entity.EquipmentSlot.MAINHAND);
        return true;
    }
    
    /**
     * Repairs an ItemStack by a specified amount.
     * @param stack The ItemStack to repair.
     * @param amount The repair amount.
     * @return True if the item was repaired successfully.
     */
    public static boolean repairItem(ItemStack stack, int amount) {
        if (isEmpty(stack) || !stack.isDamageable() || amount <= 0) return false;
        
        int currentDamage = stack.getDamage();
        int newDamage = Math.max(0, currentDamage - amount);
        stack.setDamage(newDamage);
        return true;
    }
    
    /**
     * Fully repairs an ItemStack.
     * @param stack The ItemStack to repair.
     * @return True if the item was repaired successfully.
     */
    public static boolean fullyRepairItem(ItemStack stack) {
        if (isEmpty(stack) || !stack.isDamageable()) return false;
        
        stack.setDamage(0);
        return true;
    }
    
    /**
     * Gets the NBT data of an ItemStack.
     * @param stack The ItemStack.
     * @return The NBT compound, or null if none.
     */
    public static NbtCompound getNbt(ItemStack stack) {
        if (isEmpty(stack)) return null;
        
        NbtComponent nbtComponent = stack.get(DataComponentTypes.CUSTOM_DATA);
        return nbtComponent != null ? nbtComponent.copyNbt() : null;
    }
    
    /**
     * Sets the NBT data of an ItemStack.
     * @param stack The ItemStack.
     * @param nbt The NBT compound to set.
     */
    public static void setNbt(ItemStack stack, NbtCompound nbt) {
        if (isEmpty(stack)) return;
        
        if (nbt == null || nbt.isEmpty()) {
            stack.remove(DataComponentTypes.CUSTOM_DATA);
        } else {
            stack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(nbt));
        }
    }
    
    /**
     * Checks if an ItemStack has NBT data.
     * @param stack The ItemStack.
     * @return True if the stack has NBT data.
     */
    public static boolean hasNbt(ItemStack stack) {
        return getNbt(stack) != null;
    }
    
    /**
     * Gets the enchantment level of a specific enchantment on an ItemStack.
     * @param stack The ItemStack.
     * @param enchantment The enchantment.
     * @return The enchantment level, or 0 if not present.
     */
    public static int getEnchantmentLevel(ItemStack stack, RegistryEntry<Enchantment> enchantment) {
        if (isEmpty(stack)) return 0;
        return EnchantmentHelper.getLevel(enchantment, stack);
    }
    
    /**
     * Checks if an ItemStack has a specific enchantment.
     * @param stack The ItemStack.
     * @param enchantment The enchantment.
     * @return True if the stack has the enchantment.
     */
    public static boolean hasEnchantment(ItemStack stack, RegistryEntry<Enchantment> enchantment) {
        return getEnchantmentLevel(stack, enchantment) > 0;
    }
    
    /**
     * Checks if an ItemStack is enchanted.
     * @param stack The ItemStack.
     * @return True if the stack is enchanted.
     */
    public static boolean isEnchanted(ItemStack stack) {
        if (isEmpty(stack)) return false;
        return stack.hasEnchantments();
    }
    
    /**
     * Finds the first ItemStack in an inventory matching a predicate.
     * @param inventory The inventory to search.
     * @param predicate The predicate to match.
     * @return The first matching ItemStack, or ItemStack.EMPTY if none found.
     */
    public static ItemStack findFirst(Inventory inventory, java.util.function.Predicate<ItemStack> predicate) {
        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.getStack(i);
            if (predicate.test(stack)) {
                return stack;
            }
        }
        return ItemStack.EMPTY;
    }
    
    /**
     * Finds all ItemStacks in an inventory matching a predicate.
     * @param inventory The inventory to search.
     * @param predicate The predicate to match.
     * @return A list of matching ItemStacks.
     */
    public static List<ItemStack> findAll(Inventory inventory, java.util.function.Predicate<ItemStack> predicate) {
        List<ItemStack> matches = new ArrayList<>();
        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.getStack(i);
            if (predicate.test(stack)) {
                matches.add(stack);
            }
        }
        return matches;
    }
    
    /**
     * Counts the total amount of a specific item in an inventory.
     * @param inventory The inventory to search.
     * @param item The item to count.
     * @return The total count of the item.
     */
    public static int countItem(Inventory inventory, Item item) {
        int count = 0;
        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.getStack(i);
            if (!isEmpty(stack) && stack.getItem() == item) {
                count += stack.getCount();
            }
        }
        return count;
    }
    
    /**
     * Consumes a specific amount of an item from an inventory.
     * @param inventory The inventory.
     * @param item The item to consume.
     * @param amount The amount to consume.
     * @return The actual amount consumed.
     */
    public static int consumeItem(Inventory inventory, Item item, int amount) {
        int remaining = amount;
        
        for (int i = 0; i < inventory.size() && remaining > 0; i++) {
            ItemStack stack = inventory.getStack(i);
            if (!isEmpty(stack) && stack.getItem() == item) {
                int toConsume = Math.min(remaining, stack.getCount());
                stack.decrement(toConsume);
                remaining -= toConsume;
                
                if (stack.isEmpty()) {
                    inventory.setStack(i, ItemStack.EMPTY);
                }
            }
        }
        
        return amount - remaining;
    }
    
    /**
     * Tries to insert an ItemStack into an inventory.
     * @param inventory The inventory.
     * @param stack The ItemStack to insert.
     * @return The remaining ItemStack that couldn't be inserted.
     */
    public static ItemStack insertStack(Inventory inventory, ItemStack stack) {
        if (isEmpty(stack)) return ItemStack.EMPTY;
        
        ItemStack remaining = stack.copy();
        
        // First pass: try to merge with existing stacks
        for (int i = 0; i < inventory.size() && !remaining.isEmpty(); i++) {
            ItemStack existing = inventory.getStack(i);
            if (!isEmpty(existing) && canMerge(existing, remaining)) {
                int maxStack = Math.min(existing.getMaxCount(), inventory.getMaxCountPerStack());
                int canAdd = maxStack - existing.getCount();
                if (canAdd > 0) {
                    int toAdd = Math.min(canAdd, remaining.getCount());
                    existing.increment(toAdd);
                    remaining.decrement(toAdd);
                }
            }
        }
        
        // Second pass: try to place in empty slots
        for (int i = 0; i < inventory.size() && !remaining.isEmpty(); i++) {
            if (inventory.getStack(i).isEmpty()) {
                int maxStack = Math.min(remaining.getMaxCount(), inventory.getMaxCountPerStack());
                int toPlace = Math.min(maxStack, remaining.getCount());
                inventory.setStack(i, copyWithCount(remaining, toPlace));
                remaining.decrement(toPlace);
            }
        }
        
        return remaining.isEmpty() ? ItemStack.EMPTY : remaining;
    }
    
    /**
     * Creates a simple ItemStack with a specific count.
     * @param item The item.
     * @param count The count.
     * @return The created ItemStack.
     */
    public static ItemStack createStack(Item item, int count) {
        if (item == null || count <= 0) return ItemStack.EMPTY;
        return new ItemStack(item, count);
    }
    
    /**
     * Creates a simple ItemStack with count 1.
     * @param item The item.
     * @return The created ItemStack.
     */
    public static ItemStack createStack(Item item) {
        return createStack(item, 1);
    }
    
    /**
     * Clears the item ID cache (useful for resource pack reloads).
     */
    public static void clearCache() {
        ITEM_ID_CACHE.clear();
        MAX_STACK_SIZE_CACHE.clear();
    }
    
    /**
     * Gets cache statistics for debugging.
     * @return A map containing cache statistics.
     */
    public static Map<String, Integer> getCacheStats() {
        Map<String, Integer> stats = new HashMap<>();
        stats.put("itemIdCacheSize", ITEM_ID_CACHE.size());
        stats.put("maxStackSizeCacheSize", MAX_STACK_SIZE_CACHE.size());
        return stats;
    }
}
