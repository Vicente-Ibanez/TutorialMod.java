//package net.Vicente.tutorialmod.block.custom;
//
//import net.minecraft.block.BlockState;
//import net.minecraft.core.BlockPos;
//import net.minecraft.core.Direction;
//import net.minecraft.world.entity.Entity;
//import net.minecraft.sound.BlockSoundGroup;
////import net.minecraft.util.math.BlockPos;
////import net.minecraft.util.math.Direction;
//import net.minecraft.world.World;
//import net.minecraft.world.entity.Entity;
//import net.minecraft.world.level.block.Blocks;
//import net.minecraft.world.level.block.HorizontalDirectionalBlock;
//import net.minecraft.world.level.block.state.properties.DirectionProperty;
//import net.minecraft.world.phys.Vec3;
//import net.minecraft.world.level.block.ScaffoldingBlock;
//import net.minecraft.world.level.block.state.BlockBehaviour;
//import net.minecraft.world.level.block.state.BlockState;
//import org.joml.Vector3d;
//
//import static net.minecraft.world.level.block.state.properties.BlockStateProperties.FACING;
//
//public class ClimbingBlock extends ScaffoldingBlock {
//    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
//
//    public ClimbingBlock(BlockBehaviour.Properties settings) {
//        super(settings);
//        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
//    }
//
//    @Override
//    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
//        if (!entity.isOnGround() && entity.isOnGround() && !entity.isCrouching()) {
//            Direction motion = entity.getMotionDirection();
//
//            if (motion.getStepY() <= 0) {
//
//                Direction facing = FACING
//
//
//                if (facing == Direction.NORTH || facing == Direction.SOUTH) {
//                    double height = (pos.getY() + 1) - entity.getY();
//
//                    if (facing == Direction.NORTH && height >= 0 && height <= 1) {
//                        entity.setClimbingPos(pos.up());
//                    } else if (facing == Direction.SOUTH && height >= -1 && height <= 0) {
//                        entity.setClimbingPos(pos.down());
//                    }
//                } else if (facing == Direction.WEST || facing == Direction.EAST) {
//                    double height = (pos.getZ() + 0.5) - entity.getZ();
//
//                    if (facing == Direction.WEST && height >= -0.5 && height <= 0) {
//                        entity.setClimbingPos(pos.down());
//                    } else if (facing == Direction.EAST && height >= 0 && height <= 0.5) {
//                        entity.setClimbingPos(pos.up());
//                    }
//                }
//            }
//        }
//    }
//}