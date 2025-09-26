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

        // ========== EASY POSITIONING SYSTEM ==========
        // ADJUST THESE VALUES TO MOVE ALL SLOTS:
        int BREWING_SLOTS_X_OFFSET = 40;  // Move brewing slots left/right
        int BREWING_SLOTS_Y_OFFSET = 40;  // Move brewing slots up/down
        
        int PLAYER_INVENTORY_X_OFFSET = 48; // Move player inventory left/right
        int PLAYER_INVENTORY_Y_OFFSET = 129; // Move player inventory up/down
        // =============================================
        
        // Add brewing station inventory slots - all positions relative to offsets
        // Single ingredient slot (top center)
        this.addSlot(new Slot(inventory, BrewingStationBlockEntity.INPUT_SLOT_1, 
            BREWING_SLOTS_X_OFFSET + 39, BREWING_SLOTS_Y_OFFSET + 17));
        
        // Fuel slot (left side)
        this.addSlot(new Slot(inventory, BrewingStationBlockEntity.FUEL_SLOT, 
            BREWING_SLOTS_X_OFFSET - 23, BREWING_SLOTS_Y_OFFSET + 17));
        
        // Three bottle slots (middle row) - standard brewing stand layout
        this.addSlot(new Slot(inventory, BrewingStationBlockEntity.BOTTLE_SLOT_1, 
            BREWING_SLOTS_X_OFFSET + 16, BREWING_SLOTS_Y_OFFSET + 47));
        this.addSlot(new Slot(inventory, BrewingStationBlockEntity.BOTTLE_SLOT_2, 
            BREWING_SLOTS_X_OFFSET + 39, BREWING_SLOTS_Y_OFFSET + 53));
        this.addSlot(new Slot(inventory, BrewingStationBlockEntity.BOTTLE_SLOT_3, 
            BREWING_SLOTS_X_OFFSET + 62, BREWING_SLOTS_Y_OFFSET + 47));
        
        // Output slots (bottom row) - matching bottle positions
        this.addSlot(new Slot(inventory, BrewingStationBlockEntity.OUTPUT_SLOT_1, 
            BREWING_SLOTS_X_OFFSET + 16, BREWING_SLOTS_Y_OFFSET + 71) {
            @Override
            public boolean canInsert(ItemStack stack) {
                return false; // Output only
            }
        });
        this.addSlot(new Slot(inventory, BrewingStationBlockEntity.OUTPUT_SLOT_2, 
            BREWING_SLOTS_X_OFFSET + 62, BREWING_SLOTS_Y_OFFSET + 71) {
            @Override
            public boolean canInsert(ItemStack stack) {
                return false; // Output only
            }
        });
        
        // Unused slots (hidden/disabled)
        this.addSlot(new Slot(inventory, BrewingStationBlockEntity.INPUT_SLOT_2, -1000, -1000));
        this.addSlot(new Slot(inventory, BrewingStationBlockEntity.INPUT_SLOT_3, -1000, -1000));

        // Add player inventory slots - all positions relative to X and Y offsets
        // Main inventory (3 rows of 9)
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 
                    PLAYER_INVENTORY_X_OFFSET + j * 18, PLAYER_INVENTORY_Y_OFFSET + i * 18));
            }
        }

        // Hotbar (1 row of 9)
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 
                PLAYER_INVENTORY_X_OFFSET + i * 18, PLAYER_INVENTORY_Y_OFFSET + 58));
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
