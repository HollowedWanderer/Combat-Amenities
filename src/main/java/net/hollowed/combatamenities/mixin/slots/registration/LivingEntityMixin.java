package net.hollowed.combatamenities.mixin.slots.registration;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
    @Inject(
            method = "getEquipmentSlot",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void getEquipmentSlot(int slot, CallbackInfoReturnable<EquipmentSlot> cir) {
        if (slot == 99 + EquipmentSlot.COMBATAMENITIES_BACKSLOT.getId()) {
            cir.setReturnValue(EquipmentSlot.COMBATAMENITIES_BACKSLOT);
        } else if (slot == 99 + EquipmentSlot.COMBATAMENITIES_BELTSLOT.getId()) {
            cir.setReturnValue(EquipmentSlot.COMBATAMENITIES_BELTSLOT);
        }
    }
}
