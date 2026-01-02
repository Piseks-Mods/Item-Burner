package dpdns.org.pisekpiskovec.ItemBurner.block.entity;

import dpdns.org.pisekpiskovec.ItemBurner.ItemBurner;
import dpdns.org.pisekpiskovec.ItemBurner.block.ModBlocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockEntities {
  public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
      DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, ItemBurner.MOD_ID);

  public static final RegistryObject<BlockEntityType<ItemBurnerBlockEntity>> ITEM_BURNER_BE =
      BLOCK_ENTITIES.register(
          "item_burner_be",
          () -> BlockEntityType.Builder.of(ItemBurnerBlockEntity::new, ModBlocks.BURNER.get()).build(null));

  public static void register(IEventBus eventBus) {
    BLOCK_ENTITIES.register(eventBus);
  }
}
