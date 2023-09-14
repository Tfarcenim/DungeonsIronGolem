package tfar.dungeonsirongolem;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(value = Dist.CLIENT,bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModClient {
    @SubscribeEvent
    public static void renderers(final EntityRenderersEvent.RegisterRenderers event) {
            event.registerEntityRenderer(DungeonsIronGolem.Entities.DUNGEONS_IRON_GOLEM_ENTITY, DungeonsIronGolemRenderer::new);
    }
}
