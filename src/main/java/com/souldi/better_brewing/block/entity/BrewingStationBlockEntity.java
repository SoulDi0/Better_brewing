package com.souldi.better_brewing.block.entity;

import com.souldi.better_brewing.screen.BrewingStationScreenHandler;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

public class BrewingStationBlockEntity extends BlockEntity implements ExtendedScreenHandlerFactory, SidedInventory {
    // Define inventory slots
    public static final int INVENTORY_SIZE = 9; // 3x3 brewing grid
    public static final int INPUT_SLOT_1 = 0;
    public static final int INPUT_SLOT_2 = 1;
    public static final int INPUT_SLOT_3 = 2;
    public static final int FUEL_SLOT = 3;
    public static final int BOTTLE_SLOT_1 = 4;
    public static final int BOTTLE_SLOT_2 = 5;
    public static final int BOTTLE_SLOT_3 = 6;
    public static final int OUTPUT_SLOT_1 = 7;
    public static final int OUTPUT_SLOT_2 = 8;
    
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(INVENTORY_SIZE, ItemStack.EMPTY);
    
    // Brewing progress
    private int brewTime = 0;
    private int brewTimeTotal = 400; // 20 seconds at 20 ticks per second
    
    // Property delegate for syncing data to client
    private final PropertyDelegate propertyDelegate = new PropertyDelegate() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> BrewingStationBlockEntity.this.brewTime;
                case 1 -> BrewingStationBlockEntity.this.brewTimeTotal;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0 -> BrewingStationBlockEntity.this.brewTime = value;
                case 1 -> BrewingStationBlockEntity.this.brewTimeTotal = value;
            }
        }

        @Override
        public int size() {
            return 2;
        }
    };
    
    public BrewingStationBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.BREWING_STATION_BLOCK_ENTITY, pos, state);
    }
    
    @Override
    public Text getDisplayName() {
        return Text.translatable("block.better_brewing.brewing_station");
    }
    
    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new BrewingStationScreenHandler(syncId, playerInventory, this, this.propertyDelegate);
    }
    
    @Override
    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
        buf.writeBlockPos(this.pos);
    }
    
    // Inventory implementation
    @Override
    public int size() {
        return INVENTORY_SIZE;
    }
    
    @Override
    public boolean isEmpty() {
        for (ItemStack itemStack : this.inventory) {
            if (!itemStack.isEmpty()) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public ItemStack getStack(int slot) {
        return this.inventory.get(slot);
    }
    
    @Override
    public ItemStack removeStack(int slot, int amount) {
        ItemStack result = Inventories.splitStack(this.inventory, slot, amount);
        if (!result.isEmpty()) {
            markDirty();
        }
        return result;
    }
    
    @Override
    public ItemStack removeStack(int slot) {
        ItemStack result = Inventories.removeStack(this.inventory, slot);
        markDirty();
        return result;
    }
    
    @Override
    public void setStack(int slot, ItemStack stack) {
        this.inventory.set(slot, stack);
        if (stack.getCount() > getMaxCountPerStack()) {
            stack.setCount(getMaxCountPerStack());
        }
        markDirty();
    }
    
    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return pos.isWithinDistance(player.getBlockPos(), 4.5);
    }
    
    @Override
    public void clear() {
        this.inventory.clear();
        markDirty();
    }
    
    // SidedInventory implementation for automation support
    @Override
    public int[] getAvailableSlots(Direction side) {
        if (side == Direction.UP) {
            // Top: input ingredients
            return new int[]{INPUT_SLOT_1, INPUT_SLOT_2, INPUT_SLOT_3};
        } else if (side == Direction.DOWN) {
            // Bottom: output potions
            return new int[]{OUTPUT_SLOT_1, OUTPUT_SLOT_2};
        } else {
            // Sides: fuel and bottles
            return new int[]{FUEL_SLOT, BOTTLE_SLOT_1, BOTTLE_SLOT_2, BOTTLE_SLOT_3};
        }
    }
    
    @Override
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
        // Define what items can be inserted into which slots
        if (slot >= OUTPUT_SLOT_1) {
            return false; // Can't insert into output slots
        }
        
        if (slot == FUEL_SLOT) {
            // Check if item is valid fuel (blaze powder, etc.)
            return isValidFuel(stack);
        } else if (slot >= BOTTLE_SLOT_1 && slot <= BOTTLE_SLOT_3) {
            // Check if item is a bottle
            return isValidBottle(stack);
        } else {
            // Input slots - check if item is valid brewing ingredient
            return isValidIngredient(stack);
        }
    }
    
    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        // Only allow extraction from output slots
        return slot >= OUTPUT_SLOT_1;
    }
    
    // Helper methods for item validation
    private boolean isValidFuel(ItemStack stack) {
        // Add your fuel validation logic here
        return stack.getItem().toString().contains("blaze_powder") || 
               stack.getItem().toString().contains("coal");
    }
    
    private boolean isValidBottle(ItemStack stack) {
        // Add your bottle validation logic here
        return stack.getItem().toString().contains("bottle") ||
               stack.getItem().toString().contains("potion");
    }
    
    private boolean isValidIngredient(ItemStack stack) {
        // Add your ingredient validation logic here
        // For now, allow most items except air
        return !stack.isEmpty();
    }
    
    // NBT serialization
    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        Inventories.readNbt(nbt, this.inventory);
        this.brewTime = nbt.getInt("BrewTime");
        this.brewTimeTotal = nbt.getInt("BrewTimeTotal");
    }
    
    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        Inventories.writeNbt(nbt, this.inventory);
        nbt.putInt("BrewTime", this.brewTime);
        nbt.putInt("BrewTimeTotal", this.brewTimeTotal);
    }
    
    // Brewing logic
    public void tick() {
        if (world == null || world.isClient) return;
        
        boolean wasLit = isLit();
        boolean dirty = false;
        
        if (isLit()) {
            this.brewTime--;
        }
        
        if (canBrew()) {
            if (!isLit() && canConsumeFuel()) {
                this.brewTime = this.brewTimeTotal;
                consumeFuel();
                dirty = true;
            }
            
            if (isLit() && this.brewTime == 0) {
                brew();
                this.brewTime = this.brewTimeTotal;
                dirty = true;
            }
        } else {
            this.brewTime = 0;
        }
        
        if (wasLit != isLit()) {
            dirty = true;
            // Update block state if needed (for visual changes)
        }
        
        if (dirty) {
            markDirty();
        }
    }
    
    private boolean isLit() {
        return this.brewTime > 0;
    }
    
    private boolean canBrew() {
        // Check if we have ingredients, bottles, and can produce output
        ItemStack fuel = this.inventory.get(FUEL_SLOT);
        if (fuel.isEmpty()) return false;
        
        // Check if we have at least one bottle
        boolean hasBottle = false;
        for (int i = BOTTLE_SLOT_1; i <= BOTTLE_SLOT_3; i++) {
            if (!this.inventory.get(i).isEmpty()) {
                hasBottle = true;
                break;
            }
        }
        
        if (!hasBottle) return false;
        
        // Check if we have ingredients
        boolean hasIngredient = false;
        for (int i = INPUT_SLOT_1; i <= INPUT_SLOT_3; i++) {
            if (!this.inventory.get(i).isEmpty()) {
                hasIngredient = true;
                break;
            }
        }
        
        return hasIngredient;
    }
    
    private boolean canConsumeFuel() {
        ItemStack fuel = this.inventory.get(FUEL_SLOT);
        return !fuel.isEmpty() && isValidFuel(fuel);
    }
    
    private void consumeFuel() {
        ItemStack fuel = this.inventory.get(FUEL_SLOT);
        if (!fuel.isEmpty()) {
            fuel.decrement(1);
            markDirty();
        }
    }
    
    private void brew() {
        // Implement your brewing logic here
        // For now, just move items from bottle slots to output slots as example
        for (int i = BOTTLE_SLOT_1; i <= BOTTLE_SLOT_3; i++) {
            ItemStack bottle = this.inventory.get(i);
            if (!bottle.isEmpty()) {
                // Find empty output slot
                for (int j = OUTPUT_SLOT_1; j < OUTPUT_SLOT_1 + 2; j++) {
                    if (this.inventory.get(j).isEmpty()) {
                        this.inventory.set(j, bottle.copy());
                        bottle.setCount(0);
                        break;
                    }
                }
            }
        }
        
        // Consume one ingredient
        for (int i = INPUT_SLOT_1; i <= INPUT_SLOT_3; i++) {
            ItemStack ingredient = this.inventory.get(i);
            if (!ingredient.isEmpty()) {
                ingredient.decrement(1);
                break;
            }
        }
        
        markDirty();
    }
    
    // Getters for screen synchronization
    public int getBrewTime() {
        return brewTime;
    }
    
    public int getBrewTimeTotal() {
        return brewTimeTotal;
    }
    
    public void setBrewTime(int brewTime) {
        this.brewTime = brewTime;
    }
}
