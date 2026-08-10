package net.hollowed.combatamenities.mixin.slots.registration;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.MixinIntrinsics;
import org.spongepowered.asm.mixin.Shadow;

import java.util.function.Predicate;

@Mixin(EquipmentSlotGroup.class)
public enum EquipmentSlotGroupMixin {
    COMBATAMENITIES_SLOT(MixinIntrinsics.currentEnumOrdinal(), "combatamenities:slot", slot -> slot.getType() == EquipmentSlot.Type.COMBATAMENITIES_SLOT);

    @Shadow
    EquipmentSlotGroupMixin(int id, String key, Predicate<EquipmentSlot> predicate) {
    }
}
