package net.Vicente.tutorialmod.entity;

import net.Vicente.tutorialmod.TutorialMod;
import net.Vicente.tutorialmod.entity.custom.*;
import net.Vicente.tutorialmod.entity.custom.IceArrow;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.monster.Phantom;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

//public class ModEntityTypes<T extends EntityType> {
public class ModEntityTypes{
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, TutorialMod.MOD_ID);

    public static final RegistryObject<EntityType<IceArrow>> ICE_ARROW =
            ENTITY_TYPES.register("ice_arrow",
                    () -> EntityType.Builder.<IceArrow>of(IceArrow::new, MobCategory.MISC)
                            .sized(0.5F,0.5F)
                            .clientTrackingRange(4)
                            .updateInterval(20)
                            .build(new ResourceLocation(TutorialMod.MOD_ID, "ice_arrow").toString()));

    public static final RegistryObject<EntityType<ModSkeleton>> MODSKELETON =
            ENTITY_TYPES.register("modskeleton",
                    () -> EntityType.Builder.of(ModSkeleton::new, MobCategory.MONSTER)
                            .sized(0.4f, 1.5f)
                            .build(new ResourceLocation(TutorialMod.MOD_ID, "modskeleton").toString()));
    public static final RegistryObject<EntityType<ModWitch>> MODWITCH =
            ENTITY_TYPES.register("modwitch",
                    () -> EntityType.Builder.of(ModWitch::new, MobCategory.MONSTER)
                            .sized(0.4f, 1.5f)
                            .build(new ResourceLocation(TutorialMod.MOD_ID, "modwitch").toString()));

    public static final RegistryObject<EntityType<ModPhantom>> MODPHANTOM =
            ENTITY_TYPES.register("modphantom",
                    () -> EntityType.Builder.of(ModPhantom::new, MobCategory.MONSTER)
                            .sized(0.8f, 2.5f)
                            .build(new ResourceLocation(TutorialMod.MOD_ID, "modphantom").toString()));

    public static final RegistryObject<EntityType<ModZombie>> MODZOMBIE =
            ENTITY_TYPES.register("modzombie",
                    () -> EntityType.Builder.of(ModZombie::new, MobCategory.MONSTER)
                            .sized(0.4f, 1.5f)
                            .build(new ResourceLocation(TutorialMod.MOD_ID, "modzombie").toString()));

    public static final RegistryObject<EntityType<ModZombieMiner>> MODZOMBIEMINER =
            ENTITY_TYPES.register("modzombieminer",
                    () -> EntityType.Builder.of(ModZombieMiner::new, MobCategory.MONSTER)
                            .sized(0.4f, 1.5f)
                            .build(new ResourceLocation(TutorialMod.MOD_ID, "modzombieminer").toString()));

    public static final RegistryObject<EntityType<ModCreeper>> MODCREEPER =
            ENTITY_TYPES.register("modcreeper",
                    () -> EntityType.Builder.of(ModCreeper::new, MobCategory.MONSTER)
                            .sized(0.4f, 1.5f)
                            .build(new ResourceLocation(TutorialMod.MOD_ID, "modcreeper").toString()));

    public static final RegistryObject<EntityType<ModSnowGolem>> MODSNOWGOLEM =
            ENTITY_TYPES.register("modsnowgolem",
                    () -> EntityType.Builder.of(ModSnowGolem::new, MobCategory.CREATURE)
                            .sized(0.4f, 1.5f)
                            .build(new ResourceLocation(TutorialMod.MOD_ID, "modsnowgolem").toString()));

    public static final RegistryObject<EntityType<ModIceGolem>> MODICEGOLEM =
            ENTITY_TYPES.register("modicegolem",
                    () -> EntityType.Builder.of(ModIceGolem::new, MobCategory.CREATURE)
                            .sized(0.4f, 1.5f)
                            .build(new ResourceLocation(TutorialMod.MOD_ID, "modicegolem").toString()));

    public static final RegistryObject<EntityType<ModGoldGolem>> MODGOLDGOLEM =
            ENTITY_TYPES.register("modgoldgolem",
                    () -> EntityType.Builder.of(ModGoldGolem::new, MobCategory.CREATURE)
                            .sized(1.4F, 2.7F)
                            .build(new ResourceLocation(TutorialMod.MOD_ID, "modgoldgolem").toString()));

    public static final RegistryObject<EntityType<ModIronGolem>> MODIRONGOLEM =
            ENTITY_TYPES.register("modirongolem",
                    () -> EntityType.Builder.of(ModIronGolem::new, MobCategory.CREATURE)
                            .sized(1.4F, 2.7F)
                            .build(new ResourceLocation(TutorialMod.MOD_ID, "modirongolem").toString()));


    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }


}















