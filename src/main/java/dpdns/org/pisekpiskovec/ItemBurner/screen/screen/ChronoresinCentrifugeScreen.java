package dpdns.org.pisekpiskovec.ItemBurner.screen.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import dpdns.org.pisekpiskovec.ItemBurner.ItemBurner;
import dpdns.org.pisekpiskovec.ItemBurner.screen.menu.ChronoresinCentrifugeMenu;
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

public class ChronoresinCentrifugeScreen extends AbstractContainerScreen<ChronoresinCentrifugeMenu> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(ItemBurner.MOD_ID, "textures/gui/centrifuge.png");
    private static final int CHRONOFLUX_TANK_X = 0;
    private static final int CHRONOFLUX_TANK_Y = 0;
    private static final int CHRONORESIN_TANK_X = 0;
    private static final int CHRONORESIN_TANK_Y = 0;
    private static final int FLUID_TANK_WIDTH = 0;
    private static final int FLUID_TANK_HEIGHT = 0;

    public ChronoresinCentrifugeScreen(ChronoresinCentrifugeMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }

    @Override
    protected void renderBg(GuiGraphics pGuiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);

        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        pGuiGraphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight);

        renderFluidTank(0, pGuiGraphics, x, y);
        renderFluidTank(1, pGuiGraphics, x, y);
    }

    private void renderFluidTank(int dataIndex, GuiGraphics pGuiGraphics, int x, int y) {
        FluidStack fluidStack;
        try {
            fluidStack = switch (dataIndex) {
                case 0 -> menu.getChronofluxStack();
                case 1 -> menu.getChronoresinStack();
                default -> throw new IllegalStateException("Unexpected value: " + dataIndex);
            };
        } catch (Exception e) {
            return; // If there's an error getting fluid stack, just return
        }

        if (!fluidStack.isEmpty()) {
            int fluidAmount = fluidStack.getAmount();
            int capacity = menu.getFluidCapacity();
            int fluidFilling = (fluidAmount * FLUID_TANK_WIDTH) / capacity; // Calculate the width of fluid to render

            if (fluidFilling > 0) {
                // Get the fluid's still texture
                IClientFluidTypeExtensions fluidTypeExtensions = IClientFluidTypeExtensions.of(fluidStack.getFluid());
                ResourceLocation stillTexture = fluidTypeExtensions.getStillTexture();

                if (stillTexture != null) {
                    TextureAtlasSprite sprite = minecraft.getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(stillTexture); // Get the texture from the block atlas

                    // Position where fluid rendering starts (from left)
                    int tankX = x + switch (dataIndex) {
                        case 0 -> CHRONOFLUX_TANK_X;
                        case 1 -> CHRONORESIN_TANK_X;
                        default -> throw new IllegalStateException("Unexpected value: " + dataIndex);
                    } + (FLUID_TANK_WIDTH - fluidFilling);
                    int tankY = y + switch (dataIndex) {
                        case 0 -> CHRONOFLUX_TANK_Y;
                        case 1 -> CHRONORESIN_TANK_Y;
                        default -> throw new IllegalStateException("Unexpected value: " + dataIndex);
                    };

                    RenderSystem.enableBlend();

                    // Render the fluid texture tile horizontally
                    int xOffset = 0;
                    while (xOffset < fluidFilling) {
                        int remainingWidth = Math.min(16, fluidFilling - xOffset);
                        pGuiGraphics.blit(tankX + xOffset, tankY, 0, remainingWidth, FLUID_TANK_HEIGHT, sprite);
                        xOffset += 16;
                    }

                    RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
                    RenderSystem.disableBlend();
                }
            }
        }
    }

    @Override
    public void render(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, float pPartialTick) {
        renderBackground(pGuiGraphics);
        super.render(pGuiGraphics, pMouseX, pMouseY, pPartialTick);
        renderTooltip(pGuiGraphics, pMouseX, pMouseY);

        // Render fluid tank tooltips
        // TODO
    }
}
