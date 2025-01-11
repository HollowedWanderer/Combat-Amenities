package net.hollowed.combatamenities.mixin;

import com.google.common.collect.Lists;
import net.hollowed.combatamenities.CombatAmenities;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Util;
import net.minecraft.util.collection.Weighting;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static net.minecraft.enchantment.EnchantmentHelper.removeConflicts;

@Mixin(EnchantmentHelper.class)
public class EnchantmentHelperMixin {
    @Inject(method = "getPossibleEntries", at = @At("RETURN"), cancellable = true)
    private static void filterDisallowedEnchantments(int level, ItemStack stack, Stream<RegistryEntry<Enchantment>> possibleEnchantments, CallbackInfoReturnable<List<EnchantmentLevelEntry>> cir) {
        List<EnchantmentLevelEntry> entries = cir.getReturnValue();
        entries.removeIf(entry -> isDisallowedEnchantment(entry.enchantment.value().toString()));
        cir.setReturnValue(entries);
    }

    @Inject(method = "generateEnchantments", at = @At("HEAD"), cancellable = true)
    private static void filterGeneratedEnchantments(Random random, ItemStack stack, int level, Stream<RegistryEntry<Enchantment>> possibleEnchantments, CallbackInfoReturnable<List<EnchantmentLevelEntry>> cir) {
        List<EnchantmentLevelEntry> possibleEntries = EnchantmentHelper.getPossibleEntries(level, stack, possibleEnchantments);
        possibleEntries.removeIf(entry -> isDisallowedEnchantment(entry.enchantment.value().toString()));

        if (possibleEntries.isEmpty()) {
            // If no valid enchantments remain, return an empty list to prevent crashes.
            cir.setReturnValue(List.of());
            return;
        }

        // Now generate enchantments from the filtered list
        List<EnchantmentLevelEntry> result = Util.make(Lists.newArrayList(), list -> {
            int adjustedLevel = level + 1 + random.nextInt(level / 4 + 1) + random.nextInt(level / 4 + 1);
            float variance = (random.nextFloat() + random.nextFloat() - 1.0F) * 0.15F;
            adjustedLevel = MathHelper.clamp(Math.round(adjustedLevel + adjustedLevel * variance), 1, Integer.MAX_VALUE);

            Optional<EnchantmentLevelEntry> randomEntry = Weighting.getRandom(random, possibleEntries);
            randomEntry.ifPresent(list::add);

            while (random.nextInt(50) <= adjustedLevel) {
                if (!list.isEmpty()) {
                    removeConflicts(possibleEntries, list.get(list.size() - 1));
                }

                if (possibleEntries.isEmpty()) {
                    break;
                }

                randomEntry = Weighting.getRandom(random, possibleEntries);
                randomEntry.ifPresent(list::add);
                adjustedLevel /= 2;
            }
        });

        cir.setReturnValue(result);
    }

    @Unique
    private static boolean isDisallowedEnchantment(String enchantment) {
        return (CombatAmenities.CONFIG.removeDurability && CombatAmenities.DURABILITY_ENCHANTMENTS.contains(enchantment))
                || (CombatAmenities.CONFIG.builtInLoyalty && CombatAmenities.TRIDENT_ENCHANTMENTS.contains(enchantment));
    }
}
