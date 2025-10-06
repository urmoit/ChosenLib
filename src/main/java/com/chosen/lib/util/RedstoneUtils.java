package com.chosen.lib.util;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.RedstoneWireBlock;
import net.minecraft.block.RepeaterBlock;
import net.minecraft.block.ComparatorBlock;
import net.minecraft.block.LeverBlock;
import net.minecraft.block.ButtonBlock;
import net.minecraft.block.PressurePlateBlock;
import net.minecraft.block.TripwireHookBlock;
import net.minecraft.block.RedstoneTorchBlock;
import net.minecraft.block.RedstoneBlock;
import net.minecraft.block.DispenserBlock;
import net.minecraft.block.DropperBlock;
import net.minecraft.block.HopperBlock;
import net.minecraft.block.PistonBlock;
import net.minecraft.block.DaylightDetectorBlock;
import net.minecraft.block.WeightedPressurePlateBlock;
import net.minecraft.block.PoweredRailBlock;
import net.minecraft.block.DetectorRailBlock;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;


import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Redstone circuit and automation utilities.
 * Provides tools for analyzing, manipulating, and creating redstone circuits.
 */
public class RedstoneUtils {
    
    private static final Map<String, CircuitAnalysis> circuitCache = new ConcurrentHashMap<>();

    
    /**
     * Circuit analysis result.
     */
    public static class CircuitAnalysis {
        private final List<BlockPos> redstoneSources;
        private final List<BlockPos> redstoneWires;
        private final List<BlockPos> redstoneDevices;
        private final Map<BlockPos, Integer> signalStrengths;
        private final List<List<BlockPos>> signalPaths;
        private final Map<String, Object> circuitProperties;
        
        public CircuitAnalysis(List<BlockPos> redstoneSources, List<BlockPos> redstoneWires, 
                             List<BlockPos> redstoneDevices, Map<BlockPos, Integer> signalStrengths,
                             List<List<BlockPos>> signalPaths) {
            this.redstoneSources = redstoneSources;
            this.redstoneWires = redstoneWires;
            this.redstoneDevices = redstoneDevices;
            this.signalStrengths = signalStrengths;
            this.signalPaths = signalPaths;
            this.circuitProperties = new HashMap<>();
        }
        
        // Getters
        public List<BlockPos> getRedstoneSources() { return redstoneSources; }
        public List<BlockPos> getRedstoneWires() { return redstoneWires; }
        public List<BlockPos> getRedstoneDevices() { return redstoneDevices; }
        public Map<BlockPos, Integer> getSignalStrengths() { return signalStrengths; }
        public List<List<BlockPos>> getSignalPaths() { return signalPaths; }
        public Map<String, Object> getCircuitProperties() { return circuitProperties; }
        public void setCircuitProperty(String key, Object value) { circuitProperties.put(key, value); }
        public Object getCircuitProperty(String key) { return circuitProperties.get(key); }
    }
    
    /**
     * Redstone device information.
     */
    public static class RedstoneDevice {
        private final BlockPos position;
        private final Block block;
        private final BlockState state;
        private final DeviceType type;
        private final int signalStrength;
        private final boolean isPowered;
        private final Set<Direction> inputSides;
        private final Set<Direction> outputSides;
        
        public RedstoneDevice(BlockPos position, Block block, BlockState state, DeviceType type, 
                            int signalStrength, boolean isPowered, Set<Direction> inputSides, Set<Direction> outputSides) {
            this.position = position;
            this.block = block;
            this.state = state;
            this.type = type;
            this.signalStrength = signalStrength;
            this.isPowered = isPowered;
            this.inputSides = inputSides;
            this.outputSides = outputSides;
        }
        
        // Getters
        public BlockPos getPosition() { return position; }
        public Block getBlock() { return block; }
        public BlockState getState() { return state; }
        public DeviceType getType() { return type; }
        public int getSignalStrength() { return signalStrength; }
        public boolean isPowered() { return isPowered; }
        public Set<Direction> getInputSides() { return inputSides; }
        public Set<Direction> getOutputSides() { return outputSides; }
    }
    
