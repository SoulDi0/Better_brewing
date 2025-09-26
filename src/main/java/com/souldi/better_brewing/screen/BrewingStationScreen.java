package com.souldi.better_brewing.screen;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class BrewingStationScreen extends HandledScreen<BrewingStationScreenHandler> {
    private static final Identifier BACKGROUND_TEXTURE = new Identifier("better_brewing", "textures/gui/brewing_station_main_menu.png");
    
    public BrewingStationScreen(BrewingStationScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
        // Устанавливаем размеры точно как у верстака
        this.backgroundHeight = 166;
        this.backgroundWidth = 176;
        this.playerInventoryTitleY = this.backgroundHeight - 94;
    }

    @Override
    protected void init() {
        super.init();
        // Центрируем GUI точно как у верстака
        this.x = (this.width - this.backgroundWidth) / 2;
        this.y = (this.height - this.backgroundHeight) / 2;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // Рендерим фон
        this.renderBackground(context);
        
        // Рендерим стандартный HandledScreen (слоты, предметы, курсор) СНАЧАЛА
        super.render(context, mouseX, mouseY, delta);
        
        // Рендерим наш кастомный фон поверх, но только в верхней части
        int guiX = (this.width - 256) / 2;
        int guiY = (this.height - 256) / 2;
        // Рендерим только верхнюю часть фона, чтобы не перекрывать слоты
        context.drawTexture(BACKGROUND_TEXTURE, guiX, guiY, 0, 0, 256, 128, 256, 256);
        
        // Рендерим тултипы поверх всего
        this.drawMouseoverTooltip(context, mouseX, mouseY);
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        // Рендерим стандартный фон верстака для слотов
        int i = (this.width - this.backgroundWidth) / 2;
        int j = (this.height - this.backgroundHeight) / 2;
        context.drawTexture(new Identifier("textures/gui/container/crafting_table.png"), i, j, 0, 0, this.backgroundWidth, this.backgroundHeight);
    }

    @Override
    protected void drawForeground(DrawContext context, int mouseX, int mouseY) {
        // НЕ рендерим стандартные заголовки
        // Этот метод оставляем пустым, чтобы не рисовались стандартные тексты
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
