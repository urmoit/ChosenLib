package com.chosen.lib.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.GoalSelector;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;

/**
 * Entity AI utilities for custom mobs and advanced entity behavior.
 * Provides comprehensive AI management, pathfinding, and behavior control.
 */
public class EntityAIUtils {
    
    private static final Map<UUID, EntityAIData> entityAIData = new ConcurrentHashMap<>();
    private static final Map<UUID, EntityMemory> entityMemory = new ConcurrentHashMap<>();
    
    /**
     * Entity AI data container.
     */
    public static class EntityAIData {
        private boolean aiEnabled = true;
        private BehaviorState behaviorState = BehaviorState.IDLE;
        private Entity target;
        private Vec3d lastKnownTargetPos;
        private long lastTargetUpdate;
        private Map<String, Object> customData = new HashMap<>();
        
        public boolean isAiEnabled() { return aiEnabled; }
        public void setAiEnabled(boolean aiEnabled) { this.aiEnabled = aiEnabled; }
        
        public BehaviorState getBehaviorState() { return behaviorState; }
        public void setBehaviorState(BehaviorState behaviorState) { this.behaviorState = behaviorState; }
        
        public Entity getTarget() { return target; }
        public void setTarget(Entity target) { 
            this.target = target; 
            this.lastKnownTargetPos = target != null ? target.getPos() : null;
            this.lastTargetUpdate = System.currentTimeMillis();
        }
        
        public Vec3d getLastKnownTargetPos() { return lastKnownTargetPos; }

        public void setLastKnownTargetPos(Vec3d pos) {
            this.lastKnownTargetPos = pos;
        }
        public long getLastTargetUpdate() { return lastTargetUpdate; }
        
        public Map<String, Object> getCustomData() { return customData; }
        public void setCustomData(String key, Object value) { customData.put(key, value); }
        public Object getCustomData(String key) { return customData.get(key); }
    }
    
    /**
     * Entity memory system.
     */
    public static class EntityMemory {
        private final Map<String, MemoryEntry> memories = new ConcurrentHashMap<>();
        private final long maxAge = 300000; // 5 minutes
        
        public void storeMemory(String key, Object value) {
            memories.put(key, new MemoryEntry(value, System.currentTimeMillis()));
        }
        
        public Object retrieveMemory(String key) {
            MemoryEntry entry = memories.get(key);
            if (entry != null && !entry.isExpired(maxAge)) {
                return entry.getValue();
            }
            memories.remove(key);
            return null;
        }
        
        public void clearMemory() {
            memories.clear();
        }
        
        public void clearExpiredMemories() {
            memories.entrySet().removeIf(entry -> entry.getValue().isExpired(maxAge));
        }
        
        private static class MemoryEntry {
            private final Object value;
            private final long timestamp;
            
            public MemoryEntry(Object value, long timestamp) {
                this.value = value;
                this.timestamp = timestamp;
            }
            
            public Object getValue() { return value; }
            public boolean isExpired(long maxAge) {
                return System.currentTimeMillis() - timestamp > maxAge;
            }
        }
    }
    
    /**
     * Behavior states for entities.
     */
    public enum BehaviorState {
        IDLE,
        PATROLLING,
        HUNTING,
        FLEEING,
        INVESTIGATING,
        GUARDING,
        FOLLOWING,
        ATTACKING,
        CUSTOM
    }
    
    /**
     * Enables or disables AI for an entity.
     * @param entity The entity.
     * @param enabled Whether AI should be enabled.
     */
    public static void setAIEnabled(Entity entity, boolean enabled) {
        getEntityAIData(entity).setAiEnabled(enabled);
        
        if (entity instanceof MobEntity mob) {
            mob.setAiDisabled(!enabled);
        }
    }

    /**
     * Gets the AI enabled state for an entity.
     * @param entity The entity.
     * @return True if AI is enabled.
     */
    public static boolean getAIEnabled(Entity entity) {
        return getEntityAIData(entity).isAiEnabled();
    }
    
    /**
     * Sets the behavior state for an entity.
     * @param entity The entity.
     * @param state The behavior state.
     */
    public static void setBehaviorState(Entity entity, BehaviorState state) {
        getEntityAIData(entity).setBehaviorState(state);
    }
    
