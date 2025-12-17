package net.hollowed.combatamenities.mixin.tweaks.durability;

import net.hollowed.combatamenities.CombatAmenities;
import net.hollowed.combatamenities.config.CAConfig;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {

    @Shadow public abstract Item getItem();

    @Shadow public abstract boolean isEnchanted();

    @Unique
    private static final TagKey<Item> KEEP_DURABILITY_TAG = TagKey.create(BuiltInRegistries.ITEM.key(), Identifier.fromNamespaceAndPath(CombatAmenities.MOD_ID, "keep_durability"));

    // Intercept the method used to damage items and prevent it from applying durability
    @Inject(method = "getDamageValue", at = @At("HEAD"), cancellable = true)
    private void removeDurability(CallbackInfoReturnable<Integer> cir) {
        if (CAConfig.removeDurability && !this.getItem().getDefaultInstance().getItemHolder().is(KEEP_DURABILITY_TAG) && this.isEnchanted()) {
            cir.setReturnValue(0);
        }
    }
}
