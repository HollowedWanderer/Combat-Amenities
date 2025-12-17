package net.hollowed.combatamenities.networking.slots;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import static net.hollowed.combatamenities.networking.slots.SlotClientPacketPayload.ID;

public class SlotClientPacket {

    public static void registerClientPacket() {
        ClientPlayNetworking.registerGlobalReceiver(ID, (payload, context) -> {
            int entityId = payload.entityId();
            int slotId = payload.slotId();
            ItemStack itemStack = payload.itemStack();

            context.client().execute(() -> {
                Player player = (Player) context.player().level().getEntity(entityId);
                if (player != null) {
                    player.getInventory().setItem(slotId, itemStack);
                }
            });
        });
    }
}
