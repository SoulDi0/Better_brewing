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
        // Center the GUI
        this.x = (this.width - this.backgroundWidth) / 2;
        this.y = (this.height - this.backgroundHeight) / 2;
        this.titleX = (this.backgroundWidth - this.textRenderer.getWidth(this.title)) / 2;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // Render background first
        this.renderBackground(context);
        
        // Render the standard HandledScreen (slots, items, cursor)
        super.render(context, mouseX, mouseY, delta);
        
        // Render brewing progress if available
        if (handler.isLit()) {
            int progress = handler.getBrewProgress();
            // Draw flame progress indicator (you can customize this)
            int flameX = this.x + 18;
            int flameY = this.y + 37;
            context.drawTexture(BACKGROUND_TEXTURE, flameX, flameY, 176, 0, 14, 14 - progress);
        }
        
        // Render tooltips on top of everything
        this.drawMouseoverTooltip(context, mouseX, mouseY);
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        // Render brewing stand GUI background
        int i = (this.width - this.backgroundWidth) / 2;
        int j = (this.height - this.backgroundHeight) / 2;
        context.drawTexture(new Identifier("textures/gui/container/brewing_stand.png"), i, j, 0, 0, this.backgroundWidth, this.backgroundHeight);
    }

    @Override
    protected void drawForeground(DrawContext context, int mouseX, int mouseY) {
        // Draw the title
        context.drawText(this.textRenderer, this.title, this.titleX, this.titleY, 4210752, false);
        // Draw player inventory title
        context.drawText(this.textRenderer, this.playerInventoryTitle, this.playerInventoryTitleX, this.playerInventoryTitleY, 4210752, false);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