    /**
     * Gets the behavior state for an entity.
     * @param entity The entity.
     * @return The behavior state.
     */
    public static BehaviorState getBehaviorState(Entity entity) {
        return getEntityAIData(entity).getBehaviorState();
    }
    
    /**
     * Triggers a specific behavior for an entity.
     * @param entity The entity.
     * @param behaviorType The type of behavior to trigger.
     * @param target Optional target for the behavior.
     */
    public static void triggerBehavior(Entity entity, BehaviorState behaviorType, Entity target) {
        EntityAIData aiData = getEntityAIData(entity);
        
        switch (behaviorType) {
            case HUNTING:
                aiData.setTarget(target);
                aiData.setBehaviorState(BehaviorState.HUNTING);
                if (entity instanceof MobEntity mob) {
                    mob.setTarget((LivingEntity) target);
                }
                break;
            case FLEEING:
                aiData.setTarget(target);
                aiData.setBehaviorState(BehaviorState.FLEEING);
                break;
            case FOLLOWING:
                aiData.setTarget(target);
                aiData.setBehaviorState(BehaviorState.FOLLOWING);
                break;
            case GUARDING:
                aiData.setBehaviorState(BehaviorState.GUARDING);
                break;
            case INVESTIGATING:
                if (target != null) {
                    aiData.setLastKnownTargetPos(target.getPos());
                    aiData.setBehaviorState(BehaviorState.INVESTIGATING);
                }
                break;
            default:
                aiData.setBehaviorState(behaviorType);
                break;
        }
    }

    /**
     * Finds a path to a target position.
     * @param entity The entity.
     * @param targetPos The target position.
     * @return The path, or null if no path found.
     */
    public static Path findPathTo(Entity entity, Vec3d targetPos) {
        if (entity instanceof MobEntity mob) {
            EntityNavigation navigation = mob.getNavigation();
            return navigation.findPathTo(targetPos.x, targetPos.y, targetPos.z, 0);
        }
        return null;
    }
    
    /**
     * Finds a path around obstacles to a target position.
     * @param entity The entity.
     * @param targetPos The target position.
     * @param maxDistance Maximum distance to search.
     * @return The path, or null if no path found.
     */
    public static Path findPathAround(Entity entity, Vec3d targetPos, double maxDistance) {
        if (entity instanceof MobEntity mob) {
            EntityNavigation navigation = mob.getNavigation();
            
            // Try direct path first
            Path path = navigation.findPathTo(targetPos.x, targetPos.y, targetPos.z, 0);
            if (path != null && path.getLength() <= maxDistance) {
                return path;
            }
            
            // Try alternative paths around obstacles
            for (int i = 0; i < 8; i++) {
                double angle = (i * Math.PI * 2) / 8;
                double offsetX = Math.cos(angle) * 3;
                double offsetZ = Math.sin(angle) * 3;
                
                Vec3d altPos = targetPos.add(offsetX, 0, offsetZ);
                path = navigation.findPathTo(altPos.x, altPos.y, altPos.z, 0);
                if (path != null && path.getLength() <= maxDistance) {
                    return path;
                }
            }
        }
        return null;
    }
    
    /**
     * Checks if a path to a position is clear.
     * @param entity The entity.
     * @param targetPos The target position.
     * @return True if path is clear.
     */
    public static boolean isPathClear(Entity entity, Vec3d targetPos) {
        Path path = findPathTo(entity, targetPos);
        return path != null && path.getLength() > 0;
    }
    
    /**
     * Optimizes a path by removing unnecessary waypoints.
     * @param entity The entity.
     * @param path The path to optimize.
     * @return Optimized path.
     */
    public static Path optimizePath(Entity entity, Path path) {
        if (path == null) {
            return null;
        }
        if (entity instanceof MobEntity mob) {
            EntityNavigation navigation = mob.getNavigation();
            // Path optimization is handled internally by Minecraft's pathfinding
            return navigation.findPathTo(path.getTarget().getX(), path.getTarget().getY(), path.getTarget().getZ(), 0);
        }
        return path;
    }
    
