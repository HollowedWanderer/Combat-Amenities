package net.hollowed.combatamenities.util.items;

import com.mojang.serialization.Codec;
import net.hollowed.combatamenities.CombatAmenities;
import net.minecraft.component.ComponentType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class CAComponents {
    public static final ComponentType<Integer> INTEGER_PROPERTY = Registry.register(
            Registries.DATA_COMPONENT_TYPE,
            Identifier.of(CombatAmenities.MOD_ID, "integer_property"),
            ComponentType.<Integer>builder()
                    .codec(Codec.INT)
                    .build()
    );
    public static final ComponentType<Boolean> BOOLEAN_PROPERTY = Registry.register(
            Registries.DATA_COMPONENT_TYPE,
            Identifier.of(CombatAmenities.MOD_ID, "boolean_property"),
            ComponentType.<Boolean>builder()
                    .codec(Codec.BOOL)
                    .build()
    );
    public static final ComponentType<String> STRING_PROPERTY = Registry.register(
            Registries.DATA_COMPONENT_TYPE,
            Identifier.of(CombatAmenities.MOD_ID, "string_property"),
            ComponentType.<String>builder()
                    .codec(Codec.STRING)
                    .build()
    );

    public static void initialize() {}
}
