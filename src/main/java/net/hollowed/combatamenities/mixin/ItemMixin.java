package net.hollowed.combatamenities.mixin;

import net.hollowed.combatamenities.util.ItemSlotSoundHandler;
import net.minecraft.item.Item;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(Item.class)
public class ItemMixin implements ItemSlotSoundHandler {

    @Unique
    SoundEvent sheathe = SoundEvents.INTENTIONALLY_EMPTY;
    @Unique
    SoundEvent unsheathe = SoundEvents.INTENTIONALLY_EMPTY;

    @Override
    public void combat_Amenities$setSheatheSound(SoundEvent sound) {
        this.sheathe = sound;
    }

    @Override
    public void combat_Amenities$setUnsheatheSound(SoundEvent sound) {
        this.unsheathe = sound;
    }

    @Override
    public SoundEvent combat_Amenities$getSheatheSound() {
        return this.sheathe;
    }

    @Override
    public SoundEvent combat_Amenities$getUnsheatheSound() {
        return this.unsheathe;
    }
}
