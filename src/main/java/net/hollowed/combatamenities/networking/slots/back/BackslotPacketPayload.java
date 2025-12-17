package net.hollowed.combatamenities.networking.slots.back;

import static net.hollowed.combatamenities.CombatAmenities.MOD_ID;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

public record BackslotPacketPayload(BlockPos blockPos) implements CustomPacketPayload {
    public static final Identifier BACKSLOT_PACKET_ID = Identifier.fromNamespaceAndPath(MOD_ID, "backslot_packet");

    public static final CustomPacketPayload.Type<@NotNull BackslotPacketPayload> ID = new CustomPacketPayload.Type<>(BACKSLOT_PACKET_ID);
    public static final StreamCodec<RegistryFriendlyByteBuf, BackslotPacketPayload> CODEC = StreamCodec.composite(BlockPos.STREAM_CODEC, BackslotPacketPayload::blockPos, BackslotPacketPayload::new);

    @Override
    public @NotNull Type<? extends @NotNull CustomPacketPayload> type() {
        return ID;
    }
}
