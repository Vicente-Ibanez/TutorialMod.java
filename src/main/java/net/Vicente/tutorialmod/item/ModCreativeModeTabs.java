package net.Vicente.tutorialmod.item;

import net.Vicente.tutorialmod.TutorialMod;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

// Called automatically because it has the subscriber events
@Mod.EventBusSubscriber(modid = TutorialMod.MOD_ID, bus=Mod.EventBusSubscriber.Bus.MOD)
public class ModCreativeModeTabs {

    public static CreativeModeTab TUTORIAL_TAB;

    @SubscribeEvent
    public static void registerCreativeModeTabs(CreativeModeTabEvent.Register event){
        // If i want another one copy the next three lines and past right below, and change the name (tutorial_tab) and
        // create a new public static above and also a new translation in en_us.json
        // then add the tab to addCreate in TutorialMod.java
        TUTORIAL_TAB = event.registerCreativeModeTab(new ResourceLocation(TutorialMod.MOD_ID, "tutorial_tab"),
                builder -> builder.icon(() -> new ItemStack(ModItems.BLACK_OPAL.get()))
                        .title(Component.translatable("creativemodetab.tutorial_tab")));
    }
}
