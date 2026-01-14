package dpdns.org.pisekpiskovec.ItemBurner.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class ChronofluxValveBlock extends BaseEntityBlock {
  public static final VoxelShape SHAPE = Block.box(0, 0, 0, 16, 16, 16);

  protected ChronofluxValveBlock(Properties pProperties) {
    super(pProperties);
  }

  @Override
  public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
    return SHAPE;
  }

  @Override
  public RenderShape getRenderShape(BlockState pState) {
    return RenderShape.MODEL;
  }

  @Override
  public @Nullable BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
    return null;
  }
}
