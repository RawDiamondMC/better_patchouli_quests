package me.rawdiamondmc.patchouliquests.network.packet.s2c;

import java.util.Arrays;
import java.util.List;

import me.rawdiamondmc.patchouliquests.BetterPatchouliQuests;
import me.rawdiamondmc.patchouliquests.QuestLocation;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

public record S2CQuestStatusPayload(List<QuestLocation> completedQuests) implements CustomPayload {
    public static final PacketCodec<PacketByteBuf, S2CQuestStatusPayload> CODEC = new PacketCodec<>() {
        @Override
        public S2CQuestStatusPayload decode(final PacketByteBuf buf) {
            return new S2CQuestStatusPayload(Arrays.stream(buf.readString(715_827_882).split("B")).map(QuestLocation::fromString).toList());
        }

        @Override
        public void encode(final PacketByteBuf buf, final S2CQuestStatusPayload value) {
            buf.writeString(String.join("B", value.completedQuests.stream().map(QuestLocation::toString).toList()), 715_827_882);
        }
    };
    public static final Id<S2CQuestStatusPayload> ID = new Id<>(BetterPatchouliQuests.of("quest_status"));

    @Override
    public Id<S2CQuestStatusPayload> getId() {
        return ID;
    }
}
