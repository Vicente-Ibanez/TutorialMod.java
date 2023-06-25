package net.Vicente.tutorialmod.event;

import net.Vicente.tutorialmod.TutorialMod;
import net.Vicente.tutorialmod.block.ModBlocks;
import net.Vicente.tutorialmod.block.ModBlocksOriginal;
import net.Vicente.tutorialmod.block.custom.FallingSandBlock;
import net.Vicente.tutorialmod.entity.ModEntityTypes;
import net.Vicente.tutorialmod.entity.custom.*;
import net.Vicente.tutorialmod.night.ModNight;
import net.Vicente.tutorialmod.server.level.ModServerLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Random;

public class ModEvents {


    @Mod.EventBusSubscriber(modid = TutorialMod.MOD_ID)
    public static class ForgeEvents {
        // Prevent Skeleton from taking explosion damage or damage from projectiles
        @SubscribeEvent
        public static void modSkeletonHurt(LivingHurtEvent event) {
            if (event.getEntity() instanceof ModSkeleton) {
                if(event.getSource().getEntity() instanceof Wolf){
                    event.getEntity().addEffect(new MobEffectInstance(MobEffects.HEAL, 4, 1));
                }
            }
        }

        /**
         * FARMING UPDATE CODE
         *
        **/
        @SubscribeEvent
        public static void onCropHarvest(PlayerEvent.HarvestCheck event) {
            if (event.getTargetBlock().getBlock() instanceof CropBlock) { // Check if the block is a crop block
                if (event.getEntity().getMainHandItem().getItem() instanceof HoeItem) { // Check if the player's main-hand item is a hoe
                    // Allow the crop to drop its items
                    event.setCanHarvest(true);
                } else {
                    // Prevent the crop from dropping its items
                    event.setCanHarvest(false);
                }
            }
        }

        @SubscribeEvent
        public static void onPlayerWalk(LivingEvent.LivingTickEvent event) {
            if (!(event.getEntity() instanceof Player))
                return;
            Random RANDOM = new Random();
            int UNTILL_CHANCE = 100; // 1 in 5 chance of untilling

            Player player = (Player) event.getEntity();
            Level world = player.getLevel();
            BlockPos pos = player.getOnPos();
            BlockState state = world.getBlockState(pos);

            if (state.getBlock() instanceof FarmBlock && !player.isCrouching()) {
                // Untill the soil by replacing it with dirt with 1/5 chance
                if (RANDOM.nextInt(UNTILL_CHANCE) == 0) {
//                    world.setBlockAndUpdate(pos, Blocks.DIRT.defaultBlockState()); // more glitchy
                    world.setBlock(pos, Blocks.DIRT.defaultBlockState(), 11);
                }
            }
        }

        /**
         * MINING UPDATE CODE
         *
         **/


