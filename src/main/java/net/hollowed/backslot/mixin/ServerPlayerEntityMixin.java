package net.hollowed.backslot.mixin;

import com.mojang.authlib.GameProfile;
import net.hollowed.backslot.networking.BackSlotPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import io.netty.buffer.Unpooled;
import org.spongepowered.asm.mixin.injection.At;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.world.World;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

import java.util.Collection;
import java.util.Objects;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity {

    @Unique
    private ItemStack backStack = ItemStack.EMPTY;

    public ServerPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile gameProfile) {
        super(world, pos, yaw, gameProfile);
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void onTick(CallbackInfo info) {
        if (this.getWorld().isClient()) return;

        ItemStack currentBackSlotStack = this.getInventory().getStack(41);
        if (!ItemStack.areItemsEqual(backStack, currentBackSlotStack)) {
            sendPacket(currentBackSlotStack);
        }
        backStack = currentBackSlotStack;
    }

    // New method to send back slot data to newly joined player
    @Inject(method = "onSpawn", at = @At("TAIL"))
    private void onSpawnMixin(CallbackInfo info) {
        ServerPlayerEntity player = (ServerPlayerEntity) (Object) this; // Get the player instance

        // Send back slot information to the new player
        PlayerLookup.all(Objects.requireNonNull(player.getServer())).forEach(otherPlayer -> {
            if (otherPlayer != player) { // Don't send to self
                sendBackSlotData(player, otherPlayer);
            }
        });
    }

    @Unique
    private void sendBackSlotData(ServerPlayerEntity recipient, PlayerEntity sender) {
        PacketByteBuf data = new PacketByteBuf(Unpooled.buffer());
        data.writeIntArray(new int[] { sender.getId(), 41 }); // Player ID and slot index
        data.writeItemStack(sender.getInventory().getStack(41)); // ItemStack to send
        ServerPlayNetworking.send(recipient, BackSlotPacket.BACKSLOT_CLIENT_PACKET_ID, data);
    }

    @Unique
    private void sendPacket(ItemStack itemStack) {
        Collection<ServerPlayerEntity> players = PlayerLookup.tracking((ServerWorld) this.getWorld(), this.getBlockPos());
        PacketByteBuf data = new PacketByteBuf(Unpooled.buffer());

        data.writeIntArray(new int[]{this.getId(), 41});
        data.writeItemStack(itemStack);

        players.forEach(player -> ServerPlayNetworking.send(player, BackSlotPacket.BACKSLOT_CLIENT_PACKET_ID, data));
    }
}