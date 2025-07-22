package net.hollowed.combatamenities;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.hollowed.combatamenities.networking.slots.SlotClientPacket;
import net.hollowed.combatamenities.networking.slots.SoundPacket;
import net.hollowed.combatamenities.index.CAParticles;
import net.hollowed.combatamenities.index.CAKeyBindings;
import net.hollowed.combatamenities.util.delay.ClientTickDelayScheduler;

public class CombatAmenitiesClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        CAKeyBindings.initialize();
        SlotClientPacket.registerClientPacket();
        SoundPacket.registerClientPacket();
        CAParticles.initializeClient();

        ClientTickEvents.END_CLIENT_TICK.register(server -> ClientTickDelayScheduler.tick());
    }
}
