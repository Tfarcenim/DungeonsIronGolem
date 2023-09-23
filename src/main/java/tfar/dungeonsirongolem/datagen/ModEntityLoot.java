package tfar.dungeonsirongolem.datagen;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.loot.EntityLootSubProvider;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import tfar.dungeonsirongolem.DungeonsIronGolem;

import java.util.stream.Stream;

public class ModEntityLoot extends EntityLootSubProvider {
    protected ModEntityLoot() {
        super(FeatureFlags.REGISTRY.allFlags());
    }

    @Override
    public void generate() {
        this.add(DungeonsIronGolem.Entities.DUNGEONS_IRON_GOLEM_ENTITY, LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).add(LootItem.lootTableItem(Blocks.POPPY).apply(SetItemCountFunction.setCount(UniformGenerator.between(0.0F, 2.0F))))).withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1.0F)).add(LootItem.lootTableItem(Items.IRON_INGOT).apply(SetItemCountFunction.setCount(UniformGenerator.between(3.0F, 5.0F))))));
    }

    @Override
    protected boolean canHaveLootTable(EntityType<?> pEntityType) {
        return true;
    }

    @Override
    protected Stream<EntityType<?>> getKnownEntityTypes() {
        return super.getKnownEntityTypes().filter(entityType -> BuiltInRegistries.ENTITY_TYPE.getKey(entityType).getNamespace().equals(DungeonsIronGolem.MODID));
    }
}
