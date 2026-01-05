package net.hollowed.combatamenities.mixin.tweaks.enchantments;

import net.hollowed.combatamenities.CombatAmenities;
import net.hollowed.combatamenities.config.CAConfig;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.villager.VillagerTrades;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(VillagerTrades.EnchantBookForEmeralds.class)
public class EnchantBookFactoryMixin {
    @Shadow @Final private TagKey<Enchantment> tradeableEnchantments;

    @Shadow @Final private int minLevel;

    @Shadow @Final private int maxLevel;

    @Shadow @Final private int villagerXp;

    @Inject(method = "getOffer", at = @At("HEAD"), cancellable = true)
    private void modifyTradeOffer(ServerLevel serverLevel, Entity entity, RandomSource random, CallbackInfoReturnable<MerchantOffer> cir) {
        Optional<Holder<Enchantment>> optional = entity.level().registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getRandomElementOf(this.tradeableEnchantments, random);
        int l;
        ItemStack itemStack;

        if (optional.isPresent()) {
            Holder<Enchantment> registryEntry = optional.get();
            Enchantment enchantment = registryEntry.value();
            int i = Math.max(enchantment.getMinLevel(), this.minLevel);
            int j = Math.min(enchantment.getMaxLevel(), this.maxLevel);
            int k = Mth.nextInt(random, i, j);
            itemStack = EnchantmentHelper.createBook(new EnchantmentInstance(registryEntry, k));

            if (isDisallowedEnchantment(enchantment.toString())) {
                itemStack = Items.IRON_CHAIN.getDefaultInstance();
            }

            l = 2 + random.nextInt(5 + k * 10) + 3 * k;

            if (registryEntry.is(EnchantmentTags.DOUBLE_TRADE_PRICE)) {
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
            cir.setReturnValue(new MerchantOffer(new ItemCost(Items.EMERALD, 1), itemStack, 12, this.villagerXp, 0.2F));
        } else {
            cir.setReturnValue(new MerchantOffer(new ItemCost(Items.EMERALD, l), Optional.of(new ItemCost(Items.BOOK, 1)), itemStack, 12, this.villagerXp, 0.2F));
        }
    }

    @Unique
    private static boolean isDisallowedEnchantment(String enchantment) {
        return (CAConfig.builtInLoyalty && CombatAmenities.TRIDENT_ENCHANTMENTS.contains(enchantment));
    }
}