        // Function to check if the block should fall when placed
        @SubscribeEvent
        public static void onBlockPlace(BlockEvent.EntityPlaceEvent event) {
            if (event.getEntity() != null && event.getEntity().getLevel() != null && !event.getEntity().getLevel().isClientSide) {
                Block block = event.getPlacedBlock().getBlock();

                // certain blocks don't fall
                if(!(block instanceof BaseEntityBlock) && !(block instanceof TntBlock) && !(block == Blocks.OBSIDIAN|| block == Blocks.IRON_BLOCK || block == Blocks.GOLD_BLOCK || block == Blocks.EMERALD_BLOCK || block == Blocks.GOLD_BLOCK || block == Blocks.DIAMOND_BLOCK || block == Blocks.REDSTONE_BLOCK)){
                    // check blocks near it
                        // Blocks that hold up other blocks
                        // wood logs: neighbors
                        // iron: 2 blocks away
                        // gold: 5 blocks away
                        // diamond: 10 blocks away
                        // emerald: 25 blocks away
                    // Check adjacent positions for iron blocks
                    Boolean holderFound = false;
                    for (Direction direction : Direction.values()) {
                        BlockPos adjacentPos = event.getPos().offset(direction.getNormal());
                        BlockState adjacentBlockState = event.getLevel().getBlockState(adjacentPos);
                        Block adjacentBlock = adjacentBlockState.getBlock();

                        // Check if adjacent block is iron block
                        if ((adjacentBlock == Blocks.OAK_LOG || adjacentBlock == Blocks.BIRCH_LOG ||
                                adjacentBlock == Blocks.SPRUCE_LOG || adjacentBlock == Blocks.JUNGLE_LOG ||
                                adjacentBlock == Blocks.ACACIA_LOG || adjacentBlock == Blocks.DARK_OAK_LOG ||
                                adjacentBlock == Blocks.CRIMSON_STEM || adjacentBlock == Blocks.WARPED_STEM)) {
                            holderFound = true;
                        }
                    }

                    for (int x = -2; x <= 2; x++) {
                        for (int y = -2; y <= 2; y++) {
                            for (int z = -2; z <= 2; z++) {
                                BlockPos nearbyBlockPos = event.getPos().offset(x, y, z);

                                // Check if the nearby block is an iron block
                                if (event.getLevel().getBlockState(nearbyBlockPos).getBlock() == Blocks.IRON_BLOCK) {
                                    // Do something when an iron block is found
                                    holderFound = true;
                                    return;
                                }
                            }
                        }
                    }

                    for (int x = -6; x <= 6; x++) {
                        for (int y = -6; y <= 6; y++) {
                            for (int z = -6; z <= 6; z++) {
                                BlockPos nearbyBlockPos = event.getPos().offset(x, y, z);

                                // Check if the nearby block is an iron block
                                if (event.getLevel().getBlockState(nearbyBlockPos).getBlock() == Blocks.GOLD_BLOCK) {
                                    // Do something when an iron block is found
                                    holderFound = true;
                                    return;
                                }
                            }
                        }
                    }

                    for (int x = -13; x <= 13; x++) {
                        for (int y = -13; y <= 13; y++) {
                            for (int z = -13; z <= 13; z++) {
                                BlockPos nearbyBlockPos = event.getPos().offset(x, y, z);

                                // Check if the nearby block is an iron block
                                if (event.getLevel().getBlockState(nearbyBlockPos).getBlock() == Blocks.DIAMOND_BLOCK) {
                                    // Do something when an iron block is found
                                    holderFound = true;
                                    return;
                                }
                            }
                        }
                    }

                    for (int x = -25; x <= 25; x++) {
                        for (int y = -25; y <= 25; y++) {
                            for (int z = -25; z <= 25; z++) {
                                BlockPos nearbyBlockPos = event.getPos().offset(x, y, z);

                                // Check if the nearby block is an iron block
                                if (event.getLevel().getBlockState(nearbyBlockPos).getBlock() == Blocks.EMERALD_BLOCK) {
                                    // Do something when an iron block is found
                                    holderFound = true;
                                    return;
                                }
                            }
                        }
                    }



                    if(!holderFound){
                        BlockPos blockPos = event.getPos();
                        BlockState blockState = event.getEntity().getLevel().getBlockState(blockPos);

                        // Check if the placed block is solid
                        if (!blockState.isAir()) {
                            BlockPos downPos = blockPos.below();

                            // Move the block downwards until it hits another block
                            while (event.getEntity().getLevel().getBlockState(downPos).isAir() && downPos.getY() > -64) {
                                event.getEntity().getLevel().setBlock(blockPos, Blocks.AIR.defaultBlockState(), 3);
                                event.getEntity().getLevel().setBlock(downPos, blockState, 3);
                                blockPos = downPos;
                                downPos = downPos.below();
                            }
                        }
                    }
                }

            }
        }

        // function to check if the surrounding blocks should fall when a block breaks
        @SubscribeEvent
        public static void onBlockBreak(BlockEvent.BreakEvent event) {
            if(!event.getLevel().isClientSide()){
                BlockState brokenBlockState = event.getState();
                System.out.println("TEST1");

                // Check if the broken block is an iron block
                if (brokenBlockState.getBlock() == Blocks.IRON_BLOCK) {
                    Level world = (Level) event.getLevel();
                    BlockPos brokenBlockPos = event.getPos();

                    // Loop through a 2-block radius around the broken block
                    for (int x = -2; x <= 2; x++) {
                        for (int y = -2; y <= 2; y++) {
                            for (int z = -2; z <= 2; z++) {
                                System.out.println("Checking locations");
                                BlockPos nearbyBlockPos = brokenBlockPos.offset(x, y, z);
                                BlockPos nearbyBlockPosDown = brokenBlockPos.offset(x, y-1, z);

                                // Check if the nearby block is floating (air or non-solid)
                                BlockState blockState = event.getLevel().getBlockState(nearbyBlockPosDown);


                                if (blockState.isAir() || !blockState.getMaterial().isSolid()){
                                    System.out.println("Block underneath is air or not solid");
                                    // Set the nearby block to fall
                                    fallBlock(world, nearbyBlockPos, blockState);
                                }
                            }
                        }
                    }
                }
            }

        }
        private static void fallBlock(Level world, BlockPos pos, BlockState state) {
            BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos(pos.getX(), pos.getY(), pos.getZ());

            // Move the block down until it hits a solid block or reaches the bottom
            while (mutablePos.getY() > -64 && (world.getBlockState(mutablePos.below()).isAir() || !world.getBlockState(mutablePos.below()).getMaterial().isSolid())) {
                mutablePos.move(0, -1, 0);
            }

            System.out.println("Placing block on the ground!");
            // Place the block on the first solid block below
//            world.setBlock(mutablePos, state, 3);
            world.setBlock(mutablePos, Blocks.GOLD_BLOCK.defaultBlockState(), 3);
            world.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);





//            BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos(pos.getX(), pos.getY(), pos.getZ());
//            System.out.println("fallblock");

//            // Move the block down to the ground
//            world.getHeight(Heightmap.Types.WORLD_SURFACE, pos.getX(), pos.getZ());
//
//            while (mutablePos.getY() > -64 && (world.getBlockState(mutablePos.below()).isAir() || !world.getBlockState(mutablePos.below()).getMaterial().isSolid())) {
//                System.out.println("Block Falling");
//                world.setBlock(mutablePos, Blocks.AIR.defaultBlockState(), 3);
//                world.setBlock(mutablePos.below(), state, 3);
//                mutablePos.move(0, -1, 0);
//            }
        }




