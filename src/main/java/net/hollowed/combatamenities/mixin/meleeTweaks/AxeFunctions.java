package net.hollowed.combatamenities.mixin.meleeTweaks;

import net.hollowed.combatamenities.util.interfaces.WeaponRework;
import net.minecraft.item.AxeItem;
import net.minecraft.item.Item;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import org.spongepowered.asm.mixin.Mixin;

import java.util.List;

@Mixin(AxeItem.class)
public class AxeFunctions extends Item implements WeaponRework {
    public AxeFunctions(Settings settings) {
        super(settings);
    }

    @Override
    public int combat_Amenities$delay() {
        return 2;
    }

    @Override
    public List<Object> combat_Amenities$sound() {
        return List.of(SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, SoundCategory.PLAYERS, 1.0F, 1.0F);
    }
}
