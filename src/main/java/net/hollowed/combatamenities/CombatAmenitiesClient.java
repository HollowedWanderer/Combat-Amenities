package net.hollowed.combatamenities;

import net.fabricmc.api.ClientModInitializer;
import net.hollowed.combatamenities.networking.BackSlotClientPacket;

public class CombatAmenitiesClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ModKeyBindings.initialize();
        BackSlotClientPacket.registerClientPacket();
    }
}
