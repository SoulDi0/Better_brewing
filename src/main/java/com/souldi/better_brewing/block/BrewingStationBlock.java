package com.souldi.better_brewing.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class BrewingStationBlock extends HorizontalFacingBlock {
    private static final VoxelShape SHAPE = VoxelShapes.cuboid(0.0, 0.0, 0.0, 1.0, 0.875, 1.0);

    public BrewingStationBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(Properties.HORIZONTAL_FACING, net.minecraft.util.math.Direction.NORTH));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(Properties.HORIZONTAL_FACING);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(Properties.HORIZONTAL_FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.isClient) {
            // Открываем экран на клиенте
            openBrewingStationScreen();
        }
        return ActionResult.SUCCESS;
    }

    private void openBrewingStationScreen() {
        // Этот метод будет вызван на клиенте
        // Используем рефлексию для вызова клиентского кода
        try {
            Class<?> clientClass = Class.forName("com.souldi.better_brewing.client.BetterBrewingClient");
            clientClass.getMethod("openBrewingStationScreen").invoke(null);
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | java.lang.reflect.InvocationTargetException e) {
            // Игнорируем ошибки, если клиентский код недоступен
        }
    }
}
