package net.Vicente.tutorialmod.block.custom;

import java.util.function.Predicate;
import javax.annotation.Nullable;

import net.Vicente.tutorialmod.entity.ModEntityTypes;
import net.Vicente.tutorialmod.entity.custom.ModGoldGolem;
import net.Vicente.tutorialmod.entity.custom.ModIceGolem;
import net.Vicente.tutorialmod.entity.custom.ModSnowGolem;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.animal.SnowGolem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Wearable;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.block.state.pattern.BlockPattern;
import net.minecraft.world.level.block.state.pattern.BlockPatternBuilder;
import net.minecraft.world.level.block.state.predicate.BlockMaterialPredicate;
import net.minecraft.world.level.block.state.predicate.BlockStatePredicate;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.Material;

public class CarvedPumpkinBlock extends HorizontalDirectionalBlock implements Wearable {
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    @Nullable
    private BlockPattern snowGolemBase;
    @Nullable
    private BlockPattern snowGolemFull;
    @Nullable
    private BlockPattern iceSnowGolemFull;
    @Nullable
    private BlockPattern ironGolemBase;
    @Nullable
    private BlockPattern ironGolemFull;
    @Nullable
    private BlockPattern goldGolemFull;
    private static final Predicate<BlockState> PUMPKINS_PREDICATE = (p_51396_) -> {
        return p_51396_ != null && (p_51396_.is(Blocks.CARVED_PUMPKIN) || p_51396_.is(Blocks.JACK_O_LANTERN));
    };

