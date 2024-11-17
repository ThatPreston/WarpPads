package io.github.thatpreston.warppads.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

public class WarpButton extends Button {
    public WarpButton(int x, int y, int width, int height, Component component, OnPress onPress) {
        super(x, y, width, height, component, onPress, DEFAULT_NARRATION);
    }
    @Override
    public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        Minecraft minecraft = Minecraft.getInstance();
        Component component = getMessage();
        Font font = minecraft.font;
        int x = getX();
        int y = getY();
        int v = this.isHoveredOrFocused() ? 189 : 166;
        graphics.blit(WarpSelectionScreen.TEXTURE, x, y, 0, v, 144, 23);
        int x2 = x + (144 - font.width(component)) / 2;
        int y2 = y + (23 - font.lineHeight) / 2;
        graphics.drawString(font, component, x2, y2, -1, true);
    }
}