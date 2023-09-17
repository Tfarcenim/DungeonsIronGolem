package tfar.dungeonsirongolem;

import com.mojang.logging.LogUtils;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.RegisterEvent;
import org.slf4j.Logger;
import tfar.dungeonsirongolem.datagen.ModDatagen;
import tfar.dungeonsirongolem.entity.DungeonsIronGolemEntity;
import tfar.dungeonsirongolem.world.IronGolemSavedData;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(DungeonsIronGolem.MODID)
public class DungeonsIronGolem {
    public static final String MODID = "dungeonsirongolem";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();

    public static IronGolemSavedData ironGolemSavedData;

    public static class Entities {
    public static final EntityType<DungeonsIronGolemEntity> DUNGEONS_IRON_GOLEM_ENTITY =
            EntityType.Builder.of(DungeonsIronGolemEntity::new, MobCategory.MISC).sized(2,4).build("dungeons_iron_golem");
    public static final Item GOLEM_KIT = new IronGolemKitItem(new Item.Properties());

    }
    public DungeonsIronGolem() {
        IEventBus iEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        // Register the setup method for modloading
        iEventBus.addListener(this::setup);
        // Register the enqueueIMC method for modloading
        iEventBus.addListener(this::onBlocksRegistry);
        iEventBus.addListener(this::attributes);
        iEventBus.addListener(ModDatagen::gather);
        MinecraftForge.EVENT_BUS.addListener(this::servertick);
        MinecraftForge.EVENT_BUS.addListener(this::serverStart);
    }

    private void setup(final FMLCommonSetupEvent event) {
    }

    private void servertick(TickEvent.ServerTickEvent e) {
        if (e.phase == TickEvent.Phase.START) {
            IronGolemKitItem.tickCooldowns(e.getServer());
        }
    }

    private void serverStart(ServerStartedEvent e) {
            MinecraftServer server = e.getServer();
            ironGolemSavedData = server.getLevel(Level.OVERWORLD).getDataStorage()
                    .computeIfAbsent(IronGolemSavedData::loadStatic, IronGolemSavedData::new,MODID);
    }

    private void attributes(EntityAttributeCreationEvent event) {
        event.put(Entities.DUNGEONS_IRON_GOLEM_ENTITY,DungeonsIronGolemEntity.createAttributes().build());
    }

    public void onBlocksRegistry(final RegisterEvent event) {
        event.register(Registries.ENTITY_TYPE,new ResourceLocation(MODID,"iron_golem"),() -> Entities.DUNGEONS_IRON_GOLEM_ENTITY);
        event.register(Registries.ITEM,new ResourceLocation(MODID,"golem_kit"),() -> Entities.GOLEM_KIT);
    }
}
