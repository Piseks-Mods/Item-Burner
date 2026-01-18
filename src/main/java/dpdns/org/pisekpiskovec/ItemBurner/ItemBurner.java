package dpdns.org.pisekpiskovec.ItemBurner;

import com.mojang.logging.LogUtils;
import dpdns.org.pisekpiskovec.ItemBurner.block.ModBlocks;
import dpdns.org.pisekpiskovec.ItemBurner.block.entity.ModBlockEntities;
import dpdns.org.pisekpiskovec.ItemBurner.fluid.ModFluidTypes;
import dpdns.org.pisekpiskovec.ItemBurner.fluid.ModFluids;
import dpdns.org.pisekpiskovec.ItemBurner.item.ModCreativeModTabs;
import dpdns.org.pisekpiskovec.ItemBurner.item.ModItems;
import dpdns.org.pisekpiskovec.ItemBurner.screen.ItemBurnerScreen;
import dpdns.org.pisekpiskovec.ItemBurner.screen.ModMenuTypes;
import dpdns.org.pisekpiskovec.ItemBurner.screen.screen.ChronofluxValveScreen;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(ItemBurner.MOD_ID)
public class ItemBurner {
  // Define mod id in a common place for everything to reference
  public static final String MOD_ID = "itemburner";
  // Directly reference a slf4j logger
  private static final Logger LOGGER = LogUtils.getLogger();

  public ItemBurner(FMLJavaModLoadingContext context) {
    IEventBus modEventBus = context.getModEventBus();

    ModItems.register(modEventBus);
    ModBlockEntities.register(modEventBus);
    ModBlocks.register(modEventBus);
    ModFluidTypes.register(modEventBus);
    ModFluids.register(modEventBus);
    ModCreativeModTabs.register(modEventBus);
    ModMenuTypes.register(modEventBus);

    modEventBus.addListener(this::commonSetup);

    MinecraftForge.EVENT_BUS.register(this);
    modEventBus.addListener(this::addCreative);
  }

  private void commonSetup(final FMLCommonSetupEvent event) {}

  // Add the example block item to the building blocks tab
  private void addCreative(BuildCreativeModeTabContentsEvent event) {}

  // You can use SubscribeEvent and let the Event Bus discover methods to call
  @SubscribeEvent
  public void onServerStarting(ServerStartingEvent event) {}

  // You can use EventBusSubscriber to automatically register all static methods in the class annotated with
  // @SubscribeEvent
  @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
  public static class ClientModEvents {
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
      MenuScreens.register(ModMenuTypes.ITEM_BURNER_MENU.get(), ItemBurnerScreen::new);
      MenuScreens.register(ModMenuTypes.CHRONOFLUX_VALVE_MENU.get(), ChronofluxValveScreen::new);
    }
  }
}
