package net.hollowed.combatamenities.mixin.meleeTweaks;

import net.hollowed.combatamenities.util.WeaponRework;
import net.minecraft.item.MaceItem;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import org.spongepowered.asm.mixin.Mixin;

import java.util.List;

@Mixin(MaceItem.class)
public class MaceFunctions implements WeaponRework {

    @Override
    public int combat_Amenities$delay() {
        return 7;
    }

    @Override
    public List<Object> combat_Amenities$sound() {
        return List.of(SoundEvents.BLOCK_HEAVY_CORE_PLACE, SoundCategory.PLAYERS, 1.0F, 1.3F);
    }
}
