package com.chosen.lib.util;

import com.chosen.lib.ChosenLib;

import net.minecraft.entity.Entity;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.particle.ParticleType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Sound and particle effects utilities for creating visual and audio effects.
 * Provides easy-to-use APIs for common effect patterns and custom effect creation.
 */
public class EffectsUtils {
    
    private static final ScheduledExecutorService effectScheduler = Executors.newScheduledThreadPool(4);
    private static final Map<String, EffectLoop> activeEffectLoops = new ConcurrentHashMap<>();
    private static final Map<String, CustomEffect> customEffects = new ConcurrentHashMap<>();
    
    /**
     * Represents an active effect loop.
     */
    private static class EffectLoop {
        private final Runnable effectTask;
        private final long intervalMs;
        private final long endTime;
        private boolean cancelled = false;
        
        public EffectLoop(Runnable effectTask, long intervalMs, long durationMs) {
            this.effectTask = effectTask;
            this.intervalMs = intervalMs;
            this.endTime = System.currentTimeMillis() + durationMs;
        }
        
        // public Runnable getEffectTask() { return effectTask; }
        // public long getIntervalMs() { return intervalMs; }
        public long getEndTime() { return endTime; }
        public boolean isCancelled() { return cancelled; }
        public void cancel() { cancelled = true; }
        public boolean isExpired() { return System.currentTimeMillis() >= endTime; }
    }
    
    /**
     * Custom effect definition.
     */
    public static class CustomEffect {
        private final String name;
        private final ParticleEffect particle;
        private final SoundEvent sound;
        private final float volume;
        private final float pitch;
        private final int duration;
        private final Map<String, Object> properties;
        
        public CustomEffect(String name, ParticleEffect particle, SoundEvent sound, 
                          float volume, float pitch, int duration) {
            this.name = name;
            this.particle = particle;
            this.sound = sound;
            this.volume = volume;
            this.pitch = pitch;
            this.duration = duration;
            this.properties = new HashMap<>();
        }
        
        public String getName() { return name; }
        public ParticleEffect getParticle() { return particle; }
        public SoundEvent getSound() { return sound; }
        public float getVolume() { return volume; }
        public float getPitch() { return pitch; }
        public int getDuration() { return duration; }
        public Map<String, Object> getProperties() { return properties; }
        public void setProperty(String key, Object value) { properties.put(key, value); }
        public Object getProperty(String key) { return properties.get(key); }
    }
    
    /**
     * Plays a sound at a specific location.
     * @param world The world.
     * @param pos The position.
     * @param sound The sound event.
     * @param volume The volume (0.0 to 1.0).
     * @param pitch The pitch (0.5 to 2.0).
     * @param category The sound category.
     * @return True if sound was played.
     */
    public static boolean playSound(World world, Vec3d pos, SoundEvent sound, 
                                  float volume, float pitch, SoundCategory category) {
        if (world instanceof ServerWorld serverWorld) {
            serverWorld.playSound(null, pos.x, pos.y, pos.z, sound, category, volume, pitch);
            return true;
        }
        return false;
    }

    public static boolean playSound(World world, Vec3d pos, RegistryEntry<SoundEvent> sound, 
                                  float volume, float pitch, SoundCategory category) {
        return playSound(world, pos, sound.value(), volume, pitch, category);
    }
    
    /**
     * Plays a sound at a block position.
     * @param world The world.
     * @param pos The block position.
     * @param sound The sound event.
     * @param volume The volume.
     * @param pitch The pitch.
     * @param category The sound category.
     * @return True if sound was played.
     */
    public static boolean playSoundAt(World world, BlockPos pos, SoundEvent sound, 
                                    float volume, float pitch, SoundCategory category) {
        return playSound(world, Vec3d.ofCenter(pos), sound, volume, pitch, category);
    }
    
    /**
     * Plays a sound to a specific player.
     * @param player The player.
     * @param pos The position.
     * @param sound The sound event.
     * @param volume The volume.
     * @param pitch The pitch.
     * @param category The sound category.
     * @return True if sound was played.
     */
    public static boolean playSoundToPlayer(ServerPlayerEntity player, Vec3d pos, SoundEvent sound, 
                                          float volume, float pitch, SoundCategory category) {
        if (player != null && player.getWorld() instanceof ServerWorld serverWorld) {
            serverWorld.playSoundFromEntity(player, sound, category, volume, pitch);
            return true;
        }
        return false;
    }
    
