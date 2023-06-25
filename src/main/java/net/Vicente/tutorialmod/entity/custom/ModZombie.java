package net.Vicente.tutorialmod.entity.custom;

import net.Vicente.tutorialmod.enchantment.ModEnchantments;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WallClimberNavigation;
import net.minecraft.world.entity.animal.*;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;

import java.util.Optional;

public class ModZombie extends Zombie {
    private static final EntityDataAccessor<Byte> DATA_FLAGS_ID = SynchedEntityData.defineId(ModZombie.class, EntityDataSerializers.BYTE);
    public ModZombie(EntityType<? extends Zombie> p_34271_, Level p_34272_) {
        super(p_34271_, p_34272_);
    }

    protected void registerGoals() {
        this.goalSelector.addGoal(3, new AvoidEntityGoal<>(this, Rabbit.class, 6.0F, 1.0D, 1.2D));
        this.goalSelector.addGoal(1, new ZombieAttackGoal(this, 1.0D, false));
        this.goalSelector.addGoal(6, new MoveThroughVillageGoal(this, 1.0D, true, 4, this::canBreakDoors));
        this.goalSelector.addGoal(7, new WaterAvoidingRandomStrollGoal(this, 1.0D));
//        this.targetSelector.addGoal(1, (new HurtByTargetGoal(this)).setAlertOthers(ZombifiedPiglin.class));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, AbstractVillager.class, false));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, ModSnowGolem.class, false));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolem.class, true));
        this.targetSelector.addGoal(5, new NearestAttackableTargetGoal<>(this, Turtle.class, 10, true, false, Turtle.BABY_ON_LAND_SELECTOR));
        this.goalSelector.addGoal(3, new ModZombieMine(this));
    }

    public static AttributeSupplier setAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.FOLLOW_RANGE, 150.0D)
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

        if(rnd.nextFloat() < .25){
            // Spawn with nothing else
            ItemStack shovel = new ItemStack(Items.WOODEN_AXE);
            shovel.enchant(ModEnchantments.SHOVELABILITIES.get(), 3);
            this.setItemSlot(EquipmentSlot.MAINHAND, shovel);
        }
        else if (rnd.nextFloat() < .50){
            // Spawn with iron shovel that raises zombies
            ItemStack shovel = new ItemStack(Items.IRON_SHOVEL);
            shovel.enchant(ModEnchantments.SHOVELABILITIES.get(), 1);
            this.setItemSlot(EquipmentSlot.MAINHAND, shovel);
            this.setItemSlot(EquipmentSlot.HEAD, new ItemStack(Items.LEATHER_HELMET));
            this.setItemSlot(EquipmentSlot.LEGS, new ItemStack(Items.LEATHER_LEGGINGS));
            this.setItemSlot(EquipmentSlot.FEET, new ItemStack(Items.LEATHER_BOOTS));

        }else if (rnd.nextFloat() < .75){
            // Spawn with pick and digs towards player
            this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.IRON_PICKAXE));
            this.setItemSlot(EquipmentSlot.HEAD, new ItemStack(Items.DIAMOND_HELMET));
            this.setItemSlot(EquipmentSlot.FEET, new ItemStack(Items.LEATHER_BOOTS));

        }else{
            // Spawn with Slowness Sword that helps raise zombies
            ItemStack shovel = new ItemStack(Items.GOLDEN_SHOVEL);
            shovel.enchant(ModEnchantments.SHOVELABILITIES.get(), 2);
            this.setItemSlot(EquipmentSlot.MAINHAND, shovel);
            this.setItemSlot(EquipmentSlot.HEAD, new ItemStack(Items.GOLDEN_HELMET));
            this.setItemSlot(EquipmentSlot.CHEST, new ItemStack(Items.GOLDEN_CHESTPLATE));
            this.setItemSlot(EquipmentSlot.LEGS, new ItemStack(Items.GOLDEN_LEGGINGS));
            this.setItemSlot(EquipmentSlot.FEET, new ItemStack(Items.GOLDEN_BOOTS));
        }

    }

    protected float getEquipmentDropChance(EquipmentSlot p_21520_) {
        return 0.00F;
    }

    // Goal to mine to player
    static class ModZombieMine extends Goal {
        private final ModZombie modZombie;

        public ModZombieMine(ModZombie p_32585_) {
            this.modZombie = p_32585_;
        }

        public boolean canUse() {
            if (!net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(this.modZombie.level, this.modZombie)) {
                return false;
            } else {
//                return this.modZombie.getRandom().nextInt(reducedTickDelay(20)) == 0;
                return true;
            }
        }

        public void tick() {
            Level level = this.modZombie.level;
            int i = Mth.floor(this.modZombie.getX());
            int j = Mth.floor(this.modZombie.getY());
            int k = Mth.floor(this.modZombie.getZ());

            // Destory 2 blocks at once
            BlockPos blockpos_1 = new BlockPos(i, j+1, k);
            BlockPos blockpos_2 = new BlockPos(i, j+2, k);
            BlockState blockstate = level.getBlockState(blockpos_1);

//            Vec3 vec3 = new Vec3((double)this.modZombie.getBlockX() + 0.5D, (double)j + 0.5D, (double)this.modZombie.getBlockZ() + 0.5D);
//            Vec3 vec31 = new Vec3((double)i + 0.5D, (double)j + 0.5D, (double)k + 0.5D);
//            BlockHitResult blockhitresult = level.clip(new ClipContext(vec3, vec31, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, this.modZombie));
//            boolean flag = blockhitresult.getBlockPos().equals(blockpos_1);
            //if (blockstate.is(BlockTags.ENDERMAN_HOLDABLE) && flag)
//            if(flag){
            level.removeBlock(blockpos_1, false);
            level.removeBlock(blockpos_2, false);
            level.gameEvent(GameEvent.BLOCK_DESTROY, blockpos_1, GameEvent.Context.of(this.modZombie, blockstate));

//            }

        }
    }
    public void tick() {
        super.tick();
        if (!this.level.isClientSide) {
            this.setClimbing(this.horizontalCollision);
        }

    }

    public void setClimbing(boolean p_33820_) {
        byte b0 = this.entityData.get(DATA_FLAGS_ID);
        if (p_33820_) {
            b0 = (byte)(b0 | 1);
        } else {
            b0 = (byte)(b0 & -2);
        }

        this.entityData.set(DATA_FLAGS_ID, b0);
    }

    public boolean onClimbable() {
        return this.isClimbing();
    }

    public boolean isClimbing() {
        return (this.entityData.get(DATA_FLAGS_ID) & 1) != 0;
    }

    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_FLAGS_ID, (byte)0);
    }

    protected PathNavigation createNavigation(Level p_33802_) {
        return new WallClimberNavigation(this, p_33802_);
    }



}
