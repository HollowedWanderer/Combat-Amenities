package net.hollowed.combatamenities.mixin.tweaks.enchantments;

import com.google.common.collect.Lists;
import net.hollowed.combatamenities.CombatAmenities;
import net.hollowed.combatamenities.config.CAConfig;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.WeightedRandom;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantable;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
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
    public static void filterCompatibleEnchantments(List<EnchantmentInstance> possibleEntries, EnchantmentInstance pickedEntry) {
    }

    @Unique
    private static List<EnchantmentInstance> getPossibleEntries(int level, ItemStack stack, Stream<Holder<Enchantment>> possibleEnchantments) {
        List<EnchantmentInstance> list = Lists.newArrayList();
        boolean bl = stack.is(Items.BOOK);
        possibleEnchantments.filter(enchantment -> enchantment.value().isPrimaryItem(stack) || bl).forEach(enchantmentx -> {
            Enchantment enchantment = enchantmentx.value();

            for (int j = enchantment.getMaxLevel(); j >= enchantment.getMinLevel(); j--) {
                if (level >= enchantment.getMinCost(j) && level <= enchantment.getMaxCost(j)) {
                    list.add(new EnchantmentInstance(enchantmentx, j));
                    break;
                }
            }
        });

        list.removeIf(entry -> isDisallowedEnchantment(entry.enchantment().value().toString()));
        return list;
    }

    @Inject(method = "selectEnchantment", at = @At("HEAD"), cancellable = true)
    private static void filterGeneratedEnchantments(RandomSource random, ItemStack stack, int level, Stream<Holder<Enchantment>> possibleEnchantments, CallbackInfoReturnable<List<EnchantmentInstance>> cir) {
        List<EnchantmentInstance> list = Lists.newArrayList();
        Enchantable enchantableComponent = stack.get(DataComponents.ENCHANTABLE);
        if (enchantableComponent == null) {
            cir.setReturnValue(list);
        } else {
            level += 1 + random.nextInt(enchantableComponent.value() / 4 + 1) + random.nextInt(enchantableComponent.value() / 4 + 1);
            float f = (random.nextFloat() + random.nextFloat() - 1.0F) * 0.15F;
            level = Mth.clamp(Math.round((float)level + (float)level * f), 1, Integer.MAX_VALUE);
            List<EnchantmentInstance> list2 = getPossibleEntries(level, stack, possibleEnchantments);
            if (!list2.isEmpty()) {
                WeightedRandom.getRandomItem(random, list2, EnchantmentInstance::weight).ifPresent(list::add);

                while (random.nextInt(50) <= level) {
                    if (!list.isEmpty()) {
                        filterCompatibleEnchantments(list2, list.getLast());
                    }

                    if (list2.isEmpty()) {
                        break;
                    }

                    WeightedRandom.getRandomItem(random, list2, EnchantmentInstance::weight).ifPresent(list::add);
                    level /= 2;
                }
            }

            cir.setReturnValue(list);
        }
    }


    @Unique
    private static boolean isDisallowedEnchantment(String enchantment) {
        return (CAConfig.removeDurability && CombatAmenities.DURABILITY_ENCHANTMENTS.contains(enchantment))
                || (CAConfig.builtInLoyalty && CombatAmenities.TRIDENT_ENCHANTMENTS.contains(enchantment));
    }
}
