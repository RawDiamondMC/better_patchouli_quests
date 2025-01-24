package me.rawdiamondmc.patchouliquests.server.condition;

import java.util.Optional;

import com.google.gson.annotations.SerializedName;
import me.rawdiamondmc.patchouliquests.QuestLocation;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

public final class AnyMatch extends QuestCondition {
    @SerializedName("sub")
    QuestCondition[] subConditions;

    @Override
    public Text getRequirementText(final ServerPlayerEntity player, final QuestLocation page) {
        final MutableText wrapper = Text.translatable("better_patchouli_quests.condition.any");
        for (QuestCondition condition : this.subConditions) {
            wrapper.append("\n  ");
            wrapper.append(condition.getRequirementText(player, page));
        }
        return wrapper;
    }

    @Override
    public Optional<Text> check(final ServerPlayerEntity player, final QuestLocation page) {
        for (QuestCondition condition : this.subConditions) {
            Optional<Text> result = condition.check(player, page);
            if (result.isEmpty()) return Optional.empty();
        }
        return Optional.of(Text.translatable("better_patchouli_quests.error.quest_not_satisfies"));
    }
}
