package net.hollowed.combatamenities.networking.slots;

import net.hollowed.combatamenities.CombatAmenities;
import net.minecraft.item.ItemStack;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

public record SoundPacketPayload(int soundId, Vec3d pos, boolean swap, float volume, float pitch, int itemSoundSelector, ItemStack stack) implements CustomPayload {

    public static final CustomPayload.Id<SoundPacketPayload> ID = new CustomPayload.Id<>(Identifier.of(CombatAmenities.MOD_ID, "slot_sound_packet"));

    public static final PacketCodec<RegistryByteBuf, SoundPacketPayload> CODEC = PacketCodec.of(SoundPacketPayload::write, SoundPacketPayload::new);

    public SoundPacketPayload(RegistryByteBuf buf) {
        this(buf.readInt(), buf.readVec3d(), buf.readBoolean(), buf.readFloat(), buf.readFloat(), buf.readInt(), buf.readBoolean() ? ItemStack.EMPTY : ItemStack.PACKET_CODEC.decode(buf));
    }

    public void write(RegistryByteBuf buf) {
        buf.writeInt(soundId);
        buf.writeVec3d(pos);
        buf.writeBoolean(swap);
        buf.writeFloat(volume);
        buf.writeFloat(pitch);
        buf.writeInt(itemSoundSelector);
        buf.writeBoolean(stack.isEmpty());
        if (!stack.isEmpty()) {
            ItemStack.PACKET_CODEC.encode(buf, stack);
        }
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
