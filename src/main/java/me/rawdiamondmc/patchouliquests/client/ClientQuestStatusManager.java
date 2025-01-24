package me.rawdiamondmc.patchouliquests.client;

import java.util.List;

import me.rawdiamondmc.patchouliquests.QuestLocation;
import org.jetbrains.annotations.Contract;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public final class ClientQuestStatusManager {
    private static ClientQuestStatusManager INSTANCE;
    private final List<QuestLocation> completedQuests;

    private ClientQuestStatusManager(final List<QuestLocation> completedQuests) {
        this.completedQuests = completedQuests;
    }

    @Contract(pure = true)
    public static boolean isCompleted(final QuestLocation quest) {
        return INSTANCE.completedQuests.contains(quest);
    }

    public static void setCompleted(final QuestLocation quest) {
        INSTANCE.completedQuests.add(quest);
    }

    public static void revoke(final QuestLocation quest) {
        INSTANCE.completedQuests.remove(quest);
    }

    public static boolean initialized() {
        return INSTANCE != null;
    }

    public static void init(final List<QuestLocation> quests) {
        if (initialized()) {
            throw new UnsupportedOperationException("ClientQuestStatusManager has already been initialized!");
        }
        INSTANCE = new ClientQuestStatusManager(quests);
    }
}