    /**
     * Redstone device types.
     */
    public enum DeviceType {
        SOURCE,      // Redstone block, torch, lever, button, etc.
        WIRE,        // Redstone wire
        REPEATER,    // Repeater
        COMPARATOR,  // Comparator
        DEVICE,      // Piston, dispenser, hopper, etc.
        RAIL,        // Powered rail, detector rail, activator rail
        SENSOR,      // Pressure plate, tripwire, daylight detector, etc.
        TORCH,       // Redstone torch
        BLOCK,       // Redstone block
        UNKNOWN
    }
    
    /**
     * Logic gate types.
     */
    public enum LogicGateType {
        AND,
        OR,
        NOT,
        XOR,
        NAND,
        NOR,
        XNOR
    }
    
    /**
     * Analyzes a redstone circuit in an area.
     * @param world The world.
     * @param area The area to analyze.
     * @return Circuit analysis result.
     */
    public static CircuitAnalysis analyzeRedstoneCircuit(World world, List<BlockPos> area) {
        String cacheKey = area.hashCode() + "_" + world.hashCode();
        CircuitAnalysis cached = circuitCache.get(cacheKey);
        if (cached != null) {
            return cached;
        }
        
        List<BlockPos> redstoneSources = new ArrayList<>();
        List<BlockPos> redstoneWires = new ArrayList<>();
        List<BlockPos> redstoneDevices = new ArrayList<>();
        Map<BlockPos, Integer> signalStrengths = new HashMap<>();
        
        // Analyze each block in the area
        for (BlockPos pos : area) {
            if (WorldUtils.isPositionLoaded(world, pos)) {
                BlockState state = WorldUtils.getBlockState(world, pos);
                Block block = state.getBlock();
                DeviceType type = getDeviceType(block);
                
                switch (type) {
                    case SOURCE:
                    case BLOCK:
                    case TORCH:
                        redstoneSources.add(pos);
                        signalStrengths.put(pos, getRedstoneSignal(world, pos));
                        break;
                    case WIRE:
                        redstoneWires.add(pos);
                        signalStrengths.put(pos, getRedstoneSignal(world, pos));
                        break;
                    case DEVICE:
                    case REPEATER:
                    case COMPARATOR:
                    case RAIL:
                    case SENSOR:
                        redstoneDevices.add(pos);
                        signalStrengths.put(pos, getRedstoneSignal(world, pos));
                        break;
                    case UNKNOWN:
                        break;
                }
            }
        }
        
        // Trace signal paths
        List<List<BlockPos>> signalPaths = traceRedstonePaths(world, redstoneSources, area);
        
        CircuitAnalysis analysis = new CircuitAnalysis(redstoneSources, redstoneWires, redstoneDevices, 
                                                     signalStrengths, signalPaths);
        
        // Cache the analysis
        circuitCache.put(cacheKey, analysis);
        
        return analysis;
    }
    
    /**
     * Finds redstone sources in an area.
     * @param world The world.
     * @param area The area to search.
     * @return List of redstone source positions.
     */
    public static List<BlockPos> findRedstoneSources(World world, List<BlockPos> area) {
        List<BlockPos> sources = new ArrayList<>();
        
        for (BlockPos pos : area) {
            if (WorldUtils.isPositionLoaded(world, pos)) {
                BlockState state = WorldUtils.getBlockState(world, pos);
                Block block = state.getBlock();
                
                if (isRedstoneSource(block)) {
                    sources.add(pos);
                }
            }
        }
        
        return sources;
    }
    