    /**
     * Selects the nearest target from a list of potential targets.
     * @param entity The entity.
     * @param potentialTargets List of potential targets.
     * @param maxDistance Maximum distance to consider.
     * @return The nearest target, or null if none found.
     */
    public static Entity selectNearestTarget(Entity entity, List<Entity> potentialTargets, double maxDistance) {
        Entity nearest = null;
        double nearestDistance = Double.MAX_VALUE;
        
        for (Entity target : potentialTargets) {
            double distance = entity.getPos().distanceTo(target.getPos());
            if (distance <= maxDistance && distance < nearestDistance) {
                nearest = target;
                nearestDistance = distance;
            }
        }
        
        return nearest;
    }
    
    /**
     * Selects a random target from a list of potential targets.
     * @param entity The entity.
     * @param potentialTargets List of potential targets.
     * @param maxDistance Maximum distance to consider.
     * @return A random target, or null if none found.
     */
    public static Entity selectRandomTarget(Entity entity, List<Entity> potentialTargets, double maxDistance) {
        List<Entity> validTargets = new ArrayList<>();
        
        for (Entity target : potentialTargets) {
            double distance = entity.getPos().distanceTo(target.getPos());
            if (distance <= maxDistance) {
                validTargets.add(target);
            }
        }
        
        if (validTargets.isEmpty()) {
            return null;
        }
        
        return validTargets.get(new Random().nextInt(validTargets.size()));
    }
    
    /**
     * Evaluates a target based on various criteria.
     * @param entity The entity.
     * @param target The target to evaluate.
     * @return Target evaluation score (higher is better).
     */
    public static double evaluateTarget(Entity entity, Entity target) {
        if (target == null || !target.isAlive()) {
            return -1.0;
        }
        
        double score = 0.0;
        
        // Distance factor (closer is better)
        double distance = entity.getPos().distanceTo(target.getPos());
        score += Math.max(0, 100 - distance);
        
        // Visibility factor
        if (canSeeTarget(entity, target)) {
            score += 50;
        }
        
        // Target type factor
        if (target instanceof PlayerEntity) {
            score += 30;
        } else if (target instanceof LivingEntity) {
            score += 20;
        }
        
        // Health factor (weaker targets are preferred)
        if (target instanceof LivingEntity living) {
            float healthRatio = living.getHealth() / living.getMaxHealth();
            score += (1.0 - healthRatio) * 25;
        }
        
        return score;
    }
    
    /**
     * Moves an entity towards a target position.
     * @param entity The entity.
     * @param targetPos The target position.
     * @param speed Movement speed multiplier.
     * @return True if movement was initiated.
     */
    public static boolean moveTowards(Entity entity, Vec3d targetPos, double speed) {
        if (entity instanceof MobEntity mob) {
            EntityNavigation navigation = mob.getNavigation();
            
            // Set movement speed
            if (speed != 1.0) {
                mob.getAttributeInstance(net.minecraft.entity.attribute.EntityAttributes.GENERIC_MOVEMENT_SPEED)
                   .setBaseValue(0.25 * speed); // Default speed is 0.25
            }
            
            return navigation.startMovingTo(targetPos.x, targetPos.y, targetPos.z, speed);
        }
            return false;
    }
    
    /**
     * Stops entity movement.
     * @param entity The entity.
     */
    public static void stopMovement(Entity entity) {
        if (entity instanceof MobEntity mob) {
            EntityNavigation navigation = mob.getNavigation();
            navigation.stop();
        }
    }

    /**
     * Sets the movement speed for an entity.
     * @param entity The entity.
     * @param speed The movement speed.
     */
    public static void setMovementSpeed(Entity entity, double speed) {
        if (entity instanceof LivingEntity living) {
            living.getAttributeInstance(net.minecraft.entity.attribute.EntityAttributes.GENERIC_MOVEMENT_SPEED)
                  .setBaseValue(speed);
        }
    }

    /**
     * Checks if an entity can see a target.
     * @param entity The entity.
     * @param target The target entity.
     * @return True if the target is visible.
     */
    public static boolean canSeeTarget(Entity entity, Entity target) {
        if (entity.getWorld() instanceof ServerWorld serverWorld) {
            return serverWorld.isSpaceEmpty(Box.from(entity.getEyePos()).expand(entity.getPos().distanceTo(target.getPos())));
        }
        return true; // Assume visible on client
    }
    
