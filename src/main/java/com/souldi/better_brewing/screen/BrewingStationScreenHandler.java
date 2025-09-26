package com.souldi.better_brewing.screen;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;

public class BrewingStationScreenHandler extends ScreenHandler {
    private final Inventory inventory;

    public BrewingStationScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId, playerInventory, new SimpleInventory(0));
    }

    public BrewingStationScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory) {
        super(ScreenHandlerType.GENERIC_3X3, syncId);
        this.inventory = inventory;

        // Добавляем слоты игрока точно как в CraftingScreenHandler
        // Основной инвентарь (3 ряда по 9 слотов)
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        // Хотбар (9 слотов)
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
        }
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slotIndex) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(slotIndex);
        
        if (slot != null && slot.hasStack()) {
            ItemStack slotStack = slot.getStack();
            itemStack = slotStack.copy();
            
            if (slotIndex < 27) {
                // Из основного инвентаря в хотбар
                if (!this.insertItem(slotStack, 27, 36, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (slotIndex < 36) {
                // Из хотбара в основной инвентарь
                if (!this.insertItem(slotStack, 0, 27, false)) {
                    return ItemStack.EMPTY;
                }
            }
            
            if (slotStack.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }
            
            if (slotStack.getCount() == itemStack.getCount()) {
                return ItemStack.EMPTY;
            }
            
            slot.onTakeItem(player, slotStack);
        }
        
        return itemStack;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }
}
