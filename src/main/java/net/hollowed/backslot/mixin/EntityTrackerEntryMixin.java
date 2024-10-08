package net.hollowed.backslot.mixin;

import net.hollowed.backslot.networking.BackSlotPacket;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import io.netty.buffer.Unpooled;
import org.spongepowered.asm.mixin.injection.At;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.EntityTrackerEntry;
import net.minecraft.server.network.ServerPlayerEntity;

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
        if (entity instanceof PlayerEntity player) {
            sendPacketIfNotEmpty(serverPlayer);
            sendPacketIfNotEmpty((ServerPlayerEntity) player);
        }
    }

    @Unique
    private void sendPacketIfNotEmpty(ServerPlayerEntity sender) {
        if (!sender.getInventory().getStack(41).isEmpty()) {
            sendBackSlotPacket(sender);
        }
    }

    @Unique
    private void sendBackSlotPacket(ServerPlayerEntity sender) {
        PacketByteBuf data = new PacketByteBuf(Unpooled.buffer());
        data.writeIntArray(new int[]{sender.getId(), 41});
        data.writeItemStack(sender.getInventory().getStack(41));
        ServerPlayNetworking.send(sender, BackSlotPacket.BACKSLOT_CLIENT_PACKET_ID, data);
    }
}