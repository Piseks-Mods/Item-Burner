package dpdns.org.pisekpiskovec.ItemBurner.item;

import dpdns.org.pisekpiskovec.ItemBurner.ItemBurner;
import dpdns.org.pisekpiskovec.ItemBurner.block.ModBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModCreativeModTabs {
  public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
      DeferredRegister.create(Registries.CREATIVE_MODE_TAB, ItemBurner.MOD_ID);

  public static final RegistryObject<CreativeModeTab> ITEM_BURNER_TAB =
      CREATIVE_MODE_TABS.register(
          "item_burner_tab",
          () ->
              CreativeModeTab.builder()
                  .icon(() -> new ItemStack(ModBlocks.BURNER.get()))
                  .title(Component.translatable("creativetab.item_burner_tab"))
                  .displayItems(
                      (pParameters, pOutput) -> {
                        pOutput.accept(ModBlocks.BURNER.get());
                      })
                  .build());

  public static void register(IEventBus eventBus) {
    CREATIVE_MODE_TABS.register(eventBus);
  }
}