        @SubscribeEvent
        public static void nightStart(TickEvent.PlayerTickEvent event) {
            // Problems Right Now:
            // Need to be near village for it to technically start
            // do I need bad omen: No? So remove thoses features
            // Need to move Mod Night as player moves


            if(event.player.getLevel().getSkyDarken() > 7 && event.player instanceof Player && !(event.player.getLevel().isClientSide())) {
                // Create ModServerLevel Object
                ModServerLevel modServerLevel = new ModServerLevel(event.player.getLevel().getServer().getLevel(Level.OVERWORLD));
                // Check if there is a ModNight nearby
                ModNight modNight = modServerLevel.getModNights().getNearbyModNight(event.player.getOnPos(), 9216, event.player);
                if(modNight == null){
                    // Create ModNight
//                    ModNight modNightCreate = modServerLevel.getModNights().createOrExtendModNight((ServerPlayer) event.player);
                    modServerLevel.getModNights().createOrExtendModNight(event.player);

                } else if (!(modNight==null)) {
                    modServerLevel.tick();
                }
            } else if (event.player.getLevel().getSkyDarken() < 7 && !(event.player.getLevel().isClientSide()) && event.player instanceof Player) {
                ModServerLevel modServerLevel = new ModServerLevel(event.player.getLevel().getServer().getLevel(Level.OVERWORLD));
                // Check if there is a ModNight nearby
                ModNight modNight = modServerLevel.getModNights().getNearbyModNight(event.player.getOnPos(), 9216, event.player);
                if(!(modNight == null) ){
                    modNight.stop();
                }
            }
            // have modServerLevel get foodrot and have it tick the event




        }

//        @SubscribeEvent
//        public static void chestLimitations(PlayerContainerEvent playerContainerEvent) {
////            playerContainerEvent.getEntity();
////            System.out.println(playerContainerEvent.getContainer().getCarried());
//        }

    }





    @Mod.EventBusSubscriber(modid = TutorialMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ModEventBusEvents {
        @SubscribeEvent
        public static void entityAttributeEvent(EntityAttributeCreationEvent event) {
            event.put(ModEntityTypes.MODSKELETON.get(), ModSkeleton.setAttributes());
            event.put(ModEntityTypes.MODZOMBIE.get(), ModZombie.setAttributes());
            event.put(ModEntityTypes.MODZOMBIEMINER.get(), ModZombieMiner.setAttributes());
            event.put(ModEntityTypes.MODCREEPER.get(), ModCreeper.setAttributes());
            event.put(ModEntityTypes.MODSNOWGOLEM.get(), ModSnowGolem.setAttributes());

            event.put(ModEntityTypes.MODICEGOLEM.get(), ModIceGolem.setAttributes());
            event.put(ModEntityTypes.MODPHANTOM.get(), ModPhantom.setAttributes());
            event.put(ModEntityTypes.MODWITCH.get(), ModWitch.setAttributes());
            event.put(ModEntityTypes.MODGOLDGOLEM.get(), ModGoldGolem.setAttributes());
            event.put(ModEntityTypes.MODIRONGOLEM.get(), ModGoldGolem.setAttributes());
        }
    }





}
