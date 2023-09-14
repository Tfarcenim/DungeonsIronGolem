package tfar.dungeonsirongolem.datagen;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import tfar.dungeonsirongolem.DungeonsIronGolem;

public class ModModelProvider extends ItemModelProvider {
    public ModModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, DungeonsIronGolem.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        //makeSimpleItem(DungeonsIronGolem.Entities.GOLEM_KIT);
    }

    protected void makeSimpleItem(Item item, ResourceLocation loc) {
        String s = BuiltInRegistries.ITEM.getKey(item).toString();

        getBuilder(s).parent(getExistingFile(loc));
    }

    protected void makeSimpleItem(Item item) {
        makeSimpleItem(item, new ResourceLocation(DungeonsIronGolem.MODID, "item/" + BuiltInRegistries.ITEM.getKey(item).getPath()));
    }

}
