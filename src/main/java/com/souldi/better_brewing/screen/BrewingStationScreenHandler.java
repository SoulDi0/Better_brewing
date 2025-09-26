package com.souldi.better_brewing.screen;

import com.souldi.better_brewing.block.entity.BrewingStationBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ArrayPropertyDelegate;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;

public class BrewingStationScreenHandler extends ScreenHandler {
    private final Inventory inventory;
    private final PropertyDelegate propertyDelegate;

    // Client constructor
    public BrewingStationScreenHandler(int syncId, PlayerInventory playerInventory, PacketByteBuf buf) {
        this(syncId, playerInventory, (BrewingStationBlockEntity) playerInventory.player.getWorld().getBlockEntity(buf.readBlockPos()), 
             new ArrayPropertyDelegate(2));
    }

    // Server constructor
    public BrewingStationScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory) {
        this(syncId, playerInventory, inventory, new ArrayPropertyDelegate(2));
    }

    public BrewingStationScreenHandler(int syncId, PlayerInventory playerInventory, Inventory inventory, PropertyDelegate propertyDelegate) {
        super(ScreenHandlers.BREWING_STATION_SCREEN_HANDLER, syncId);
        checkSize(inventory, BrewingStationBlockEntity.INVENTORY_SIZE);
        this.inventory = inventory;
        this.propertyDelegate = propertyDelegate;
        
        inventory.onOpen(playerInventory.player);
        this.addProperties(propertyDelegate);

        // Add brewing station inventory slots
        // Input ingredients (top row)
        this.addSlot(new Slot(inventory, BrewingStationBlockEntity.INPUT_SLOT_1, 79, 17));
        this.addSlot(new Slot(inventory, BrewingStationBlockEntity.INPUT_SLOT_2, 102, 17));
        this.addSlot(new Slot(inventory, BrewingStationBlockEntity.INPUT_SLOT_3, 125, 17));
        
        // Fuel slot (left side)
        this.addSlot(new Slot(inventory, BrewingStationBlockEntity.FUEL_SLOT, 17, 17));
        
        // Bottle slots (middle row)
        this.addSlot(new Slot(inventory, BrewingStationBlockEntity.BOTTLE_SLOT_1, 79, 53));
        this.addSlot(new Slot(inventory, BrewingStationBlockEntity.BOTTLE_SLOT_2, 102, 53));
        this.addSlot(new Slot(inventory, BrewingStationBlockEntity.BOTTLE_SLOT_3, 125, 53));
        
        // Output slots (bottom)
        this.addSlot(new Slot(inventory, BrewingStationBlockEntity.OUTPUT_SLOT_1, 79, 76) {
            @Override
            public boolean canInsert(ItemStack stack) {
                return false; // Output only
            }
        });
        this.addSlot(new Slot(inventory, BrewingStationBlockEntity.OUTPUT_SLOT_2, 102, 76) {
            @Override
            public boolean canInsert(ItemStack stack) {
                return false; // Output only
            }
        });

        // Add player inventory slots
        // Main inventory (3 rows of 9)
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 104 + i * 18));
            }
        }

        // Hotbar (1 row of 9)
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 162));
        }
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slotIndex) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(slotIndex);
        
        if (slot != null && slot.hasStack()) {
            ItemStack slotStack = slot.getStack();
            itemStack = slotStack.copy();
            
            // Brewing station has 9 slots (0-8), player inventory starts at slot 9
            if (slotIndex < BrewingStationBlockEntity.INVENTORY_SIZE) {
                // From brewing station to player inventory
                if (!this.insertItem(slotStack, BrewingStationBlockEntity.INVENTORY_SIZE, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (slotIndex < this.slots.size()) {
                // From player inventory to brewing station
                if (!this.insertItem(slotStack, 0, BrewingStationBlockEntity.INVENTORY_SIZE, false)) {
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
        return this.inventory.canPlayerUse(player);
    }
    
    @Override
    public void onClosed(PlayerEntity player) {
        super.onClosed(player);
        this.inventory.onClose(player);
    }
    
    // Getters for brewing progress (for GUI progress bars)
    public int getBrewProgress() {
        int brewTime = this.propertyDelegate.get(0);
        int brewTimeTotal = this.propertyDelegate.get(1);
        return brewTimeTotal != 0 && brewTime != 0 ? brewTime * 24 / brewTimeTotal : 0;
    }
    
    public boolean isLit() {
        return this.propertyDelegate.get(0) > 0;
    }
}
