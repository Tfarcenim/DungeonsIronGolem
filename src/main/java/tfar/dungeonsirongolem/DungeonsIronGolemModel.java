package tfar.dungeonsirongolem;


import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;
import tfar.dungeonsirongolem.entity.DungeonsIronGolemEntity;

public class DungeonsIronGolemModel extends GeoModel<DungeonsIronGolemEntity> {
    private final String prefix;

    public DungeonsIronGolemModel(String prefix) {
        this.prefix = prefix;
    }

        public ResourceLocation getModelResource(DungeonsIronGolemEntity object) {
            return new ResourceLocation(DungeonsIronGolem.MODID, "geo/"+prefix+".geo.json");
        }

        public ResourceLocation getTextureResource(DungeonsIronGolemEntity object) {
            return new ResourceLocation(DungeonsIronGolem.MODID, "textures/model/entity/"+prefix+".png");
        }

        public ResourceLocation getAnimationResource(DungeonsIronGolemEntity animatable) {
            return new ResourceLocation(DungeonsIronGolem.MODID, "animations/"+prefix+".animation.json");
        }
}
