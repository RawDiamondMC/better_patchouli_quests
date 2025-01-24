package me.rawdiamondmc.patchouliquests.server;

import java.util.Optional;

import me.rawdiamondmc.patchouliquests.QuestLocation;
import me.rawdiamondmc.patchouliquests.network.packet.c2s.C2SQuestSubmissionPayload;
import me.rawdiamondmc.patchouliquests.network.packet.c2s.C2SRequestTextPayload;
import me.rawdiamondmc.patchouliquests.network.packet.s2c.S2CQuestSubmissionPayload;
import me.rawdiamondmc.patchouliquests.network.packet.s2c.S2CQuestTextPayload;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

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
            final QuestLocation quest = payload.quest();
            if (!QuestRegistry.questRegistered(quest)) {
                ServerPlayNetworking.send(player, new S2CQuestSubmissionPayload(Text.translatable("better_patchouli_quests.result.illegal_quest")));
                return;
            }
            final Optional<Text> result = QuestRegistry.check(quest, player);
            if (result.isEmpty()) {
                ServerQuestStatusManager.INSTANCE.setCompleted(player.getUuid(), quest);
                ServerPlayNetworking.send(player, new S2CQuestSubmissionPayload(Text.empty()));
                return;
            }
            ServerPlayNetworking.send(player, new S2CQuestSubmissionPayload(result.get()));
        }));
        ServerPlayNetworking.registerGlobalReceiver(C2SRequestTextPayload.ID, (payload, context) -> {
            final ServerPlayerEntity player = context.player();
            final QuestLocation quest = payload.quest();
            if (!QuestRegistry.questRegistered(quest)) {
                ServerPlayNetworking.send(player, new S2CQuestSubmissionPayload(Text.translatable("better_patchouli_quests.result.illegal_quest")));
                return;
            }
            final Text text = QuestRegistry.getRequirementText(quest, player);
            ServerPlayNetworking.send(player, new S2CQuestTextPayload(text));
        });
    }
}
