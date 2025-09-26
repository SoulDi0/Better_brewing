package com.souldi.better_brewing.block.entity;

import com.souldi.better_brewing.Better_Brewing;
import com.souldi.better_brewing.block.ModBlocks;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlockEntities {
    public static final BlockEntityType<BrewingStationBlockEntity> BREWING_STATION_BLOCK_ENTITY =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, new Identifier(Better_Brewing.MOD_ID, "brewing_station"),
                    FabricBlockEntityTypeBuilder.create(BrewingStationBlockEntity::new, ModBlocks.BREWING_STATION).build());

    public static void registerBlockEntities() {
        Better_Brewing.LOGGER.info("Registering Block Entities for " + Better_Brewing.MOD_ID);
    }
}
