package net.hollowed.backslot;

import net.fabricmc.api.ClientModInitializer;
import net.hollowed.backslot.client.BackslotHudOverlay;

public class BackslotClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ModKeyBindings.initialize();
        BackslotHudOverlay.init();
    }
}
