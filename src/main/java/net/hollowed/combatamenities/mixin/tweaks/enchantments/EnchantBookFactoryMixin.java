package net.hollowed.combatamenities.mixin.tweaks.enchantments;

import net.hollowed.combatamenities.CombatAmenities;
import net.hollowed.combatamenities.config.CAConfig;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.EnchantmentTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOffers;
import net.minecraft.village.TradedItem;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(TradeOffers.EnchantBookFactory.class)
public class EnchantBookFactoryMixin {
    @Shadow @Final private TagKey<Enchantment> possibleEnchantments;

    @Shadow @Final private int minLevel;

    @Shadow @Final private int maxLevel;

    @Shadow @Final private int experience;

    @Inject(method = "create", at = @At("HEAD"), cancellable = true)
    private void modifyTradeOffer(Entity entity, Random random, CallbackInfoReturnable<TradeOffer> cir) {
        Optional<RegistryEntry<Enchantment>> optional = entity.getEntityWorld().getRegistryManager().getOrThrow(RegistryKeys.ENCHANTMENT).getRandomEntry(this.possibleEnchantments, random);
        int l;
        ItemStack itemStack;

        if (optional.isPresent()) {
            RegistryEntry<Enchantment> registryEntry = optional.get();
            Enchantment enchantment = registryEntry.value();
            int i = Math.max(enchantment.getMinLevel(), this.minLevel);
            int j = Math.min(enchantment.getMaxLevel(), this.maxLevel);
            int k = MathHelper.nextInt(random, i, j);
            itemStack = EnchantmentHelper.getEnchantedBookWith(new EnchantmentLevelEntry(registryEntry, k));

            if (isDisallowedEnchantment(enchantment.toString())) {
                itemStack = Items.IRON_CHAIN.getDefaultStack();
            }

            l = 2 + random.nextInt(5 + k * 10) + 3 * k;

            if (registryEntry.isIn(EnchantmentTags.DOUBLE_TRADE_PRICE)) {
                l *= 2;
            }

            if (l > 64) {
                l = 64;
            }
        } else {
            l = 1;
            itemStack = new ItemStack(Items.BOOK);
        }

        if (itemStack.getItem() == Items.IRON_CHAIN) {
            itemStack.setCount(6);
            cir.setReturnValue(new TradeOffer(new TradedItem(Items.EMERALD, 1), itemStack, 12, this.experience, 0.2F));
        } else {
            cir.setReturnValue(new TradeOffer(new TradedItem(Items.EMERALD, l), Optional.of(new TradedItem(Items.BOOK, 1)), itemStack, 12, this.experience, 0.2F));
        }
    }

    @Unique
    private static boolean isDisallowedEnchantment(String enchantment) {
        return (CAConfig.removeDurability && CombatAmenities.DURABILITY_ENCHANTMENTS.contains(enchantment))
                || (CAConfig.builtInLoyalty && CombatAmenities.TRIDENT_ENCHANTMENTS.contains(enchantment));
    }
}
