package net.hollowed.combatamenities.util.items;

import com.mojang.serialization.Codec;
import net.hollowed.combatamenities.CombatAmenities;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;

public class CAComponents {
    public static final DataComponentType<Integer> INTEGER_PROPERTY = Registry.register(
            BuiltInRegistries.DATA_COMPONENT_TYPE,
            Identifier.fromNamespaceAndPath(CombatAmenities.MOD_ID, "integer_property"),
            DataComponentType.<Integer>builder()
                    .persistent(Codec.INT)
                    .build()
    );
    public static final DataComponentType<Boolean> BOOLEAN_PROPERTY = Registry.register(
            BuiltInRegistries.DATA_COMPONENT_TYPE,
            Identifier.fromNamespaceAndPath(CombatAmenities.MOD_ID, "boolean_property"),
            DataComponentType.<Boolean>builder()
                    .persistent(Codec.BOOL)
                    .build()
    );
    public static final DataComponentType<String> STRING_PROPERTY = Registry.register(
            BuiltInRegistries.DATA_COMPONENT_TYPE,
            Identifier.fromNamespaceAndPath(CombatAmenities.MOD_ID, "string_property"),
            DataComponentType.<String>builder()
                    .persistent(Codec.STRING)
                    .build()
    );

    public static void initialize() {}
}
