package dev.amble.ait.core.blockentities;

import dev.amble.ait.api.tardis.link.v2.block.InteriorLinkableBlockEntity;
import dev.amble.ait.core.drinks.DrinkRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;

import dev.amble.ait.core.AITBlockEntityTypes;

public class FoodMachineBlockEntity extends InteriorLinkableBlockEntity {
    private FoodMachineMode currentMode = FoodMachineMode.FOOD_CUBES;
    private int currentIndex = 0;
    private ItemStack selectedItem;

    public FoodMachineBlockEntity(BlockPos pos, BlockState state) {
        super(AITBlockEntityTypes.FOOD_MACHINE_BLOCK_ENTITY_TYPE, pos, state);
    }

    public enum FoodMachineMode {
        FOOD_CUBES,
        DRINKS,
        OVERCHARGED_FOOD_CUBES;
    }

    public void setMode(FoodMachineMode mode) {
        this.currentMode = mode;
    }

    public FoodMachineMode getMode() {
        return currentMode;
    }

    public int getCurrentIndex(){
        return this.currentIndex;
    }

    public void setCurrentIndex(int value) {
        int size = DrinkRegistry.getInstance().size();
        if (size == 0) {
            this.currentIndex = 0;
        }
    }

    public void eatFuel(FoodMachineMode mode){
        switch (mode){
            case FOOD_CUBES -> {
                this.tardis().get().fuel().removeFuel(139);
                return;
            }
            case DRINKS -> {
                this.tardis().get().fuel().removeFuel(193);
                return;
            }
            case OVERCHARGED_FOOD_CUBES -> {
                this.tardis().get().fuel().removeFuel(437);
                return;
            }
        }
    }

    public boolean isPoweredOn(){
        return this.tardis().get().fuel().hasPower();
    }
    public ItemStack getSelectedItem() {
        return this.selectedItem;
    }
    public void setSelectedItem(ItemStack item){
        this.selectedItem = item;
    }

}
