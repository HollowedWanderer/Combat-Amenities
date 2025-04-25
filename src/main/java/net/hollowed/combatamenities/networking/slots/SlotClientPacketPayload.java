package net.hollowed.combatamenities.networking.slots;

import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record SlotClientPacketPayload(int entityId, int slotId, ItemStack itemStack) implements CustomPayload {

    public static final CustomPayload.Id<SlotClientPacketPayload> BACKSLOT_CLIENT_PACKET_ID = new CustomPayload.Id<>(Identifier.of("combatamenities", "backslot_client_packet"));

    public static final PacketCodec<RegistryByteBuf, SlotClientPacketPayload> CODEC = PacketCodec.of(SlotClientPacketPayload::write, SlotClientPacketPayload::new);

    public SlotClientPacketPayload(RegistryByteBuf buf) {
        this(buf.readInt(), buf.readInt(), buf.readBoolean() ? ItemStack.EMPTY : ItemStack.PACKET_CODEC.decode(buf));
    }

    public void write(RegistryByteBuf buf) {
        buf.writeInt(entityId);
        buf.writeInt(slotId);
        buf.writeBoolean(itemStack.isEmpty());
        if (!itemStack.isEmpty()) {
            ItemStack.PACKET_CODEC.encode(buf, itemStack);
        }
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return BACKSLOT_CLIENT_PACKET_ID;
    }
}
