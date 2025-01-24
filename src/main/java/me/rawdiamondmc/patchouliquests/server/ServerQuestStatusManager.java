package me.rawdiamondmc.patchouliquests.server;

import java.io.IOException;
import java.nio.file.Path;
import java.util.UUID;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import me.rawdiamondmc.patchouliquests.BetterPatchouliQuests;
import me.rawdiamondmc.patchouliquests.QuestLocation;
import org.jetbrains.annotations.Contract;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtSizeTracker;
import net.minecraft.nbt.NbtString;

import net.fabricmc.loader.api.FabricLoader;

public final class ServerQuestStatusManager {
    public static final ServerQuestStatusManager INSTANCE = new ServerQuestStatusManager();
    public final Path path = FabricLoader.getInstance().getGameDir().resolve(BetterPatchouliQuests.MOD_ID).resolve("status.nbt");
    private final Multimap<UUID, QuestLocation> data = HashMultimap.create();

    private ServerQuestStatusManager() {
    }

    @Contract(mutates = "this")
    public void load() {
        this.data.clear();
        final NbtCompound nbtCompound;
        try {
            nbtCompound = NbtIo.readCompressed(this.path, NbtSizeTracker.ofUnlimitedBytes());
        } catch (Exception e) {
            BetterPatchouliQuests.LOGGER.error("Couldn't load data!", e);
            return;
        }
        if (nbtCompound == null) {
            return;
        }
        for (final String key : nbtCompound.getKeys()) {
            final UUID uuid;
            try {
                uuid = UUID.fromString(key);
            } catch (IllegalArgumentException e) {
                BetterPatchouliQuests.LOGGER.error("Failed to parse '{}' as UUID! Ignored.", key);
                continue;
            }
            final NbtList nbtList = nbtCompound.getList(key, NbtElement.STRING_TYPE);
            for (int i = 0; i < nbtList.size(); i++) {
                final String value = nbtList.getString(i);
                final QuestLocation page = QuestLocation.fromString(value);
                if (page == null) {
                    BetterPatchouliQuests.LOGGER.error("Failed to parse '{}' as a quest! Ignored.", value);
                    continue;
                }
                this.data.put(uuid, page);
            }
        }
        BetterPatchouliQuests.LOGGER.info("Data loaded from the disk.");
    }

    @Contract(mutates = "io")
    public void save() {
        try {
            // noinspection ResultOfMethodCallIgnored
            this.path.toFile().createNewFile();
        } catch (final IOException e) {
            BetterPatchouliQuests.LOGGER.error("Failed to create data file!");
            return;
        }
        final NbtCompound nbtCompound = new NbtCompound();
        this.data.keySet().forEach(uuid -> {
            final NbtList nbtList = new NbtList();
            nbtCompound.put(uuid.toString(), nbtList);
            this.data.get(uuid).forEach(page -> nbtList.add(NbtString.of(page.toString())));
        });
        try {
            NbtIo.writeCompressed(nbtCompound, this.path);
            BetterPatchouliQuests.LOGGER.info("Data saved.");
        } catch (final IOException e) {
            BetterPatchouliQuests.LOGGER.error("Failed to save data file!");
        }
    }

    @Contract(pure = true)
    public boolean isCompleted(final UUID uuid, final QuestLocation page) {
        return data.containsEntry(uuid, page);
    }

    @Contract(mutates = "this")
    public boolean setCompleted(final UUID uuid, final QuestLocation page) {
        return data.put(uuid, page);
    }

    @Contract(mutates = "this")
    public boolean revoke(final UUID uuid, final QuestLocation page) {
        return data.remove(uuid, page);
    }
}
