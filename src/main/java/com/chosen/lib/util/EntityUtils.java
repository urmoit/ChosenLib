package com.chosen.lib.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;

/**
 * Utility class for entity-related operations.
 * Provides helpers for entity manipulation, queries, and common operations.
 */
public class EntityUtils {
    
    /**
     * Safely gets an entity's health.
     * @param entity The entity.
     * @return The entity's health, or 0 if not a living entity.
     */
    public static float getHealth(Entity entity) {
        if (entity instanceof LivingEntity living) {
            return living.getHealth();
        }
        return 0.0f;
    }
    
    /**
     * Safely gets an entity's maximum health.
     * @param entity The entity.
     * @return The entity's maximum health, or 0 if not a living entity.
     */
    public static float getMaxHealth(Entity entity) {
        if (entity instanceof LivingEntity living) {
            return living.getMaxHealth();
        }
        return 0.0f;
    }
    
    /**
     * Safely sets an entity's health.
     * @param entity The entity.
     * @param health The health value to set.
     * @return True if the health was set successfully.
     */
    public static boolean setHealth(Entity entity, float health) {
        if (entity instanceof LivingEntity living) {
            living.setHealth(Math.max(0, Math.min(health, living.getMaxHealth())));
            return true;
        }
        return false;
    }
    
    /**
     * Heals an entity by the specified amount.
     * @param entity The entity to heal.
     * @param amount The amount to heal.
     * @return True if the entity was healed.
     */
    public static boolean heal(Entity entity, float amount) {
        if (entity instanceof LivingEntity living) {
            living.heal(amount);
            return true;
        }
        return false;
    }
    
    /**
     * Damages an entity with the specified damage source and amount.
     * @param entity The entity to damage.
     * @param damageSource The damage source.
     * @param amount The damage amount.
     * @return True if the entity was damaged.
     */
    public static boolean damage(Entity entity, DamageSource damageSource, float amount) {
        return entity.damage(damageSource, amount);
    }
    
    /**
     * Checks if an entity has a specific status effect.
     * @param entity The entity.
     * @param effect The status effect to check for.
     * @return True if the entity has the effect.
     */
    public static boolean hasStatusEffect(Entity entity, RegistryEntry<StatusEffect> effect) {
        if (entity instanceof LivingEntity living) {
            return living.hasStatusEffect(effect);
        }
        return false;
    }
    
    /**
     * Gets a status effect instance from an entity.
     * @param entity The entity.
     * @param effect The status effect.
     * @return The status effect instance, or null if not present.
     */
    public static StatusEffectInstance getStatusEffect(Entity entity, RegistryEntry<StatusEffect> effect) {
        if (entity instanceof LivingEntity living) {
            return living.getStatusEffect(effect);
        }
        return null;
    }
    
    /**
     * Adds a status effect to an entity.
     * @param entity The entity.
     * @param effect The status effect instance.
     * @return True if the effect was added successfully.
     */
    public static boolean addStatusEffect(Entity entity, StatusEffectInstance effect) {
        if (entity instanceof LivingEntity living) {
            return living.addStatusEffect(effect);
        }
        return false;
    }
    
    /**
     * Removes a status effect from an entity.
     * @param entity The entity.
     * @param effect The status effect to remove.
     * @return True if the effect was removed.
     */
    public static boolean removeStatusEffect(Entity entity, RegistryEntry<StatusEffect> effect) {
        if (entity instanceof LivingEntity living) {
            return living.removeStatusEffect(effect);
        }
        return false;
    }
    
    /**
     * Clears all status effects from an entity.
     * @param entity The entity.
     * @return True if effects were cleared.
     */
    public static boolean clearStatusEffects(Entity entity) {
        if (entity instanceof LivingEntity living) {
            living.clearStatusEffects();
            return true;
        }
        return false;
    }
    
    /**
     * Gets an entity's attribute value.
     * @param entity The entity.
     * @param attribute The attribute.
     * @return The attribute value, or 0 if not found.
     */
    public static double getAttributeValue(Entity entity, RegistryEntry<EntityAttribute> attribute) {
        if (entity instanceof LivingEntity living) {
            EntityAttributeInstance instance = living.getAttributeInstance(attribute);
            return instance != null ? instance.getValue() : 0.0;
        }
        return 0.0;
    }
    
