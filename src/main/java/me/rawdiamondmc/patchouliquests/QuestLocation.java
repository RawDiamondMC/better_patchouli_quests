package me.rawdiamondmc.patchouliquests;

import java.util.Arrays;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.util.Identifier;

// Anchor of a quest
// the pageNum starts at 0
public record QuestLocation(Identifier bookId, Identifier entryId, int pageNum) {
    public static @Nullable QuestLocation fromString(final String s) {
        try {
            final String[] parts = s.split("A");
            if (parts.length < 3) {
                BetterPatchouliQuests.LOGGER.error("Illegal syntax: {}.", s);
                return null;
            }
            if (parts.length > 3) {
                BetterPatchouliQuests.LOGGER.warn("Extra data found: {}. Ignored.", String.join("A", Arrays.copyOfRange(parts, 2, parts.length)));
            }
            final Identifier bookId = Identifier.of(parts[0]);
            final Identifier entryId = Identifier.of(parts[1]);
            final int pageNum = Integer.parseInt(parts[2]);
            return new QuestLocation(bookId, entryId, pageNum);
        } catch (Exception e) {
            BetterPatchouliQuests.LOGGER.error("Error while parsing quest {}. Ignored.", s, e);
            return null;
        }
    }

    @Override
    @Contract(pure = true)
    public @NotNull String toString() {
        return bookId + "A" + entryId + "A" + pageNum;
    }
}
