package net.hollowed.combatamenities.networking;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

import static net.hollowed.combatamenities.networking.BackSlotCreativeClientPacketPayload.BACKSLOT_CREATIVE_CLIENT_PACKET_ID;

public class BackSlotCreativeClientPacket {

    public static void registerClientPacket() {
        ServerPlayNetworking.registerGlobalReceiver(BACKSLOT_CREATIVE_CLIENT_PACKET_ID, (payload, context) -> {
            int slotId = payload.slotId();
            ItemStack itemStack = payload.itemStack();

            context.server().execute(() -> {
                PlayerEntity player = context.player();
                if (player != null && player.isCreative()) {
                    // Set the stack to the correct item or empty stack
                    player.getInventory().setStack(slotId, itemStack);
                }
            });
        });
    }
}
