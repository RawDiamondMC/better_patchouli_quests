package me.rawdiamondmc.patchouliquests.client;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import me.rawdiamondmc.patchouliquests.BetterPatchouliQuests;
import me.rawdiamondmc.patchouliquests.network.packet.s2c.S2CQuestStatusPayload;
import me.rawdiamondmc.patchouliquests.network.packet.s2c.S2CQuestStatusUpdatePayload;
import org.jetbrains.annotations.Nullable;
import vazkii.patchouli.client.book.ClientBookRegistry;

import net.minecraft.network.DisconnectionInfo;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.text.Text;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientConfigurationConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientConfigurationNetworking;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

@Environment(EnvType.CLIENT)
public final class BetterPatchouliQuestsClient implements ClientModInitializer {
    public static <T extends CustomPayload> @Nullable T requestData(final long timeoutMillis, final CustomPayload request, final CustomPayload.Id<T> resultId) {
        final CompletableFuture<T> future = new CompletableFuture<>();
        ClientPlayNetworking.registerGlobalReceiver(resultId, (payload, context) -> future.complete(payload));
        ClientPlayNetworking.send(request);
        try {
            final T result = future.get(timeoutMillis, TimeUnit.MILLISECONDS);
            ClientPlayNetworking.unregisterReceiver(resultId.id());
            return result;
        } catch (final Exception e) {
            future.completeExceptionally(e);
            ClientPlayNetworking.unregisterReceiver(resultId.id());
            return null;
        }
    }

    @Override
    public void onInitializeClient() {
        ClientBookRegistry.INSTANCE.pageTypes.put(BetterPatchouliQuests.of("quest"), QuestPage.class);
        ClientConfigurationNetworking.registerGlobalReceiver(S2CQuestStatusPayload.ID, (payload, context) -> ClientQuestStatusManager.init(payload.completedQuests()));
        ClientConfigurationConnectionEvents.COMPLETE.register((clientConfigurationNetworkHandler, minecraftClient) -> {
            if (!ClientQuestStatusManager.initialized()) {
                clientConfigurationNetworkHandler.onDisconnected(new DisconnectionInfo(Text.translatable("better_patchouli_quests.result.packet_not_received"), Optional.empty(), Optional.of(BetterPatchouliQuests.BUG_REPORT_URL)));
            }
        });
        ClientPlayNetworking.registerReceiver(S2CQuestStatusUpdatePayload.ID, (payload, context) -> {
            if (payload.add()) {
                ClientQuestStatusManager.setCompleted(payload.quest());
            } else {
                ClientQuestStatusManager.revoke(payload.quest());
            }
        });
    }
}
