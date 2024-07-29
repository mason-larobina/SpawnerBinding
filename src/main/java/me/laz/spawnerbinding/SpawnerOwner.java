package me.laz.spawnerbinding;

import java.util.UUID;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;

public record SpawnerOwner(UUID uuid, String name) {
    private static final NamespacedKey OWNER_UUID = new NamespacedKey(SpawnerBinding.getInstance(), "owner_uuid");
    private static final NamespacedKey OWNER_NAME = new NamespacedKey(SpawnerBinding.getInstance(), "owner_name");

    public static SpawnerOwner from(Player player) {
        return new SpawnerOwner(player.getUniqueId(), player.getName());
    }

    public static @Nullable SpawnerOwner from(PersistentDataContainer pdc) {
        if (pdc == null) {
            return null;
        }
        String uuid = pdc.get(OWNER_UUID, PersistentDataType.STRING);
        if (uuid == null) {
            return null;
        }
        String name = pdc.get(OWNER_NAME, PersistentDataType.STRING);
        if (name == null) {
            return null;
        }
        return new SpawnerOwner(UUID.fromString(uuid), name);
    }

    public void set(PersistentDataContainer pdc) {
        pdc.set(OWNER_UUID, PersistentDataType.STRING, uuid().toString());
        pdc.set(OWNER_NAME, PersistentDataType.STRING, name());
    }
}