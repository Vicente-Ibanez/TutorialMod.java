package net.Vicente.tutorialmod.enchantment;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class BowAbilities extends Enchantment {


    protected BowAbilities(Rarity pRarity, EnchantmentCategory pCategory, EquipmentSlot... pApplicableSlots) {
        super(pRarity, pCategory, pApplicableSlots);
    }

    @Override
    public void doPostAttack(LivingEntity pAttacker, Entity pTarget, int pLevel) {
        if(!pAttacker.level.isClientSide()){
            ServerLevel world = ((ServerLevel) pAttacker.level);
            BlockPos position = pTarget.blockPosition();

            if(pLevel == 1){
                // spawn silverfish that attack nearby enemy
                EntityType.SILVERFISH.spawn(world, (ItemStack) null, null, position,
                        MobSpawnType.TRIGGERED, true, true);
            } else if (pLevel == 2) {
                //
//                pAttacker.get // viewvector or Xrot,yrot,zrot
                EntityType.TNT.spawn(world, (ItemStack) null, null, position,
                        MobSpawnType.TRIGGERED, true, true);
            } else if (pLevel == 3){
                // Fireball! spawn lightning down on spot
                EntityType.LIGHTNING_BOLT.spawn(world, (ItemStack) null, null, position,
                        MobSpawnType.TRIGGERED, true, true);
            }
        }
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }
}