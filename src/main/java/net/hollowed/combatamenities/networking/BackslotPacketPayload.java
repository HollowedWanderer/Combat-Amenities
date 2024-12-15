package net.hollowed.combatamenities.networking;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import static net.hollowed.combatamenities.CombatAmenities.MOD_ID;

public record BackslotPacketPayload(BlockPos blockPos) implements CustomPayload {
    public static final Identifier BACKSLOT_PACKET_ID = Identifier.of(MOD_ID, "backslot_packet");

    public static final CustomPayload.Id<BackslotPacketPayload> ID = new CustomPayload.Id<>(BACKSLOT_PACKET_ID);
    public static final PacketCodec<RegistryByteBuf, BackslotPacketPayload> CODEC = PacketCodec.tuple(BlockPos.PACKET_CODEC, BackslotPacketPayload::blockPos, BackslotPacketPayload::new);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
