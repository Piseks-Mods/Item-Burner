package dpdns.org.pisekpiskovec.ItemBurner.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import dpdns.org.pisekpiskovec.ItemBurner.ItemBurner;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.fluids.FluidStack;

public class ItemBurnerScreen extends AbstractContainerScreen<ItemBurnerMenu> {
  private static final ResourceLocation TEXTURE = new ResourceLocation(ItemBurner.MOD_ID, "textures/gui/burner.png");

  public ItemBurnerScreen(ItemBurnerMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
    super(pMenu, pPlayerInventory, pTitle);
  }

  @Override
  protected void init() {
    super.init();
  }

  @Override
  protected void renderBg(GuiGraphics guiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
    RenderSystem.setShader(GameRenderer::getPositionTexShader);
    RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    RenderSystem.setShaderTexture(0, TEXTURE);
    int x = (width - imageWidth) / 2;
    int y = (height - imageHeight) / 2;

    guiGraphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight);

    renderProgressArrow(guiGraphics, x, y);
    renderFluidTank(guiGraphics, x, y);
  }

  private void renderProgressArrow(GuiGraphics guiGraphics, int x, int y) {
    if (menu.isCrafting()) {
      guiGraphics.blit(TEXTURE, x + 79, y + 35, 176, 14, menu.getScaledProgress(), 17);
    }
  }

  private void renderFluidTank(GuiGraphics guiGraphics, int x, int y) {
    FluidStack fluidStack = menu.getFluidStack();
    if (!fluidStack.isEmpty()) {
      int fluidAmount = fluidStack.getAmount();
      int capacity = menu.getFluidCapacity();
      int fluidHeight = (fluidAmount * 52) / capacity; // 52 pixels is the height of the tank

      // TODO: Renter fluid texture
      int tankX = x + 152;
      int tankY = y + 11 + (52 - fluidHeight);

      // Draw a blue-ish rectangle representing the fluid
      guiGraphics.fill(tankX, tankY, tankX + 16, tankY + fluidHeight, 0xFFADD3FE);
    }
  }

  @Override
  public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
    renderBackground(guiGraphics);
    super.render(guiGraphics, mouseX, mouseY, delta);
    renderTooltip(guiGraphics, mouseX, mouseY);

    // Render fluid tank tooltip
    int x = (width - imageWidth) / 2;
    int y = (height - imageHeight) / 2;
    if (mouseX >= x + 152 && mouseX <= x + 168 && mouseX >= y + 11 && mouseY <= y + 63) {
      FluidStack fluidStack = menu.getFluidStack();
      if (!fluidStack.isEmpty()) {
        guiGraphics.renderTooltip(
            this.font,
            Component.literal(fluidStack.getAmount() + " / " + menu.getFluidCapacity() + " mB"),
            mouseX,
            mouseY);
      } else {
        guiGraphics.renderTooltip(this.font, Component.literal("Empty"), mouseX, mouseY);
      }
    }
  }
}
