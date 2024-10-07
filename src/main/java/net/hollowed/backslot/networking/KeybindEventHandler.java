package net.hollowed.backslot.networking;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.hollowed.backslot.ModKeyBindings;

public class KeybindEventHandler {
    private static boolean wasBackSlotKeyPressed = false;

    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            boolean isKeyPressed = ModKeyBindings.backSlotBinding.isPressed();

            if (isKeyPressed && !wasBackSlotKeyPressed && client.player != null) {
                // Send a packet to the server to handle the back slot key press
                ClientPlayNetworking.send(new BackslotPacketPayload(client.player.getBlockPos()));
            }

            // Update the key press state
            wasBackSlotKeyPressed = isKeyPressed;
        });
    }
}