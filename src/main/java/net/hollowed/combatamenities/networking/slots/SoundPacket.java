package net.hollowed.combatamenities.networking.slots;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.hollowed.combatamenities.config.CAConfig;
import net.hollowed.combatamenities.util.json.ItemTransformData;
import net.hollowed.combatamenities.util.json.ItemTransformResourceReloadListener;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public class SoundPacket {
    public static void registerClientPacket() {
        ClientPlayNetworking.registerGlobalReceiver(SoundPacketPayload.ID, (payload, context) -> context.client().execute(() -> {
            Vec3 pos = payload.pos();
            Player player = context.player();
            SoundEvent sound = SoundEvents.EMPTY;

            if (payload.stack() != null) {
                ItemStack stack = payload.stack();
                ItemTransformData data = ItemTransformResourceReloadListener.getTransform(BuiltInRegistries.ITEM.getKey(stack.getItem()));
                switch (payload.itemSoundSelector()) {
                    case 1 -> sound = BuiltInRegistries.SOUND_EVENT.getValue(data.unsheatheId());
                    case 2 -> sound = BuiltInRegistries.SOUND_EVENT.getValue(data.sheatheId());
                }
            }
            
            if (sound == null) sound = SoundEvents.EMPTY;

            player.level().playLocalSound(pos.x(), pos.y(), pos.z(), sound, SoundSource.PLAYERS, payload.volume() * ((float) (payload.swap() ? CAConfig.backslotSwapSoundVolume : CAConfig.backslotAmbientSoundVolume) / 100), payload.pitch(), true);
        }));
    }
}
