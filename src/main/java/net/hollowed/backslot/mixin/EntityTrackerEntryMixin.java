package net.hollowed.backslot.mixin;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.hollowed.backslot.networking.BackSlotClientPacketPayload;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.EntityTrackerEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityTrackerEntry.class)
public class EntityTrackerEntryMixin {
    @Mutable
    @Final
    @Shadow
    private final Entity entity;

    public EntityTrackerEntryMixin(Entity entity) {
        this.entity = entity;
    }

    @Inject(method = "startTracking", at = @At(value = "TAIL"))
    public void startTrackingMixin(ServerPlayerEntity serverPlayer, CallbackInfo info) {
        if (entity instanceof ServerPlayerEntity player) {
            // Send the back slot item from the player to the new server player
            sendBackSlotUpdate(serverPlayer, player);
            // Send the back slot item from the server player to the player itself
            sendBackSlotUpdate(player, player);
        }
    }

    @Unique
    private void sendBackSlotUpdate(ServerPlayerEntity recipient, ServerPlayerEntity sourcePlayer) {
        ItemStack backSlotItem = sourcePlayer.getInventory().getStack(41);

        // Send the back slot item (or an empty item stack if it's empty)
        ServerPlayNetworking.send(recipient,
                new BackSlotClientPacketPayload(sourcePlayer.getId(), 41, backSlotItem.isEmpty() ? ItemStack.EMPTY : backSlotItem));
    }
}

