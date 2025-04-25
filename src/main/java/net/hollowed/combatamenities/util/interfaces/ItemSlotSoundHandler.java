package net.hollowed.combatamenities.util.interfaces;

import net.minecraft.sound.SoundEvent;

public interface ItemSlotSoundHandler {
    @SuppressWarnings("unused")
    void combat_Amenities$setSheatheSound(SoundEvent sound);
    void combat_Amenities$setUnsheatheSound(SoundEvent sound);

    SoundEvent combat_Amenities$getSheatheSound();
    SoundEvent combat_Amenities$getUnsheatheSound();
}
