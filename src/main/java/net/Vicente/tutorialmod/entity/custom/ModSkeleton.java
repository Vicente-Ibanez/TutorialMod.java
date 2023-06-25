package net.Vicente.tutorialmod.entity.custom;

import net.Vicente.tutorialmod.enchantment.ModEnchantments;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.animal.SnowGolem;
import net.minecraft.world.entity.animal.Turtle;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.LargeFireball;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public class ModSkeleton extends AbstractSkeleton {

    public ModSkeleton(EntityType<? extends AbstractSkeleton> p_32133_, Level p_32134_) {
        super(p_32133_, p_32134_);
    }


    protected void registerGoals() {
        this.goalSelector.addGoal(2, new RestrictSunGoal(this));
        this.goalSelector.addGoal(3, new FleeSunGoal(this, 1.0D));
        this.goalSelector.addGoal(3, new AvoidEntityGoal<>(this, Wolf.class, 6.0F, 1.0D, 1.2D));
        this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
//        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));

        this.goalSelector.addGoal(2, new RangedBowAttackGoal<ModSkeleton>(this, 4.0D, 10, 15.0F));

        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, IronGolem.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, SnowGolem.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Turtle.class, 10, true, false, Turtle.BABY_ON_LAND_SELECTOR));
    }

    public static AttributeSupplier setAttributes(){
        return AbstractSkeleton.createAttributes()
                .add(Attributes.MAX_HEALTH, 20.0D)
                .add(Attributes.ATTACK_DAMAGE, 3.0f)
                .add(Attributes.ATTACK_SPEED, 1.0f)
                .add(Attributes.FOLLOW_RANGE, 150)
                .add(Attributes.MOVEMENT_SPEED, 0.3f).build();
    }

    protected boolean shouldDropLoot() {
        return false;
    }

    protected SoundEvent getAmbientSound() {
        return SoundEvents.SKELETON_AMBIENT;
    }

    protected SoundEvent getHurtSound(DamageSource p_33579_) {
        return SoundEvents.SKELETON_HURT;
    }
    protected SoundEvent getDeathSound() {
        return SoundEvents.SKELETON_DEATH;
    }
    @Override
    protected SoundEvent getStepSound() {
        {return SoundEvents.SKELETON_STEP;}
    }

    // Changing Weaponry

    protected void populateDefaultEquipmentSlots(RandomSource rnd, DifficultyInstance p_218950_) {
        super.populateDefaultEquipmentSlots(rnd, p_218950_);

        this.setItemSlot(EquipmentSlot.LEGS, new ItemStack(Items.CHAINMAIL_LEGGINGS));
        this.setItemSlot(EquipmentSlot.FEET, new ItemStack(Items.CHAINMAIL_BOOTS));

        if(rnd.nextFloat() < .25){
            // Spawn with regular bow (25%)
            this.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.BOW));

        }else if (rnd.nextFloat() < .50){
            // Spawn with lighting? bow (25%)
            ItemStack bow = new ItemStack(Items.BOW);
            bow.enchant(ModEnchantments.BOWABILITIES.get(), 1);
            this.setItemSlot(EquipmentSlot.MAINHAND, bow);
            this.setItemSlot(EquipmentSlot.HEAD, new ItemStack(Items.LEATHER_HELMET));
            this.setItemSlot(EquipmentSlot.CHEST, new ItemStack(Items.LEATHER_CHESTPLATE));

        }else if (rnd.nextFloat() < .75){
            // Spawn with silverfish bow (25%)
            ItemStack bow = new ItemStack(Items.BOW);
            bow.enchant(ModEnchantments.BOWABILITIES.get(), 2);
            this.setItemSlot(EquipmentSlot.MAINHAND, bow);
            this.setItemSlot(EquipmentSlot.HEAD, new ItemStack(Items.CHAINMAIL_HELMET));
            this.setItemSlot(EquipmentSlot.CHEST, new ItemStack(Items.CHAINMAIL_CHESTPLATE));

        }else{
            // Spawn with flame bow (25%)
            ItemStack bow = new ItemStack(Items.BOW);
            bow.enchant(ModEnchantments.BOWABILITIES.get(), 3);
            this.setItemSlot(EquipmentSlot.MAINHAND, bow);
            this.setItemSlot(EquipmentSlot.HEAD, new ItemStack(Items.GOLDEN_HELMET));
            this.setItemSlot(EquipmentSlot.CHEST, new ItemStack(Items.GOLDEN_CHESTPLATE));
        }

    }

    protected float getEquipmentDropChance(EquipmentSlot p_21520_) {
        return 0.00F;
    }

    public boolean isInvulnerableTo(DamageSource p_238289_) {
        return explosionImmune(p_238289_) | super.isInvulnerableTo(p_238289_);
    }

    public static boolean explosionImmune(DamageSource damageSource) {
        return damageSource.isExplosion() | damageSource.isProjectile();
    }


}
