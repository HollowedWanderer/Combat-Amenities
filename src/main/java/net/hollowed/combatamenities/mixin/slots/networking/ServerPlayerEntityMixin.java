package net.hollowed.combatamenities.mixin.slots.networking;

import com.mojang.authlib.GameProfile;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.hollowed.combatamenities.networking.slots.SlotClientPacketPayload;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collection;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity {

    @Unique
    ItemStack backSlotStack = ItemStack.EMPTY;

    @Unique
    ItemStack beltSlotStack = ItemStack.EMPTY;

    public ServerPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile gameProfile) {
        super(world, pos, yaw, gameProfile);
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void tickMixin(CallbackInfo info) {
        if (!this.getWorld().isClient()) {
            ItemStack currentBackSlotStack = this.getInventory().getStack(41);
            ItemStack currentBeltSlotStack = this.getInventory().getStack(42);

            // Check if the current back slot stack is different from the saved one
            if (!ItemStack.areItemsEqual(backSlotStack, currentBackSlotStack)) {
                // Update the back slot stack to the current one
                backSlotStack = currentBackSlotStack.copy();

                // Create a new payload with the current back slot stack
                SlotClientPacketPayload payload = new SlotClientPacketPayload(this.getId(), 41, backSlotStack);
                Collection<ServerPlayerEntity> players = PlayerLookup.tracking((ServerWorld) this.getWorld(), this.getBlockPos());
                players.forEach(player -> ServerPlayNetworking.send(player, payload));
            }

            // Check if the current back slot stack is different from the saved one
            if (!ItemStack.areItemsEqual(beltSlotStack, currentBeltSlotStack)) {
                // Update the back slot stack to the current one
                beltSlotStack = currentBeltSlotStack.copy();

                // Create a new payload with the current back slot stack
                SlotClientPacketPayload payload = new SlotClientPacketPayload(this.getId(), 42, beltSlotStack);
                Collection<ServerPlayerEntity> players = PlayerLookup.tracking((ServerWorld) this.getWorld(), this.getBlockPos());
                players.forEach(player -> ServerPlayNetworking.send(player, payload));
            }
        }
    }
}