package net.hollowed.combatamenities.mixin.slots.networking;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.hollowed.combatamenities.networking.slots.SlotClientPacketPayload;
import net.hollowed.combatamenities.networking.slots.SoundPacketPayload;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.UUID;

@Mixin(ServerEntity.class)
public class EntityTrackerEntryMixin {
    @Mutable
    @Final
    @Shadow
    private final Entity entity;
    @Unique
    private static final HashMap<UUID, Vec3> previousPositions = new HashMap<>();
    @Unique
    private final Queue<Float> verticalVelocityHistory = new LinkedList<>();
    @Unique
    private boolean wasOnGroundLastTick = true;

    public EntityTrackerEntryMixin(Entity entity) {
        this.entity = entity;
    }

    @Inject(method = "addPairing", at = @At(value = "TAIL"))
    public void startTrackingMixin(ServerPlayer serverPlayer, CallbackInfo info) {
        if (entity instanceof ServerPlayer player) {
            sendBackSlotUpdate(serverPlayer, player);
            sendBackSlotUpdate(player, player);
        }
    }

    @Inject(method = "sendChanges", at = @At(value = "HEAD"))
    public void onTick(CallbackInfo info) {
        if (entity instanceof Player player) {
            UUID playerId = player.getUUID();

            Vec3 currentPosition = player.position();
            Vec3 previousPosition = previousPositions.getOrDefault(playerId, currentPosition);
            Vec3 velocity = currentPosition.subtract(previousPosition);

            Item backStack = player.getInventory().getItem(41).getItem();

            if (!(backStack instanceof BlockItem) && player.getInventory().getItem(41) != ItemStack.EMPTY) {
                if (detectLanding(player)) {
                    playLandingSound(player, velocity.y);
                }
            }

            int VELOCITY_HISTORY_SIZE = 5;
            if (verticalVelocityHistory.size() >= VELOCITY_HISTORY_SIZE) {
                verticalVelocityHistory.poll();
            }

            verticalVelocityHistory.offer((float) velocity.y);
            previousPositions.put(playerId, currentPosition);
            wasOnGroundLastTick = player.onGround();
        }
    }

    @Unique
    private void sendBackSlotUpdate(ServerPlayer recipient, ServerPlayer sourcePlayer) {
        ItemStack backSlotItem = sourcePlayer.getInventory().getItem(41);
        ItemStack beltSlotItem = sourcePlayer.getInventory().getItem(42);

        ServerPlayNetworking.send(recipient,
                new SlotClientPacketPayload(sourcePlayer.getId(), 41, backSlotItem.isEmpty() ? ItemStack.EMPTY : backSlotItem));
        ServerPlayNetworking.send(recipient,
                new SlotClientPacketPayload(sourcePlayer.getId(), 42, beltSlotItem.isEmpty() ? ItemStack.EMPTY : beltSlotItem));
    }

    @Unique
    private boolean detectLanding(Player playerEntity) {
        if (playerEntity.onGround() && !wasOnGroundLastTick) {
            for (float velocity : verticalVelocityHistory) {
                if (velocity < -0.1F) {
                    return true;
                }
            }
        }
        return false;
    }

    @Unique
    private void playLandingSound(Player playerEntity, double verticalVelocity) {
        float volume = Mth.clamp((float) (-verticalVelocity / 2.0), 0.1F, 1.0F);

        if (playerEntity instanceof ServerPlayer serverPlayer) {
            for (ServerPlayer player : serverPlayer.level().players()) {
                ServerPlayNetworking.send(player, new SoundPacketPayload(0, playerEntity.position(), false, volume, 1.0F, 0, playerEntity.getInventory().getItem(41)));
            }
        }
    }
}
