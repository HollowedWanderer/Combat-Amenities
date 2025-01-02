package net.hollowed.combatamenities.networking;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import static net.hollowed.combatamenities.CombatAmenities.MOD_ID;

public record BeltslotPacketPayload(BlockPos blockPos) implements CustomPayload {
    public static final Identifier BELTSLOT_PACKET_ID = Identifier.of(MOD_ID, "beltslot_packet");

    public static final Id<BeltslotPacketPayload> ID = new Id<>(BELTSLOT_PACKET_ID);
    public static final PacketCodec<RegistryByteBuf, BeltslotPacketPayload> CODEC = PacketCodec.tuple(BlockPos.PACKET_CODEC, BeltslotPacketPayload::blockPos, BeltslotPacketPayload::new);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
