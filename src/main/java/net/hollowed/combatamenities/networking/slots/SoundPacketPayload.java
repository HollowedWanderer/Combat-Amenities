package net.hollowed.combatamenities.networking.slots;

import net.hollowed.combatamenities.CombatAmenities;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public record SoundPacketPayload(int soundId, Vec3 pos, boolean swap, float volume, float pitch, int itemSoundSelector, ItemStack stack) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<@NotNull SoundPacketPayload> ID = new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath(CombatAmenities.MOD_ID, "slot_sound_packet"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SoundPacketPayload> CODEC = StreamCodec.ofMember(SoundPacketPayload::write, SoundPacketPayload::new);

    public SoundPacketPayload(RegistryFriendlyByteBuf buf) {
        this(buf.readInt(), buf.readVec3(), buf.readBoolean(), buf.readFloat(), buf.readFloat(), buf.readInt(), buf.readBoolean() ? ItemStack.EMPTY : ItemStack.STREAM_CODEC.decode(buf));
    }

    public void write(RegistryFriendlyByteBuf buf) {
        buf.writeInt(soundId);
        buf.writeVec3(pos);
        buf.writeBoolean(swap);
        buf.writeFloat(volume);
        buf.writeFloat(pitch);
        buf.writeInt(itemSoundSelector);
        buf.writeBoolean(stack.isEmpty());
        if (!stack.isEmpty()) {
            ItemStack.STREAM_CODEC.encode(buf, stack);
        }
    }

    @Override
    public @NotNull Type<? extends @NotNull CustomPacketPayload> type() {
        return ID;
    }
}
