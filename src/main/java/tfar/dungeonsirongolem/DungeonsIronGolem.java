package tfar.dungeonsirongolem;

import com.mojang.logging.LogUtils;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.IModBusEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.RegisterEvent;
import org.slf4j.Logger;

import java.util.stream.Collectors;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(DungeonsIronGolem.MODID)
public class DungeonsIronGolem {
    public static final String MODID = "dungeonsirongolem";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();

    public static class Entities {
    public static final EntityType<DungeonsIronGolemEntity> DUNGEONS_IRON_GOLEM_ENTITY =
            EntityType.Builder.of(DungeonsIronGolemEntity::new, MobCategory.MISC).sized(2,4).build("dungeons_iron_golem");

    }
    public DungeonsIronGolem() {
        IEventBus iEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        // Register the setup method for modloading
        iEventBus.addListener(this::setup);
        // Register the enqueueIMC method for modloading
        iEventBus.addListener(this::onBlocksRegistry);
        iEventBus.addListener(this::attributes);
    }

    private void setup(final FMLCommonSetupEvent event) {
    }

    private void attributes(EntityAttributeCreationEvent event) {
        event.put(Entities.DUNGEONS_IRON_GOLEM_ENTITY,DungeonsIronGolemEntity.createAttributes().build());
    }

    public void onBlocksRegistry(final RegisterEvent event) {
        event.register(Registries.ENTITY_TYPE,new ResourceLocation(MODID,"iron_golem"),() -> Entities.DUNGEONS_IRON_GOLEM_ENTITY);
    }
}
