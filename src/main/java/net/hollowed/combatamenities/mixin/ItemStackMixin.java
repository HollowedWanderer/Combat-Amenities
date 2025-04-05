package net.hollowed.combatamenities.mixin;

import net.hollowed.combatamenities.CombatAmenities;
import net.hollowed.combatamenities.util.ExtraSlots;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {

    @Shadow public abstract Item getItem();

    @Unique
    private static final TagKey<Item> KEEP_DURABILITY_TAG = TagKey.of(Registries.ITEM.getKey(), Identifier.of(CombatAmenities.MOD_ID, "keep_durability"));

    // Intercept the method used to damage items and prevent it from applying durability
    @Inject(method = "getDamage", at = @At("HEAD"), cancellable = true)
    private void removeDurability(CallbackInfoReturnable<Integer> cir) {
        if (CombatAmenities.CONFIG.removeDurability && !this.getItem().getRegistryEntry().isIn(KEEP_DURABILITY_TAG)) {
            cir.setReturnValue(0);
        }
    }
}
