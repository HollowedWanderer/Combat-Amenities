package net.hollowed.combatamenities.networking.slots;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.hollowed.combatamenities.CombatAmenities;
import net.hollowed.combatamenities.util.json.ItemTransformData;
import net.hollowed.combatamenities.util.json.ItemTransformResourceReloadListener;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;

public class SoundPacket {
    public static void registerClientPacket() {
        ClientPlayNetworking.registerGlobalReceiver(SoundPacketPayload.ID, (payload, context) -> context.client().execute(() -> {
            int soundId = payload.soundId();
            Vec3d pos = payload.pos();
            PlayerEntity player = context.player();
            SoundEvent sound = switch (soundId) {
                case 0 -> SoundEvents.ITEM_ARMOR_EQUIP_DIAMOND.value();
                case 1 -> SoundEvents.ITEM_ARMOR_EQUIP_CHAIN.value();
                default -> SoundEvents.INTENTIONALLY_EMPTY;
            };

            if (payload.stack() != null) {
                ItemStack stack = payload.stack();
                ItemTransformData data = ItemTransformResourceReloadListener.getTransform(Registries.ITEM.getId(stack.getItem()));
                switch (payload.itemSoundSelector()) {
                    case 1 -> sound = Registries.SOUND_EVENT.get(data.unsheatheId());
                    case 2 -> sound = Registries.SOUND_EVENT.get(data.sheatheId());
                }
            }

            player.getWorld().playSoundClient(pos.getX(), pos.getY(), pos.getZ(), sound, SoundCategory.PLAYERS, payload.volume() * ((float) (payload.swap() ? CombatAmenities.CONFIG.backslotSwapSoundVolume : CombatAmenities.CONFIG.backslotAmbientSoundVolume) / 100), payload.pitch(), true);
        }));
    }
}
