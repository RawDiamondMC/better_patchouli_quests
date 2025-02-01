package me.rawdiamondmc.patchouliquests.award;

import net.minecraft.server.network.ServerPlayerEntity;

public abstract class QuestAward {
    public static final QuestAward EMPTY = new QuestAward() {
        @Override
        public void award(ServerPlayerEntity player) {
        }
    };

    public QuestAward() {
    }

    public abstract void award(ServerPlayerEntity player);
}
