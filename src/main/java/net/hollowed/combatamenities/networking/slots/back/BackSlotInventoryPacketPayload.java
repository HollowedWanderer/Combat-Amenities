package net.hollowed.combatamenities.networking.slots.back;

import net.hollowed.combatamenities.CombatAmenities;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record BackSlotInventoryPacketPayload(ItemStack itemStack, int i) implements CustomPayload {

    public static final Id<BackSlotInventoryPacketPayload> BACKSLOT_INVENTORY_PACKET_ID = new Id<>(Identifier.of(CombatAmenities.MOD_ID, "backslot_inventory_packet"));

    public static final PacketCodec<RegistryByteBuf, BackSlotInventoryPacketPayload> CODEC = PacketCodec.of(BackSlotInventoryPacketPayload::write, BackSlotInventoryPacketPayload::new);

    public BackSlotInventoryPacketPayload(RegistryByteBuf buf) {
        this(buf.readBoolean() ? ItemStack.EMPTY : ItemStack.PACKET_CODEC.decode(buf), buf.readInt());
    }

    public void write(RegistryByteBuf buf) {
        buf.writeBoolean(itemStack.isEmpty());
        if (!itemStack.isEmpty()) {
            ItemStack.PACKET_CODEC.encode(buf, itemStack);
        }
        buf.writeInt(i);
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return BACKSLOT_INVENTORY_PACKET_ID;
    }
}
