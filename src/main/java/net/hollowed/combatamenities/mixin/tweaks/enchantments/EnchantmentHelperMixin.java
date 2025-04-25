package net.hollowed.combatamenities.mixin.tweaks.enchantments;

import com.google.common.collect.Lists;
import net.hollowed.combatamenities.CombatAmenities;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.EnchantableComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Util;
import net.minecraft.util.collection.Weighting;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.stream.Stream;

@Mixin(EnchantmentHelper.class)
public abstract class EnchantmentHelperMixin {

    @Shadow
    public static void removeConflicts(List<EnchantmentLevelEntry> possibleEntries, EnchantmentLevelEntry pickedEntry) {
    }

    @Unique
    private static List<EnchantmentLevelEntry> getPossibleEntries(int level, ItemStack stack, Stream<RegistryEntry<Enchantment>> possibleEnchantments) {
        List<EnchantmentLevelEntry> list = Lists.newArrayList();
        boolean bl = stack.isOf(Items.BOOK);
        possibleEnchantments.filter(enchantment -> enchantment.value().isPrimaryItem(stack) || bl).forEach(enchantmentx -> {
            Enchantment enchantment = enchantmentx.value();

            for (int j = enchantment.getMaxLevel(); j >= enchantment.getMinLevel(); j--) {
                if (level >= enchantment.getMinPower(j) && level <= enchantment.getMaxPower(j)) {
                    list.add(new EnchantmentLevelEntry(enchantmentx, j));
                    break;
                }
            }
        });

        list.removeIf(entry -> isDisallowedEnchantment(entry.enchantment().value().toString()));
        return list;
    }

    @Inject(method = "generateEnchantments", at = @At("HEAD"), cancellable = true)
    private static void filterGeneratedEnchantments(Random random, ItemStack stack, int level, Stream<RegistryEntry<Enchantment>> possibleEnchantments, CallbackInfoReturnable<List<EnchantmentLevelEntry>> cir) {
        List<EnchantmentLevelEntry> list = Lists.newArrayList();
        EnchantableComponent enchantableComponent = stack.get(DataComponentTypes.ENCHANTABLE);
        if (enchantableComponent == null) {
            cir.setReturnValue(list);
        } else {
            level += 1 + random.nextInt(enchantableComponent.value() / 4 + 1) + random.nextInt(enchantableComponent.value() / 4 + 1);
            float f = (random.nextFloat() + random.nextFloat() - 1.0F) * 0.15F;
            level = MathHelper.clamp(Math.round((float)level + (float)level * f), 1, Integer.MAX_VALUE);
            List<EnchantmentLevelEntry> list2 = getPossibleEntries(level, stack, possibleEnchantments);
            if (!list2.isEmpty()) {
                Weighting.getRandom(random, list2, EnchantmentLevelEntry::getWeight).ifPresent(list::add);

                while (random.nextInt(50) <= level) {
                    if (!list.isEmpty()) {
                        removeConflicts(list2, Util.getLast(list));
                    }

                    if (list2.isEmpty()) {
                        break;
                    }

                    Weighting.getRandom(random, list2, EnchantmentLevelEntry::getWeight).ifPresent(list::add);
                    level /= 2;
                }
            }

            cir.setReturnValue(list);
        }
    }


    @Unique
    private static boolean isDisallowedEnchantment(String enchantment) {
        return (CombatAmenities.CONFIG.removeDurability && CombatAmenities.DURABILITY_ENCHANTMENTS.contains(enchantment))
                || (CombatAmenities.CONFIG.builtInLoyalty && CombatAmenities.TRIDENT_ENCHANTMENTS.contains(enchantment));
    }
}
