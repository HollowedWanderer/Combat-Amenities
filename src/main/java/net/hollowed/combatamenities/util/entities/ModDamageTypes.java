package net.hollowed.combatamenities.util.entities;

import net.hollowed.combatamenities.CombatAmenities;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

public class ModDamageTypes {
    public static final ResourceKey<DamageType> CLEAVED = ResourceKey.create(Registries.DAMAGE_TYPE, Identifier.fromNamespaceAndPath(CombatAmenities.MOD_ID, "cleaved"));

    public static DamageSource of(Level world, ResourceKey<DamageType> key) {
        return new DamageSource(world.registryAccess().lookupOrThrow(Registries.DAMAGE_TYPE).getOrThrow(key));
    }

    public static DamageSource of(Level world, ResourceKey<DamageType> key, Entity entity) {
        return new DamageSource(world.registryAccess().lookupOrThrow(Registries.DAMAGE_TYPE).getOrThrow(key), entity);
    }
}