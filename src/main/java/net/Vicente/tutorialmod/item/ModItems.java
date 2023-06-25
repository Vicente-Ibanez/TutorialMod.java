package net.Vicente.tutorialmod.item;

import net.Vicente.tutorialmod.TutorialMod;
import net.Vicente.tutorialmod.entity.ModEntityTypes;
import net.Vicente.tutorialmod.item.modItems.IceArrowItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    // Register Items
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, TutorialMod.MOD_ID);

    // Aleady in game, but not in creative tab, also no texture or name
    // To add to a creative mode tab, go to tutorialmod.java
    // To add texture, go to resources --> Asset
    public static final RegistryObject<Item> BLACK_OPAL = ITEMS.register("black_opal",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> ICE_ARROW = ITEMS.register("ice_arrow",
            ()-> new IceArrowItem(new Item.Properties().stacksTo(64), 1.5F));


    public static final RegistryObject<ForgeSpawnEggItem> MOD_SKELETON_EGG = ITEMS.register("mod_skeleton_egg",
            ()-> new ForgeSpawnEggItem(ModEntityTypes.MODSKELETON, 0x22b341, 0x19732e,
                    new Item.Properties().stacksTo(64)));
    public static final RegistryObject<ForgeSpawnEggItem> MOD_ZOMBIE_EGG = ITEMS.register("mod_zombie_egg",
            ()-> new ForgeSpawnEggItem(ModEntityTypes.MODZOMBIE, 0x22b341, 0x19732e,
                    new Item.Properties().stacksTo(64 )));
    public static final RegistryObject<ForgeSpawnEggItem> MOD_ZOMBIE_MINER_EGG = ITEMS.register("mod_zombie_miner_egg",
            ()-> new ForgeSpawnEggItem(ModEntityTypes.MODZOMBIEMINER, 0x22b341, 0x19732e,
                    new Item.Properties().stacksTo(64 )));

    public static final RegistryObject<ForgeSpawnEggItem> MOD_CREEPER_EGG = ITEMS.register("mod_creeper_egg",
            ()-> new ForgeSpawnEggItem(ModEntityTypes.MODCREEPER, 0x22b341, 0x19732e,
                    new Item.Properties().stacksTo(64 )));

    public static final RegistryObject<ForgeSpawnEggItem> MOD_SNOW_GOLEM_EGG = ITEMS.register("mod_snow_golem_egg",
            ()-> new ForgeSpawnEggItem(ModEntityTypes.MODSNOWGOLEM, 0x22b341, 0x19732e,
                    new Item.Properties().stacksTo(64 )));

    public static final RegistryObject<ForgeSpawnEggItem> MOD_PHANTOM_EGG = ITEMS.register("mod_phantom_egg",
            ()-> new ForgeSpawnEggItem(ModEntityTypes.MODPHANTOM, 0x22b341, 0x19732e,
                    new Item.Properties().stacksTo(64 )));

    public static final RegistryObject<ForgeSpawnEggItem> MOD_WITCH_EGG = ITEMS.register("mod_witch_egg",
            ()-> new ForgeSpawnEggItem(ModEntityTypes.MODWITCH, 0x22b341, 0x19732e,
                    new Item.Properties().stacksTo(64 )));

    public static final RegistryObject<ForgeSpawnEggItem> MOD_GOLD_GOLEM_EGG = ITEMS.register("mod_gold_golem_egg",
            ()-> new ForgeSpawnEggItem(ModEntityTypes.MODGOLDGOLEM, 0x22b341, 0x19732e,
                    new Item.Properties().stacksTo(64 )));
    public static final RegistryObject<ForgeSpawnEggItem> MOD_IRON_GOLEM_EGG = ITEMS.register("mod_iron_golem_egg",
            ()-> new ForgeSpawnEggItem(ModEntityTypes.MODIRONGOLEM, 0x22b341, 0x19732e,
                    new Item.Properties().stacksTo(64 )));

    // Register items in the event bus
    public static void register(IEventBus eventBus){
        ITEMS.register(eventBus);
    }
}
