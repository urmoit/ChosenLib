package com.chosen.lib.util;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;

/**
 * Utility class for item-related operations in Minecraft mods.
 * Provides helpers for item validation and common item patterns.
 */
public class ItemUtils {
    
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
     * Gets the item ID as a string.
     * @param item The item.
     * @return The item ID string.
     */
    public static String getItemId(Item item) {
        if (item == null) return "null";
        return Registries.ITEM.getId(item).toString();
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
     * Copies an ItemStack with a new count.
     * @param stack The original ItemStack.
     * @param count The new count.
     * @return A new ItemStack with the specified count.
     */
    public static ItemStack copyWithCount(ItemStack stack, int count) {
        if (isEmpty(stack)) return ItemStack.EMPTY;
        
        ItemStack copy = stack.copy();
        copy.setCount(count);
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
        if (isEmpty(stack1) || isEmpty(stack2)) return false;
        return ItemStack.areEqual(stack1, stack2);
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
     * Gets the maximum stack size for an item.
     * @param item The item.
     * @return The maximum stack size.
     */
    public static int getMaxStackSize(Item item) {
        if (item == null) return 0;
        return item.getMaxCount();
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
     * Gets the durability of an ItemStack.
     * @param stack The ItemStack.
     * @return The current durability, or -1 if not damageable.
     */
    public static int getDurability(ItemStack stack) {
        if (isEmpty(stack)) return -1;
        return stack.getMaxDamage() - stack.getDamage();
    }
    
    /**
     * Gets the maximum durability of an ItemStack.
     * @param stack The ItemStack.
     * @return The maximum durability, or -1 if not damageable.
     */
    public static int getMaxDurability(ItemStack stack) {
        if (isEmpty(stack)) return -1;
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
     * Gets the damage percentage of an ItemStack.
     * @param stack The ItemStack.
     * @return The damage percentage (0.0 to 1.0), or -1 if not damageable.
     */
    public static double getDamagePercentage(ItemStack stack) {
        if (isEmpty(stack)) return -1;
        
        int maxDamage = stack.getMaxDamage();
        if (maxDamage <= 0) return -1;
        
        return (double) stack.getDamage() / maxDamage;
    }
}
