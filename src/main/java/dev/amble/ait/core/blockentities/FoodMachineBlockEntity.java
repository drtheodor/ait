package dev.amble.ait.core.blockentities;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;

import dev.amble.ait.core.AITBlockEntityTypes;

public class FoodMachineBlockEntity extends BlockEntity {

    public FoodMachineBlockEntity(BlockPos pos, BlockState state) {
        super(AITBlockEntityTypes.FOOD_MACHINE_BLOCK_ENTITY_TYPE, pos, state);
    }

    public enum FoodMachineMode {
        FOOD_CUBES,
        DRINKS,
        OVERCHARGED_FOOD_CUBES
    }

    private FoodMachineMode currentMode = FoodMachineMode.FOOD_CUBES;

    public void setMode(FoodMachineMode mode) {
        this.currentMode = mode;
    }

    public FoodMachineMode getMode() {
        return currentMode;
    }

}
