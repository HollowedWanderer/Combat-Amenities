package net.hollowed.backslot.networking;

import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.hollowed.backslot.ModKeyBindings;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;

import java.util.Objects;

public class KeybindEventHandler {
    private static final MinecraftClient client = MinecraftClient.getInstance();
    private static boolean wasBackSlotKeyPressed = false;

    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            boolean isKeyPressed = ModKeyBindings.backSlotBinding.isPressed();

            if (isKeyPressed && !wasBackSlotKeyPressed && client.player != null) {
                // Send a packet to the server to handle the back slot key press
                PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
                buf.writeInt(41);
                Objects.requireNonNull(client.getNetworkHandler()).sendPacket(new CustomPayloadC2SPacket(BackSlotPacket.BACKSLOT_PACKET_ID, buf));
            }

            // Update the key press state
            wasBackSlotKeyPressed = isKeyPressed;
        });
    }
}