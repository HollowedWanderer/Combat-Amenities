package net.hollowed.combatamenities.mixin.slots.networking;

import com.mojang.authlib.GameProfile;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.hollowed.combatamenities.networking.slots.SlotClientPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collection;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerEntityMixin extends Player {

    @Shadow public abstract @NotNull ServerLevel level();

    @Unique
    ItemStack backSlotStack = ItemStack.EMPTY;

    @Unique
    ItemStack beltSlotStack = ItemStack.EMPTY;

    public ServerPlayerEntityMixin(Level world, GameProfile gameProfile) {
        super(world, gameProfile);
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void tickMixin(CallbackInfo info) {
        if (!this.level().isClientSide()) {
            ItemStack currentBackSlotStack = this.getInventory().getItem(41);
            ItemStack currentBeltSlotStack = this.getInventory().getItem(42);

            // Check if the current back slot stack is different from the saved one
            if (!ItemStack.isSameItem(backSlotStack, currentBackSlotStack)) {
                // Update the back slot stack to the current one
                backSlotStack = currentBackSlotStack.copy();

                // Create a new payload with the current back slot stack
                SlotClientPacketPayload payload = new SlotClientPacketPayload(this.getId(), 41, backSlotStack);
                Collection<ServerPlayer> players = PlayerLookup.tracking(this.level(), this.blockPosition());
                players.forEach(player -> ServerPlayNetworking.send(player, payload));
            }

            // Check if the current back slot stack is different from the saved one
            if (!ItemStack.isSameItem(beltSlotStack, currentBeltSlotStack)) {
                // Update the back slot stack to the current one
                beltSlotStack = currentBeltSlotStack.copy();

                // Create a new payload with the current back slot stack
                SlotClientPacketPayload payload = new SlotClientPacketPayload(this.getId(), 42, beltSlotStack);
                Collection<ServerPlayer> players = PlayerLookup.tracking(this.level(), this.blockPosition());
                players.forEach(player -> ServerPlayNetworking.send(player, payload));
            }
        }
    }
}