package io.github.thatpreston.warppads.client.gui;

import io.github.thatpreston.warppads.WarpPads;
import io.github.thatpreston.warppads.menu.WarpSelectionMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;

import java.util.*;

public class WarpSelectionScreen extends AbstractContainerScreen<WarpSelectionMenu> {
    public static final ResourceLocation TEXTURE = new ResourceLocation(WarpPads.MOD_ID, "textures/gui/warp_selection.png");
    private final List<WarpButton> warpButtons;
    private float scrollOffset;
    private int startButton;
    private int extraButtons;
    private boolean scrolling;
    public WarpSelectionScreen(WarpSelectionMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        warpButtons = new ArrayList<>();
    }
    @Override
    protected void init() {
        super.init();
        titleLabelX = (imageWidth - font.width(title)) / 2;
        warpButtons.clear();
        for(WarpSelectionMenu.WarpOption option : menu.getWarpOptions()) {
            WarpButton warpButton = new WarpButton(leftPos + 7, topPos, 144, 23, Component.literal(option.getName()), button -> {
                menu.selectWarpOption(option);
                onClose();
            });
            addWidget(warpButton);
            warpButtons.add(warpButton);
        }
        extraButtons = warpButtons.size() - 6;
    }
    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if(scrolling && extraButtons > 0) {
            int y = topPos + 18;
            scrollOffset = Mth.clamp((float)(mouseY - y - 7.5F) / 123, 0, 1);
            startButton = Math.max((int)(scrollOffset * extraButtons + 0.5F), 0);
            return true;
        } else {
            return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
        }
    }
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if(extraButtons > 0) {
            float f = (float)delta / (float)extraButtons;
            scrollOffset = Mth.clamp(scrollOffset - f, 0, 1);
            startButton = Math.max((int)(scrollOffset * extraButtons + 0.5F), 0);
        }
        return true;
    }
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        scrolling = false;
        if(extraButtons > 0) {
            int x = leftPos + 157;
            int y = topPos + 18;
            if(mouseX >= x && mouseX <= x + 12 && mouseY >= y && mouseY <= y + 138) {
                scrolling = true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }
    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(font, title, titleLabelX, titleLabelY, 4210752, false);
    }
    @Override
    protected void renderBg(GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
        graphics.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);
        int x = leftPos + 157;
        int y = topPos + 18 + (int)(123 * scrollOffset);
        int extraButtons = warpButtons.size() - 6;
        graphics.blit(TEXTURE, x, y, 232 + (extraButtons > 0 ? 0 : 12), 0, 12, 15);
    }
    private void renderButtons(GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
        for(int i = 0; i < warpButtons.size(); i++) {
            int index = i - startButton;
            if(index >= 0 && index < 6) {
                WarpButton button = warpButtons.get(i);
                button.setY(topPos + 18 + 23 * index);
                button.render(graphics, mouseX, mouseY, partialTicks);
            }
        }
    }
    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.render(graphics, mouseX, mouseY, partialTicks);
        renderButtons(graphics, partialTicks, mouseX, mouseY);
        renderTooltip(graphics, mouseX, mouseY);
    }
}