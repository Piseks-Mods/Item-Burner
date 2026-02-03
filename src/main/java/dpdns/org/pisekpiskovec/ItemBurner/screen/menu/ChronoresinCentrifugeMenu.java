package dpdns.org.pisekpiskovec.ItemBurner.screen.menu;

import dpdns.org.pisekpiskovec.ItemBurner.block.ModBlocks;
import dpdns.org.pisekpiskovec.ItemBurner.block.entity.ChronoresinCentrifugeBlockEntity;
import dpdns.org.pisekpiskovec.ItemBurner.fluid.ModFluids;
import dpdns.org.pisekpiskovec.ItemBurner.screen.ModMenuTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.fluids.FluidStack;

public class ChronoresinCentrifugeMenu extends AbstractContainerMenu {
    public final ChronoresinCentrifugeBlockEntity blockEntity;
    private final Level level;
    private final ContainerData data;

    public ChronoresinCentrifugeMenu(int pContainerId, Inventory inv, FriendlyByteBuf extraData) {
        this(pContainerId, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()), new SimpleContainerData(2));
    }

    public ChronoresinCentrifugeMenu(int pContainerId, Inventory inv, BlockEntity entity, ContainerData data) {
        super(ModMenuTypes.CHRONORESIN_CENTRIFUGE_MENU.get(), pContainerId);
        this.blockEntity = (ChronoresinCentrifugeBlockEntity) entity;
        this.level = inv.player.level();
        this.data = data;
    }

    public FluidStack getChronofluxStack() {
        if (this.level.isClientSide()) {
            // On client side, create FluidStack from synced data
            if (this.data == null || this.data.getCount() < 2) return FluidStack.EMPTY;

            int amount = this.data.get(0);
            if (amount > 0) return new FluidStack(ModFluids.SOURCE_CHRONOFLUX.get(), amount);
            return FluidStack.EMPTY;
        }

        // On server side, get from block entity
        if (this.blockEntity != null && this.blockEntity.getChronofluxTank() != null)
            return this.blockEntity.getChronofluxTank().getFluid();
        return FluidStack.EMPTY;
    }

    public FluidStack getChronoresinStack() {
        if (this.level.isClientSide()) {
            // On client side, create FluidStack from synced data
            if (this.data == null || this.data.getCount() < 2) return FluidStack.EMPTY;

            int amount = this.data.get(1);
            if (amount > 0) return new FluidStack(ModFluids.SOURCE_CHRONORESIN.get(), amount);
            return FluidStack.EMPTY;
        }

        // On server side, get from block entity
        if (this.blockEntity != null && this.blockEntity.getChronoresinTank() != null)
            return this.blockEntity.getChronoresinTank().getFluid();
        return FluidStack.EMPTY;
    }

    public int getFluidCapacity() {
        return Math.min(this.blockEntity.getChronofluxTank().getCapacity(), this.blockEntity.getChronoresinTank().getCapacity());
    }

    @Override
    public ItemStack quickMoveStack(Player pPlayer, int pIndex) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()), pPlayer, ModBlocks.CENTRIFUGE.get());
    }
}
