package dpdns.org.pisekpiskovec.ItemBurner.block.entity;

import dpdns.org.pisekpiskovec.ItemBurner.fluid.ModFluids;
import dpdns.org.pisekpiskovec.ItemBurner.screen.ItemBurnerMenu;
import java.util.Map;
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

public class ItemBurnerBlockEntity extends BlockEntity implements MenuProvider {
  private final ItemStackHandler itemHandler =
      new ItemStackHandler(1) {
        @Override
        protected void onContentsChanged(int slot) {
          setChanged();
        }
      };

  private static final int INPUT_SLOT = 0;
  public static final int FLUID_TANK_CAPACITY = 10000; // 10 buckets
  public static int CONFIGURED_SLOT_OUTPUT = 64; // TODO: Connect to configuration

  private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();

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
  private int progress = 0;
  private int maxProgress = 22;
  private int fluidAmount = 0;

  public ItemBurnerBlockEntity(BlockPos pPos, BlockState pBlockState) {
    super(ModBlockEntities.ITEM_BURNER_BE.get(), pPos, pBlockState);

    this.data =
        new ContainerData() {
          @Override
          public int get(int pIndex) {
            return switch (pIndex) {
              case 0 -> ItemBurnerBlockEntity.this.progress;
              case 1 -> ItemBurnerBlockEntity.this.maxProgress;
              case 2 -> ItemBurnerBlockEntity.this.fluidAmount;
              default -> 0;
            };
          }

          @Override
          public void set(int pIndex, int pValue) {
            switch (pIndex) {
              case 0 -> ItemBurnerBlockEntity.this.progress = pValue;
              case 1 -> ItemBurnerBlockEntity.this.maxProgress = pValue;
              case 2 -> ItemBurnerBlockEntity.this.fluidAmount = pValue;
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
    return Component.translatable("block.itemburner.burner");
  }

  @Override
  public @Nullable AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
    return new ItemBurnerMenu(pContainerId, pPlayerInventory, this, this.data);
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

  private void resetProgress() {
    progress = 0;
  }

  private void craftItem() {
    ItemStack inputStack = this.itemHandler.getStackInSlot(INPUT_SLOT);
    if (inputStack.isEmpty()) return;

    int fluidAmount = calculateFluidAmount(inputStack);

    // Add fluid to tank
    FluidStack fluidStack = new FluidStack(ModFluids.SOURCE_CHRONOFLUX.get(), fluidAmount);
    fluidTank.fill(fluidStack, IFluidHandler.FluidAction.EXECUTE);

    // Remove the input item
    this.itemHandler.extractItem(INPUT_SLOT, 1, false);
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
      baseMb = (CONFIGURED_SLOT_OUTPUT * 1) / maxStack;
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
    ItemStack inputStack = this.itemHandler.getStackInSlot(INPUT_SLOT);

    // Check if there's an item in the input slot
    if (inputStack.isEmpty()) {
      return false;
    }

    // Calculate how much fluid this item would produce
    int fluidAmount = calculateFluidAmount(inputStack);

    // Check if we have space in the tank
    FluidStack simulatedFluid = new FluidStack(ModFluids.SOURCE_CHRONOFLUX.get(), fluidAmount);
    int fillAmount = fluidTank.fill(simulatedFluid, IFluidHandler.FluidAction.SIMULATE);

    return fillAmount == fluidAmount;
  }

  public FluidTank getFluidTank() {
    return fluidTank;
  }
}
