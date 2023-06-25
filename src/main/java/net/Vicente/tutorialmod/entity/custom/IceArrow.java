package net.Vicente.tutorialmod.entity.custom;

import net.Vicente.tutorialmod.entity.ModEntityTypes;
import net.Vicente.tutorialmod.item.ModItems;
import net.minecraft.commands.arguments.item.ItemInput;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemNameBlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class IceArrow extends AbstractArrow {
    private final Item referenceItem;
    private int life;

    public IceArrow(EntityType<? extends AbstractArrow> type, Level level) {
        super(type, level);
        this.referenceItem = ModItems.ICE_ARROW.get();

    }

    public IceArrow(LivingEntity shooter, Level level, Item referenceItem) {
        super(ModEntityTypes.ICE_ARROW.get(), shooter, level);
        this.referenceItem = referenceItem;
    }

    @Override
    public ItemStack getPickupItem() {
        return new ItemStack(this.referenceItem);
    }

    @Override
    protected void tickDespawn() {
        ++this.life;
        if (this.life >= 20) {
            this.discard();
        }

    }

    protected void doPostHurtEffects(LivingEntity entity) {
        super.doPostHurtEffects(entity);
        MobEffectInstance mobeffectinstance = new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 25, 0);
        entity.addEffect(mobeffectinstance, this.getEffectSource());
    }

}
