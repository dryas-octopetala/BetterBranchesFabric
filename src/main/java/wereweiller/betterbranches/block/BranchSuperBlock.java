package wereweiller.betterbranches.block;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.LeadItem;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.tick.ScheduledTickView;
import wereweiller.betterbranches.util.AllTags;

public class BranchSuperBlock extends ConnectingBlock implements Waterloggable {
    public static final MapCodec<BranchSuperBlock> CODEC = createCodec(BranchSuperBlock::new);

    // N-S-E-W-U-D Facings already inherited from ConnectingBlock
    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;

    @Override
    public MapCodec<BranchSuperBlock> getCodec() {
        return CODEC;
    }

    public BranchSuperBlock(Settings settings) {
        super(0.3125f, settings);
        this.setDefaultState(
                this.stateManager
                        .getDefaultState()
                        .with(NORTH, Boolean.FALSE)
                        .with(EAST, Boolean.FALSE)
                        .with(SOUTH, Boolean.FALSE)
                        .with(WEST, Boolean.FALSE)
                        .with(UP, Boolean.FALSE)
                        .with(DOWN, Boolean.FALSE)
                        .with(WATERLOGGED, Boolean.FALSE)
        );
    }

    /* FANCY NOTES
    Removed boxing for any boolean.valueOf statements but might have to add that back if necessary for some reason

	Look at FenceBlock for dynamic specific VoxelShape & cameraCollision based on blockstates.
	Need to implement custom culling shapes and also onUse with leads

            * * * * *
            * \     * \             S  U
            *   * * * * *             \|
            *   *   *   *         E ―   ― W
            * * * * *   *              |\
              \ *     \ *              D  N
                * * * * *
     */

    //Lets player add Leads to branches!! Need to make sure the model lines up properly.
    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        return (!world.isClient() ? LeadItem.attachHeldMobsToBlock(player, world, pos) : ActionResult.PASS);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return withConnectionProperties(ctx.getWorld(), ctx.getBlockPos(), this.getDefaultState(), ctx.getWorld().getFluidState(ctx.getBlockPos()));
    }

    public static BlockState withConnectionProperties(BlockView world, BlockPos pos, BlockState state, FluidState fluidState) {
        BlockState blockState = world.getBlockState(pos.down());
        BlockState blockState2 = world.getBlockState(pos.up());
        BlockState blockState3 = world.getBlockState(pos.north());
        BlockState blockState4 = world.getBlockState(pos.east());
        BlockState blockState5 = world.getBlockState(pos.south());
        BlockState blockState6 = world.getBlockState(pos.west());
        // `block` references self FYI. Unused unless we want branches to not connect to other wood types.
        Block block = state.getBlock();

        /* Might need to change `blockState.isIn(BlockTags.LOGS)` to a custom BlockTag.
        Adding BRANCH_SUPER to the vanilla "Logs" tag through
        craftweaker, etc. would break the behaviour.
        */
        return state
                .withIfExists(DOWN, blockState.isIn(AllTags.Blocks.BRANCHES) || blockState.isIn(BlockTags.LOGS))
                .withIfExists(UP, blockState2.isIn(AllTags.Blocks.BRANCHES) || blockState2.isIn(BlockTags.LOGS))
                .withIfExists(NORTH, blockState3.isIn(AllTags.Blocks.BRANCHES) || blockState3.isIn(BlockTags.LOGS))
                .withIfExists(EAST, blockState4.isIn(AllTags.Blocks.BRANCHES) || blockState4.isIn(BlockTags.LOGS))
                .withIfExists(SOUTH, blockState5.isIn(AllTags.Blocks.BRANCHES) || blockState5.isIn(BlockTags.LOGS))
                .withIfExists(WEST, blockState6.isIn(AllTags.Blocks.BRANCHES) || blockState6.isIn(BlockTags.LOGS))

                //added WATERLOGGED to state behaviors like in FenceBlock
                .withIfExists(WATERLOGGED, fluidState.getFluid() == Fluids.WATER);
    }

    @Override
    protected BlockState getStateForNeighborUpdate(
            BlockState state,
            WorldView world,
            ScheduledTickView tickView,
            BlockPos pos,
            Direction direction,
            BlockPos neighborPos,
            BlockState neighborState,
            Random random
    ) {
        if (!state.canPlaceAt(world, pos)) {
            tickView.scheduleBlockTick(pos, this, 1);
            return super.getStateForNeighborUpdate(state, world, tickView, pos, direction, neighborPos, neighborState, random);
        } else {
            if (state.get(WATERLOGGED)) {
                tickView.scheduleFluidTick(pos, Fluids.WATER, Fluids.WATER.getTickRate(world));
            }

            boolean bl = neighborState.isOf(this) || neighborState.isIn(AllTags.Blocks.BRANCHES) || direction == Direction.DOWN && neighborState.isIn(BlockTags.LOGS);
            return state.with(FACING_PROPERTIES.get(direction), bl);
        }
    }

    //Checks if block can exist and if not then breaks itself.
    @Override
    protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (!state.canPlaceAt(world, pos)) {
            world.breakBlock(pos, true);
        }
    }

    //Checks if block can exist

    //Need to modify statements for all sides not just DOWN and make sure behavior is intended
    @Override
    protected boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        BlockState blockState = world.getBlockState(pos.down());
        boolean bl = !world.getBlockState(pos.up()).isAir() && !blockState.isAir();

        for (Direction direction : Direction.Type.HORIZONTAL) {
            BlockPos blockPos = pos.offset(direction);
            BlockState blockState2 = world.getBlockState(blockPos);
            if (blockState2.isOf(this)) {
                if (bl) {
                    return false;
                }

                BlockState blockState3 = world.getBlockState(blockPos.down());
                if (blockState3.isOf(this) || blockState3.isIn(BlockTags.LOGS)) {
                    return true;
                }
            }
        }

        return blockState.isOf(this) || blockState.isIn(BlockTags.LOGS);
    }


    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(NORTH, EAST, SOUTH, WEST, UP, DOWN, WATERLOGGED);
    }
}