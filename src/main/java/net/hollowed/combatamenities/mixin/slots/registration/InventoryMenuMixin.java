package net.hollowed.combatamenities.mixin.slots.registration;

import net.minecraft.world.inventory.InventoryMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(InventoryMenu.class)
public class InventoryMenuMixin {
    @Inject(
            method = "isHotbarSlot",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void isHotbarSlot(int slot, CallbackInfoReturnable<Boolean> cir) {
        if (slot == 46 || slot == 47) {
            cir.setReturnValue(true);
        }
    }
}
