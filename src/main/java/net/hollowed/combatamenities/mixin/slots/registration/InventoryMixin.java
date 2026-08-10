package net.hollowed.combatamenities.mixin.slots.registration;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Inventory.class)
public class InventoryMixin {
    @Shadow
    @Final
    public static Int2ObjectMap<EquipmentSlot> EQUIPMENT_SLOT_MAPPING;

    static {
        EQUIPMENT_SLOT_MAPPING.put(46, EquipmentSlot.COMBATAMENITIES_BACKSLOT);
        EQUIPMENT_SLOT_MAPPING.put(47, EquipmentSlot.COMBATAMENITIES_BELTSLOT);
    }
}