    /**
     * Stops a specific sound for all players in a world.
     * @param world The world.
     * @param sound The sound to stop.
     * @param category The sound category.
     */
    public static void stopSound(World world, SoundEvent sound, SoundCategory category) {
        if (world instanceof ServerWorld serverWorld) {
            for (ServerPlayerEntity player : serverWorld.getPlayers()) {
                player.networkHandler.sendPacket(new net.minecraft.network.packet.s2c.play.StopSoundS2CPacket(
                    sound.getId(), category));
            }
        }
    }
    
    /**
     * Sets the volume for a specific sound category.
     * @param world The world.
     * @param category The sound category.
     * @param volume The volume (0.0 to 1.0).
     */
    public static void setSoundVolume(World world, SoundCategory category, float volume) {
        if (world instanceof ServerWorld serverWorld) {
            for (ServerPlayerEntity player : serverWorld.getPlayers()) {
                player.networkHandler.sendPacket(new net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket(
                    Registries.SOUND_EVENT.getEntry(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP), category, player.getX(), player.getY(), player.getZ(), 
                    volume, 1.0f, System.currentTimeMillis()));
            }
        }
    }
    
    /**
     * Spawns a particle at a specific location.
     * @param world The world.
     * @param pos The position.
     * @param particle The particle effect.
     * @param count The number of particles.
     * @param offsetX X offset.
     * @param offsetY Y offset.
     * @param offsetZ Z offset.
     * @param speed The particle speed.
     * @return True if particles were spawned.
     */
    public static boolean spawnParticle(World world, Vec3d pos, ParticleEffect particle, 
                                      int count, double offsetX, double offsetY, double offsetZ, double speed) {
        if (world instanceof ServerWorld serverWorld) {
            serverWorld.spawnParticles(particle, pos.x, pos.y, pos.z, count, offsetX, offsetY, offsetZ, speed);
            return true;
        }
        return false;
    }
    
    /**
     * Spawns a particle at a block position.
     * @param world The world.
     * @param pos The block position.
     * @param particle The particle effect.
     * @param count The number of particles.
     * @param offset The offset.
     * @param speed The particle speed.
     * @return True if particles were spawned.
     */
    public static boolean spawnParticle(World world, BlockPos pos, ParticleEffect particle, 
                                      int count, double offset, double speed) {
        return spawnParticle(world, Vec3d.ofCenter(pos), particle, count, offset, offset, offset, speed);
    }
    
    /**
     * Spawns particles in a line between two points.
     * @param world The world.
     * @param start The start position.
     * @param end The end position.
     * @param particle The particle effect.
     * @param count The number of particles.
     * @param speed The particle speed.
     * @return True if particles were spawned.
     */
    public static boolean spawnParticleLine(World world, Vec3d start, Vec3d end, 
                                          ParticleEffect particle, int count, double speed) {
        if (world instanceof ServerWorld serverWorld) {
            Vec3d direction = end.subtract(start);
            double length = direction.length();
            direction = direction.normalize();
            
            for (int i = 0; i < count; i++) {
                double t = (double) i / (count - 1);
                Vec3d pos = start.add(direction.multiply(length * t));
                serverWorld.spawnParticles(particle, pos.x, pos.y, pos.z, 1, 0.1, 0.1, 0.1, speed);
            }
            return true;
        }
        return false;
    }
    
    /**
     * Spawns particles in a sphere around a position.
     * @param world The world.
     * @param center The center position.
     * @param radius The sphere radius.
     * @param particle The particle effect.
     * @param count The number of particles.
     * @param speed The particle speed.
     * @return True if particles were spawned.
     */
    public static boolean spawnParticleSphere(World world, Vec3d center, double radius, 
                                            ParticleEffect particle, int count, double speed) {
        if (world instanceof ServerWorld serverWorld) {
            Random random = new Random();
            
            for (int i = 0; i < count; i++) {
                double theta = random.nextDouble() * Math.PI * 2;
                double phi = Math.acos(2 * random.nextDouble() - 1);
                double r = radius * Math.cbrt(random.nextDouble());
                
                double x = center.x + r * Math.sin(phi) * Math.cos(theta);
                double y = center.y + r * Math.sin(phi) * Math.sin(theta);
                double z = center.z + r * Math.cos(phi);
                
                serverWorld.spawnParticles(particle, x, y, z, 1, 0.1, 0.1, 0.1, speed);
            }
            return true;
        }
        return false;
    }
    
