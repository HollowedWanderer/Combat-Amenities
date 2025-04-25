package net.hollowed.combatamenities;

import net.fabricmc.api.ClientModInitializer;
import net.hollowed.combatamenities.networking.slots.SlotClientPacket;
import net.hollowed.combatamenities.particles.ModParticles;
import net.hollowed.combatamenities.util.ModKeyBindings;

public class CombatAmenitiesClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ModKeyBindings.initialize();
        SlotClientPacket.registerClientPacket();
        ModParticles.initializeClient();
    }
}
