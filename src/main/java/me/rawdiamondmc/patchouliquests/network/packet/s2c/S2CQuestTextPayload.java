package me.rawdiamondmc.patchouliquests.network.packet.s2c;

import me.rawdiamondmc.patchouliquests.BetterPatchouliQuests;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;

public record S2CQuestTextPayload(Text result) implements CustomPayload {
    public static final PacketCodec<PacketByteBuf, S2CQuestTextPayload> CODEC = new PacketCodec<>() {
        @Override
        public S2CQuestTextPayload decode(final PacketByteBuf buf) {
            final String s = buf.readString(715_827_882);
            return new S2CQuestTextPayload(s.isEmpty() ? Text.empty() : Text.Serialization.fromJson(s, DynamicRegistryManager.of(Registries.REGISTRIES)));
        }

        @Override
        public void encode(final PacketByteBuf buf, final S2CQuestTextPayload value) {
            buf.writeString(Text.Serialization.toJsonString(value.result, DynamicRegistryManager.of(Registries.REGISTRIES)), 715_827_882);
        }
    };
    public static final Id<S2CQuestTextPayload> ID = new Id<>(BetterPatchouliQuests.of("quest_text_s2c"));

    @Override
    public Id<S2CQuestTextPayload> getId() {
        return ID;
    }
}