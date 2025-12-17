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
            // Send the back slot item from the player to the new server player
            sendBackSlotUpdate(serverPlayer, player);
            // Send the back slot item from the server player to the player itself
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

                // Landing detection
                boolean isLanding = detectLanding(player);

                if (isLanding) {
                    playLandingSound(player, velocity.y); // Pass the vertical velocity to adjust volume
                }
            }

            int VELOCITY_HISTORY_SIZE = 5;
            if (verticalVelocityHistory.size() >= VELOCITY_HISTORY_SIZE) {
                verticalVelocityHistory.poll(); // Remove the oldest velocity
            }
            verticalVelocityHistory.offer((float) velocity.y); // Add the current velocity

            // Update previous position
            previousPositions.put(playerId, currentPosition);
            wasOnGroundLastTick = player.onGround();
        }
    }

    @Unique
    private void sendBackSlotUpdate(ServerPlayer recipient, ServerPlayer sourcePlayer) {
        ItemStack backSlotItem = sourcePlayer.getInventory().getItem(41);

        // Send the back slot item (or an empty item stack if it's empty)
        ServerPlayNetworking.send(recipient,
                new SlotClientPacketPayload(sourcePlayer.getId(), 41, backSlotItem.isEmpty() ? ItemStack.EMPTY : backSlotItem));
        ServerPlayNetworking.send(recipient,
                new SlotClientPacketPayload(sourcePlayer.getId(), 42, sourcePlayer.getInventory().getItem(42).isEmpty() ? ItemStack.EMPTY : sourcePlayer.getInventory().getItem(42)));
    }

    // Detect landing based on velocity history
    @Unique
    private boolean detectLanding(Player playerEntity) {
        if (playerEntity.onGround() && !wasOnGroundLastTick) {
            // Check if recent velocities indicate falling
            for (float velocity : verticalVelocityHistory) {
                if (velocity < -0.1F) { // Threshold for downward motion
                    return true;
                }
            }
        }
        return false;
    }

    // Play landing sound with volume based on vertical velocity
    @Unique
    private void playLandingSound(Player playerEntity, double verticalVelocity) {
        // Calculate volume based on velocity
        float volume = Mth.clamp((float) (-verticalVelocity / 2.0), 0.1F, 1.0F);

        if (playerEntity instanceof ServerPlayer serverPlayer) {
            // Play the sound with the calculated volume
            for (ServerPlayer player : serverPlayer.level().players()) {
                ServerPlayNetworking.send(player, new SoundPacketPayload(0, playerEntity.position(), false, volume, 1.0F, 0, playerEntity.getInventory().getItem(41)));
            }
        }
    }
}
