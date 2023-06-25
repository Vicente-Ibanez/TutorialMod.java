package net.Vicente.tutorialmod.entity.custom;

import net.Vicente.tutorialmod.enchantment.ModEnchantments;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.animal.Rabbit;
import net.minecraft.world.entity.animal.Turtle;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ModZombieMiner extends Zombie {
    @Nullable
    private LivingEntity target;
    int tick = 0;
    public ModZombieMiner(EntityType<? extends Zombie> p_34271_, Level p_34272_) {
        super(p_34271_, p_34272_);
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(3, new AvoidEntityGoal<>(this, Rabbit.class, 6.0F, 1.0D, 1.2D));
        this.goalSelector.addGoal(1, new ZombieAttackGoal(this, 1.0D, false));
        this.goalSelector.addGoal(6, new MoveThroughVillageGoal(this, 1.0D, true, 4, this::canBreakDoors));
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.goalSelector.addGoal(3, new ModZombieMine(this));
    }

    public static AttributeSupplier setAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.FOLLOW_RANGE, 160.0D)
                .add(Attributes.MOVEMENT_SPEED, (double)0.34F)
                .add(Attributes.ATTACK_DAMAGE, 4.0D)
                .add(Attributes.ARMOR, 2.0D)
                .add(Attributes.SPAWN_REINFORCEMENTS_CHANCE).build();
    }


//     Changing Weaponry
//     If Zombie has pickaxe, its goal is to mine to the player...?

    protected void populateDefaultEquipmentSlots(RandomSource rnd, DifficultyInstance p_218950_) {
        super.populateDefaultEquipmentSlots(rnd, p_218950_);

        this.setItemSlot(EquipmentSlot.CHEST, new ItemStack(Items.IRON_CHESTPLATE));

            // Spawn with pick and digs towards player
            this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.IRON_PICKAXE));
            this.setItemSlot(EquipmentSlot.HEAD, new ItemStack(Items.DIAMOND_HELMET));
            this.setItemSlot(EquipmentSlot.CHEST, new ItemStack(Items.CHAINMAIL_CHESTPLATE));
            this.setItemSlot(EquipmentSlot.LEGS, new ItemStack(Items.LEATHER_LEGGINGS));
            this.setItemSlot(EquipmentSlot.FEET, new ItemStack(Items.LEATHER_BOOTS));

    }

    protected float getEquipmentDropChance(EquipmentSlot p_21520_) {
        return 0.00F;
    }

    // Goal to mine to player
    static class ModZombieMine extends Goal {

        private final ModZombieMiner modZombie;

        public ModZombieMine(ModZombieMiner p_32585_) {
            this.modZombie = p_32585_;

        }

        public boolean canUse() {
            if (!net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(this.modZombie.level, this.modZombie)) {
                return false;
            } else {
                return this.modZombie.getRandom().nextInt(reducedTickDelay(20)) == 0;
            }
        }

        public void tick() {
            RandomSource randomsource = this.modZombie.getRandom();
            Level level = this.modZombie.level;
            if(this.modZombie.tick%30 == 0 && !(this.modZombie.getTarget() == null) && this.modZombie.getTarget() instanceof Player){
                if(modZombie.tick>10){
                    Player player = (Player) this.modZombie.getTarget();
                    BlockPos blockPos = player.getOnPos();

                    BlockState blockState = this.modZombie.level.getBlockState(blockPos);
                    this.modZombie.getLevel().getServer().getLevel(Level.OVERWORLD).setBlockAndUpdate(blockPos, Blocks.SLIME_BLOCK.defaultBlockState());

                    List<double[]> points = getIntermediatePoints(this.modZombie.getX(), this.modZombie.getY(), this.modZombie.getZ(), player.getX(), player.getY(), player.getZ());

                    // check if block is infront of mob
                    // ADD CODE!
//                    BlockPos.MutableBlockPos blockPosCheck = new BlockPos(this.modZombie.getX(), this.modZombie.getY(), this.modZombie.getZ()).mutable();
//                    Block block = level.getBlockState(blockPosCheck).getBlock();
//
//                    if(!(block == Blocks.AIR)){
//                        System.out.println(block);
////                        placeMine(level, blockPosCheck, points);
//                    }

                }
            }
            ++this.modZombie.tick;
        }


        public List<double[]> getIntermediatePoints(double x1, double y1, double z1, double x2, double y2, double z2) {
            List<double[]> intermediatePoints = new ArrayList<>();

            double deltaX = x2 - x1;
            double deltaY = y2 - y1;
            double deltaZ = z2 - z1;

            double maxDelta = Math.max(Math.abs(deltaX), Math.max(Math.abs(deltaY), Math.abs(deltaZ)));

            double stepX = deltaX / maxDelta;
            double stepY = deltaY > 0 ? 1.0 : -1.0; // Determine the direction of movement for Y coordinate
            double stepZ = deltaZ / maxDelta;

            double currentX = x1;
            double currentY = y1;
            double currentZ = z1;

            for (int i = 0; i < maxDelta; i++) {
                currentX += stepX;
                currentY += stepY;
                currentZ += stepZ;

                intermediatePoints.add(new double[]{currentX, currentY, currentZ});
                BlockPos.MutableBlockPos blockPos = new BlockPos(currentX, currentY, currentZ).mutable();
                this.modZombie.getLevel().getServer().getLevel(Level.OVERWORLD).setBlockAndUpdate(blockPos, Blocks.OAK_PLANKS.defaultBlockState());
//                this.modZombie.getLevel().getServer().getLevel(Level.OVERWORLD).setBlockAndUpdate(blockPos.setX(blockPos.getX()+1), Blocks.OAK_PLANKS.defaultBlockState());
//                this.modZombie.getLevel().getServer().getLevel(Level.OVERWORLD).setBlockAndUpdate(blockPos.setZ(blockPos.getZ()+1), Blocks.OAK_WOOD.defaultBlockState());
//                this.modZombie.getLevel().getServer().getLevel(Level.OVERWORLD).setBlockAndUpdate(blockPos.setX(blockPos.getX()-1), Blocks.OAK_PLANKS.defaultBlockState());
//                this.modZombie.getLevel().getServer().getLevel(Level.OVERWORLD).setBlockAndUpdate(blockPos.setX(blockPos.getX()-1), Blocks.OAK_WOOD.defaultBlockState());
//                this.modZombie.getLevel().getServer().getLevel(Level.OVERWORLD).setBlockAndUpdate(blockPos.setZ(blockPos.getZ()-1), Blocks.OAK_PLANKS.defaultBlockState());
//                this.modZombie.getLevel().getServer().getLevel(Level.OVERWORLD).setBlockAndUpdate(blockPos.setZ(blockPos.getZ()-1), Blocks.OAK_WOOD.defaultBlockState());
//                this.modZombie.getLevel().getServer().getLevel(Level.OVERWORLD).setBlockAndUpdate(blockPos.setX(blockPos.getX()+1), Blocks.OAK_PLANKS.defaultBlockState());
//

            }

            return intermediatePoints;
        }










//                this.modZombie.level.removeBlock(blockPos, false);
//                this.modZombie.level.removeBlock(blockPos.setY(blockPos.getY()+1), false);
//                this.modZombie.level.setBlock(blockPos.setY(blockPos.getY()-2), Blocks.OAK_WOOD.defaultBlockState(), 1);


    }
    @Override
    @Nullable
    public LivingEntity getTarget() {
        return (!(this.level.getNearestPlayer(this, 9216)==null) && !this.level.getNearestPlayer(this, 9216).isInvulnerable()) ? this.level.getNearestPlayer(this, 9216):this.target;
    }

}
