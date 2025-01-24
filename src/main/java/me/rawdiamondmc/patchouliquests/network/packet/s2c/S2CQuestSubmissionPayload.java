package me.rawdiamondmc.patchouliquests.network.packet.s2c;

import me.rawdiamondmc.patchouliquests.BetterPatchouliQuests;
import org.jetbrains.annotations.Contract;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;

public record S2CQuestSubmissionPayload(Text result) implements CustomPayload {
    public static final PacketCodec<PacketByteBuf, S2CQuestSubmissionPayload> CODEC = new PacketCodec<>() {
        @Override
        public S2CQuestSubmissionPayload decode(final PacketByteBuf buf) {
            final String s = buf.readString(715_827_882);
            return new S2CQuestSubmissionPayload(s.isEmpty() ? Text.empty() : Text.Serialization.fromJson(s, DynamicRegistryManager.of(Registries.REGISTRIES)));
        }

        @Override
        public void encode(final PacketByteBuf buf, final S2CQuestSubmissionPayload value) {
            buf.writeString(Text.Serialization.toJsonString(value.result, DynamicRegistryManager.of(Registries.REGISTRIES)), 715_827_882);
        }
    };
    public static final Id<S2CQuestSubmissionPayload> ID = new Id<>(BetterPatchouliQuests.of("quest_submission_s2c"));

    @Contract(pure = true)
    public boolean isSuccessful() {
        return this.result.equals(Text.empty());
    }

    @Override
    public Id<S2CQuestSubmissionPayload> getId() {
        return ID;
    }
}
