package net.Vicente.tutorialmod.entity.client;

import net.Vicente.tutorialmod.TutorialMod;
import net.minecraft.client.model.SnowGolemModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.layers.SnowGolemHeadLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.SnowGolem;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ModIceGolemRenderer extends MobRenderer<SnowGolem, SnowGolemModel<SnowGolem>> {
    private static final ResourceLocation ICE_GOLEM_LOCATION = new ResourceLocation(TutorialMod.MOD_ID, "textures/entity/ice_golem.png");

    public ModIceGolemRenderer(EntityRendererProvider.Context p_174393_) {
        super(p_174393_, new SnowGolemModel<>(p_174393_.bakeLayer(ModelLayers.SNOW_GOLEM)), 0.5F);
        this.addLayer(new SnowGolemHeadLayer(this, p_174393_.getBlockRenderDispatcher(), p_174393_.getItemRenderer()));
    }

    public ResourceLocation getTextureLocation(SnowGolem p_115993_) {
        return ICE_GOLEM_LOCATION;
    }
}