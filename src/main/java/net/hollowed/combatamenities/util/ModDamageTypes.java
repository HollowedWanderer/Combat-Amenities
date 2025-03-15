package net.hollowed.combatamenities.util;

import net.hollowed.combatamenities.CombatAmenities;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class ModDamageTypes {
    public static final RegistryKey<DamageType> CLEAVED = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, Identifier.of(CombatAmenities.MOD_ID, "cleaved"));

    public static DamageSource of(World world, RegistryKey<DamageType> key) {
        return new DamageSource(world.getRegistryManager().getOrThrow(RegistryKeys.DAMAGE_TYPE).getOrThrow(key));
    }

    public static DamageSource of(World world, RegistryKey<DamageType> key, Entity entity) {
        return new DamageSource(world.getRegistryManager().getOrThrow(RegistryKeys.DAMAGE_TYPE).getOrThrow(key), entity);
    }
}