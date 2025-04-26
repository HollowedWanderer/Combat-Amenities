package net.hollowed.combatamenities.util;

import net.hollowed.combatamenities.CombatAmenities;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class ModSounds {
    @SuppressWarnings("unused")
    public static SoundEvent SWORD_UNSHEATH = register("sword_unsheath");

    @SuppressWarnings("all")
    private static SoundEvent register(String id) {
        return register(Identifier.of(CombatAmenities.MOD_ID, id));
    }

    private static SoundEvent register(Identifier id) {
        return register(id, id);
    }

    private static SoundEvent register(Identifier id, Identifier soundId) {
        return Registry.register(Registries.SOUND_EVENT, id, SoundEvent.of(soundId));
    }

    public static void initialize() {

    }
}
