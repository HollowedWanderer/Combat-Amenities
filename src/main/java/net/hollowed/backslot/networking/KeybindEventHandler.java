package net.hollowed.backslot.networking;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.hollowed.backslot.ModKeyBindings;

public class KeybindEventHandler {
    private static boolean wasBackSlotKeyPressed = false;
    private static long lastKeyPressTime = 0;  // Variable to store the last key press time

    private static final long COOLDOWN_TIME_MS = 500;  // 1 second cooldown (in milliseconds)

    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            boolean isKeyPressed = ModKeyBindings.backSlotBinding.isPressed();

            // Check if the key is pressed and if enough time has passed since the last key press
            if (isKeyPressed && !wasBackSlotKeyPressed && client.player != null) {
                long currentTime = System.currentTimeMillis();

                // Only trigger if cooldown period has passed
                if (currentTime - lastKeyPressTime >= COOLDOWN_TIME_MS) {
                    // Send the packet to the server to handle the back slot key press
                    ClientPlayNetworking.send(new BackslotPacketPayload(client.player.getBlockPos()));

                    // Update last key press time
                    lastKeyPressTime = currentTime;
                }
            }

            // Update the key press state
            wasBackSlotKeyPressed = isKeyPressed;
        });
    }
}
