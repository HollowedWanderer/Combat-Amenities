package net.hollowed.combatamenities.util.items;

import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.item.ItemStack;

import static net.minecraft.component.DataComponentTypes.ENCHANTMENTS;

@SuppressWarnings("unused")
public class EnchantmentListener {

    public static boolean hasEnchantment(ItemStack stack, String enchantKey) {
        final var enchantments = stack.getOrDefault(ENCHANTMENTS, ItemEnchantmentsComponent.DEFAULT).getEnchantmentEntries();

        for (final var entry : enchantments) {
            String enchant = entry.getKey().getIdAsString();

            if (enchant.contains(enchantKey)) {
                return true;
            }
        }

        return false;
    }
}
