package me.rawdiamondmc.patchouliquests.network.packet.c2s;

import me.rawdiamondmc.patchouliquests.BetterPatchouliQuests;
import me.rawdiamondmc.patchouliquests.QuestLocation;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

public record C2SQuestSubmissionPayload(QuestLocation quest) implements CustomPayload {
    public static final PacketCodec<PacketByteBuf, C2SQuestSubmissionPayload> CODEC = new PacketCodec<>() {
        @Override
        public C2SQuestSubmissionPayload decode(final PacketByteBuf buf) {
            final String s = buf.readString(715_827_882);
            final QuestLocation quest = QuestLocation.fromString(s);
            if (quest == null) {
                throw new RuntimeException("Illegal packet received! '%s' cannot be parsed to a quest!".formatted(s));
            }
            return new C2SQuestSubmissionPayload(quest);
        }

        @Override
        public void encode(final PacketByteBuf buf, final C2SQuestSubmissionPayload value) {
            buf.writeString(value.quest.toString(), 715_827_882);
        }
    };
    public static final CustomPayload.Id<C2SQuestSubmissionPayload> ID = new CustomPayload.Id<>(BetterPatchouliQuests.of("quest_submission_c2s"));

    @Override
    public Id<C2SQuestSubmissionPayload> getId() {
        return ID;
    }
}
