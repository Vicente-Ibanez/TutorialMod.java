package net.Vicente.tutorialmod.item.modItems;

import net.minecraft.core.Holder;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.Instrument;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.Optional;

public class IceArrowItem extends ArrowItem {
    public final float damage;

    public IceArrowItem(Properties properties, float damage){
        super(properties);
        this.damage = damage;
    }

    @Override
    public AbstractArrow createArrow(Level p_40513_, ItemStack p_40514_, LivingEntity p_40515_) {
        Arrow arrow = new Arrow(p_40513_, p_40515_);
        arrow.setBaseDamage(this.damage);
        return arrow;
    }

    @Override
    public boolean isInfinite(ItemStack stack, ItemStack bow, net.minecraft.world.entity.player.Player player) {
        int enchant = net.minecraft.world.item.enchantment.EnchantmentHelper.getItemEnchantmentLevel(net.minecraft.world.item.enchantment.Enchantments.INFINITY_ARROWS, bow);
        return enchant <= 0 ? false : this.getClass() == IceArrowItem.class;
    }

//    public InteractionResultHolder<ItemStack> use(Level p_220123_, Player p_220124_, InteractionHand p_220125_) {
//        ItemStack itemstack = p_220124_.getItemInHand(p_220125_);
//        p_220124_.startUsingItem(p_220125_);
//        p_220124_.getCooldowns().addCooldown(this, 30);
//        System.out.println("TEST_____");
//        return InteractionResultHolder.consume(itemstack);
//
//    }
}