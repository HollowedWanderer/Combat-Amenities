package net.hollowed.combatamenities.mixin.tweaks.enchantments;

import net.hollowed.combatamenities.CombatAmenities;
import net.hollowed.combatamenities.config.CAConfig;
import net.minecraft.core.Holder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Enchantment.class)
public abstract class EnchantmentMixin {
    @Shadow public abstract String toString();

    @Inject(method = "canEnchant", at = @At("HEAD"), cancellable = true)
    private void disableDisallowedEnchantments(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (isDisallowedEnchantment(((Enchantment) (Object) this).toString())) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "areCompatible", at = @At("HEAD"), cancellable = true)
    private static void disableDisallowedCombination(Holder<Enchantment> first, Holder<Enchantment> second, CallbackInfoReturnable<Boolean> cir) {
        if (isDisallowedEnchantment(first.value().toString()) || isDisallowedEnchantment(second.value().toString())) {
            cir.setReturnValue(false);
        }
    }

    @Unique
    private static boolean isDisallowedEnchantment(String enchantment) {
        return (CAConfig.removeDurability && CombatAmenities.DURABILITY_ENCHANTMENTS.contains(enchantment))
                || (CAConfig.builtInLoyalty && CombatAmenities.TRIDENT_ENCHANTMENTS.contains(enchantment));
    }
}
