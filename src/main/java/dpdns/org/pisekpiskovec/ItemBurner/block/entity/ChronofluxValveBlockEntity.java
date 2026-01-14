package dpdns.org.pisekpiskovec.ItemBurner.block.entity;

import dpdns.org.pisekpiskovec.ItemBurner.fluid.ModFluids;
import dpdns.org.pisekpiskovec.ItemBurner.screen.menus.ChronofluxValveMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ChronofluxValveBlockEntity extends BlockEntity implements MenuProvider {
  public static final int FLUID_TANK_CAPACITY = 1000; // 1 bucket

  private final FluidTank fluidTank =
      new FluidTank(FLUID_TANK_CAPACITY) {
        @Override
        protected void onContentsChanged() {
          setChanged();
        }

        @Override
        public boolean isFluidValid(FluidStack stack) {
          return stack.getFluid() == ModFluids.SOURCE_CHRONOFLUX.get();
        }
      };

  private LazyOptional<IFluidHandler> lazyFluidHandler = LazyOptional.empty();

  protected final ContainerData data;
  private int fluidAmount = 0;

  public ChronofluxValveBlockEntity(BlockPos pPos, BlockState pBlockState) {
    super(ModBlockEntities.CHRONOFLUX_VALVE_BE.get(), pPos, pBlockState);

    this.data =
        new ContainerData() {
          @Override
          public int get(int pIndex) {
            return switch (pIndex) {
              case 0 -> ChronofluxValveBlockEntity.this.fluidAmount;
              default -> 0;
            };
          }

          @Override
          public void set(int pIndex, int pValue) {
            if (pIndex == 0) {
              ChronofluxValveBlockEntity.this.fluidAmount = pValue;
            }
          }

          @Override
          public int getCount() {
            return 1;
          }
        };
  }

  @Override
  public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
    if (cap == ForgeCapabilities.FLUID_HANDLER) {
      return lazyFluidHandler.cast();
    }
    return super.getCapability(cap, side);
  }

  @Override
  public void onLoad() {
    super.onLoad();
    lazyFluidHandler = LazyOptional.of(() -> fluidTank);
  }

  @Override
  public void invalidateCaps() {
    super.invalidateCaps();
    lazyFluidHandler.invalidate();
  }

  @Override
  public Component getDisplayName() {
    return Component.translatable("block.itemburner.valve");
  }

  @Override
  public @Nullable AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
    return new ChronofluxValveMenu(pContainerId, pPlayerInventory, this, this.data);
  }

  @Override
  protected void saveAdditional(CompoundTag pTag) {
    pTag.put("fluid_tank", fluidTank.writeToNBT(new CompoundTag()));
    super.saveAdditional(pTag);
  }

  @Override
  public void load(CompoundTag pTag) {
    super.load(pTag);
    fluidTank.readFromNBT(pTag.getCompound("fluid_tank"));
  }

  public void tick(Level pLevel, BlockPos pPos, BlockState pState) {
    if (!pLevel.isClientSide()) {
      // Try to pump fluid from block below
      pumpFromBelow(pLevel, pPos);

      // Sync fluid amount to client
      this.fluidAmount = this.fluidTank.getFluidAmount();
      setChanged();
    }
  }

  private void pumpFromBelow(Level pLevel, BlockPos pPos) {
    BlockPos belowPos = pPos.below();
    BlockEntity belowEntity = pLevel.getBlockEntity(belowPos);

    if (belowEntity != null) {
      belowEntity
          .getCapability(ForgeCapabilities.FLUID_HANDLER, Direction.UP)
          .ifPresent(
              handler -> {
                // Try to drain 10 mB from below
                FluidStack drained = handler.drain(10, IFluidHandler.FluidAction.SIMULATE);
                if (!drained.isEmpty() && drained.getFluid() == ModFluids.SOURCE_CHRONOFLUX.get()) {
                  int filled = fluidTank.fill(drained, IFluidHandler.FluidAction.SIMULATE);
                  if (filled > 0) {
                    FluidStack actualDrained = handler.drain(filled, IFluidHandler.FluidAction.EXECUTE);
                    fluidTank.fill(actualDrained, IFluidHandler.FluidAction.EXECUTE);
                  }
                }
              });
    }
  }

  public boolean retrieveFluid(Player player, int amount) {
    FluidStack drained = fluidTank.drain(amount, IFluidHandler.FluidAction.SIMULATE);
    if (drained.getAmount() >= amount) {
      fluidTank.drain(amount, IFluidHandler.FluidAction.EXECUTE);
      player.giveExperiencePoints(amount);
      setChanged();
      return true;
    }
    return false;
  }

  public FluidTank getFluidTank() {
    return fluidTank;
  }
}
