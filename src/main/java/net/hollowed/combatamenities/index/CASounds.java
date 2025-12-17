package net.hollowed.combatamenities.index;

import net.hollowed.combatamenities.CombatAmenities;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;

public class CASounds {
    @SuppressWarnings("unused")
    public static SoundEvent SWORD_UNSHEATH = register("sword_unsheath");

    @SuppressWarnings("all")
    private static SoundEvent register(String id) {
        return register(Identifier.fromNamespaceAndPath(CombatAmenities.MOD_ID, id));
    }

    private static SoundEvent register(Identifier id) {
        return register(id, id);
    }

    private static SoundEvent register(Identifier id, Identifier soundId) {
        return Registry.register(BuiltInRegistries.SOUND_EVENT, id, SoundEvent.createVariableRangeEvent(soundId));
    }

    public static void initialize() {

    }
}
