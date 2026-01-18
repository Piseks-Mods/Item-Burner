package dpdns.org.pisekpiskovec.ItemBurner.screen.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import dpdns.org.pisekpiskovec.ItemBurner.ItemBurner;
import dpdns.org.pisekpiskovec.ItemBurner.screen.menu.ChronofluxValveMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.fluids.FluidStack;

public class ChronofluxValveScreen extends AbstractContainerScreen<ChronofluxValveMenu> {
  private static final ResourceLocation TEXTURE = new ResourceLocation(ItemBurner.MOD_ID, "textures/gui/valve.png");

  public ChronofluxValveScreen(ChronofluxValveMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
    super(pMenu, pPlayerInventory, pTitle);
  }

  @Override
  protected void init() {
    super.init();

    int x = (width - imageWidth) / 2;
    int y = (height - imageHeight) / 2;

    // Button dimensions
    int[] buttonWidth = {30, 40, 50};
    int buttonsGap = 2;

    // Calculate total width of all buttons and gaps
    int totalWidth = buttonsGap * (buttonWidth.length - 1);
    for (int i : buttonWidth) {
      totalWidth += i;
    }

    // Calculate starting X position to center all buttons
    int startX = x + (imageWidth - totalWidth) / 2;

    // Button for 1 mB
    this.addRenderableWidget(
        Button.builder(
                Component.literal("1 mB"),
                button -> {
                  if (minecraft != null && minecraft.gameMode != null) {
                    minecraft.gameMode.handleInventoryButtonClick(menu.containerId, 0);
                  }
                })
            .bounds(startX, y + 40, buttonWidth[0], 20)
            .build());

    // Button for 10 mB
    this.addRenderableWidget(
        Button.builder(
                Component.literal("10 mB"),
                button -> {
                  if (minecraft != null && minecraft.gameMode != null) {
                    minecraft.gameMode.handleInventoryButtonClick(menu.containerId, 1);
                  }
                })
            .bounds(startX + buttonWidth[0] + buttonsGap, y + 40, buttonWidth[1], 20)
            .build());

    // Button for 100 mB
    this.addRenderableWidget(
        Button.builder(
                Component.literal("100 mB"),
                button -> {
                  if (minecraft != null && minecraft.gameMode != null) {
                    minecraft.gameMode.handleInventoryButtonClick(menu.containerId, 2);
                  }
                })
            .bounds(startX + buttonWidth[0] + buttonsGap + buttonWidth[1] + buttonsGap, y + 40, buttonWidth[2], 20)
            .build());
  }

  @Override
  protected void renderBg(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
    RenderSystem.setShader(GameRenderer::getPositionTexShader);
    RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    RenderSystem.setShaderTexture(0, TEXTURE);
    int x = (width - imageWidth) / 2;
    int y = (height - imageHeight) / 2;

    pGuiGraphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight);
  }

  @Override
  protected void renderLabels(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY) {
    super.renderLabels(pGuiGraphics, pMouseX, pMouseY);

    // Get fluid amount
    FluidStack fluidStack = menu.getFluidStack();
    int amount = fluidStack.isEmpty() ? 0 : fluidStack.getAmount();
    int capacity = menu.getFluidCapacity();

    // Render fluid amount as label
    Component fluidLabel = Component.literal("Chronoflux: " + amount + " / " + capacity + " mB");
    pGuiGraphics.drawString(this.font, fluidLabel, 8, 20, 0x404040, false);
  }

  @Override
  public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
    renderBackground(pGuiGraphics);
    super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
    renderTooltip(pGuiGraphics, pMouseX, pMouseY);
  }
}