    /**
     * Sets an entity's base attribute value.
     * @param entity The entity.
     * @param attribute The attribute.
     * @param value The value to set.
     * @return True if the attribute was set.
     */
    public static boolean setAttributeBaseValue(Entity entity, RegistryEntry<EntityAttribute> attribute, double value) {
        if (entity instanceof LivingEntity living) {
            EntityAttributeInstance instance = living.getAttributeInstance(attribute);
            if (instance != null) {
                instance.setBaseValue(value);
                return true;
            }
        }
        return false;
    }
    
    /**
     * Teleports an entity to a specific position.
     * @param entity The entity to teleport.
     * @param pos The target position.
     * @return True if the teleportation was successful.
     */
    public static boolean teleport(Entity entity, Vec3d pos) {
        if (entity.getWorld() instanceof ServerWorld serverWorld) {
            return entity.teleport(serverWorld, pos.x, pos.y, pos.z, java.util.Set.of(), entity.getYaw(), entity.getPitch());
        }
        return false;
    }
    
    /**
     * Teleports an entity to a specific block position.
     * @param entity The entity to teleport.
     * @param pos The target block position.
     * @return True if the teleportation was successful.
     */
    public static boolean teleport(Entity entity, BlockPos pos) {
        return teleport(entity, Vec3d.ofCenter(pos));
    }
    
    /**
     * Teleports an entity to another entity's position.
     * @param entity The entity to teleport.
     * @param target The target entity.
     * @return True if the teleportation was successful.
     */
    public static boolean teleportToEntity(Entity entity, Entity target) {
        return teleport(entity, target.getPos());
    }
    
    /**
     * Gets the distance between two entities.
     * @param entity1 The first entity.
     * @param entity2 The second entity.
     * @return The distance between the entities.
     */
    public static double getDistance(Entity entity1, Entity entity2) {
        return entity1.getPos().distanceTo(entity2.getPos());
    }
    
    /**
     * Gets the squared distance between two entities (more efficient).
     * @param entity1 The first entity.
     * @param entity2 The second entity.
     * @return The squared distance between the entities.
     */
    public static double getSquaredDistance(Entity entity1, Entity entity2) {
        return entity1.getPos().squaredDistanceTo(entity2.getPos());
    }
    
    /**
     * Checks if two entities are within a certain distance.
     * @param entity1 The first entity.
     * @param entity2 The second entity.
     * @param distance The maximum distance.
     * @return True if the entities are within the specified distance.
     */
    public static boolean isWithinDistance(Entity entity1, Entity entity2, double distance) {
        return getSquaredDistance(entity1, entity2) <= distance * distance;
    }
    
    /**
     * Gets all entities within a radius of a position.
     * @param world The world.
     * @param center The center position.
     * @param radius The search radius.
     * @param predicate Optional predicate to filter entities.
     * @return A list of entities within the radius.
     */
    public static List<Entity> getEntitiesInRadius(World world, Vec3d center, double radius, Predicate<Entity> predicate) {
        Box box = new Box(center.subtract(radius, radius, radius), center.add(radius, radius, radius));
        List<Entity> entities = world.getOtherEntities(null, box);
        
        if (predicate != null) {
            entities.removeIf(entity -> !predicate.test(entity));
        }
        
        // Filter by actual distance (box search includes corners)
        entities.removeIf(entity -> entity.getPos().distanceTo(center) > radius);
        
        return entities;
    }
    
    /**
     * Gets all living entities within a radius.
     * @param world The world.
     * @param center The center position.
     * @param radius The search radius.
     * @return A list of living entities within the radius.
     */
    public static List<LivingEntity> getLivingEntitiesInRadius(World world, Vec3d center, double radius) {
        List<Entity> entities = getEntitiesInRadius(world, center, radius, entity -> entity instanceof LivingEntity);
        List<LivingEntity> livingEntities = new ArrayList<>();
        
        for (Entity entity : entities) {
            livingEntities.add((LivingEntity) entity);
        }
        
        return livingEntities;
    }
    
    /**
     * Gets all players within a radius.
     * @param world The world.
     * @param center The center position.
     * @param radius The search radius.
     * @return A list of players within the radius.
     */
    public static List<PlayerEntity> getPlayersInRadius(World world, Vec3d center, double radius) {
        List<Entity> entities = getEntitiesInRadius(world, center, radius, entity -> entity instanceof PlayerEntity);
        List<PlayerEntity> players = new ArrayList<>();
        
        for (Entity entity : entities) {
            players.add((PlayerEntity) entity);
        }
        
        return players;
    }
    
