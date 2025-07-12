package net.hollowed.combatamenities.networking.slots;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

import static net.hollowed.combatamenities.networking.slots.SlotCreativeClientPacketPayload.ID;

public class SlotCreativeClientPacket {

    public static void registerClientPacket() {
        ServerPlayNetworking.registerGlobalReceiver(ID, (payload, context) -> {
            int slotId = payload.slotId();
            ItemStack itemStack = payload.itemStack();

            context.server().execute(() -> {
                PlayerEntity player = context.player();
                if (player != null && player.isCreative()) {
                    player.getInventory().setStack(slotId, itemStack);
                }
            });
        });
    }
}