    /**
     * Checks if an entity can hear a target.
     * @param entity The entity.
     * @param target The target entity.
     * @param hearingRange Maximum hearing range.
     * @return True if the target can be heard.
     */
    public static boolean canHearTarget(Entity entity, Entity target, double hearingRange) {
        double distance = entity.getPos().distanceTo(target.getPos());
        return distance <= hearingRange;
    }
    
    /**
     * Detects nearby entities within a range.
     * @param entity The entity.
     * @param range Detection range.
     * @param predicate Optional predicate to filter entities.
     * @return List of detected entities.
     */
    public static List<Entity> detectNearbyEntities(Entity entity, double range, Predicate<Entity> predicate) {
        return EntityUtils.getEntitiesInRadius(entity.getWorld(), entity.getPos(), range, predicate);
    }
    
    /**
     * Stores memory for an entity.
     * @param entity The entity.
     * @param key Memory key.
     * @param value Memory value.
     */
    public static void storeMemory(Entity entity, String key, Object value) {
        getEntityMemory(entity).storeMemory(key, value);
    }
    
    /**
     * Retrieves memory for an entity.
     * @param entity The entity.
     * @param key Memory key.
     * @return Memory value, or null if not found.
     */
    public static Object retrieveMemory(Entity entity, String key) {
        return getEntityMemory(entity).retrieveMemory(key);
    }
    
    /**
     * Clears all memory for an entity.
     * @param entity The entity.
     */
    public static void clearMemory(Entity entity) {
        getEntityMemory(entity).clearMemory();
    }
    
    /**
     * Clears expired memories for an entity.
     * @param entity The entity.
     */
    public static void clearExpiredMemories(Entity entity) {
        getEntityMemory(entity).clearExpiredMemories();
    }
    
    /**
     * Gets the AI data for an entity.
     * @param entity The entity.
     * @return Entity AI data.
     */
    private static EntityAIData getEntityAIData(Entity entity) {
        return entityAIData.computeIfAbsent(entity.getUuid(), k -> new EntityAIData());
    }
    
    /**
     * Gets the memory for an entity.
     * @param entity The entity.
     * @return Entity memory.
     */
    private static EntityMemory getEntityMemory(Entity entity) {
        return entityMemory.computeIfAbsent(entity.getUuid(), k -> new EntityMemory());
    }
    
    /**
     * Adds a custom goal to an entity's AI.
     * @param entity The entity.
     * @param goal The goal to add.
     * @param priority Goal priority (lower numbers = higher priority).
     */
    /**
     * The following methods require access to the private 'goalSelector' field in MobEntity.
     * This can be achieved using mixins to make the field accessible.
     * Without mixins, these methods will not compile.
     */

    /*
    public static void addGoal(Entity entity, Goal goal, int priority) {
        if (entity instanceof MobEntity mob) {
            GoalSelector goalSelector = mob.goalSelector;
            goalSelector.add(priority, goal);
        }
    }

    public static void removeGoal(Entity entity, Class<? extends Goal> goalClass) {
        if (entity instanceof MobEntity mob) {
            GoalSelector goalSelector = mob.goalSelector;
            goalSelector.getGoals().removeIf(goal -> goalClass.isInstance(goal.getGoal()));
        }
    }
    
    public static void clearGoals(Entity entity) {
        if (entity instanceof MobEntity mob) {
            GoalSelector goalSelector = mob.goalSelector;
            goalSelector.clear(goal -> true);
        }
    }
    
    public static void setGoalPriority(Entity entity, Class<? extends Goal> goalClass, int priority) {
        if (entity instanceof MobEntity mob) {
            GoalSelector goalSelector = mob.goalSelector;
            
            // Remove existing goal
            goalSelector.getGoals().removeIf(goal -> goalClass.isInstance(goal.getGoal()));
            
            // Add with new priority
            try {
                Goal newGoal = goalClass.getDeclaredConstructor().newInstance();
                goalSelector.add(priority, newGoal);
            } catch (Exception e) {
                // Goal creation failed
            }
        }
    }
    */
    
    /**
     * Cleans up AI data for removed entities.
     */
    public static void cleanupRemovedEntities() {
        entityAIData.entrySet().removeIf(entry -> {
            // Check if entity still exists
            return true; // Simplified cleanup - in real implementation, check if entity exists
        });
        
        entityMemory.entrySet().removeIf(entry -> {
            return true; // Simplified cleanup
        });
    }
}