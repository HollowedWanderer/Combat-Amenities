package net.hollowed.combatamenities.mixin.meleeTweaks;

import net.hollowed.combatamenities.util.interfaces.WeaponRework;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.MaceItem;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

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

    @Inject(method = "shouldDealAdditionalDamage", at = @At("HEAD"), cancellable = true)
    private static void shouldDealAdditionalDamage(LivingEntity attacker, CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(attacker.fallDistance > 7 && !attacker.isGliding());
    }
}
