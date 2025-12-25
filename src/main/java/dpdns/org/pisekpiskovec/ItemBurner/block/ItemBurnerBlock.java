package dpdns.org.pisekpiskovec.ItemBurner.block;

import net.minecraft.world.level.block.Block;

public class ItemBurnerBlock extends Block {
  public ItemBurnerBlock(Properties properties) {
    super(properties.strength(5f, 1200f).lightLevel(state -> 7));
  }
}
