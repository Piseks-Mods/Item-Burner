package dpdns.org.pisekpiskovec.ItemBurner.block;

import dpdns.org.pisekpiskovec.ItemBurner.ItemBurner;
import dpdns.org.pisekpiskovec.ItemBurner.fluid.ModFluids;
import dpdns.org.pisekpiskovec.ItemBurner.item.ModItems;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, ItemBurner.MOD_ID);

    public static final RegistryObject<Block> BURNER = registerBlock("burner", () -> new ItemBurnerBlock(BlockBehaviour.Properties.copy(Blocks.ENCHANTING_TABLE)));

    public static final RegistryObject<Block> VALVE = registerBlock("valve", () -> new ChronofluxValveBlock(BlockBehaviour.Properties.copy(Blocks.CAULDRON)));

    public static final RegistryObject<LiquidBlock> CHRONOFLUX_BLOCK = BLOCKS.register("chronoflux_block", () -> new LiquidBlock(ModFluids.SOURCE_CHRONOFLUX, BlockBehaviour.Properties.copy(Blocks.WATER).noCollission().strength(100f).noLootTable()));

    public static final RegistryObject<LiquidBlock> CHRONORESIN_BLOCK = BLOCKS.register("chronoresin_block", () -> new LiquidBlock(ModFluids.SOURCE_CHRONORESIN, BlockBehaviour.Properties.copy(Blocks.WATER).noCollission().strength(100f).noLootTable()));

    private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block) {
        RegistryObject<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    private static <T extends Block> RegistryObject<Item> registerBlockItem(String name, RegistryObject<T> block) {
        return ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
