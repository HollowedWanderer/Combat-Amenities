package net.hollowed.combatamenities.mixin.tweaks.enchantments;

import net.hollowed.combatamenities.CombatAmenities;
import net.hollowed.combatamenities.config.CAConfig;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.stream.IntStream;

@Mixin(targets = "net.minecraft.world.item.CreativeModeTabs")
public abstract class ItemGroupMixin {

    @Inject(method = "generateEnchantmentBookTypesOnlyMaxLevel", at = @At("HEAD"), cancellable = true)
    private static void filterMaxLevelBooks(CreativeModeTab.Output entries, HolderLookup<Enchantment> registryWrapper, CreativeModeTab.TabVisibility stackVisibility, CallbackInfo ci) {
        registryWrapper.listElements()
                .map((enchantmentEntry) -> EnchantmentHelper.createBook(
                        new EnchantmentInstance(enchantmentEntry, enchantmentEntry.value().getMaxLevel())))
                .filter(ItemGroupMixin::isAllowedBook)
                .forEach((stack) -> entries.accept(stack, stackVisibility));

        ci.cancel();
    }

    @Inject(method = "generateEnchantmentBookTypesAllLevels", at = @At("HEAD"), cancellable = true)
    private static void filterAllLevelBooks(CreativeModeTab.Output entries, HolderLookup<Enchantment> registryWrapper, CreativeModeTab.TabVisibility stackVisibility, CallbackInfo ci) {
        registryWrapper.listElements()
                .flatMap((enchantmentEntry) -> IntStream.rangeClosed(
                                enchantmentEntry.value().getMinLevel(),
                                enchantmentEntry.value().getMaxLevel())
                        .mapToObj((level) -> EnchantmentHelper.createBook(
                                new EnchantmentInstance(enchantmentEntry, level))))
                .filter(ItemGroupMixin::isAllowedBook)
                .forEach((stack) -> entries.accept(stack, stackVisibility));

        ci.cancel();
    }

    @Unique
    private static boolean isAllowedBook(ItemStack stack) {
        return EnchantmentHelper.getEnchantmentsForCrafting(stack).keySet().stream().noneMatch(enchantment -> {
            String id = enchantment.value().toString();
            return isDisallowedEnchantment(id);
        });
    }

    @Unique
    private static boolean isDisallowedEnchantment(String enchantment) {
        return (CAConfig.builtInLoyalty && CombatAmenities.TRIDENT_ENCHANTMENTS.contains(enchantment));
    }
}
