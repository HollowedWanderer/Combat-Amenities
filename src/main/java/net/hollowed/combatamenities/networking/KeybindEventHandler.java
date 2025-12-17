package net.hollowed.combatamenities.networking;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.hollowed.combatamenities.networking.slots.back.BackslotPacketPayload;
import net.hollowed.combatamenities.networking.slots.belt.BeltslotPacketPayload;
import net.hollowed.combatamenities.index.CAKeyBindings;

public class KeybindEventHandler {
    private static boolean wasBackSlotKeyPressed = false;
    private static boolean wasBeltSlotKeyPressed = false;
    private static long lastKeyPressTime = 0;  // Variable to store the last key press time
    private static long lastKeyPressTime1 = 0;  // Variable to store the last key press time


    private static final long COOLDOWN_TIME_MS = 500;  // 1 second cooldown (in milliseconds)

    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            boolean isKeyPressed = CAKeyBindings.backSlotBinding.isDown();
            boolean isKeyPressed1 = CAKeyBindings.beltSlotBinding.isDown();

            // Check if the key is pressed and if enough time has passed since the last key press
            if (isKeyPressed && !wasBackSlotKeyPressed && client.player != null) {
                long currentTime = System.currentTimeMillis();

                // Only trigger if cooldown period has passed
                if (currentTime - lastKeyPressTime >= COOLDOWN_TIME_MS) {
                    // Send the packet to the server to handle the back slot key press
                    ClientPlayNetworking.send(new BackslotPacketPayload(client.player.blockPosition()));

                    // Update last key press time
                    lastKeyPressTime = currentTime;
                }
            }

            // Check if the key is pressed and if enough time has passed since the last key press
            if (isKeyPressed1 && !wasBeltSlotKeyPressed && client.player != null) {
                long currentTime = System.currentTimeMillis();

                // Only trigger if cooldown period has passed
                if (currentTime - lastKeyPressTime1 >= COOLDOWN_TIME_MS) {
                    // Send the packet to the server to handle the back slot key press
                    ClientPlayNetworking.send(new BeltslotPacketPayload(client.player.blockPosition()));

                    // Update last key press time
                    lastKeyPressTime1 = currentTime;
                }
            }

            // Update the key press states
            wasBackSlotKeyPressed = isKeyPressed;
            wasBeltSlotKeyPressed = isKeyPressed1;
        });
    }
}
