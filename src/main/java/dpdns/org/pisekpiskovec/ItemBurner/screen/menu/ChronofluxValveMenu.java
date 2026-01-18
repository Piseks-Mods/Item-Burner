package dpdns.org.pisekpiskovec.ItemBurner.screen.menu;

import dpdns.org.pisekpiskovec.ItemBurner.block.ModBlocks;
import dpdns.org.pisekpiskovec.ItemBurner.block.entity.ChronofluxValveBlockEntity;
import dpdns.org.pisekpiskovec.ItemBurner.fluid.ModFluids;
import dpdns.org.pisekpiskovec.ItemBurner.screen.ModMenuTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.fluids.FluidStack;

public class ChronofluxValveMenu extends AbstractContainerMenu {
  public final ChronofluxValveBlockEntity blockEntity;
  private final Level level;
  private final ContainerData data;

  public ChronofluxValveMenu(int pContainerId, Inventory inv, FriendlyByteBuf extraData) {
    this(pContainerId, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()), new SimpleContainerData(1));
  }

  public ChronofluxValveMenu(int pContainerId, Inventory inv, BlockEntity entity, ContainerData data) {
    super(ModMenuTypes.CHRONOFLUX_VALVE_MENU.get(), pContainerId);
    blockEntity = ((ChronofluxValveBlockEntity) entity);
    this.level = inv.player.level();
    this.data = data;

    addPlayerInventory(inv);
    addPlayerHotbar(inv);

    addDataSlots(data);
  }

  public FluidStack getFluidStack() {
    if (this.level.isClientSide()) {
      // On client side, create FluidStack from synced data
      if (this.data == null || this.data.getCount() < 1) {
        return FluidStack.EMPTY;
      }

      int amount = this.data.get(0);
      if (amount > 0) {
        return new FluidStack(ModFluids.SOURCE_CHRONOFLUX.get(), amount);
      }

      return FluidStack.EMPTY;
    }

    // On server side, get from block entity
    if (this.blockEntity != null && this.blockEntity.getFluidTank() != null) {
      return this.blockEntity.getFluidTank().getFluid();
    }

    return FluidStack.EMPTY;
  }

  public int getFluidCapacity() {
    return this.blockEntity.getFluidTank().getCapacity();
  }

  public boolean clickMenuButton(Player pPlayer, int pId) {
    if (pId >= 0 && pId <= 2) {
      int amount =
          switch (pId) {
            case 0 -> 1;
            case 1 -> 10;
            case 2 -> 100;
            default -> 0;
          };
      return this.blockEntity.retrieveFluid(pPlayer, amount);
    }
    return false;
  }

  @Override
  public ItemStack quickMoveStack(Player pPlayer, int pIndex) {
    return ItemStack.EMPTY;
  }

  @Override
  public boolean stillValid(Player pPlayer) {
    return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()), pPlayer, ModBlocks.VALVE.get());
  }

  private void addPlayerInventory(Inventory playerInventory) {
    for (int i = 0; i < 3; ++i) {
      for (int l = 0; l < 9; ++l) {
        this.addSlot(new Slot(playerInventory, l + i * 9 + 9, 8 + l * 18, 84 + i * 18));
      }
    }
  }

  private void addPlayerHotbar(Inventory playerInventory) {
    for (int i = 0; i < 9; ++i) {
      this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
    }
  }
}
