package me.rawdiamondmc.patchouliquests.network.packet.c2s;

import me.rawdiamondmc.patchouliquests.BetterPatchouliQuests;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record C2SQuestSubmissionPayload(Identifier questId) implements CustomPayload {
    public static final PacketCodec<PacketByteBuf, C2SQuestSubmissionPayload> CODEC = new PacketCodec<>() {
        @Override
        public C2SQuestSubmissionPayload decode(final PacketByteBuf buf) {
            return new C2SQuestSubmissionPayload(Identifier.of(buf.readString(715_827_882)));
        }

        @Override
        public void encode(final PacketByteBuf buf, final C2SQuestSubmissionPayload value) {
            buf.writeString(value.questId.toString(), 715_827_882);
        }
    };
    public static final CustomPayload.Id<C2SQuestSubmissionPayload> ID = new CustomPayload.Id<>(BetterPatchouliQuests.of("quest_submission_c2s"));

    @Override
    public Id<C2SQuestSubmissionPayload> getId() {
        return ID;
    }
}