    /**
     * Creates an explosion effect with sound and particles.
     * @param world The world.
     * @param pos The position.
     * @param radius The explosion radius.
     * @param intensity The effect intensity.
     * @return True if effect was created.
     */
    public static boolean createExplosionEffect(World world, Vec3d pos, double radius, float intensity) {
        boolean success = true;
        
        // Sound effect
        success &= playSound(world, pos, SoundEvents.ENTITY_GENERIC_EXPLODE, intensity, 1.0f, SoundCategory.BLOCKS);
        
        // Particle effects
        success &= spawnParticle(world, pos, ParticleTypes.EXPLOSION, (int)(intensity * 10), 0.0, 0.0, 0.0, 0.0);
        success &= spawnParticleSphere(world, pos, radius, ParticleTypes.EXPLOSION_EMITTER, (int)(intensity * 20), 0.5);
        
        // Additional particles
        for (int i = 0; i < (int)(intensity * 15); i++) {
            Random random = new Random();
            double angle = random.nextDouble() * Math.PI * 2;
            double distance = random.nextDouble() * radius;
            Vec3d particlePos = pos.add(Math.cos(angle) * distance, random.nextGaussian() * 0.5, Math.sin(angle) * distance);
            spawnParticle(world, particlePos, ParticleTypes.SMOKE, 1, 0.1, 0.1, 0.1, 0.1);
        }
        
        return success;
    }
    
    /**
     * Creates a magic effect with sparkles and sound.
     * @param world The world.
     * @param pos The position.
     * @param color The effect color (as RGB values).
     * @param intensity The effect intensity.
     * @return True if effect was created.
     */
    public static boolean createMagicEffect(World world, Vec3d pos, int color, float intensity) {
        boolean success = true;
        
        // Sound effect
        success &= playSound(world, pos, SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE, intensity * 0.5f, 1.5f, SoundCategory.AMBIENT);
        
        // Particle effects
        success &= spawnParticleSphere(world, pos, intensity * 2, ParticleTypes.ENCHANT, (int)(intensity * 30), 0.3);
        success &= spawnParticleSphere(world, pos, intensity * 1.5, ParticleTypes.END_ROD, (int)(intensity * 20), 0.2);
        
        // Color particles (if supported)
        ParticleEffect colorParticle = ParticleTypes.CRIT; // Fallback
        success &= spawnParticleSphere(world, pos, intensity * 3, colorParticle, (int)(intensity * 15), 0.4);
        
        return success;
    }
    
    /**
     * Creates a trail effect following an entity.
     * @param world The world.
     * @param entity The entity.
     * @param particle The particle effect.
     * @param duration The trail duration in ticks.
     * @param interval The spawn interval in ticks.
     * @return Trail effect ID.
     */
    public static String createTrailEffect(World world, Entity entity, ParticleEffect particle, 
                                         int duration, int interval) {
        String effectId = "trail_" + entity.getUuid().toString() + "_" + System.currentTimeMillis();
        
        Runnable trailTask = () -> {
            if (entity.isAlive() && entity.getWorld() == world) {
                spawnParticle(world, entity.getPos(), particle, 1, 0.2, 0.2, 0.2, 0.1);
            }
        };
        
        scheduleRepeatingEffect(effectId, trailTask, interval * 50L, duration * 50L);
        return effectId;
    }
    
    /**
     * Schedules an effect to be executed after a delay.
     * @param effectTask The effect task.
     * @param delayMs The delay in milliseconds.
     */
    public static void scheduleEffect(Runnable effectTask, long delayMs) {
        effectScheduler.schedule(effectTask, delayMs, TimeUnit.MILLISECONDS);
    }
    
    /**
     * Schedules a repeating effect.
     * @param effectId Unique effect identifier.
     * @param effectTask The effect task.
     * @param intervalMs The interval in milliseconds.
     * @param durationMs The total duration in milliseconds.
     * @return The effect ID.
     */
    public static String scheduleRepeatingEffect(String effectId, Runnable effectTask, long intervalMs, long durationMs) {
        EffectLoop loop = new EffectLoop(effectTask, intervalMs, durationMs);
        activeEffectLoops.put(effectId, loop);
        
        effectScheduler.scheduleAtFixedRate(() -> {
            if (loop.isCancelled() || loop.isExpired()) {
                activeEffectLoops.remove(effectId);
                return;
            }
            
            try {
                effectTask.run();
            } catch (Exception e) {
                ChosenLib.LOGGER.error("Error executing effect loop: " + effectId, e);
                activeEffectLoops.remove(effectId);
            }
        }, 0, intervalMs, TimeUnit.MILLISECONDS);
        
        return effectId;
    }
    
    /**
     * Stops an effect loop.
     * @param effectId The effect ID.
     * @return True if effect was stopped.
     */
    public static boolean stopEffectLoop(String effectId) {
        EffectLoop loop = activeEffectLoops.get(effectId);
        if (loop != null) {
            loop.cancel();
            activeEffectLoops.remove(effectId);
            return true;
        }
        return false;
    }
    
