package dpdns.org.pisekpiskovec.ItemBurner.fluid;

import dpdns.org.pisekpiskovec.ItemBurner.ItemBurner;
import dpdns.org.pisekpiskovec.ItemBurner.block.ModBlocks;
import dpdns.org.pisekpiskovec.ItemBurner.item.ModItems;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModFluids {
    public static final DeferredRegister<Fluid> FLUIDS = DeferredRegister.create(ForgeRegistries.FLUIDS, ItemBurner.MOD_ID);

    public static final RegistryObject<FlowingFluid> SOURCE_CHRONOFLUX = FLUIDS.register("chronoflux_source", () -> new ForgeFlowingFluid.Source(ModFluids.CHRONOFLUX_PROPERTIES));

    public static final RegistryObject<FlowingFluid> FLOWING_CHRONOFLUX = FLUIDS.register("chronoflux_flowing", () -> new ForgeFlowingFluid.Flowing(ModFluids.CHRONOFLUX_PROPERTIES));

    public static final RegistryObject<FlowingFluid> SOURCE_CHRONORESIN = FLUIDS.register("chronoresin_source", () -> new ForgeFlowingFluid.Source(ModFluids.CHRONORESIN_PROPERTIES));

    public static final RegistryObject<FlowingFluid> FLOWING_CHRONORESIN = FLUIDS.register("chronoresin_flowing", () -> new ForgeFlowingFluid.Flowing(ModFluids.CHRONORESIN_PROPERTIES));

    public static final ForgeFlowingFluid.Properties CHRONOFLUX_PROPERTIES = new ForgeFlowingFluid.Properties(ModFluidTypes.CHRONOFLUX_FLUID_TYPE, SOURCE_CHRONOFLUX, FLOWING_CHRONOFLUX).slopeFindDistance(2).levelDecreasePerBlock(2).block(ModBlocks.CHRONOFLUX_BLOCK).bucket(ModItems.CHRONOFLUX_BUCKET);

    public static final ForgeFlowingFluid.Properties CHRONORESIN_PROPERTIES = new ForgeFlowingFluid.Properties(ModFluidTypes.CHRONORESIN_FLUID_TYPE, SOURCE_CHRONORESIN, FLOWING_CHRONORESIN).slopeFindDistance(1).levelDecreasePerBlock(4).block(ModBlocks.CHRONORESIN_BLOCK).bucket(ModItems.CHRONORESIN_BUCKET);

    public static void register(IEventBus eventBus) {
        FLUIDS.register(eventBus);
    }
}
