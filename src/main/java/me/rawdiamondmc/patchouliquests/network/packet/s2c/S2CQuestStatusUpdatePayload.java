package me.rawdiamondmc.patchouliquests.network.packet.s2c;

import me.rawdiamondmc.patchouliquests.BetterPatchouliQuests;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record S2CQuestStatusUpdatePayload(boolean add, Identifier questId) implements CustomPayload {
    public static final PacketCodec<PacketByteBuf, S2CQuestStatusUpdatePayload> CODEC = new PacketCodec<>() {
        @Override
        public S2CQuestStatusUpdatePayload decode(final PacketByteBuf buf) {
            final String s = buf.readString(715_827_882);
            final char addChar = s.charAt(0);
            final Identifier questId = Identifier.of(s.substring(1));
            return switch (addChar) {
                case '0' -> new S2CQuestStatusUpdatePayload(false, questId);
                case '1' -> new S2CQuestStatusUpdatePayload(true, questId);
                default -> {
                    throw new RuntimeException("Illegal packet received! Unrecognized character '%s' from the packet!".formatted(addChar));
                }
            };
        }

        @Override
        public void encode(final PacketByteBuf buf, final S2CQuestStatusUpdatePayload value) {
            buf.writeString((value.add ? "1" : "0") + value.questId.toString(), 715_827_882);
        }
    };
    public static final CustomPayload.Id<S2CQuestStatusUpdatePayload> ID = new CustomPayload.Id<>(BetterPatchouliQuests.of("quest_status_update"));

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
