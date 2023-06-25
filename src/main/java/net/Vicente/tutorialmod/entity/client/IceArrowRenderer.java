package net.Vicente.tutorialmod.entity.client;

import net.Vicente.tutorialmod.TutorialMod;
import net.Vicente.tutorialmod.entity.custom.IceArrow;
import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class IceArrowRenderer extends ArrowRenderer<IceArrow> {

    private static final ResourceLocation ICE_ARROW_LOCATION = new ResourceLocation(TutorialMod.MOD_ID,"textures/entity/projectiles/ice_arrow.png");

    public IceArrowRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    public ResourceLocation getTextureLocation(IceArrow iceArrow) {
        return ICE_ARROW_LOCATION;
    }


}
