package dpdns.org.pisekpiskovec.ItemBurner.screen;

import dpdns.org.pisekpiskovec.ItemBurner.ItemBurner;
import dpdns.org.pisekpiskovec.ItemBurner.screen.menu.ChronofluxValveMenu;
import dpdns.org.pisekpiskovec.ItemBurner.screen.menu.ItemBurnerMenu;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.network.IContainerFactory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModMenuTypes {
  public static final DeferredRegister<MenuType<?>> MENUS =
      DeferredRegister.create(ForgeRegistries.MENU_TYPES, ItemBurner.MOD_ID);

  public static final RegistryObject<MenuType<ItemBurnerMenu>> ITEM_BURNER_MENU =
      registerMenuType("item_burner_menu", ItemBurnerMenu::new);

  public static final RegistryObject<MenuType<ChronofluxValveMenu>> CHRONOFLUX_VALVE_MENU =
      registerMenuType("chronoflux_valve_menu", ChronofluxValveMenu::new);

  private static <T extends AbstractContainerMenu> RegistryObject<MenuType<T>> registerMenuType(
      String name, IContainerFactory factory) {
    return MENUS.register(name, () -> IForgeMenuType.create(factory));
  }

  public static void register(IEventBus eventBus) {
    MENUS.register(eventBus);
  }
}
