package net.hollowed.backslot.mixin;

import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {

    // Intercept the method used to damage items and prevent it from applying durability
    @Inject(method = "getDamage", at = @At("HEAD"), cancellable = true)
    private void removeDurability(CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(0);
    }
}
