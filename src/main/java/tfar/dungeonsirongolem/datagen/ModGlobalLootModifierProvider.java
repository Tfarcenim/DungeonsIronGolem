package tfar.dungeonsirongolem.datagen;

import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.predicates.AnyOfCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.data.GlobalLootModifierProvider;
import net.minecraftforge.common.loot.LootTableIdCondition;
import tfar.dungeonsirongolem.AddItemWithChanceLootModifier;
import tfar.dungeonsirongolem.DungeonsIronGolem;

public class ModGlobalLootModifierProvider extends GlobalLootModifierProvider {
    public ModGlobalLootModifierProvider(PackOutput output) {
        super(output, DungeonsIronGolem.MODID);
    }

    @Override
    protected void start() {
        add("add_golem_kit", new AddItemWithChanceLootModifier(
                new LootItemCondition[]{
                        AnyOfCondition.anyOf(
                                new LootTableIdCondition.Builder(BuiltInLootTables.SIMPLE_DUNGEON),
                                new LootTableIdCondition.Builder(BuiltInLootTables.ABANDONED_MINESHAFT),
                                new LootTableIdCondition.Builder(BuiltInLootTables.ANCIENT_CITY),
                                new LootTableIdCondition.Builder(BuiltInLootTables.WOODLAND_MANSION),
                                new LootTableIdCondition.Builder(BuiltInLootTables.DESERT_PYRAMID),
                                new LootTableIdCondition.Builder(BuiltInLootTables.RUINED_PORTAL),
                                new LootTableIdCondition.Builder(BuiltInLootTables.BASTION_TREASURE)
        ).build()
                }, DungeonsIronGolem.Entities.GOLEM_KIT, .05f
        ));
    }
}