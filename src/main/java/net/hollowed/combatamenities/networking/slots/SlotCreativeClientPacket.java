package net.hollowed.combatamenities.networking.slots;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

import static net.hollowed.combatamenities.networking.slots.SlotCreativeClientPacketPayload.ID;

public class SlotCreativeClientPacket {

    public static void registerClientPacket() {
        ServerPlayNetworking.registerGlobalReceiver(ID, (payload, context) -> {
            int entityId = payload.entityId();
            int slotId = payload.slotId();
            ItemStack itemStack = payload.itemStack();

            context.server().execute(() -> {
                PlayerEntity player = (PlayerEntity) context.player().getWorld().getEntityById(entityId);
                if (player instanceof PlayerEntity) {
                    // Set the stack to the correct item or empty stack
                    player.getInventory().setStack(slotId, itemStack);
                }
            });
        });
    }
}
