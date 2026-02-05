package dpdns.org.pisekpiskovec.ItemBurner.block.entity;

import dpdns.org.pisekpiskovec.ItemBurner.fluid.ModFluids;
import dpdns.org.pisekpiskovec.ItemBurner.screen.menu.ChronoresinCentrifugeMenu;
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

public class ChronoresinCentrifugeBlockEntity extends BlockEntity implements MenuProvider {
    public static final int CHRONOFLUX_TANK_CAPACITY = 10000;
    public static final int CHRONORESIN_TANK_CAPACITY = 10000;
    public static final int CRAFTING_INPUT_REQUIRED = 10; // TODO: Connect to configuration
    public static final int CRAFTING_OUTPUT_RESULTED = 1; // TODO: Connect to configuration

    private final FluidTank chronofluxTank = new FluidTank(CHRONOFLUX_TANK_CAPACITY) {
        @Override
        protected void onContentsChanged() {
            setChanged();
        }

        @Override
        public boolean isFluidValid(FluidStack stack) {
            return stack.getFluid() == ModFluids.SOURCE_CHRONOFLUX.get();
        }
    };

    private final FluidTank chronoresinTank = new FluidTank(CHRONORESIN_TANK_CAPACITY) {
        @Override
        protected void onContentsChanged() {
            setChanged();
        }

        @Override
        public boolean isFluidValid(FluidStack stack) {
            return stack.getFluid() == ModFluids.SOURCE_CHRONORESIN.get();
        }
    };

    private LazyOptional<IFluidHandler> lazyFluidHandler = LazyOptional.empty();

    protected final ContainerData data;
    private int chronofluxAmount = 0;
    private int chronoresinAmount = 0;

    public ChronoresinCentrifugeBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.CHRONORESIN_CENTRIFUGE_BE.get(), pPos, pBlockState);

        this.data = new ContainerData() {
            @Override
            public int get(int pIndex) {
                return switch (pIndex) {
                    case 0 -> ChronoresinCentrifugeBlockEntity.this.chronofluxAmount;
                    case 1 -> ChronoresinCentrifugeBlockEntity.this.chronoresinAmount;
                    default -> 0;
                };
            }

            @Override
            public void set(int pIndex, int pValue) {
                switch (pIndex) {
                    case 0 -> ChronoresinCentrifugeBlockEntity.this.chronofluxAmount = pValue;
                    case 1 -> ChronoresinCentrifugeBlockEntity.this.chronoresinAmount = pValue;
                }
            }

            @Override
            public int getCount() {
                return 2;
            }
        };
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.FLUID_HANDLER && (side == Direction.DOWN || side == Direction.UP))
            return lazyFluidHandler.cast();
        return super.getCapability(cap, side);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        lazyFluidHandler = LazyOptional.of(() -> chronofluxTank);
        lazyFluidHandler = LazyOptional.of(() -> chronoresinTank);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyFluidHandler.invalidate();
    }

    @Override
    public Component getDisplayName() {
        return Component.translatableWithFallback("block.itemburner.centrifuge", "Chronoresin Centrifuge");
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return new ChronoresinCentrifugeMenu(pContainerId, pPlayerInventory, this, this.data);
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        pTag.put("chronoflux_tank", chronofluxTank.writeToNBT(new CompoundTag()));
        pTag.put("chronoresin_tank", chronoresinTank.writeToNBT(new CompoundTag()));
        super.saveAdditional(pTag);
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        chronofluxTank.readFromNBT(pTag.getCompound("chronoflux_tank"));
        chronoresinTank.readFromNBT(pTag.getCompound("chronoresin_tank"));
    }

    public void tick(Level pLevel, BlockPos pPos, BlockState pState) {
        if (!pLevel.isClientSide()) {
            pumpFromBelow(pLevel, pPos); // Try to pump Chronoflux from block below

            // Sync fluid amount to client
            this.chronofluxAmount = this.chronofluxTank.getFluidAmount();
            this.chronoresinAmount = this.chronoresinTank.getFluidAmount();
            setChanged();
        }
    }

    private void pumpFromBelow(Level pLevel, BlockPos pPos) {
        BlockPos belowPos = pPos.below();
        BlockEntity belowEntity = pLevel.getBlockEntity(belowPos);

        if (belowEntity != null) {
            belowEntity.getCapability(ForgeCapabilities.FLUID_HANDLER, Direction.UP).ifPresent(handler -> {
                // Try to drain 10 mB from below
                FluidStack drained = handler.drain(10, IFluidHandler.FluidAction.SIMULATE);
                if (!drained.isEmpty() && drained.getFluid() == ModFluids.SOURCE_CHRONOFLUX.get()) {
                    int filled = chronofluxTank.fill(drained, IFluidHandler.FluidAction.SIMULATE);
                    if (filled > 0) {
                        FluidStack actualDrained = handler.drain(filled, IFluidHandler.FluidAction.EXECUTE);
                        chronofluxTank.fill(actualDrained, IFluidHandler.FluidAction.EXECUTE);
                    }
                }
            });
        }
    }

    public void craft() {
        FluidStack inputStack = this.chronofluxTank.getFluid();
        if (inputStack.isEmpty() || inputStack.getAmount() < CRAFTING_INPUT_REQUIRED) return;
        FluidStack outputStack = new FluidStack(ModFluids.SOURCE_CHRONORESIN.get(), CRAFTING_OUTPUT_RESULTED);
        chronoresinTank.fill(outputStack, IFluidHandler.FluidAction.EXECUTE);
        this.chronofluxTank.drain(CRAFTING_INPUT_REQUIRED, IFluidHandler.FluidAction.EXECUTE);
    }

    public FluidTank getChronofluxTank() {
        return chronofluxTank;
    }

    public FluidTank getChronoresinTank() {
        return chronoresinTank;
    }
}
