package dpdns.org.pisekpiskovec.ItemBurner.block.entity;

import dpdns.org.pisekpiskovec.ItemBurner.config.ModConfig;
import dpdns.org.pisekpiskovec.ItemBurner.fluid.ModFluids;
import dpdns.org.pisekpiskovec.ItemBurner.screen.menu.ChronoresinFabricatorMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class ChronoresinFabricatorBlockEntity extends BlockEntity implements MenuProvider {
    private final ItemStackHandler itemHandler = new ItemStackHandler(2) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }

        @Override
        public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
            if (slot == INPUT_SLOT) { // Limit input slot to 1 item only
                if (getStackInSlot(slot).isEmpty()) { // If slot is empty, insert 1 item
                    ItemStack singleItem = stack.copy();
                    singleItem.setCount(1);
                    ItemStack remainder = stack.copy();
                    remainder.shrink(1);
                    if (!simulate) setStackInSlot(slot, singleItem);
                    return remainder;
                }
                return stack; // If slot isn't empty, do nothing
            }
            return super.insertItem(slot, stack, simulate); // Other slots works normally
        }

        @Override
        public int getSlotLimit(int slot) {
            if (slot == INPUT_SLOT) return 1;
            return super.getSlotLimit(slot);
        }
    };

    private static final int INPUT_SLOT = 0;
    private static final int OUTPUT_SLOT = 1;
    public static final int FLUID_TANK_CAPACITY = 1000; // 1 bucket
    public static int CONFIGURED_SLOT_OUTPUT = ModConfig.COMMON.maxBurnStack.get();

    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();

    private final FluidTank fluidTank = new FluidTank(FLUID_TANK_CAPACITY) {
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
    private int progress = 0;
    private int maxProgress = 22;
    private int fluidAmount = 0;

    public ChronoresinFabricatorBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.CHRONORESIN_FABRICATOR_BE.get(), pPos, pBlockState);

        this.data = new ContainerData() {
            @Override
            public int get(int pIndex) {
                return switch (pIndex) {
                    case 0 -> ChronoresinFabricatorBlockEntity.this.progress;
                    case 1 -> ChronoresinFabricatorBlockEntity.this.maxProgress;
                    case 2 -> ChronoresinFabricatorBlockEntity.this.fluidAmount;
                    default -> 0;
                };
            }

            @Override
            public void set(int pIndex, int pValue) {
                switch (pIndex) {
                    case 0 -> ChronoresinFabricatorBlockEntity.this.progress = pValue;
                    case 1 -> ChronoresinFabricatorBlockEntity.this.maxProgress = pValue;
                    case 2 -> ChronoresinFabricatorBlockEntity.this.fluidAmount = pValue;
                }
            }

            @Override
            public int getCount() {
                return 3;
            }
        };
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return lazyItemHandler.cast();
        }
        if (cap == ForgeCapabilities.FLUID_HANDLER) {
            return lazyFluidHandler.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        lazyItemHandler = LazyOptional.of(() -> itemHandler);
        lazyFluidHandler = LazyOptional.of(() -> fluidTank);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyItemHandler.invalidate();
        lazyFluidHandler.invalidate();
    }

    public void drops() {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for (int i = 0; i < itemHandler.getSlots(); i++) {
            inventory.setItem(i, itemHandler.getStackInSlot(i));
        }
        Containers.dropContents(this.level, this.worldPosition, inventory);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.itemburner.fabricator");
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return new ChronoresinFabricatorMenu(pContainerId, pPlayerInventory, this, this.data);
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        pTag.put("inventory", itemHandler.serializeNBT());
        pTag.put("fluid_tank", fluidTank.writeToNBT(new CompoundTag()));
        pTag.putInt("item_burner.progress", progress);

        super.saveAdditional(pTag);
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        itemHandler.deserializeNBT(pTag.getCompound("inventory"));
        fluidTank.readFromNBT(pTag.getCompound("fluid_tank"));
        progress = pTag.getInt("item_burner.progress");
    }

    public void tick(Level pLevel, BlockPos pPos, BlockState pState) {
        if (!pLevel.isClientSide()) {
            // Try to pump fluid from block below
            pumpFromBelow(pLevel, pPos);

            // Sync fluid amount to client
            this.fluidAmount = this.fluidTank.getFluidAmount();
            setChanged();
        }

        if (hasRecipe()) {
            increaseCraftingProgress();
            setChanged(pLevel, pPos, pState);

            if (hasProgressFinished()) {
                craftItem();
                resetProgress();
            }
        } else {
            resetProgress();
        }

        // Sync fluid amount to client
        this.fluidAmount = this.fluidTank.getFluidAmount();
    }

    private void pumpFromBelow(Level pLevel, BlockPos pPos) {
        BlockPos belowPos = pPos.below();
        BlockEntity belowEntity = pLevel.getBlockEntity(belowPos);

        if (belowEntity != null) {
            belowEntity.getCapability(ForgeCapabilities.FLUID_HANDLER, Direction.UP).ifPresent(handler -> {
                // Try to drain 10 mB from below
                FluidStack drained = handler.drain(10, IFluidHandler.FluidAction.SIMULATE);
                if (!drained.isEmpty() && drained.getFluid() == ModFluids.SOURCE_CHRONORESIN.get()) {
                    int filled = fluidTank.fill(drained, IFluidHandler.FluidAction.SIMULATE);
                    if (filled > 0) {
                        FluidStack actualDrained = handler.drain(filled, IFluidHandler.FluidAction.EXECUTE);
                        fluidTank.fill(actualDrained, IFluidHandler.FluidAction.EXECUTE);
                    }
                }
            });
        }
    }

    private void resetProgress() {
        progress = 0;
    }

    private void craftItem() {
        ItemStack templateStack = this.itemHandler.getStackInSlot(INPUT_SLOT);
        int requiredFluid = calculateFluidAmount(templateStack);

        // Drain fluid from tank
        FluidStack drained = fluidTank.drain(requiredFluid, IFluidHandler.FluidAction.EXECUTE);

        // Create duplicate in output slot
        ItemStack duplicate = templateStack.copy();
        duplicate.setCount(1);
        this.itemHandler.insertItem(OUTPUT_SLOT, duplicate, false);
    }

    private int calculateFluidAmount(ItemStack stack) {
        int baseMb = 0;

        // Check if item is a tool (i.e. has durability)
        if (stack.isDamageableItem()) {
            int maxDurability = stack.getMaxDamage();
            int currentDurability = maxDurability - stack.getDamageValue();
            baseMb = (CONFIGURED_SLOT_OUTPUT * currentDurability) / maxDurability;
        } else {
            // Item is stackable
            int maxStack = stack.getMaxStackSize();
            int currentStack = stack.getCount();
            baseMb = (CONFIGURED_SLOT_OUTPUT) / maxStack;
        }

        // Add enchantment bonus
        Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(stack);
        int enchantmentBonus = 0;
        for (int level : enchantments.values()) {
            enchantmentBonus += level;
        }

        return baseMb + enchantmentBonus;
    }

    private boolean hasProgressFinished() {
        return progress >= maxProgress;
    }

    private void increaseCraftingProgress() {
        progress++;
    }

    private boolean hasRecipe() {
        ItemStack templateStack = this.itemHandler.getStackInSlot(INPUT_SLOT);
        if (templateStack.isEmpty()) return false;

        // Check if there's enough Chronoresin
        int requiredFluid = calculateFluidAmount(templateStack);
        if (fluidTank.getFluidAmount() < requiredFluid) return false;
        if (fluidTank.getFluid().getFluid() != ModFluids.SOURCE_CHRONORESIN.get()) return false;

        // Check if output slot can accept the duplicate
        ItemStack outputStack = this.itemHandler.getStackInSlot(OUTPUT_SLOT);
        if (outputStack.isEmpty()) return true;

        return ItemStack.isSameItemSameTags(templateStack, outputStack) && outputStack.getCount() < outputStack.getMaxStackSize();
    }

    public FluidTank getFluidTank() {
        return fluidTank;
    }
}
