package net.hollowed.combatamenities.mixin.meleeTweaks;

import net.hollowed.combatamenities.util.interfaces.WeaponRework;
import net.minecraft.item.TridentItem;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import org.spongepowered.asm.mixin.Mixin;

import java.util.List;

@Mixin(TridentItem.class)
public class TridentFunctions implements WeaponRework {
    @Override
    public int combat_Amenities$delay() {
        return 2;
    }

    @Override
    public List<Object> combat_Amenities$sound() {
        return List.of(SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, SoundCategory.PLAYERS, 1.0F, 1.0F);
    }
}
