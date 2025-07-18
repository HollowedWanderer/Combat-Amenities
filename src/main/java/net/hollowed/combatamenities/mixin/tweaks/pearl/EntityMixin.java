package net.hollowed.combatamenities.mixin.tweaks.pearl;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.hollowed.combatamenities.networking.slots.SoundPacketPayload;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
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
                if (player instanceof ServerPlayerEntity serverPlayer) {
                    // Play the sound with the calculated volume
                    for (ServerPlayerEntity serverPlayerTemp : serverPlayer.getWorld().getPlayers()) {
                        ServerPlayNetworking.send(serverPlayerTemp, new SoundPacketPayload(1, player.getPos(), false, 0.15F, 1.2F, 0, ItemStack.EMPTY));
                    }
                }
            }
        }
    }
}
