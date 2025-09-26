package com.souldi.better_brewing.screen;

import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;

public class ScreenHandlers {
    public static final ScreenHandlerType<BrewingStationScreenHandler> BREWING_STATION_SCREEN_HANDLER =
            Registry.register(Registries.SCREEN_HANDLER, new Identifier("better_brewing", "brewing_station"),
                    new ScreenHandlerType<>(BrewingStationScreenHandler::new, null));

    public static void registerScreenHandlers() {
        // Регистрация происходит в статических блоках выше
    }
}
