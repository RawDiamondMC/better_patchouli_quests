package me.rawdiamondmc.patchouliquests.condition;

import java.util.Optional;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public abstract class QuestCondition {
    public static final QuestCondition EMPTY = new QuestCondition() {
        @Override
        public Optional<Text> check(ServerPlayerEntity player) {
            return Optional.empty();
        }
    };

    public QuestCondition() {
    }

    public abstract Optional<Text> check(ServerPlayerEntity player);
}
