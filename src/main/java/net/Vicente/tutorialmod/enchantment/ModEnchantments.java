package net.Vicente.tutorialmod.enchantment;

import net.Vicente.tutorialmod.TutorialMod;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEnchantments {
    public static final DeferredRegister<Enchantment> ENCHANTMENTS =
            DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, TutorialMod.MOD_ID);

    // Register specific enchantment
    public static RegistryObject<Enchantment> BOWABILITIES =
            ENCHANTMENTS.register("bow_abilities",
                    () -> new BowAbilities(Enchantment.Rarity.UNCOMMON,
                            EnchantmentCategory.BOW, EquipmentSlot.MAINHAND));
    public static RegistryObject<Enchantment> SHOVELABILITIES =
            ENCHANTMENTS.register("shovel_abilities",
                    () -> new ShovelAbilities(Enchantment.Rarity.UNCOMMON,
                            EnchantmentCategory.WEAPON, EquipmentSlot.MAINHAND));

    public static void register(IEventBus eventBus) {
        ENCHANTMENTS.register(eventBus);
    }
}