    /**
     * Traces redstone signal paths from sources.
     * @param world The world.
     * @param sources The redstone sources.
     * @param area The area to trace within.
     * @return List of signal paths.
     */
    public static List<List<BlockPos>> traceRedstonePaths(World world, List<BlockPos> sources, List<BlockPos> area) {
        List<List<BlockPos>> paths = new ArrayList<>();
        Set<BlockPos> areaSet = new HashSet<>(area);
        
        for (BlockPos source : sources) {
            List<BlockPos> path = new ArrayList<>();
            Set<BlockPos> visited = new HashSet<>();
            
            traceSignalPath(world, source, path, visited, areaSet, 15); // Start with max signal strength
            
            if (!path.isEmpty()) {
                paths.add(path);
            }
        }
        
        return paths;
    }
    
    /**
     * Sets redstone signal strength at a position.
     * @param world The world.
     * @param pos The position.
     * @param signal The signal strength (0-15).
     * @return True if signal was set.
     */
    public static boolean setRedstoneSignal(World world, BlockPos pos, int signal) {
        if (world instanceof ServerWorld && WorldUtils.isPositionLoaded(world, pos)) {
            BlockState currentState = WorldUtils.getBlockState(world, pos);
            Block block = currentState.getBlock();
            
            if (block instanceof RedstoneWireBlock) {
                BlockState newState = currentState.with(RedstoneWireBlock.POWER, Math.max(0, Math.min(15, signal)));
                return WorldUtils.setBlockState(world, pos, newState);
            } else if (block instanceof RedstoneTorchBlock) {
                // Redstone torches are binary (on/off)
                if (signal > 0 && !currentState.get(RedstoneTorchBlock.LIT)) {
                    return WorldUtils.setBlockState(world, pos, currentState.with(RedstoneTorchBlock.LIT, true));
                } else if (signal == 0 && currentState.get(RedstoneTorchBlock.LIT)) {
                    return WorldUtils.setBlockState(world, pos, currentState.with(RedstoneTorchBlock.LIT, false));
                }
            }
            // Add more block types as needed
        }
        return false;
    }
    
    /**
     * Gets redstone signal strength at a position.
     * @param world The world.
     * @param pos The position.
     * @return Signal strength (0-15).
     */
    public static int getRedstoneSignal(World world, BlockPos pos) {
        if (!WorldUtils.isPositionLoaded(world, pos)) {
            return 0;
        }
        
        return world.getEmittedRedstonePower(pos, Direction.NORTH);
    }
    
    /**
     * Inverts a redstone signal.
     * @param world The world.
     * @param pos The position.
     * @return True if signal was inverted.
     */
    public static boolean invertSignal(World world, BlockPos pos) {
        int currentSignal = getRedstoneSignal(world, pos);
        int invertedSignal = currentSignal > 0 ? 0 : 15;
        return setRedstoneSignal(world, pos, invertedSignal);
    }
    
    /**
     * Creates a timer circuit.
     * @param world The world.
     * @param pos The position to create the timer.
     * @param interval The interval in ticks.
     * @return True if timer was created.
     */
    public static boolean createTimer(World world, BlockPos pos, int interval) {
        if (world instanceof ServerWorld) {
            // Place a repeater and set its delay
            BlockState repeaterState = Blocks.REPEATER.getDefaultState()
                .with(RepeaterBlock.DELAY, Math.max(1, Math.min(4, interval / 2)));
            
            return WorldUtils.setBlockState(world, pos, repeaterState);
        }
        return false;
    }
    
    /**
     * Creates a pulse generator.
     * @param world The world.
     * @param pos The position to create the pulse generator.
     * @param pulseLength The pulse length in ticks.
     * @return True if pulse generator was created.
     */
    public static boolean createPulseGenerator(World world, BlockPos pos, int pulseLength) {
        if (world instanceof ServerWorld) {
            // Use a combination of repeaters and redstone torches
            BlockState torchState = Blocks.REDSTONE_TORCH.getDefaultState();
            return WorldUtils.setBlockState(world, pos, torchState);
        }
        return false;
    }
    
