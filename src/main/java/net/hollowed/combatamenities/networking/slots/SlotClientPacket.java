package net.hollowed.combatamenities.networking.slots;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

import static net.hollowed.combatamenities.networking.slots.SlotClientPacketPayload.ID;

public class SlotClientPacket {

    public static void registerClientPacket() {
        ClientPlayNetworking.registerGlobalReceiver(ID, (payload, context) -> {
            int entityId = payload.entityId();
            int slotId = payload.slotId();
            ItemStack itemStack = payload.itemStack();

            context.client().execute(() -> {
                PlayerEntity player = (PlayerEntity) context.player().getWorld().getEntityById(entityId);
                if (player != null) {
                    // Set the stack to the correct item or empty stack
                    player.getInventory().setStack(slotId, itemStack);
                }
            });
        });
    }
}
