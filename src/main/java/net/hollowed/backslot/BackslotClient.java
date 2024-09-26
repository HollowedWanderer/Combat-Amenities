package net.hollowed.backslot;

import net.fabricmc.api.ClientModInitializer;

public class BackslotClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ModKeyBindings.initialize();
    }
}
