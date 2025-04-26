package net.hollowed.combatamenities.networking.slots;

import net.hollowed.combatamenities.CombatAmenities;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record SlotCreativeClientPacketPayload(int entityId, int slotId, ItemStack itemStack) implements CustomPayload {

    public static final Id<SlotCreativeClientPacketPayload> ID = new Id<>(Identifier.of(CombatAmenities.MOD_ID, "backslot_creative_client_packet"));

    public static final PacketCodec<RegistryByteBuf, SlotCreativeClientPacketPayload> CODEC = PacketCodec.of(SlotCreativeClientPacketPayload::write, SlotCreativeClientPacketPayload::new);

    public SlotCreativeClientPacketPayload(RegistryByteBuf buf) {
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
        return ID;
    }
}
