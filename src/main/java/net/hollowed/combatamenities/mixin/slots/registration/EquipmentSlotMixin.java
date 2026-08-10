package net.hollowed.combatamenities.mixin.slots.registration;

import net.minecraft.world.entity.EquipmentSlot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.MixinIntrinsics;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(EquipmentSlot.class)
public enum EquipmentSlotMixin {
    COMBATAMENITIES_BACKSLOT(EquipmentSlot.Type.COMBATAMENITIES_SLOT, 0, MixinIntrinsics.currentEnumOrdinal(), "combatamenities:backslot"),
    COMBATAMENITIES_BELTSLOT(EquipmentSlot.Type.COMBATAMENITIES_SLOT, 1, MixinIntrinsics.currentEnumOrdinal(), "combatamenities:beltslot");

    @Shadow
    EquipmentSlotMixin(EquipmentSlot.Type type, int index, int id, String name) {
    }
}
