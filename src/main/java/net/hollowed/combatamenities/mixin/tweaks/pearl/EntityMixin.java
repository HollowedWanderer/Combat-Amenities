package net.hollowed.combatamenities.mixin.tweaks.pearl;

import net.hollowed.combatamenities.CombatAmenities;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public class EntityMixin {
    @Inject(method = "playStepSound", at = @At("HEAD"))
    private void onPlayStepSound(BlockPos pos, BlockState state, CallbackInfo ci) {
        if ((Object) this instanceof PlayerEntity player) {
            ItemStack backSlotItem = player.getInventory().getStack(41);
            ItemStack beltSlotItem = player.getInventory().getStack(42);

            // Check if back slot item exists and is valid
            if ((!backSlotItem.isEmpty() && !(backSlotItem.getItem() instanceof BlockItem)) || (!beltSlotItem.isEmpty() && !(beltSlotItem.getItem() instanceof BlockItem))) {
                player.getWorld().playSound(
                        null,
                        player.getBlockPos(),
                        SoundEvents.ITEM_ARMOR_EQUIP_CHAIN.value(),
                        SoundCategory.PLAYERS,
                        0.15F * (CombatAmenities.CONFIG.backslotAmbientSoundVolume / 100F),
                        1.2F
                );
            }
        }
    }
}