    public CarvedPumpkinBlock(BlockBehaviour.Properties p_51375_) {
        super(p_51375_);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    public void onPlace(BlockState p_51387_, Level p_51388_, BlockPos p_51389_, BlockState p_51390_, boolean p_51391_) {
        if (!p_51390_.is(p_51387_.getBlock())) {
            this.trySpawnGolem(p_51388_, p_51389_);
        }
    }

    public boolean canSpawnGolem(LevelReader p_51382_, BlockPos p_51383_) {
        return this.getOrCreateSnowGolemBase().find(p_51382_, p_51383_) != null || this.getOrCreateIronGolemBase().find(p_51382_, p_51383_) != null;
    }

    private void trySpawnGolem(Level p_51379_, BlockPos p_51380_) {
        BlockPattern.BlockPatternMatch blockpattern$blockpatternmatch = this.getOrCreateSnowGolemFull().find(p_51379_, p_51380_);
        BlockPattern.BlockPatternMatch blockpattern$blockpatternmatch2 = this.getOrCreateIceSnowGolemFull().find(p_51379_, p_51380_);
        BlockPattern.BlockPatternMatch blockpattern$blockpatternmatch3 = this.getOrCreateIronGolemFull().find(p_51379_, p_51380_);
        BlockPattern.BlockPatternMatch blockpattern$blockpatternmatch4 = this.getOrCreateModGoldGolemFull().find(p_51379_, p_51380_);
        if (blockpattern$blockpatternmatch != null) {
            ModSnowGolem modSnowgolem = ModEntityTypes.MODSNOWGOLEM.get().create(p_51379_);
            if (modSnowgolem != null) {
                spawnGolemInWorld(p_51379_, blockpattern$blockpatternmatch, modSnowgolem, blockpattern$blockpatternmatch.getBlock(0, 2, 0).getPos());
            }
        } else if (blockpattern$blockpatternmatch2 != null) {
            ModIceGolem iceGolem = ModEntityTypes.MODICEGOLEM.get().create(p_51379_);
            if (iceGolem != null) {
                spawnGolemInWorld(p_51379_, blockpattern$blockpatternmatch2, iceGolem, blockpattern$blockpatternmatch2.getBlock(0, 2, 0).getPos());
            }
        } else if (blockpattern$blockpatternmatch3 != null){
            IronGolem irongolem = ModEntityTypes.MODIRONGOLEM.get().create(p_51379_);
            if (irongolem != null) {
                irongolem.setPlayerCreated(true);
                spawnGolemInWorld(p_51379_, blockpattern$blockpatternmatch3, irongolem, blockpattern$blockpatternmatch3.getBlock(1, 2, 0).getPos());
            }
        }else if (blockpattern$blockpatternmatch4 != null){
            ModGoldGolem modGoldGolem = ModEntityTypes.MODGOLDGOLEM.get().create(p_51379_);
            if (modGoldGolem != null) {
                modGoldGolem.setPlayerCreated(true);
                spawnGolemInWorld(p_51379_, blockpattern$blockpatternmatch4, modGoldGolem, blockpattern$blockpatternmatch4.getBlock(1, 2, 0).getPos());
            }
        }

    }

    private static void spawnGolemInWorld(Level p_249110_, BlockPattern.BlockPatternMatch p_251293_, Entity p_251251_, BlockPos p_251189_) {
        clearPatternBlocks(p_249110_, p_251293_);
        p_251251_.moveTo((double)p_251189_.getX() + 0.5D, (double)p_251189_.getY() + 0.05D, (double)p_251189_.getZ() + 0.5D, 0.0F, 0.0F);
        p_249110_.addFreshEntity(p_251251_);

        for(ServerPlayer serverplayer : p_249110_.getEntitiesOfClass(ServerPlayer.class, p_251251_.getBoundingBox().inflate(5.0D))) {
            CriteriaTriggers.SUMMONED_ENTITY.trigger(serverplayer, p_251251_);
        }

        updatePatternBlocks(p_249110_, p_251293_);
    }

    public static void clearPatternBlocks(Level p_249604_, BlockPattern.BlockPatternMatch p_251190_) {
        for(int i = 0; i < p_251190_.getWidth(); ++i) {
            for(int j = 0; j < p_251190_.getHeight(); ++j) {
                BlockInWorld blockinworld = p_251190_.getBlock(i, j, 0);
                p_249604_.setBlock(blockinworld.getPos(), Blocks.AIR.defaultBlockState(), 2);
                p_249604_.levelEvent(2001, blockinworld.getPos(), Block.getId(blockinworld.getState()));
            }
        }

    }

    public static void updatePatternBlocks(Level p_248711_, BlockPattern.BlockPatternMatch p_251935_) {
        for(int i = 0; i < p_251935_.getWidth(); ++i) {
            for(int j = 0; j < p_251935_.getHeight(); ++j) {
                BlockInWorld blockinworld = p_251935_.getBlock(i, j, 0);
                p_248711_.blockUpdated(blockinworld.getPos(), Blocks.AIR);
            }
        }

    }

    public BlockState getStateForPlacement(BlockPlaceContext p_51377_) {
        return this.defaultBlockState().setValue(FACING, p_51377_.getHorizontalDirection().getOpposite());
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> p_51385_) {
        p_51385_.add(FACING);
    }

    private BlockPattern getOrCreateSnowGolemBase() {
        if (this.snowGolemBase == null) {
            this.snowGolemBase = BlockPatternBuilder.start().aisle(" ", "#", "#").where('#', BlockInWorld.hasState(BlockStatePredicate.forBlock(Blocks.SNOW_BLOCK))).build();
        }

        return this.snowGolemBase;
    }

    private BlockPattern getOrCreateSnowGolemFull() {
        if (this.snowGolemFull == null) {
            this.snowGolemFull = BlockPatternBuilder.start().aisle("^", "#", "#").where('^', BlockInWorld.hasState(PUMPKINS_PREDICATE)).where('#', BlockInWorld.hasState(BlockStatePredicate.forBlock(Blocks.SNOW_BLOCK))).build();
        }

        return this.snowGolemFull;
    }

    private BlockPattern getOrCreateIceSnowGolemFull() {
        if (this.iceSnowGolemFull == null) {
            this.iceSnowGolemFull = BlockPatternBuilder.start().aisle("^", "#", "#").where('^', BlockInWorld.hasState(PUMPKINS_PREDICATE)).where('#', BlockInWorld.hasState(BlockStatePredicate.forBlock(Blocks.PACKED_ICE))).build();
        }

        return this.iceSnowGolemFull;
    }

    private BlockPattern getOrCreateIronGolemBase() {
        if (this.ironGolemBase == null) {
            this.ironGolemBase = BlockPatternBuilder.start().aisle("~ ~", "###", "~#~").where('#', BlockInWorld.hasState(BlockStatePredicate.forBlock(Blocks.IRON_BLOCK))).where('~', BlockInWorld.hasState(BlockMaterialPredicate.forMaterial(Material.AIR))).build();
        }

        return this.ironGolemBase;
    }

    private BlockPattern getOrCreateIronGolemFull() {
        if (this.ironGolemFull == null) {
            this.ironGolemFull = BlockPatternBuilder.start().aisle("~^~", "###", "~#~").where('^', BlockInWorld.hasState(PUMPKINS_PREDICATE)).where('#', BlockInWorld.hasState(BlockStatePredicate.forBlock(Blocks.IRON_BLOCK))).where('~', BlockInWorld.hasState(BlockMaterialPredicate.forMaterial(Material.AIR))).build();

        }

        return this.ironGolemFull;
    }

    private BlockPattern getOrCreateModGoldGolemFull() {
        if (this.goldGolemFull == null) {
            this.goldGolemFull = BlockPatternBuilder.start().aisle("~^~", "###", "~#~").where('^', BlockInWorld.hasState(PUMPKINS_PREDICATE)).where('#', BlockInWorld.hasState(BlockStatePredicate.forBlock(Blocks.GOLD_BLOCK))).where('~', BlockInWorld.hasState(BlockMaterialPredicate.forMaterial(Material.AIR))).build();

        }

        return this.goldGolemFull;
    }
}
