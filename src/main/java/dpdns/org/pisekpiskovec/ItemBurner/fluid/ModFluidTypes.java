package dpdns.org.pisekpiskovec.ItemBurner.fluid;

import dpdns.org.pisekpiskovec.ItemBurner.ItemBurner;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraftforge.common.SoundAction;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.joml.Vector3f;

public class ModFluidTypes {
    public static final ResourceLocation WATER_STILL_RL = new ResourceLocation("block/water_still");
    public static final ResourceLocation WATER_FLOWING_RL = new ResourceLocation("block/water_flow");
    public static final ResourceLocation WATER_OVERLAY_RL = new ResourceLocation("block/water_overlay");

    public static final DeferredRegister<FluidType> FLUID_TYPES = DeferredRegister.create(ForgeRegistries.Keys.FLUID_TYPES, ItemBurner.MOD_ID);

    public static final RegistryObject<FluidType> CHRONOFLUX_FLUID_TYPE = FLUID_TYPES.register("chronoflux_fluid", () -> new BaseFluidType(new ResourceLocation(ItemBurner.MOD_ID, "block/chronoflux_still"), new ResourceLocation(ItemBurner.MOD_ID, "block/chronoflux_flow"), WATER_OVERLAY_RL, 0xA1ADD3FE, new Vector3f(173f / 255f, 222f / 255f, 230f / 255f), FluidType.Properties.create().lightLevel(8).density(15).viscosity(5).sound(SoundAction.get("drink"), SoundEvents.HONEY_DRINK)));

    public static final RegistryObject<FluidType> CHRONORESIN_FLUID_TYPE = FLUID_TYPES.register("chronoresin_fluid", () -> new BaseFluidType(new ResourceLocation(ItemBurner.MOD_ID, "block/chronoresin_still"), new ResourceLocation(ItemBurner.MOD_ID, "block/chronoresin_flow"), WATER_OVERLAY_RL, 0xA1ADD300, new Vector3f(173f / 255f, 222f / 255f, 000f / 255f), FluidType.Properties.create().lightLevel(4).density(25).viscosity(15).sound(SoundAction.get("drink"), SoundEvents.HONEY_DRINK)));

    public static void register(IEventBus eventBus) {
        FLUID_TYPES.register(eventBus);
    }
}
