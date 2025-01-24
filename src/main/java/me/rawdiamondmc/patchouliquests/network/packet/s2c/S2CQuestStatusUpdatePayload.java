package me.rawdiamondmc.patchouliquests.network.packet.s2c;

import me.rawdiamondmc.patchouliquests.BetterPatchouliQuests;
import me.rawdiamondmc.patchouliquests.QuestLocation;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

public record S2CQuestStatusUpdatePayload(boolean add, QuestLocation quest) implements CustomPayload {
    public static final PacketCodec<PacketByteBuf, S2CQuestStatusUpdatePayload> CODEC = new PacketCodec<>() {
        @Override
        public S2CQuestStatusUpdatePayload decode(final PacketByteBuf buf) {
            final String s = buf.readString(715_827_882);
            final char addChar = s.charAt(0);
            final String rawPage = s.substring(1);
            final QuestLocation quest = QuestLocation.fromString(rawPage);
            if (quest == null) {
                throw new RuntimeException("Illegal packet received! '%s' cannot be parsed to a quest!".formatted(rawPage));
            }
            return switch (addChar) {
                case '0' -> new S2CQuestStatusUpdatePayload(false, quest);
                case '1' -> new S2CQuestStatusUpdatePayload(true, quest);
                default -> {
                    throw new RuntimeException("Illegal packet received! Unrecognized character '%s' from the packet!".formatted(addChar));
                }
            };
        }

        @Override
        public void encode(final PacketByteBuf buf, final S2CQuestStatusUpdatePayload value) {
            buf.writeString((value.add ? "1" : "0") + value.quest.toString(), 715_827_882);
        }
    };
    public static final CustomPayload.Id<S2CQuestStatusUpdatePayload> ID = new CustomPayload.Id<>(BetterPatchouliQuests.of("quest_status_update"));

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
