package com.souldi.better_brewing.client;

import com.souldi.better_brewing.screen.BrewingStationScreen;
import com.souldi.better_brewing.screen.BrewingStationScreenHandler;
import com.souldi.better_brewing.screen.ScreenHandlers;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

public class BetterBrewingClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // Регистрируем экран
        ScreenRegistry.register(ScreenHandlers.BREWING_STATION_SCREEN_HANDLER, BrewingStationScreen::new);
    }
    
    public static void openBrewingStationScreen() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player != null) {
            // Создаем ScreenHandler напрямую для клиентской части
            BrewingStationScreenHandler handler = new BrewingStationScreenHandler(0, client.player.getInventory());
            client.setScreen(new BrewingStationScreen(handler, client.player.getInventory(), 
                Text.translatable("screen.better_brewing.brewing_station")));
        }
    }
}
