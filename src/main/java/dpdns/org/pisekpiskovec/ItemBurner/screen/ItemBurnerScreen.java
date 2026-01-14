package dpdns.org.pisekpiskovec.ItemBurner.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import dpdns.org.pisekpiskovec.ItemBurner.ItemBurner;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidStack;

public class ItemBurnerScreen extends AbstractContainerScreen<ItemBurnerMenu> {
  private static final ResourceLocation TEXTURE = new ResourceLocation(ItemBurner.MOD_ID, "textures/gui/burner.png");
  private static final int FLUID_TANK_X = 112; // X position relative to GUI
  private static final int FLUID_TANK_Y = 17; // Y position relative to GUI
  private static final int FLUID_TANK_WIDTH = 24;
  private static final int FLUID_TANK_HEIGHT = 52;

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
    renderBurning(guiGraphics, x, y);
    renderFluidTank(guiGraphics, x, y);
  }

  private void renderProgressArrow(GuiGraphics guiGraphics, int x, int y) {
    if (menu.isCrafting()) {
      guiGraphics.blit(TEXTURE, x + 79, y + 35, 176, 14, menu.getScaledProgress(), 17);
    }
  }

  private void renderBurning(GuiGraphics guiGraphics, int x, int y) {
    if (menu.isCrafting()) {
      guiGraphics.blit(TEXTURE, x + 57, y + 37, 176, 0, 14, 14);
    }
  }

  private void renderFluidTank(GuiGraphics guiGraphics, int x, int y) {
    FluidStack fluidStack;
    try {
      fluidStack = menu.getFluidStack();
    } catch (Exception e) {
      // If there's an error getting fluid stack, just return
      return;
    }
    if (!fluidStack.isEmpty()) {
      int fluidAmount = fluidStack.getAmount();
      int capacity = menu.getFluidCapacity();
      int fluidHeight = (fluidAmount * FLUID_TANK_HEIGHT) / capacity; // Calculate the height of fluid to render

      if (fluidHeight > 0) {
        // Get the fluid's still texture
        IClientFluidTypeExtensions fluidTypeExtensions = IClientFluidTypeExtensions.of(fluidStack.getFluid());
        ResourceLocation stillTexture = fluidTypeExtensions.getStillTexture();

        if (stillTexture != null) {
          // Get the texture from the block atlas
          TextureAtlasSprite sprite = minecraft.getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(stillTexture);

          // Position where fluid rendering starts (from bottom)
          int tankX = x + FLUID_TANK_X;
          int tankY = y + FLUID_TANK_Y + (FLUID_TANK_HEIGHT - fluidHeight);

          RenderSystem.enableBlend();

          // Render the fluid texture tiled vertically
          int yOffset = 0;
          while (yOffset < fluidHeight) {
            int remainingHeight = Math.min(16, fluidHeight - yOffset);

            guiGraphics.blit(tankX, tankY + yOffset, 0, FLUID_TANK_WIDTH, remainingHeight, sprite);

            yOffset += 16;
          }

          RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
          RenderSystem.disableBlend();
        }
      }
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
    int tankX = x + FLUID_TANK_X;
    int tankY = y + FLUID_TANK_Y;

    if (mouseX >= tankX
        && mouseX <= tankX + FLUID_TANK_WIDTH
        && mouseY >= tankY
        && mouseY <= tankY + FLUID_TANK_HEIGHT) {
      FluidStack fluidStack = menu.getFluidStack();
      if (!fluidStack.isEmpty()) {
        Component fluidName = fluidStack.getDisplayName();
        Component amount = Component.literal(fluidStack.getAmount() + " / " + menu.getFluidCapacity() + " mB");
        guiGraphics.renderComponentTooltip(this.font, java.util.List.of(fluidName, amount), mouseX, mouseY);
      } else {
        guiGraphics.renderTooltip(this.font, Component.translatable("gui.itemburner.tank.empty"), mouseX, mouseY);
      }
    }
  }
}