    /**
     * Finds the nearest entity of a specific type.
     * @param world The world.
     * @param center The center position.
     * @param radius The search radius.
     * @param entityClass The entity class to search for.
     * @param <T> The entity type.
     * @return The nearest entity, or null if none found.
     */
    public static <T extends Entity> T findNearestEntity(World world, Vec3d center, double radius, Class<T> entityClass) {
        List<Entity> entities = getEntitiesInRadius(world, center, radius, entityClass::isInstance);
        
        T nearest = null;
        double nearestDistance = Double.MAX_VALUE;
        
        for (Entity entity : entities) {
            double distance = entity.getPos().distanceTo(center);
            if (distance < nearestDistance) {
                nearest = entityClass.cast(entity);
                nearestDistance = distance;
            }
        }
        
        return nearest;
    }
    
    /**
     * Finds the nearest player to a position.
     * @param world The world.
     * @param center The center position.
     * @param radius The search radius.
     * @return The nearest player, or null if none found.
     */
    public static PlayerEntity findNearestPlayer(World world, Vec3d center, double radius) {
        return findNearestEntity(world, center, radius, PlayerEntity.class);
    }
    
    /**
     * Checks if an entity is alive.
     * @param entity The entity.
     * @return True if the entity is alive.
     */
    public static boolean isAlive(Entity entity) {
        return entity.isAlive();
    }
    
    /**
     * Checks if an entity is dead.
     * @param entity The entity.
     * @return True if the entity is dead.
     */
    public static boolean isDead(Entity entity) {
        if (entity instanceof LivingEntity living) {
            return living.isDead();
        }
        return !entity.isAlive();
    }
    
    /**
     * Gets an entity's UUID.
     * @param entity The entity.
     * @return The entity's UUID.
     */
    public static UUID getUuid(Entity entity) {
        return entity.getUuid();
    }
    
    /**
     * Gets an entity's custom name.
     * @param entity The entity.
     * @return The entity's custom name, or null if none.
     */
    public static Text getCustomName(Entity entity) {
        return entity.getCustomName();
    }
    
    /**
     * Sets an entity's custom name.
     * @param entity The entity.
     * @param name The name to set.
     */
    public static void setCustomName(Entity entity, Text name) {
        entity.setCustomName(name);
    }
    
    /**
     * Checks if an entity has a custom name.
     * @param entity The entity.
     * @return True if the entity has a custom name.
     */
    public static boolean hasCustomName(Entity entity) {
        return entity.hasCustomName();
    }
    
    /**
     * Makes an entity invulnerable.
     * @param entity The entity.
     * @param invulnerable Whether the entity should be invulnerable.
     */
    public static void setInvulnerable(Entity entity, boolean invulnerable) {
        entity.setInvulnerable(invulnerable);
    }
    
    /**
     * Checks if an entity is invulnerable.
     * @param entity The entity.
     * @return True if the entity is invulnerable.
     */
    public static boolean isInvulnerable(Entity entity) {
        return entity.isInvulnerable();
    }
    
    /**
     * Sets an entity's velocity.
     * @param entity The entity.
     * @param velocity The velocity vector.
     */
    public static void setVelocity(Entity entity, Vec3d velocity) {
        entity.setVelocity(velocity);
        entity.velocityModified = true;
    }
    
    /**
     * Adds velocity to an entity.
     * @param entity The entity.
     * @param velocity The velocity to add.
     */
    public static void addVelocity(Entity entity, Vec3d velocity) {
        entity.addVelocity(velocity.x, velocity.y, velocity.z);
    }
    
    /**
     * Launches an entity towards a target position.
     * @param entity The entity to launch.
     * @param target The target position.
     * @param force The launch force.
     */
    public static void launchTowards(Entity entity, Vec3d target, double force) {
        Vec3d direction = target.subtract(entity.getPos()).normalize();
        setVelocity(entity, direction.multiply(force));
    }
    
    /**
     * Launches an entity towards another entity.
     * @param entity The entity to launch.
     * @param target The target entity.
     * @param force The launch force.
     */
    public static void launchTowards(Entity entity, Entity target, double force) {
        launchTowards(entity, target.getPos(), force);
    }
    
