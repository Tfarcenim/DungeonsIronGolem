package tfar.dungeonsirongolem.datagen;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;
import tfar.dungeonsirongolem.DungeonsIronGolem;
import tfar.dungeonsirongolem.entity.goal.AttackSlamGoal;

import java.util.concurrent.CompletableFuture;

public class ModEntityTypeTagsProvider extends EntityTypeTagsProvider {
    public ModEntityTypeTagsProvider(PackOutput pOutput, CompletableFuture<HolderLookup.Provider> pProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(pOutput, pProvider, DungeonsIronGolem.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider pProvider) {
        this.tag(AttackSlamGoal.IGNORE).add(DungeonsIronGolem.Entities.DUNGEONS_IRON_GOLEM_ENTITY, EntityType.IRON_GOLEM,
                EntityType.CAT,EntityType.VILLAGER,EntityType.WANDERING_TRADER);
        this.tag(EntityTypeTags.FALL_DAMAGE_IMMUNE).add(DungeonsIronGolem.Entities.DUNGEONS_IRON_GOLEM_ENTITY);
    }
}
