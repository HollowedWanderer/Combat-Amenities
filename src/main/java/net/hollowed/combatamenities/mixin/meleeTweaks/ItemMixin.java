package net.hollowed.combatamenities.mixin.meleeTweaks;

import net.hollowed.combatamenities.util.WeaponRework;
import net.minecraft.item.Item;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import org.spongepowered.asm.mixin.Mixin;

import java.util.List;

@Mixin(Item.class)
public class ItemMixin implements WeaponRework {

    @Override
    public int combat_Amenities$delay() {
        return 0;
    }

    @Override
    public List<Object> combat_Amenities$sound() {
        return List.of(SoundEvents.INTENTIONALLY_EMPTY, SoundCategory.MASTER, 0.0F, 0.0F);
    }
}
