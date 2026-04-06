package net.hollowed.combatamenities.networking;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.hollowed.combatamenities.networking.slots.back.BackslotPacketPayload;
import net.hollowed.combatamenities.networking.slots.belt.BeltslotPacketPayload;
import net.hollowed.combatamenities.index.CAKeyBindings;

public class KeybindEventHandler {
    private static boolean wasBackSlotKeyPressed = false;
    private static boolean wasBeltSlotKeyPressed = false;
    private static long lastKeyPressTime = 0;
    private static long lastKeyPressTime1 = 0;

    private static final long COOLDOWN_TIME_MS = 100;

    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            boolean isKeyPressed = CAKeyBindings.backSlotBinding.isDown();
            boolean isKeyPressed1 = CAKeyBindings.beltSlotBinding.isDown();

            if (isKeyPressed && !wasBackSlotKeyPressed && client.player != null) {
                long currentTime = System.currentTimeMillis();

                if (currentTime - lastKeyPressTime >= COOLDOWN_TIME_MS) {
                    ClientPlayNetworking.send(new BackslotPacketPayload(client.player.blockPosition()));

//                    PlayerAnimationController controller = (PlayerAnimationController) PlayerAnimationAccess.getPlayerAnimationLayer(client.player, CombatAmenitiesClient.ANIMATION_LAYER_ID);
//                    if (controller == null) {
//                        return;
//                    }
//
//                    controller.triggerAnimation(CombatAmenities.id("backflip"));

                    lastKeyPressTime = currentTime;
                }
            }

            if (isKeyPressed1 && !wasBeltSlotKeyPressed && client.player != null) {
                long currentTime = System.currentTimeMillis();

                if (currentTime - lastKeyPressTime1 >= COOLDOWN_TIME_MS) {
                    ClientPlayNetworking.send(new BeltslotPacketPayload(client.player.blockPosition()));
                    lastKeyPressTime1 = currentTime;
                }
            }

            wasBackSlotKeyPressed = isKeyPressed;
            wasBeltSlotKeyPressed = isKeyPressed1;
        });
    }
}
