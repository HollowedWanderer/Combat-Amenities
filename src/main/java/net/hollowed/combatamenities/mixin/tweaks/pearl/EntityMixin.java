package net.hollowed.combatamenities.mixin.tweaks.pearl;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.hollowed.combatamenities.networking.slots.SoundPacketPayload;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public class EntityMixin {
    @Inject(method = "playStepSound", at = @At("HEAD"))
    private void onPlayStepSound(BlockPos pos, BlockState state, CallbackInfo ci) {
        if ((Object) this instanceof Player player) {
            ItemStack backSlotItem = player.getInventory().getItem(41);
            ItemStack beltSlotItem = player.getInventory().getItem(42);

            // Check if back slot item exists and is valid
            if ((!backSlotItem.isEmpty() && !(backSlotItem.getItem() instanceof BlockItem)) || (!beltSlotItem.isEmpty() && !(beltSlotItem.getItem() instanceof BlockItem))) {
                if (player instanceof ServerPlayer serverPlayer) {
                    // Play the sound with the calculated volume
                    for (ServerPlayer serverPlayerTemp : serverPlayer.level().players()) {
                        ServerPlayNetworking.send(serverPlayerTemp, new SoundPacketPayload(1, player.position(), false, 0.15F, 1.2F, 0, ItemStack.EMPTY));
                    }
                }
            }
        }
    }
}
