package net.Vicente.tutorialmod;

import com.mojang.logging.LogUtils;
import net.Vicente.tutorialmod.block.ModBlocks;
import net.Vicente.tutorialmod.block.ModBlocksOriginal;
import net.Vicente.tutorialmod.enchantment.ModEnchantments;
import net.Vicente.tutorialmod.entity.ModEntityTypes;
import net.Vicente.tutorialmod.entity.client.*;
import net.Vicente.tutorialmod.inventory.Slot;
import net.Vicente.tutorialmod.item.ModCreativeModeTabs;
import net.Vicente.tutorialmod.item.ModItems;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ObjectHolder;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(TutorialMod.MOD_ID)
public class TutorialMod
{
    // Define mod id in a common place for everything to reference
    public static final String MOD_ID = "tutorialmod";
    // Directly reference a slf4j logger
//    private static final Logger LOGGER = LogUtils.getLogger();

//    @ObjectHolder(registryName = "minecraft", value = "minecraft:world.inventory.Slot")
//    public static final Slot slot = null;

    public TutorialMod()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModItems.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModBlocksOriginal.register(modEventBus);

        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        // Mod entity types
        ModEntityTypes.register(modEventBus);

        // Register Enchantments
        ModEnchantments.register(modEventBus);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);

        // Register the item to a creative tab
        modEventBus.addListener(this::addCreative);
    }



    private void commonSetup(final FMLCommonSetupEvent event)
    { event.enqueueWork(() ->{
        SpawnPlacements.register(ModEntityTypes.MODSKELETON.get(),
                SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Monster::checkMonsterSpawnRules);
        SpawnPlacements.register(ModEntityTypes.MODZOMBIE.get(),
                SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Monster::checkMonsterSpawnRules);
    });

    }

    private void addCreative(CreativeModeTabEvent.BuildContents event)
    {
        // Checks tab being registered, if it is the specific tab, it will add items into tab
        if(event.getTab() == CreativeModeTabs.INGREDIENTS){
            event.accept(ModItems.BLACK_OPAL);

        }
        // add items to tutorial tab
        if(event.getTab() == ModCreativeModeTabs.TUTORIAL_TAB){
            event.accept(ModItems.BLACK_OPAL);
            event.accept(ModItems.MOD_SKELETON_EGG);
            event.accept(ModItems.MOD_ZOMBIE_EGG);
            event.accept(ModItems.MOD_ZOMBIE_MINER_EGG);
            event.accept(ModItems.MOD_CREEPER_EGG);
            event.accept(ModItems.MOD_SNOW_GOLEM_EGG);
            event.accept(ModItems.MOD_PHANTOM_EGG);
            event.accept(ModItems.MOD_WITCH_EGG);
            event.accept(ModItems.MOD_GOLD_GOLEM_EGG);
            event.accept(ModItems.MOD_IRON_GOLEM_EGG);
            event.accept(ModBlocks.PUMPKINBLOCK);
            event.accept(ModBlocks.CARVEDPUMPKINBLOCK);
            event.accept(ModItems.ICE_ARROW);
            event.accept(ModBlocksOriginal.FALLING_SAND_BLOCK);
        }
    }



    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            EntityRenderers.register(ModEntityTypes.MODSKELETON.get(), ModSkeletonRenderer::new);
            EntityRenderers.register(ModEntityTypes.MODZOMBIE.get(), ModZombieRenderer::new);
            EntityRenderers.register(ModEntityTypes.MODZOMBIEMINER.get(), ModZombieMinerRenderer::new);
            EntityRenderers.register(ModEntityTypes.MODCREEPER.get(), ModCreeperRenderer::new);
            EntityRenderers.register(ModEntityTypes.MODSNOWGOLEM.get(), ModSnowGolemRenderer::new);
            EntityRenderers.register(ModEntityTypes.MODICEGOLEM.get(), ModIceGolemRenderer::new);
            EntityRenderers.register(ModEntityTypes.MODPHANTOM.get(), ModPhantomRenderer::new);
            EntityRenderers.register(ModEntityTypes.MODWITCH.get(), ModWitchRenderer::new);
            EntityRenderers.register(ModEntityTypes.MODGOLDGOLEM.get(), ModGoldGolemRenderer::new);
            EntityRenderers.register(ModEntityTypes.MODIRONGOLEM.get(), ModIronGolemRenderer::new);
            EntityRenderers.register(ModEntityTypes.ICE_ARROW.get(), IceArrowRenderer::new);

        }
    }

}

