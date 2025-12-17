package net.hollowed.combatamenities.networking.slots;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import static net.hollowed.combatamenities.networking.slots.SlotCreativeClientPacketPayload.ID;

public class SlotCreativeClientPacket {

    public static void registerClientPacket() {
        ServerPlayNetworking.registerGlobalReceiver(ID, (payload, context) -> {
            int slotId = payload.slotId();
            ItemStack itemStack = payload.itemStack();

            context.server().execute(() -> {
                Player player = context.player();
                if (player.isCreative()) {
                    player.getInventory().setItem(slotId, itemStack);
                }
            });
        });
    }
}
