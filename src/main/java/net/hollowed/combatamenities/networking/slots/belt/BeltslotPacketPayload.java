package net.hollowed.combatamenities.networking.slots.belt;

import static net.hollowed.combatamenities.CombatAmenities.MOD_ID;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

public record BeltslotPacketPayload(BlockPos blockPos) implements CustomPacketPayload {
    public static final Identifier BELTSLOT_PACKET_ID = Identifier.fromNamespaceAndPath(MOD_ID, "beltslot_packet");

    public static final Type<@NotNull BeltslotPacketPayload> ID = new Type<>(BELTSLOT_PACKET_ID);
    public static final StreamCodec<RegistryFriendlyByteBuf, BeltslotPacketPayload> CODEC = StreamCodec.composite(BlockPos.STREAM_CODEC, BeltslotPacketPayload::blockPos, BeltslotPacketPayload::new);

    @Override
    public @NotNull Type<? extends @NotNull CustomPacketPayload> type() {
        return ID;
    }
}
