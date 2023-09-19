package tfar.dungeonsirongolem.client;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.DefaultedEntityGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import tfar.dungeonsirongolem.DungeonsIronGolem;
import tfar.dungeonsirongolem.entity.DungeonsIronGolemEntity;

public class DungeonsIronGolemRenderer extends GeoEntityRenderer<DungeonsIronGolemEntity> {
    public DungeonsIronGolemRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new DefaultedEntityGeoModel<>(new ResourceLocation(DungeonsIronGolem.MODID,"iron_golem"),true));
        shadowRadius = 1;
    }
}