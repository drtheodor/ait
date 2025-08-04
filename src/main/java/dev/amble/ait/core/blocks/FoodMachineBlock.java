package dev.amble.ait.core.blocks;

import dev.amble.ait.AITMod;
import dev.amble.ait.core.AITItems;
import dev.amble.ait.core.blockentities.FoodMachineBlockEntity;
import dev.amble.ait.core.drinks.Drink;
import dev.amble.ait.core.drinks.DrinkRegistry;
import dev.amble.ait.core.drinks.DrinkUtil;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.RotationPropertyHelper;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import net.minecraft.util.math.BlockPos;
import dev.amble.ait.core.AITBlockEntityTypes;

public class FoodMachineBlock extends BlockWithEntity implements BlockEntityProvider {
    public static final int MAX_ROTATION_INDEX = RotationPropertyHelper.getMax();
    private static final int MAX_ROTATIONS = MAX_ROTATION_INDEX + 1;
    public static final IntProperty ROTATION = Properties.ROTATION;

    public enum FoodMachineMode {
        FOOD_CUBES,
        DRINKS,
        OVERCHARGED_FOOD_CUBES
    }

    public FoodMachineBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(ROTATION, 0));
    }

    private static final String defaultDrink = "water";
    private static String selectedDrink = defaultDrink;


    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        BlockEntity be = world.getBlockEntity(pos);
        if (!(be instanceof FoodMachineBlockEntity)) return ActionResult.PASS;
        FoodMachineBlockEntity machine = (FoodMachineBlockEntity) be;

        //mode logic
        ItemStack stack = player.getStackInHand(hand);

        if (player.isSneaking() && stack.isEmpty()) {
            FoodMachineMode currentMode = machine.getMode();
            char currentModeMessage = 0;
            switch (currentMode) {
                case FOOD_CUBES:
                    machine.setMode(FoodMachineMode.DRINKS);
                    currentModeMessage = 'D';
                    break;
                case DRINKS:
                    machine.setMode(FoodMachineMode.OVERCHARGED_FOOD_CUBES);
                    currentModeMessage = 'O';
                    break;
                case OVERCHARGED_FOOD_CUBES:
                    machine.setMode(FoodMachineMode.FOOD_CUBES);
                    currentModeMessage = 'F';
                    break;
            }
            switch (currentModeMessage){
                case 'F': player.sendMessage(Text.of("Food Cubes").copy().formatted(Formatting.GREEN), true); break;
                case 'D': player.sendMessage(Text.of("Drinks").copy().formatted(Formatting.AQUA), true); break;
                case 'O': player.sendMessage(Text.of("Overcharged Food Cubes").copy().formatted(Formatting.LIGHT_PURPLE), true); break;
            }

        }

        if (!player.isSneaking()) {
            if (machine.getMode() == FoodMachineMode.FOOD_CUBES) {
                player.getInventory().insertStack(Items.CARROT.getDefaultStack());
            } else if (machine.getMode() == FoodMachineMode.DRINKS) {
                player.getInventory().insertStack(getDrink(selectedDrink));
            } else if (machine.getMode() == FoodMachineMode.OVERCHARGED_FOOD_CUBES) {
                player.getInventory().insertStack(Items.ENCHANTED_GOLDEN_APPLE.getDefaultStack());
            }
        }


        return ActionResult.SUCCESS;
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return AITBlockEntityTypes.FOOD_MACHINE_BLOCK_ENTITY_TYPE.instantiate(pos, state);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.INVISIBLE;
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(ROTATION, RotationPropertyHelper.fromYaw(ctx.getPlayerYaw()));
    }

    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(ROTATION, rotation.rotate(state.get(ROTATION), MAX_ROTATIONS));
    }

    @Override
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.with(ROTATION, mirror.mirror(state.get(ROTATION), MAX_ROTATIONS));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(ROTATION);
    }


}
