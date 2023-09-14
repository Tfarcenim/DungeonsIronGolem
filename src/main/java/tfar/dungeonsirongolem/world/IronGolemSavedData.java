package tfar.dungeonsirongolem.world;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class IronGolemSavedData extends SavedData {

    private List<UUID> ACTIVE_GOLEMS = new ArrayList<>();

    public IronGolemSavedData() {
    }


    @Override
    public CompoundTag save(CompoundTag compoundTag) {
        compoundTag.put("contents", serializeuuids(ACTIVE_GOLEMS));
        return compoundTag;
    }

    public static List<UUID> deserializeuuids(ListTag list) {
        List<UUID> uuids = new ArrayList<>();
        for (Tag tag : list) {
            StringTag nbtTagString = (StringTag) tag;
            UUID uuid = UUID.fromString(nbtTagString.getAsString());
            uuids.add(uuid);
        }
        return uuids;
    }

    public static ListTag serializeuuids(List<UUID> uuids) {
        ListTag listTag = new ListTag();
        for (UUID uuid : uuids) {
            listTag.add(StringTag.valueOf(uuid.toString()));
        }
        return listTag;
    }

    public static IronGolemSavedData loadStatic(CompoundTag compoundTag) {
        IronGolemSavedData ironGolemSavedData = new IronGolemSavedData();
        ironGolemSavedData.load(compoundTag);
        return ironGolemSavedData;
    }

    protected void load(CompoundTag compoundTag) {
        ACTIVE_GOLEMS = deserializeuuids(compoundTag.getList("contents", Tag.TAG_STRING));
    }

    public void addGolem(UUID uuid) {
        ACTIVE_GOLEMS.add(uuid);
        setDirty();
    }

    public void removeGolem(UUID uuid) {
        ACTIVE_GOLEMS.remove(uuid);
        setDirty();
    }

    public boolean inPlay(UUID uuid) {
        return ACTIVE_GOLEMS.contains(uuid);
    }

    public void clearAll() {
        ACTIVE_GOLEMS.clear();
        setDirty();
    }
}
