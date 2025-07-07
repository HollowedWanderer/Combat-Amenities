package net.hollowed.combatamenities.networking.slots.back;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.hollowed.combatamenities.networking.slots.SoundPacketPayload;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;

public class BackSlotServerPacket {

    public static void registerServerPacket() {
        ServerPlayNetworking.registerGlobalReceiver(BackslotPacketPayload.ID, ((payload, context) -> context.server().execute(() -> {
            PlayerEntity player = context.player();
            if (player == null || player.currentScreenHandler == null) {
                return;
            }

            ItemStack offhandStack = player.getOffHandStack();
            ItemStack handStack = player.getMainHandStack();
            ItemStack backStack = player.getInventory().getStack(41);

            if (player instanceof ServerPlayerEntity serverPlayer) {
                for (ServerPlayerEntity serverPlayerTemp : serverPlayer.getWorld().getPlayers()) {
                    if (!backStack.isEmpty()) {
                        ServerPlayNetworking.send(serverPlayerTemp, new SoundPacketPayload(0, player.getPos(), true, 1.0F, 1.0F, 1, backStack));
                    }
                    if (!handStack.isEmpty() || (!offhandStack.isEmpty() && backStack.isEmpty())) {
                        ServerPlayNetworking.send(serverPlayerTemp, new SoundPacketPayload(0, player.getPos(), true, 1.0F, 1.0F, 2, !handStack.isEmpty() ? handStack : offhandStack));
                    }
                }
            }

            if (!handStack.isEmpty()) {
                player.setStackInHand(Hand.MAIN_HAND, backStack.copy());
                player.getInventory().setStack(41, handStack.copy());
            } else {
                if (backStack.isEmpty()) {
                    player.setStackInHand(Hand.OFF_HAND, backStack.copy());
                    player.getInventory().setStack(41, offhandStack.copy());
                } else {
                    player.setStackInHand(Hand.MAIN_HAND, backStack.copy());
                    player.getInventory().setStack(41, handStack.copy());
                }
            }

            // Sync the player's inventory back to the client
            player.currentScreenHandler.sendContentUpdates();
        })));
    }
}
