package net.Vicente.tutorialmod.enchantment;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class ShovelAbilities extends Enchantment {


    protected ShovelAbilities(Rarity pRarity, EnchantmentCategory pCategory, EquipmentSlot... pApplicableSlots) {
        super(pRarity, pCategory, pApplicableSlots);
    }

    @Override
    public void doPostAttack(LivingEntity pAttacker, Entity pTarget, int pLevel) {
        if(!pAttacker.level.isClientSide()){
            ServerLevel world = ((ServerLevel) pAttacker.level);
            BlockPos position = pAttacker.blockPosition();

            if(pLevel == 1){
                // spawn zombie
                EntityType.ZOMBIE.spawn(world, (ItemStack) null, null, position,
                        MobSpawnType.TRIGGERED, true, true);
            } else if (pLevel == 2) {
                // Spawn Lightning
                EntityType.PIGLIN.spawn(world, (ItemStack) null, null, position,
                        MobSpawnType.TRIGGERED, true, true);
            } else if (pLevel == 3){
                //
                if(pTarget.isAlive()){
                    ((LivingEntity)pTarget).addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 2, 1));
                }
            }
        }
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }
}
