package tfar.dungeonsirongolem.datagen;

import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.LanguageProvider;
import tfar.dungeonsirongolem.DungeonsIronGolem;

public class ModLangProvider extends LanguageProvider {
    public ModLangProvider(PackOutput output) {
        super(output, DungeonsIronGolem.MODID, "en_us");
    }

    @Override
    protected void addTranslations() {
        add(DungeonsIronGolem.Entities.GOLEM_KIT,"Golem Kit");
        add(DungeonsIronGolem.Entities.DUNGEONS_IRON_GOLEM_ENTITY,"Dungeon's Iron Golem");
    }
}
