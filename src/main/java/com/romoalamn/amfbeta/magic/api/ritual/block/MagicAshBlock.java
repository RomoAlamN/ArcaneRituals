package com.romoalamn.amfbeta.magic.api.ritual.block;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.romoalamn.amfbeta.magic.api.ritual.RitualBlocks;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.particles.RedstoneParticleData;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.RedstoneSide;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

@SuppressWarnings("deprecation")
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class MagicAshBlock extends Block {
    public static final EnumProperty<RedstoneSide> NORTH = BlockStateProperties.REDSTONE_NORTH;
    public static final EnumProperty<RedstoneSide> EAST = BlockStateProperties.REDSTONE_EAST;
    public static final EnumProperty<RedstoneSide> SOUTH = BlockStateProperties.REDSTONE_SOUTH;
    public static final EnumProperty<RedstoneSide> WEST = BlockStateProperties.REDSTONE_WEST;

    public static final IntegerProperty POWER = BlockStateProperties.POWER_0_15;

    public static final Map<Direction, EnumProperty<RedstoneSide>> FACING_PROPERTY_MAP =
            Maps.newEnumMap(ImmutableMap.of(Direction.NORTH, NORTH, Direction.SOUTH, SOUTH, Direction.EAST, EAST, Direction.WEST, WEST));
    protected static final VoxelShape[] SHAPES = new VoxelShape[]{
            Block.makeCuboidShape(3.0D, 0.0D, 3.0D, 13.0D, 1.0D, 13.0D),
            Block.makeCuboidShape(3.0D, 0.0D, 3.0D, 13.0D, 1.0D, 16.0D),
            Block.makeCuboidShape(0.0D, 0.0D, 3.0D, 13.0D, 1.0D, 13.0D),
            Block.makeCuboidShape(0.0D, 0.0D, 3.0D, 13.0D, 1.0D, 16.0D),
            Block.makeCuboidShape(3.0D, 0.0D, 0.0D, 13.0D, 1.0D, 13.0D),
            Block.makeCuboidShape(3.0D, 0.0D, 0.0D, 13.0D, 1.0D, 16.0D),
            Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 13.0D, 1.0D, 13.0D),
            Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 13.0D, 1.0D, 16.0D),
            Block.makeCuboidShape(3.0D, 0.0D, 3.0D, 16.0D, 1.0D, 13.0D),
            Block.makeCuboidShape(3.0D, 0.0D, 3.0D, 16.0D, 1.0D, 16.0D),
            Block.makeCuboidShape(0.0D, 0.0D, 3.0D, 16.0D, 1.0D, 13.0D),
            Block.makeCuboidShape(0.0D, 0.0D, 3.0D, 16.0D, 1.0D, 16.0D),
            Block.makeCuboidShape(3.0D, 0.0D, 0.0D, 16.0D, 1.0D, 13.0D),
            Block.makeCuboidShape(3.0D, 0.0D, 0.0D, 16.0D, 1.0D, 16.0D),
            Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 1.0D, 13.0D),
            Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 1.0D, 16.0D)
    };

    private boolean canProvidePower = true;
    private final Set<BlockPos> blocksNeedingUpdate = Sets.newHashSet();

    public MagicAshBlock(Properties properties) {
        super(properties);
        setDefaultState(this.getStateContainer().getBaseState()
                .with(NORTH, RedstoneSide.NONE)
                .with(EAST, RedstoneSide.NONE)
                .with(SOUTH, RedstoneSide.NONE)
                .with(WEST, RedstoneSide.NONE)
        );
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return SHAPES[getAABBIndex(state)];
    }

    private int getAABBIndex(BlockState state) {
        int i = 0;
        boolean flag = state.get(NORTH) != RedstoneSide.NONE;
        boolean flag1 = state.get(EAST) != RedstoneSide.NONE;
        boolean flag2 = state.get(SOUTH) != RedstoneSide.NONE;
        boolean flag3 = state.get(WEST) != RedstoneSide.NONE;
        if (flag || flag2 && !flag1 && !flag3) {
            i |= 1 << Direction.NORTH.getHorizontalIndex();
        }
        if (flag1 || flag3 && !flag && !flag2) {
            i |= 1 << Direction.EAST.getHorizontalIndex();
        }

        if (flag2 || flag && !flag1 && !flag3) {
            i |= 1 << Direction.SOUTH.getHorizontalIndex();
        }

        if (flag3 || flag1 && !flag && !flag2) {
            i |= 1 << Direction.WEST.getHorizontalIndex();
        }
        return i;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        IBlockReader reader = context.getWorld();
        BlockPos pos = context.getPos();
        return getStateFromSides(reader, pos);
    }

    public BlockState getStateFromSides(IBlockReader reader, BlockPos pos) {
        return this.getDefaultState()
                .with(WEST, getSide(reader, pos, Direction.WEST))
                .with(EAST, getSide(reader, pos, Direction.EAST))
                .with(NORTH, getSide(reader, pos, Direction.NORTH))
                .with(SOUTH, getSide(reader, pos, Direction.SOUTH));
    }

    @Override
    public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
        if (facing == Direction.DOWN) {
            return stateIn;
        } else {
            return facing == Direction.UP ? getStateFromSides(worldIn, currentPos) : stateIn.with(FACING_PROPERTY_MAP.get(facing), getSide(worldIn, currentPos, facing));
        }
    }

    @Override
    public void updateDiagonalNeighbors(BlockState state, IWorld worldIn, BlockPos pos, int flags) {
        try (BlockPos.PooledMutable mut = BlockPos.PooledMutable.retain()) {
            for (Direction direction : Direction.Plane.HORIZONTAL) {
                RedstoneSide side = state.get(FACING_PROPERTY_MAP.get(direction));
                if (side != RedstoneSide.NONE && worldIn.getBlockState(mut.setPos(pos).move(direction)).getBlock() != this) {
                    mut.move(Direction.DOWN);
                    BlockState downState = worldIn.getBlockState(mut);
                    if (downState.getBlock() != Blocks.OBSERVER) {
                        BlockPos newPos = mut.offset(direction.getOpposite());
                        BlockState state1 = downState.updatePostPlacement(direction.getOpposite(), worldIn.getBlockState(newPos), worldIn, mut, newPos);
                        replaceBlock(downState, state1, worldIn, mut, flags);
                    }
                    mut.setPos(pos).move(direction).move(Direction.UP);
                    BlockState state3 = worldIn.getBlockState(mut);
                    if (state3.getBlock() != Blocks.OBSERVER) {
                        BlockPos newPos = mut.offset(direction.getOpposite());
                        BlockState state1 = state3.updatePostPlacement(direction.getOpposite(), worldIn.getBlockState(newPos), worldIn, mut, newPos);
                        replaceBlock(state3, state1, worldIn, mut, flags);
                    }
                }
            }
        }
    }

    private RedstoneSide getSide(IBlockReader reader, BlockPos pos, Direction face) {
        BlockPos newPos = pos.offset(face);
        BlockState state = reader.getBlockState(newPos);
        BlockPos upPos = pos.up();
        BlockState upState = reader.getBlockState(upPos);
        if (!upState.isNormalCube(reader, upPos)) {
            boolean flag = state.isSolidSide(reader, newPos, Direction.UP) || state.getBlock() == Blocks.HOPPER;
            if (flag && canConnectTo(reader.getBlockState(newPos.up()), reader, newPos.up(), null)) {
                if (state.isCollisionShapeOpaque(reader, newPos)) {
                    return RedstoneSide.UP;
                }
            }
            return RedstoneSide.SIDE;
        }
        return !canConnectTo(state, reader, newPos, face)
                && (state.isNormalCube(reader, newPos) || !canConnectTo(reader.getBlockState(newPos.down()), reader, newPos.down(), null))
                ? RedstoneSide.NONE : RedstoneSide.SIDE;
    }

    @Override
    public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos) {
        BlockPos underPos = pos.down();
        BlockState underState = worldIn.getBlockState(underPos);
        return underState.isSolidSide(worldIn, underPos, Direction.UP) || underState.getBlock() == Blocks.HOPPER;
    }

    private BlockState updateSurroundingAsh(World worldIn, BlockPos pos, BlockState state) {
        state = updatePower(worldIn, pos, state);
        List<BlockPos> list = Lists.newArrayList(this.blocksNeedingUpdate);
        this.blocksNeedingUpdate.clear();
        for (BlockPos neighbor : list) {
            worldIn.notifyNeighborsOfStateChange(neighbor, this);
        }
        return state;
    }

    private BlockState updatePower(World worldIn, BlockPos pos, BlockState state) {
        BlockState tempState = state;
        int i = state.get(POWER);
        this.canProvidePower = false;
        int j = getAshPowerFromNeighbors(worldIn, pos);
        this.canProvidePower = true;
        int k = 0;
        if (j < 15) {
            for (Direction dir : Direction.Plane.HORIZONTAL) {
                BlockPos newPos = pos.offset(dir);
                BlockState newState = worldIn.getBlockState(newPos);
                k = maxSignal(k, newState);
                BlockPos upPos = pos.up();
                if (newState.isNormalCube(worldIn, newPos) && !worldIn.getBlockState(upPos).isNormalCube(worldIn, upPos)) {
                    k = maxSignal(k, worldIn.getBlockState(upPos));
                } else if (!newState.isNormalCube(worldIn, newPos)) {
                    k = maxSignal(k, worldIn.getBlockState(newPos.down()));
                }
            }
        }
        int l = k - 1;
        if (j > l) {
            l = j;
        }
        if (i != l) {
            state = state.with(POWER, l);
            if (worldIn.getBlockState(pos) == tempState)
                worldIn.setBlockState(pos, state, 2);

            this.blocksNeedingUpdate.add(pos);
            for (Direction dir : Direction.values()) {
                this.blocksNeedingUpdate.add(pos.offset(dir));
            }
        }
        return state;
    }

    private int getAshPowerFromNeighbors(World worldIn, BlockPos pos) {
        for (Direction dir : Direction.values()) {
            if (isPowerSourceAt(worldIn, pos, dir)) {
                return 15;
            }
        }
        return 0;
    }

    private void notifyAshNeighborsOfStateChange(World worldIn, BlockPos pos) {
        if (worldIn.getBlockState(pos).getBlock() == this) {
            worldIn.notifyNeighborsOfStateChange(pos, this);
            for (Direction dir : Direction.values()) {
                worldIn.notifyNeighborsOfStateChange(pos.offset(dir), this);
            }
        }
    }

    @Override
    public void onBlockAdded(BlockState state, World worldIn, BlockPos pos, BlockState oldState, boolean isMoving) {
        if (oldState.getBlock() != state.getBlock() && !worldIn.isRemote) {
            updateSurroundingAsh(worldIn, pos, state);
            for (Direction dir : Direction.Plane.VERTICAL) {
                worldIn.notifyNeighborsOfStateChange(pos.offset(dir), this);
            }
            updateNeighbors(worldIn, pos);
        }
    }

    @Override
    public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!isMoving && state.getBlock() != newState.getBlock()) {
            super.onReplaced(state, worldIn, pos, newState, false);
            if (!worldIn.isRemote) {
                for (Direction dir : Direction.values()) {
                    worldIn.notifyNeighborsOfStateChange(pos.offset(dir), this);
                }
                this.updateSurroundingAsh(worldIn, pos, state);
                updateNeighbors(worldIn, pos);
            }

        }
    }

    private void updateNeighbors(World worldIn, BlockPos pos) {
        for (Direction dir : Direction.Plane.HORIZONTAL) {
            notifyAshNeighborsOfStateChange(worldIn, pos.offset(dir));
        }
        for (Direction dir : Direction.Plane.HORIZONTAL) {
            BlockPos newPos = pos.offset(dir);
            if (worldIn.getBlockState(newPos).isNormalCube(worldIn, newPos)) {
                notifyAshNeighborsOfStateChange(worldIn, newPos.up());
            } else {
                notifyAshNeighborsOfStateChange(worldIn, newPos.down());
            }
        }
    }

    private int maxSignal(int existing, BlockState neighbor) {
        if (neighbor.getBlock() != this) {
            return existing;
        } else {
            int i = neighbor.get(POWER);
            return Math.max(i, existing);
        }
    }

    @Override
    public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
        if (!worldIn.isRemote) {
            if (state.isValidPosition(worldIn, pos)) {
                updateSurroundingAsh(worldIn, pos, state);
            } else {
                spawnDrops(state, worldIn, pos);
                worldIn.removeBlock(pos, false);
            }
        }
    }

    private boolean isPowerSourceAt(IBlockReader worldIn, BlockPos pos, Direction side) {
        BlockPos newPos = pos.offset(side);
        BlockState newState = worldIn.getBlockState(newPos);
        return newState.getBlock() == RitualBlocks.RITUAL_FOCUS.get();
    }

    protected static boolean canConnectTo(BlockState state, IBlockReader reader, BlockPos pos, @Nullable Direction side) {
        Block block = state.getBlock();
        if (block == RitualBlocks.ASH_WIRE.get()) {
            return true;
        }
        if(block == RitualBlocks.RITUAL_FOCUS.get()){
            return true;
        }
        if (block == RitualBlocks.ALTAR.get()) {
            return true;
        }
        return false;
    }

    @OnlyIn(Dist.CLIENT)
    public static int colorMultiplier(int power) {
        float powerFactor = getPowerFactor(power);
        float redFactor = powerFactor * 0.6F + 0.4F;
        if (power == 0) {
            redFactor = 0.3F;
        }

        float blueFactor = powerFactor * powerFactor * 0.7F - 0.5F;
        float greenFactor = powerFactor * powerFactor * 0.6F - 0.7F;
        if (blueFactor < 0.0F) {
            blueFactor = 0.0F;
        }

        if (greenFactor < 0.0F) {
            greenFactor = 0.0F;
        }

        int i = MathHelper.clamp((int) (redFactor * 255.0F), 0, 255);
        int j = MathHelper.clamp((int) (blueFactor * 255.0F), 0, 255);
        int k = MathHelper.clamp((int) (greenFactor * 255.0F), 0, 255);
        return -16777216 | i << 16 | j << 8 | k;
    }

    private static float getPowerFactor(int power) {
        float powerFactor;
        if (power > 0) {
            powerFactor = 1;
        } else {
            powerFactor = 0;
        }
        return powerFactor;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {
        int i = stateIn.get(POWER);
        if (i != 0) {
            double d0 = (double) pos.getX() + 0.5D + (rand.nextDouble() - 0.5D) * 0.2D;
            double d1 = (pos.getY() + 0.0625);
            double d2 = (double) pos.getZ() + 0.5D + (rand.nextDouble() - 0.5D) * 0.2D;
            float f = getPowerFactor(i);
            float f1 = f * 0.6F + 0.4F;
            float f2 = Math.max(0.0F, f * f * 0.7F - 0.5F);
            float f3 = Math.max(0.0F, f * f * 0.6F - 0.7F);
            worldIn.addParticle(new RedstoneParticleData(f1, f2, f3, 1.0F), d0, d1, d2, 0.0D, 0.0D, 0.0D);
        }
    }

    /**
     * Returns the blockstate with the given rotation from the passed blockstate. If inapplicable, returns the passed
     * blockstate.
     *
     * @param state
     * @param rot
     * @deprecated call viawhenever possible. Implementing/overriding is
     * fine.
     */
    @Override
    public BlockState rotate(BlockState state, Rotation rot) {
        switch (rot) {
            case CLOCKWISE_180:
                return state.with(NORTH, state.get(SOUTH)).with(EAST, state.get(WEST)).with(SOUTH, state.get(NORTH)).with(WEST, state.get(EAST));
            case COUNTERCLOCKWISE_90:
                return state.with(NORTH, state.get(EAST)).with(EAST, state.get(SOUTH)).with(SOUTH, state.get(WEST)).with(WEST, state.get(NORTH));
            case CLOCKWISE_90:
                return state.with(NORTH, state.get(WEST)).with(EAST, state.get(NORTH)).with(SOUTH, state.get(EAST)).with(WEST, state.get(SOUTH));
            default:
                return state;
        }
    }

    /**
     * Returns the blockstate with the given mirror of the passed blockstate. If inapplicable, returns the passed
     * blockstate.
     *
     * @param state
     * @param mirrorIn
     * @deprecated call via  whenever possible. Implementing/overriding is fine.
     */
    @Override
    public BlockState mirror(BlockState state, Mirror mirrorIn) {
        switch (mirrorIn) {
            case LEFT_RIGHT:
                return state.with(NORTH, state.get(SOUTH)).with(SOUTH, state.get(NORTH));
            case FRONT_BACK:
                return state.with(EAST, state.get(WEST)).with(WEST, state.get(EAST));
            default:
                return super.mirror(state, mirrorIn);
        }
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(NORTH, EAST, SOUTH, WEST, POWER);
    }
}
