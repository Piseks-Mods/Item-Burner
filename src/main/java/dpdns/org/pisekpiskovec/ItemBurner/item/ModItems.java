package dpdns.org.pisekpiskovec.ItemBurner.item;

import dpdns.org.pisekpiskovec.ItemBurner.ItemBurner;
import dpdns.org.pisekpiskovec.ItemBurner.fluid.ModFluids;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
  public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, ItemBurner.MOD_ID);

  public static final RegistryObject<Item> CHRONOFLUX_BUCKET =
      ITEMS.register(
          "chronoflux_bucket",
          () ->
              new BucketItem(
                  ModFluids.SOURCE_CHRONOFLUX, new Item.Properties().craftRemainder(Items.BUCKET).stacksTo(1)));

  public static void register(IEventBus eventBus) {
    ITEMS.register(eventBus);
  }
}
