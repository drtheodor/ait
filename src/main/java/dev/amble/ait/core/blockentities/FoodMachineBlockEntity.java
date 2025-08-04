package dev.amble.ait.core.blockentities;

import dev.amble.ait.core.blocks.FoodMachineBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;

import dev.amble.ait.core.AITBlockEntityTypes;

public class FoodMachineBlockEntity extends BlockEntity {

    public FoodMachineBlockEntity(BlockPos pos, BlockState state) {
        super(AITBlockEntityTypes.FOOD_MACHINE_BLOCK_ENTITY_TYPE, pos, state);
    }

    private FoodMachineBlock.FoodMachineMode mode = FoodMachineBlock.FoodMachineMode.FOOD_CUBES;

    public FoodMachineBlock.FoodMachineMode getMode() {
        return mode;
    }

    public void setMode(FoodMachineBlock.FoodMachineMode mode) {
        this.mode = mode;
        markDirty();
    }
}
