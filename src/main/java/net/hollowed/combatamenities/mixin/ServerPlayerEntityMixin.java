package net.hollowed.combatamenities.mixin;

import com.mojang.authlib.GameProfile;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.hollowed.combatamenities.networking.BackSlotClientPacketPayload;
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

    public ServerPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile gameProfile) {
        super(world, pos, yaw, gameProfile);
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void tickMixin(CallbackInfo info) {
        if (!this.getWorld().isClient()) {
            ItemStack currentBackSlotStack = this.getInventory().getStack(41);

            // Check if the current back slot stack is different from the saved one
            if (!ItemStack.areItemsEqual(backSlotStack, currentBackSlotStack)) {
                // Update the back slot stack to the current one
                backSlotStack = currentBackSlotStack.copy();

                // Create a new payload with the current back slot stack
                BackSlotClientPacketPayload payload = new BackSlotClientPacketPayload(this.getId(), 41, backSlotStack);
                Collection<ServerPlayerEntity> players = PlayerLookup.tracking((ServerWorld) this.getWorld(), this.getBlockPos());
                players.forEach(player -> ServerPlayNetworking.send(player, payload));
            }
        }
    }
}