package com.souldi.better_brewing.item;

import com.souldi.better_brewing.Better_Brewing;
import com.souldi.better_brewing.block.ModBlocks;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ModItemGroups {
    public static ItemGroup BETTER_BREWING_GROUP;

    public static void registerItemGroups() {
        BETTER_BREWING_GROUP = Registry.register(Registries.ITEM_GROUP,
                new Identifier(Better_Brewing.MOD_ID, "better_brewing"),
                FabricItemGroup.builder()
                        .displayName(Text.translatable("itemgroup.better_brewing"))
                        .icon(() -> new ItemStack(ModBlocks.BREWING_STATION))
                        .entries((displayContext, entries) -> {
                            entries.add(ModBlocks.BREWING_STATION);
                        })
                        .build());
        
        Better_Brewing.LOGGER.info("Registering Item Groups for " + Better_Brewing.MOD_ID);
    }
}
