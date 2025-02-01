package me.rawdiamondmc.patchouliquests.client;

import java.util.List;

import org.jetbrains.annotations.Contract;

import net.minecraft.util.Identifier;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public final class ClientQuestStatusManager {
    private static ClientQuestStatusManager INSTANCE;
    private final List<Identifier> completedQuestIds;

    private ClientQuestStatusManager(final List<Identifier> completedQuestIds) {
        this.completedQuestIds = completedQuestIds;
    }

    @Contract(pure = true)
    public static boolean isCompleted(final Identifier questId) {
        return INSTANCE.completedQuestIds.contains(questId);
    }

    public static void setCompleted(final Identifier questId) {
        INSTANCE.completedQuestIds.add(questId);
    }

    public static void revoke(final Identifier questId) {
        INSTANCE.completedQuestIds.remove(questId);
    }

    public static boolean initialized() {
        return INSTANCE != null;
    }

    public static void init(final List<Identifier> questIds) {
        if (initialized()) {
            throw new UnsupportedOperationException("ClientQuestStatusManager has already been initialized!");
        }
        INSTANCE = new ClientQuestStatusManager(questIds);
    }
}
