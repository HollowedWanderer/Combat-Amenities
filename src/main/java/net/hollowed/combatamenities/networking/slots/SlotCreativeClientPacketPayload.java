package net.hollowed.combatamenities.networking.slots;

import net.hollowed.combatamenities.CombatAmenities;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public record SlotCreativeClientPacketPayload(int slotId, ItemStack itemStack) implements CustomPacketPayload {

    public static final Type<@NotNull SlotCreativeClientPacketPayload> ID = new Type<>(Identifier.fromNamespaceAndPath(CombatAmenities.MOD_ID, "backslot_creative_client_packet"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SlotCreativeClientPacketPayload> CODEC = StreamCodec.ofMember(SlotCreativeClientPacketPayload::write, SlotCreativeClientPacketPayload::new);

    public SlotCreativeClientPacketPayload(RegistryFriendlyByteBuf buf) {
        this(buf.readInt(), buf.readBoolean() ? ItemStack.EMPTY : ItemStack.STREAM_CODEC.decode(buf));
    }

    public void write(RegistryFriendlyByteBuf buf) {
        buf.writeInt(slotId);
        buf.writeBoolean(itemStack.isEmpty());
        if (!itemStack.isEmpty()) {
            ItemStack.STREAM_CODEC.encode(buf, itemStack);
        }
    }

    @Override
    public @NotNull Type<? extends @NotNull CustomPacketPayload> type() {
        return ID;
    }
}