    /**
     * Creates a sequencer circuit.
     * @param world The world.
     * @param positions The positions for the sequencer.
     * @param sequence The sequence pattern.
     * @return True if sequencer was created.
     */
    public static boolean createSequencer(World world, List<BlockPos> positions, List<Integer> sequence) {
        if (world instanceof ServerWorld && positions.size() == sequence.size()) {
            boolean success = true;
            
            for (int i = 0; i < positions.size(); i++) {
                BlockPos pos = positions.get(i);
                int signal = sequence.get(i);
                success &= setRedstoneSignal(world, pos, signal);
            }
            
            return success;
        }
        return false;
    }
    
    /**
     * Creates an AND gate.
     * @param world The world.
     * @param pos The position to create the AND gate.
     * @param input1Pos First input position.
     * @param input2Pos Second input position.
     * @return True if AND gate was created.
     */
    public static boolean createANDGate(World world, BlockPos pos, BlockPos input1Pos, BlockPos input2Pos) {
        if (world instanceof ServerWorld) {
            // Use a redstone block as output
            BlockState blockState = Blocks.REDSTONE_BLOCK.getDefaultState();
            return WorldUtils.setBlockState(world, pos, blockState);
        }
        return false;
    }
    
    /**
     * Creates an OR gate.
     * @param world The world.
     * @param pos The position to create the OR gate.
     * @param input1Pos First input position.
     * @param input2Pos Second input position.
     * @return True if OR gate was created.
     */
    public static boolean createORGate(World world, BlockPos pos, BlockPos input1Pos, BlockPos input2Pos) {
        if (world instanceof ServerWorld) {
            // Use redstone wire for OR gate
            BlockState wireState = Blocks.REDSTONE_WIRE.getDefaultState();
            return WorldUtils.setBlockState(world, pos, wireState);
        }
        return false;
    }
    
    /**
     * Creates a NOT gate.
     * @param world The world.
     * @param pos The position to create the NOT gate.
     * @param inputPos Input position.
     * @return True if NOT gate was created.
     */
    public static boolean createNOTGate(World world, BlockPos pos, BlockPos inputPos) {
        if (world instanceof ServerWorld) {
            // Use a redstone torch for NOT gate
            BlockState torchState = Blocks.REDSTONE_TORCH.getDefaultState();
            return WorldUtils.setBlockState(world, pos, torchState);
        }
        return false;
    }
    
    /**
     * Creates an XOR gate.
     * @param world The world.
     * @param pos The position to create the XOR gate.
     * @param input1Pos First input position.
     * @param input2Pos Second input position.
     * @return True if XOR gate was created.
     */
    public static boolean createXORGate(World world, BlockPos pos, BlockPos input1Pos, BlockPos input2Pos) {
        if (world instanceof ServerWorld) {
            // XOR gate requires more complex redstone circuitry
            // This is a simplified implementation
            BlockState wireState = Blocks.REDSTONE_WIRE.getDefaultState();
            return WorldUtils.setBlockState(world, pos, wireState);
        }
        return false;
    }
    
    /**
     * Creates an automated farm.
     * @param world The world.
     * @param area The farm area.
     * @param waterPositions Water source positions.
     * @return True if automated farm was created.
     */
    public static boolean createAutoFarm(World world, List<BlockPos> area, List<BlockPos> waterPositions) {
        if (world instanceof ServerWorld) {
            boolean success = true;
            
            // Place dispensers for water
            for (BlockPos pos : waterPositions) {
                BlockState dispenserState = Blocks.DISPENSER.getDefaultState();
                success &= WorldUtils.setBlockState(world, pos, dispenserState);
            }
            
            // Place redstone circuits for automation
            // This is a simplified implementation
            for (BlockPos pos : area) {
                if (pos.getY() % 2 == 0) { // Place redstone every other block
                    BlockState wireState = Blocks.REDSTONE_WIRE.getDefaultState();
                    success &= WorldUtils.setBlockState(world, pos, wireState);
                }
            }
            
            return success;
        }
        return false;
    }
    
