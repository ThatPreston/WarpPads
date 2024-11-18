package io.github.thatpreston.warppads.client.gui;

import io.github.thatpreston.warppads.WarpPads;
import io.github.thatpreston.warppads.menu.WarpConfigMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class WarpConfigScreen extends AbstractContainerScreen<WarpConfigMenu> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(WarpPads.MOD_ID, "textures/gui/warp_config.png");
    private EditBox name;
    private int priority;
    public WarpConfigScreen(WarpConfigMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        imageHeight = 142;
        inventoryLabelY = 48;
        priority = menu.getInfo().getPriority();
    }
    @Override
    protected void init() {
        super.init();
        titleLabelX = (imageWidth - font.width(title)) / 2;
        String currentName = name != null ? name.getValue() : menu.getInfo().getName();
        EditBox name = new EditBox(font, leftPos + 16, topPos + 23, 100, 12, Component.translatable("container.warppads.warp_config.hint"));
        name.setCanLoseFocus(true);
        name.setTextColor(-1);
        name.setTextColorUneditable(-1);
        name.setBordered(false);
        name.setMaxLength(40);
        name.setValue(currentName);
        if(currentName.isEmpty()) {
            setInitialFocus(name);
        }
        addWidget(name);
        this.name = name;
        addWidget(Button.builder(Component.empty(), button -> priority = Math.min(priority + 1, 99)).pos(leftPos + 151, topPos + 10).size(10, 10).build());
        addWidget(Button.builder(Component.empty(), button -> priority = Math.max(priority - 1, 0)).pos(leftPos + 151, topPos + 34).size(10, 10).build());
    }
    @Override
    public void onClose() {
        menu.saveChanges(name.getValue(), priority);
        super.onClose();
    }
    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(font, title, titleLabelX, titleLabelY, 4210752, false);
        graphics.drawString(font, playerInventoryTitle, inventoryLabelX, inventoryLabelY, 4210752, false);
        String priorityString = String.valueOf(priority);
        graphics.drawString(font, priorityString, 156 - font.width(priorityString) / 2, 24, 4210752, false);
    }
    @Override
    protected void renderBg(GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
        graphics.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);
        if(!menu.hasDye()) {
            graphics.blit(TEXTURE, leftPos + 126, topPos + 19, 176, 0, 20, 20);
        }
    }
    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        super.render(graphics, mouseX, mouseY, partialTicks);
        name.render(graphics, mouseX, mouseY, partialTicks);
        renderTooltip(graphics, mouseX, mouseY);
    }
    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if(keyCode == 256) {
            onClose();
        }
        return name.keyPressed(keyCode, scanCode, modifiers) || name.canConsumeInput() || super.keyPressed(keyCode, scanCode, modifiers);
    }
}