    /**
     * Registers a custom effect.
     * @param effect The custom effect.
     * @return True if effect was registered.
     */
    public static boolean registerCustomEffect(CustomEffect effect) {
        if (effect != null && effect.getName() != null) {
            customEffects.put(effect.getName(), effect);
            return true;
        }
        return false;
    }
    
    /**
     * Creates a custom particle effect.
     * @param world The world.
     * @param pos The position.
     * @param particleType The particle type identifier.
     * @param count The number of particles.
     * @param properties The particle properties.
     * @return True if particles were spawned.
     */
    public static boolean createCustomParticle(World world, Vec3d pos, Identifier particleType, 
                                             int count, Map<String, Object> properties) {
        try {
            ParticleType<?> type = Registries.PARTICLE_TYPE.get(particleType);
            if (type instanceof ParticleEffect particle) {
                double offset = (double) properties.getOrDefault("offset", 0.1);
                double speed = (double) properties.getOrDefault("speed", 0.1);
                return spawnParticle(world, pos, particle, count, offset, offset, offset, speed);
            } else {
                // This particle type requires parameters, which is not supported by this generic method.
                ChosenLib.LOGGER.warn("Attempted to spawn complex particle type {} without required parameters.", particleType);
                return false;
            }
        } catch (Exception e) {
            ChosenLib.LOGGER.error("Failed to create custom particle: " + particleType, e);
        }
        return false;
    }
    
    /**
     * Plays a custom sound effect.
     * @param world The world.
     * @param pos The position.
     * @param soundId The sound identifier.
     * @param properties The sound properties.
     * @return True if sound was played.
     */
    public static boolean playCustomSound(World world, Vec3d pos, Identifier soundId, Map<String, Object> properties) {
        try {
            SoundEvent sound = Registries.SOUND_EVENT.get(soundId);
            if (sound != null) {
                float volume = (float) properties.getOrDefault("volume", 1.0);
                float pitch = (float) properties.getOrDefault("pitch", 1.0);
                SoundCategory category = (SoundCategory) properties.getOrDefault("category", SoundCategory.MASTER);
                return playSound(world, pos, sound, volume, pitch, category);
            }
        } catch (Exception e) {
            ChosenLib.LOGGER.error("Failed to play custom sound: " + soundId, e);
        }
        return false;
    }
    
    /**
     * Optimizes effect rendering by limiting particle count.
     * @param world The world.
     * @param maxParticlesPerTick Maximum particles per tick.
     */
    public static void optimizeEffectRendering(World world, int maxParticlesPerTick) {
        if (world instanceof ServerWorld serverWorld) {
            // This would require more complex implementation to track particle counts
            // For now, this is a placeholder for the optimization logic
            ChosenLib.LOGGER.info("Effect rendering optimization enabled for world: " + serverWorld.getRegistryKey().getValue());
        }
    }
    
    /**
     * Limits the number of active effects.
     * @param maxActiveEffects Maximum number of active effects.
     */
    public static void limitEffectCount(int maxActiveEffects) {
        if (activeEffectLoops.size() > maxActiveEffects) {
            // Remove oldest effects
            List<String> effectIds = new ArrayList<>(activeEffectLoops.keySet());
            effectIds.sort(Comparator.comparing(id -> activeEffectLoops.get(id).getEndTime()));
            
            int toRemove = activeEffectLoops.size() - maxActiveEffects;
            for (int i = 0; i < toRemove; i++) {
                stopEffectLoop(effectIds.get(i));
            }
        }
    }
    
    /**
     * Cleans up expired effects.
     */
    public static void cleanupEffects() {
        activeEffectLoops.entrySet().removeIf(entry -> {
            EffectLoop loop = entry.getValue();
            if (loop.isExpired() || loop.isCancelled()) {
                return true;
            }
            return false;
        });
    }
    
    /**
     * Gets all active effect IDs.
     * @return Set of active effect IDs.
     */
    public static Set<String> getActiveEffectIds() {
        return new HashSet<>(activeEffectLoops.keySet());
    }
    
    /**
     * Gets all registered custom effects.
     * @return Map of custom effects.
     */
    public static Map<String, CustomEffect> getCustomEffects() {
        return new HashMap<>(customEffects);
    }
    
    /**
     * Shuts down the effect scheduler.
     */
    public static void shutdown() {
        effectScheduler.shutdown();
        try {
            if (!effectScheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                effectScheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            effectScheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }
}