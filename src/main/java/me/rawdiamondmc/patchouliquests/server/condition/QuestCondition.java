package me.rawdiamondmc.patchouliquests.server.condition;

import java.util.Optional;

import me.rawdiamondmc.patchouliquests.QuestLocation;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public abstract class QuestCondition {
    public QuestCondition() {
    }

    public abstract Text getRequirementText(ServerPlayerEntity player, QuestLocation page);

    public abstract Optional<Text> check(ServerPlayerEntity player, QuestLocation page);
}
