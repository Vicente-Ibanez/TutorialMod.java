package net.Vicente.tutorialmod.entity.client;

import net.Vicente.tutorialmod.TutorialMod;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.IronGolemRenderer;
import net.minecraft.client.renderer.entity.PhantomRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.IronGolem;

public class ModGoldGolemRenderer extends IronGolemRenderer {


    private static final ResourceLocation GOLEM_LOCATION = new ResourceLocation(TutorialMod.MOD_ID, "textures/entity/gold_golem.png");


    public ModGoldGolemRenderer(EntityRendererProvider.Context p_174380_) {
        super(p_174380_);
    }

    @Override
    public ResourceLocation getTextureLocation(IronGolem p_115012_) {
        return GOLEM_LOCATION;
    }
}