package net.hollowed.backslot;

import net.fabricmc.api.ClientModInitializer;
import net.hollowed.backslot.client.BackslotHudOverlay;
import net.hollowed.backslot.networking.BackSlotClientPacket;

public class CombatAmenitiesClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ModKeyBindings.initialize();
        BackslotHudOverlay.init();
        BackSlotClientPacket.registerClientPacket();
    }
}
