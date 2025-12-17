package net.hollowed.combatamenities.networking.slots.belt;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.hollowed.combatamenities.networking.slots.SoundPacketPayload;
import net.hollowed.combatamenities.util.items.CAComponents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class BeltSlotServerPacket {

    public static void registerServerPacket() {
        ServerPlayNetworking.registerGlobalReceiver(BeltslotPacketPayload.ID, ((payload, context) -> context.server().execute(() -> {
            Player player = context.player();

            ItemStack offhandStack = player.getOffhandItem();
            ItemStack handStack = player.getMainHandItem();
            ItemStack backStack = player.getInventory().getItem(42);

            if (player instanceof ServerPlayer serverPlayer) {
                for (ServerPlayer serverPlayerTemp : serverPlayer.level().players()) {
                    if (!backStack.isEmpty()) {
                        ServerPlayNetworking.send(serverPlayerTemp, new SoundPacketPayload(0, player.position(), true, 1.0F, 1.0F, 1, backStack));
                    }
                    if (!handStack.isEmpty() || (!offhandStack.isEmpty() && backStack.isEmpty())) {
                        ServerPlayNetworking.send(serverPlayerTemp, new SoundPacketPayload(0, player.position(), true, 1.0F, 1.0F, 2, !handStack.isEmpty() ? handStack : offhandStack));
                    }
                }
            }

            if (!handStack.isEmpty()) {
                handStack.set(CAComponents.STRING_PROPERTY, "bob5");
                player.setItemInHand(InteractionHand.MAIN_HAND, backStack.copy());
                player.getInventory().setItem(42, handStack.copy());
            } else {
                if (backStack.isEmpty()) {
                    offhandStack.set(CAComponents.STRING_PROPERTY, "bob5");
                    player.setItemInHand(InteractionHand.OFF_HAND, backStack.copy());
                    player.getInventory().setItem(42, offhandStack.copy());
                } else {
                    handStack.set(CAComponents.STRING_PROPERTY, "bob5");
                    player.setItemInHand(InteractionHand.MAIN_HAND, backStack.copy());
                    player.getInventory().setItem(42, handStack.copy());
                }
            }

            // Sync the player's inventory back to the client
            player.containerMenu.broadcastChanges();
        })));
    }
}
