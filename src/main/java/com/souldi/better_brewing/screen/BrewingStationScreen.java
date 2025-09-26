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
        // Use your custom texture dimensions
        this.backgroundHeight = 256;
        this.backgroundWidth = 256;
        
        // ADJUST THESE VALUES TO POSITION TITLES:
        this.titleY = 46;  // Main title Y position
        this.playerInventoryTitleY = 142; // "Inventory" text Y position
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
        
        // TODO: Add custom brewing progress indicators here if your texture includes them
        // You can customize this based on your brewing_station_main_menu.png layout
        if (handler.isLit()) {
            int progress = handler.getBrewProgress();
            // Example: Draw a custom progress bar or animation
            // Uncomment and adjust coordinates based on your texture:
            // int progressX = this.x + [your_progress_x];
            // int progressY = this.y + [your_progress_y];
            // context.drawTexture(BACKGROUND_TEXTURE, progressX, progressY, [u], [v], [width], [height], 256, 256);
        }
        
        // Render tooltips on top of everything
        this.drawMouseoverTooltip(context, mouseX, mouseY);
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        // Render your custom brewing station GUI background
        int x = (this.width - this.backgroundWidth) / 2;
        int y = (this.height - this.backgroundHeight) / 2;
        
        // Draw your custom texture scaled to fit standard brewing stand size
        context.drawTexture(BACKGROUND_TEXTURE, x, y, 0, 0, this.backgroundWidth, this.backgroundHeight, 256, 256);
    }

    @Override
    protected void drawForeground(DrawContext context, int mouseX, int mouseY) {
        // Draw the title
        // context.drawText(this.textRenderer, this.title, this.titleX, this.titleY, 4210752, false);
        // Draw player inventory title
        // context.drawText(this.textRenderer, this.playerInventoryTitle, this.playerInventoryTitleX, this.playerInventoryTitleY, 4210752, false);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
