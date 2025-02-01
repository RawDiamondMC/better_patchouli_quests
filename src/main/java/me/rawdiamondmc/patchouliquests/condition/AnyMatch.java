package me.rawdiamondmc.patchouliquests.condition;

import java.util.Optional;

import com.google.gson.annotations.SerializedName;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public final class AnyMatch extends QuestCondition {
    @SerializedName("sub")
    QuestCondition[] subConditions;

    @Override
    public Optional<Text> check(final ServerPlayerEntity player) {
        for (QuestCondition condition : this.subConditions) {
            Optional<Text> result = condition.check(player);
            if (result.isEmpty()) return Optional.empty();
        }
        return Optional.of(Text.translatable("better_patchouli_quests.error.quest_not_satisfies"));
    }
}
