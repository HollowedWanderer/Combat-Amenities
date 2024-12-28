package net.hollowed.combatamenities.mixin;

import net.hollowed.combatamenities.CombatAmenities;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Enchantment.class)
public abstract class EnchantmentMixin {
    @Shadow public abstract String toString();

    @Inject(method = "isAcceptableItem", at = @At("HEAD"), cancellable = true)
    private void disableDisallowedEnchantments(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (isDisallowedEnchantment(((Enchantment) (Object) this).toString())) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "canBeCombined", at = @At("HEAD"), cancellable = true)
    private static void disableDisallowedCombination(RegistryEntry<Enchantment> first, RegistryEntry<Enchantment> second, CallbackInfoReturnable<Boolean> cir) {
        if (isDisallowedEnchantment(first.value().toString()) || isDisallowedEnchantment(second.value().toString())) {
            cir.setReturnValue(false);
        }
    }

    @Unique
    private static boolean isDisallowedEnchantment(String enchantment) {
        return (CombatAmenities.CONFIG.removeDurability && CombatAmenities.DURABILITY_ENCHANTMENTS.contains(enchantment))
                || (CombatAmenities.CONFIG.builtInLoyalty && CombatAmenities.TRIDENT_ENCHANTMENTS.contains(enchantment));
    }
}
