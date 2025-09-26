package com.souldi.better_brewing.client;

import com.souldi.better_brewing.screen.BrewingStationScreen;
import com.souldi.better_brewing.screen.ScreenHandlers;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.gui.screen.ingame.HandledScreens;

public class BetterBrewingClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // Register the screen using the new API
        HandledScreens.register(ScreenHandlers.BREWING_STATION_SCREEN_HANDLER, BrewingStationScreen::new);
    }
}
