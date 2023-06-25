package net.Vicente.tutorialmod.block;

import net.Vicente.tutorialmod.block.custom.CarvedPumpkinBlock;
import net.Vicente.tutorialmod.block.custom.FallingSandBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.PumpkinBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ObjectHolder;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, "minecraft");

    private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block) {
        RegistryObject<T> toReturn = BLOCKS.register(name, block);
        //registerBlockItem(name, toReturn);
        return toReturn;
    }

    @ObjectHolder(registryName = "minecraft", value = "minecraft:pumpkin")
    public static final RegistryObject<Block> PUMPKINBLOCK =
            BLOCKS.register("pumpkin",
                    ()-> new PumpkinBlock(BlockBehaviour.Properties.of(Material.VEGETABLE, MaterialColor.COLOR_ORANGE).strength(1.0F).sound(SoundType.WOOD)));

    @ObjectHolder(registryName = "minecraft", value = "minecraft:carved_pumpkin")
    public static final RegistryObject<Block> CARVEDPUMPKINBLOCK =
            BLOCKS.register("carved_pumpkin",
                    ()-> new CarvedPumpkinBlock(BlockBehaviour.Properties.of(Material.VEGETABLE, MaterialColor.COLOR_ORANGE).strength(1.0F).sound(SoundType.WOOD)));

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}