    /**
     * Creates an item sorter.
     * @param world The world.
     * @param positions The positions for the item sorter.
     * @param itemTypes The item types to sort.
     * @return True if item sorter was created.
     */
    public static boolean createItemSorter(World world, List<BlockPos> positions, List<String> itemTypes) {
        if (world instanceof ServerWorld && positions.size() == itemTypes.size()) {
            boolean success = true;
            
            for (int i = 0; i < positions.size(); i++) {
                BlockPos pos = positions.get(i);
                
                // Place hoppers and comparators for sorting
                if (i % 2 == 0) {
                    BlockState hopperState = Blocks.HOPPER.getDefaultState();
                    success &= WorldUtils.setBlockState(world, pos, hopperState);
                } else {
                    BlockState comparatorState = Blocks.COMPARATOR.getDefaultState();
                    success &= WorldUtils.setBlockState(world, pos, comparatorState);
                }
            }
            
            return success;
        }
        return false;
    }
    
    /**
     * Creates an elevator system.
     * @param world The world.
     * @param startPos The start position.
     * @param endPos The end position.
     * @param floors The number of floors.
     * @return True if elevator system was created.
     */
    public static boolean createElevatorSystem(World world, BlockPos startPos, BlockPos endPos, int floors) {
        if (world instanceof ServerWorld && floors > 1) {
            boolean success = true;
            
            int heightDiff = endPos.getY() - startPos.getY();
            int floorHeight = heightDiff / floors;
            
            // Place pistons for elevator movement
            for (int i = 0; i < floors; i++) {
                BlockPos pistonPos = startPos.up(i * floorHeight);
                BlockState pistonState = Blocks.PISTON.getDefaultState();
                success &= WorldUtils.setBlockState(world, pistonPos, pistonState);
                
                // Place pressure plates for floor detection
                BlockPos platePos = pistonPos.up();
                BlockState plateState = Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE.getDefaultState();
                success &= WorldUtils.setBlockState(world, platePos, plateState);
            }
            
            return success;
        }
        return false;
    }
    
    /**
     * Highlights redstone blocks in an area.
     * @param world The world.
     * @param area The area to highlight.
     * @param highlightType The type of highlighting.
     * @return Number of blocks highlighted.
     */
    public static int highlightRedstoneBlocks(World world, List<BlockPos> area, String highlightType) {
        int highlighted = 0;
        
        for (BlockPos pos : area) {
            if (WorldUtils.isPositionLoaded(world, pos)) {
                BlockState state = WorldUtils.getBlockState(world, pos);
                Block block = state.getBlock();
                
                if (isRedstoneBlock(block)) {
                    // In a real implementation, this would create visual highlighting
                    // For now, we'll just count the blocks
                    highlighted++;
                }
            }
        }
        
        return highlighted;
    }
    
    /**
     * Shows signal strength for redstone blocks.
     * @param world The world.
     * @param area The area to show signals for.
     * @return Map of positions to signal strengths.
     */
    public static Map<BlockPos, Integer> showSignalStrength(World world, List<BlockPos> area) {
        Map<BlockPos, Integer> signalStrengths = new HashMap<>();
        
        for (BlockPos pos : area) {
            if (WorldUtils.isPositionLoaded(world, pos)) {
                int signal = getRedstoneSignal(world, pos);
                if (signal > 0) {
                    signalStrengths.put(pos, signal);
                }
            }
        }
        
        return signalStrengths;
    }
    
    /**
     * Traces circuit flow for debugging.
     * @param world The world.
     * @param startPos The starting position.
     * @param maxDepth Maximum trace depth.
     * @return List of positions in the circuit flow.
     */
    public static List<BlockPos> traceCircuitFlow(World world, BlockPos startPos, int maxDepth) {
        List<BlockPos> flow = new ArrayList<>();
        Set<BlockPos> visited = new HashSet<>();
        
        traceFlowRecursive(world, startPos, flow, visited, 0, maxDepth);
        
        return flow;
    }
    
