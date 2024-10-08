package net.hollowed.backslot.networking;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;

import static net.hollowed.backslot.networking.BackSlotPacket.BACKSLOT_CLIENT_PACKET_ID;

public class BackSlotClientPacket {

    public static void registerClientPacket() {
        ClientPlayNetworking.registerGlobalReceiver(BACKSLOT_CLIENT_PACKET_ID, (client, handler, buffer, responseSender) -> {
            int[] bufferArray = buffer.readIntArray();
            int entityId = bufferArray[0];
            ItemStack itemStack = buffer.readItemStack();

            client.execute(() -> updatePlayerInventory(client, entityId, itemStack));
        });
    }

    private static void updatePlayerInventory(MinecraftClient client, int entityId, ItemStack itemStack) {
        if (client.player == null) return;

        PlayerEntity player = (PlayerEntity) client.player.getWorld().getEntityById(entityId);
        if (player != null) {
            player.getInventory().setStack(41, itemStack.copy());
        }
    }
}
