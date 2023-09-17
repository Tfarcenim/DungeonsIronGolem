package tfar.dungeonsirongolem;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import tfar.dungeonsirongolem.entity.DungeonsIronGolemEntity;

public class DungeonsIronGolemRenderer extends GeoEntityRenderer<DungeonsIronGolemEntity> {
    public DungeonsIronGolemRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new DungeonsIronGolemModel("iron_golem"));
        shadowRadius = 1;
    }
}