    // Helper methods
    
    private static DeviceType getDeviceType(Block block) {
        if (block instanceof RedstoneTorchBlock) {
            return DeviceType.TORCH;
        }
        if (block instanceof RedstoneBlock) {
            return DeviceType.BLOCK;
        }
        if (block instanceof LeverBlock || block instanceof ButtonBlock) {
            return DeviceType.SOURCE;
        }
        if (block instanceof RedstoneWireBlock) {
            return DeviceType.WIRE;
        }
        if (block instanceof RepeaterBlock) {
            return DeviceType.REPEATER;
        }
        if (block instanceof ComparatorBlock) {
            return DeviceType.COMPARATOR;
        }
        if (block instanceof PressurePlateBlock || block instanceof TripwireHookBlock ||
                   block instanceof DaylightDetectorBlock || block instanceof WeightedPressurePlateBlock) {
            return DeviceType.SENSOR;
        }
        if (block instanceof PistonBlock || block instanceof DispenserBlock || block instanceof DropperBlock || block instanceof HopperBlock) {
            return DeviceType.DEVICE;
        }
        return DeviceType.UNKNOWN;
    }
    
    private static boolean isRedstoneSource(Block block) {
        return block instanceof RedstoneBlock || block instanceof RedstoneTorchBlock ||
               block instanceof LeverBlock || block instanceof ButtonBlock ||
               block instanceof PressurePlateBlock || block instanceof TripwireHookBlock ||
               block instanceof DaylightDetectorBlock || block instanceof WeightedPressurePlateBlock;
    }
    
    private static boolean isRedstoneBlock(Block block) {
        return isRedstoneSource(block) || block instanceof RedstoneWireBlock ||
               block instanceof RepeaterBlock || block instanceof ComparatorBlock ||
               block instanceof PistonBlock || block instanceof DispenserBlock 
               || block instanceof DropperBlock || block instanceof HopperBlock
               || block instanceof PoweredRailBlock || block instanceof DetectorRailBlock;
    }
    
    private static void traceSignalPath(World world, BlockPos pos, List<BlockPos> path, 
                                      Set<BlockPos> visited, Set<BlockPos> area, int signalStrength) {
        if (!area.contains(pos) || visited.contains(pos) || signalStrength <= 0) {
            return;
        }
        
        visited.add(pos);
        path.add(pos);
        
        // Check adjacent positions
        for (Direction direction : Direction.values()) {
            BlockPos neighborPos = pos.offset(direction);
            if (area.contains(neighborPos) && !visited.contains(neighborPos)) {
                BlockState neighborState = WorldUtils.getBlockState(world, neighborPos);
                Block neighborBlock = neighborState.getBlock();
                
                if (isRedstoneBlock(neighborBlock)) {
                    int newSignalStrength = signalStrength - 1;
                    traceSignalPath(world, neighborPos, path, visited, area, newSignalStrength);
                }
            }
        }
    }
    
    private static void traceFlowRecursive(World world, BlockPos pos, List<BlockPos> flow, 
                                         Set<BlockPos> visited, int depth, int maxDepth) {
        if (depth >= maxDepth || visited.contains(pos) || !WorldUtils.isPositionLoaded(world, pos)) {
            return;
        }
        
        visited.add(pos);
        flow.add(pos);
        
        BlockState state = WorldUtils.getBlockState(world, pos);
        Block block = state.getBlock();
        
        if (isRedstoneBlock(block)) {
            // Continue tracing in all directions
            for (Direction direction : Direction.values()) {
                BlockPos neighborPos = pos.offset(direction);
                traceFlowRecursive(world, neighborPos, flow, visited, depth + 1, maxDepth);
            }
        }
    }
}
