package dpdns.org.pisekpiskovec.ItemBurner.block;

import dpdns.org.pisekpiskovec.ItemBurner.block.entity.ChronofluxValveBlockEntity;
import dpdns.org.pisekpiskovec.ItemBurner.block.entity.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.NetworkHooks;
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
  public InteractionResult use(
      BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
    if (!pLevel.isClientSide()) {
      BlockEntity entity = pLevel.getBlockEntity(pPos);
      if (entity instanceof ChronofluxValveBlockEntity) {
        NetworkHooks.openScreen(((ServerPlayer) pPlayer), (ChronofluxValveBlockEntity) entity, pPos);
      } else {
        throw new IllegalStateException("Container provider is missing!");
      }
    }
    return InteractionResult.sidedSuccess(pLevel.isClientSide());
  }

  @Override
  public @Nullable BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
    return new ChronofluxValveBlockEntity(pPos, pState);
  }

  @Override
  public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(
      Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
    if (pLevel.isClientSide()) {
      return null;
    }

    return createTickerHelper(
        pBlockEntityType,
        ModBlockEntities.CHRONOFLUX_VALVE_BE.get(),
        (pLevel1, pPos, pState1, pBlockEntity) -> pBlockEntity.tick(pLevel1, pPos, pState1));
  }
}
