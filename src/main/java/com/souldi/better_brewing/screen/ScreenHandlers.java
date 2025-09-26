package com.souldi.better_brewing.screen;

import com.souldi.better_brewing.Better_Brewing;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;

public class ScreenHandlers {
    public static final ScreenHandlerType<BrewingStationScreenHandler> BREWING_STATION_SCREEN_HANDLER =
            Registry.register(Registries.SCREEN_HANDLER, new Identifier(Better_Brewing.MOD_ID, "brewing_station"),
                    new ExtendedScreenHandlerType<>(BrewingStationScreenHandler::new));

    public static void registerScreenHandlers() {
        Better_Brewing.LOGGER.info("Registering Screen Handlers for " + Better_Brewing.MOD_ID);
    }
}
