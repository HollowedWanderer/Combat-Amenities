package net.hollowed.combatamenities.mixin.tweaks.enchantments;

import net.hollowed.combatamenities.CombatAmenities;
import net.hollowed.combatamenities.config.CAConfig;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryWrapper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.stream.IntStream;

@Mixin(targets = "net.minecraft.item.ItemGroups")
public abstract class ItemGroupMixin {

    @Inject(method = "addMaxLevelEnchantedBooks", at = @At("HEAD"), cancellable = true)
    private static void filterMaxLevelBooks(ItemGroup.Entries entries, RegistryWrapper<Enchantment> registryWrapper, ItemGroup.StackVisibility stackVisibility, CallbackInfo ci) {
        registryWrapper.streamEntries()
                .map((enchantmentEntry) -> EnchantmentHelper.getEnchantedBookWith(
                        new EnchantmentLevelEntry(enchantmentEntry, enchantmentEntry.value().getMaxLevel())))
                .filter(ItemGroupMixin::isAllowedBook)
                .forEach((stack) -> entries.add(stack, stackVisibility));

        ci.cancel();
    }

    @Inject(method = "addAllLevelEnchantedBooks", at = @At("HEAD"), cancellable = true)
    private static void filterAllLevelBooks(ItemGroup.Entries entries, RegistryWrapper<Enchantment> registryWrapper, ItemGroup.StackVisibility stackVisibility, CallbackInfo ci) {
        registryWrapper.streamEntries()
                .flatMap((enchantmentEntry) -> IntStream.rangeClosed(
                                enchantmentEntry.value().getMinLevel(),
                                enchantmentEntry.value().getMaxLevel())
                        .mapToObj((level) -> EnchantmentHelper.getEnchantedBookWith(
                                new EnchantmentLevelEntry(enchantmentEntry, level))))
                .filter(ItemGroupMixin::isAllowedBook)
                .forEach((stack) -> entries.add(stack, stackVisibility));

        ci.cancel();
    }

    @Unique
    private static boolean isAllowedBook(ItemStack stack) {
        return EnchantmentHelper.getEnchantments(stack).getEnchantments().stream().noneMatch(enchantment -> {
            String id = enchantment.value().toString();
            return isDisallowedEnchantment(id);
        });
    }

    @Unique
    private static boolean isDisallowedEnchantment(String enchantment) {
        return (CAConfig.removeDurability && CombatAmenities.DURABILITY_ENCHANTMENTS.contains(enchantment))
                || (CAConfig.builtInLoyalty && CombatAmenities.TRIDENT_ENCHANTMENTS.contains(enchantment));
    }
}