    /**
     * Knocks back an entity from a source position.
     * @param entity The entity to knock back.
     * @param source The source position.
     * @param force The knockback force.
     */
    public static void knockback(Entity entity, Vec3d source, double force) {
        Vec3d direction = entity.getPos().subtract(source).normalize();
        addVelocity(entity, direction.multiply(force));
    }
    
    /**
     * Gets an entity's NBT data.
     * @param entity The entity.
     * @return The entity's NBT compound.
     */
    public static NbtCompound getNbt(Entity entity) {
        NbtCompound nbt = new NbtCompound();
        entity.writeNbt(nbt);
        return nbt;
    }
    
    /**
     * Applies NBT data to an entity.
     * @param entity The entity.
     * @param nbt The NBT compound to apply.
     */
    public static void setNbt(Entity entity, NbtCompound nbt) {
        entity.readNbt(nbt);
    }
    
    /**
     * Checks if an entity is a baby (for entities that support age).
     * @param entity The entity.
     * @return True if the entity is a baby.
     */
    public static boolean isBaby(Entity entity) {
        if (entity instanceof LivingEntity living) {
            return living.isBaby();
        }
        return false;
    }
    
    /**
     * Sets an entity's age (for entities that support age).
     * @param entity The entity.
     * @param age The age to set.
     * @return True if the age was set successfully.
     */
    public static boolean setAge(Entity entity, int age) {
        if (entity instanceof AnimalEntity animal) {
            animal.setBreedingAge(age);
            return true;
        }
        return false;
    }
    
    /**
     * Makes an entity an adult (for entities that support age).
     * @param entity The entity.
     * @return True if the entity was made an adult.
     */
    public static boolean makeAdult(Entity entity) {
        return setAge(entity, 0);
    }
    
    /**
     * Makes an entity a baby (for entities that support age).
     * @param entity The entity.
     * @return True if the entity was made a baby.
     */
    public static boolean makeBaby(Entity entity) {
        return setAge(entity, -24000); // Standard baby age
    }
    
    /**
     * Sends a message to a player entity.
     * @param entity The entity (must be a player).
     * @param message The message to send.
     * @param overlay Whether to show as overlay text.
     * @return True if the message was sent.
     */
    public static boolean sendMessage(Entity entity, Text message, boolean overlay) {
        if (entity instanceof ServerPlayerEntity player) {
            player.sendMessage(message, overlay);
            return true;
        }
        return false;
    }
    
    /**
     * Sends a message to a player entity in chat.
     * @param entity The entity (must be a player).
     * @param message The message to send.
     * @return True if the message was sent.
     */
    public static boolean sendMessage(Entity entity, Text message) {
        return sendMessage(entity, message, false);
    }
    
    /**
     * Gives an item stack to a player.
     * @param entity The entity (must be a player).
     * @param stack The item stack to give.
     * @return True if the item was given successfully.
     */
    public static boolean giveItem(Entity entity, ItemStack stack) {
        if (entity instanceof PlayerEntity player) {
            return player.getInventory().insertStack(stack);
        }
        return false;
    }
    
    /**
     * Checks if an entity is on the ground.
     * @param entity The entity.
     * @return True if the entity is on the ground.
     */
    public static boolean isOnGround(Entity entity) {
        return entity.isOnGround();
    }
    
    /**
     * Checks if an entity is in water.
     * @param entity The entity.
     * @return True if the entity is in water.
     */
    public static boolean isInWater(Entity entity) {
        return entity.isTouchingWater();
    }
    
    /**
     * Checks if an entity is in lava.
     * @param entity The entity.
     * @return True if the entity is in lava.
     */
    public static boolean isInLava(Entity entity) {
        return entity.isInLava();
    }
    
    /**
     * Checks if an entity is on fire.
     * @param entity The entity.
     * @return True if the entity is on fire.
     */
    public static boolean isOnFire(Entity entity) {
        return entity.isOnFire();
    }
    
    /**
     * Sets an entity on fire for a specified duration.
     * @param entity The entity.
     * @param seconds The duration in seconds.
     */
    public static void setOnFire(Entity entity, int seconds) {
        entity.setOnFireFor(seconds);
    }
    
    /**
     * Extinguishes an entity (removes fire).
     * @param entity The entity.
     */
    public static void extinguish(Entity entity) {
        entity.extinguish();
    }
}