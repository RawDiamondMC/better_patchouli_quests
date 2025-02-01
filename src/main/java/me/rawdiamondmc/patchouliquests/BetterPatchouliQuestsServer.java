package me.rawdiamondmc.patchouliquests;

import java.util.Optional;

import me.rawdiamondmc.patchouliquests.network.packet.c2s.C2SQuestSubmissionPayload;
import me.rawdiamondmc.patchouliquests.network.packet.s2c.S2CQuestSubmissionPayload;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

public final class BetterPatchouliQuestsServer implements DedicatedServerModInitializer {
    @Override
    public void onInitializeServer() {
        ServerQuestStatusManager.INSTANCE.load();
        // Save when the server saves
        ServerLifecycleEvents.AFTER_SAVE.register((minecraftServer, flush, force) -> ServerQuestStatusManager.INSTANCE.save());
        ServerPlayNetworking.registerGlobalReceiver(C2SQuestSubmissionPayload.ID, ((payload, context) -> {
            final ServerPlayerEntity player = context.player();
            final Identifier questId = payload.questId();
            if (!QuestRegistry.questRegistered(questId)) {
                ServerPlayNetworking.send(player, new S2CQuestSubmissionPayload(Text.translatable("better_patchouli_quests.result.illegal_quest")));
                return;
            }
            final Optional<Text> result = QuestRegistry.check(questId, player);
            if (result.isEmpty()) {
                ServerQuestStatusManager.INSTANCE.setCompleted(player.getUuid(), questId);
                ServerPlayNetworking.send(player, new S2CQuestSubmissionPayload(Text.empty()));
                return;
            }
            ServerPlayNetworking.send(player, new S2CQuestSubmissionPayload(result.get()));
        }));
    }
}
