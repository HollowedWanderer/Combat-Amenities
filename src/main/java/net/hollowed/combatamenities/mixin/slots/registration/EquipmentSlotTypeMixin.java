package net.hollowed.combatamenities.mixin.slots.registration;

import net.minecraft.world.entity.EquipmentSlot;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(EquipmentSlot.Type.class)
public enum EquipmentSlotTypeMixin {
    COMBATAMENITIES_SLOT
}
