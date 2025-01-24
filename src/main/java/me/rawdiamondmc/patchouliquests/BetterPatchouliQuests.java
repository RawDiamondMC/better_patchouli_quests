package me.rawdiamondmc.patchouliquests;

import java.net.URI;
import java.net.URISyntaxException;

import me.rawdiamondmc.patchouliquests.network.packet.c2s.C2SQuestSubmissionPayload;
import me.rawdiamondmc.patchouliquests.network.packet.c2s.C2SRequestTextPayload;
import me.rawdiamondmc.patchouliquests.network.packet.s2c.S2CQuestStatusPayload;
import me.rawdiamondmc.patchouliquests.network.packet.s2c.S2CQuestStatusUpdatePayload;
import me.rawdiamondmc.patchouliquests.network.packet.s2c.S2CQuestSubmissionPayload;
import me.rawdiamondmc.patchouliquests.network.packet.s2c.S2CQuestTextPayload;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.minecraft.util.Identifier;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

public final class BetterPatchouliQuests implements ModInitializer {
    public static final String MOD_ID = "better_patchouli_quests";
    public static final Logger LOGGER = LoggerFactory.getLogger("Better Patchouli Quests");
    public static final URI BUG_REPORT_URL;

    static {
        try {
            BUG_REPORT_URL = new URI("https://github.com/RawDiamondMC/better_patchouli_quests/issues");
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    @Contract("_ -> new")
    public static @NotNull Identifier of(String path) {
        return Identifier.of(MOD_ID, path);
    }

    @Override
    public void onInitialize() {
        PayloadTypeRegistry.configurationS2C().register(S2CQuestStatusPayload.ID, S2CQuestStatusPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(C2SQuestSubmissionPayload.ID, C2SQuestSubmissionPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(C2SRequestTextPayload.ID, C2SRequestTextPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(S2CQuestStatusUpdatePayload.ID, S2CQuestStatusUpdatePayload.CODEC);
        PayloadTypeRegistry.playS2C().register(S2CQuestSubmissionPayload.ID, S2CQuestSubmissionPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(S2CQuestTextPayload.ID, S2CQuestTextPayload.CODEC);
    }
}