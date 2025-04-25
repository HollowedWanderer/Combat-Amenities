package net.hollowed.combatamenities.particles;

import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.hollowed.combatamenities.CombatAmenities;
import net.minecraft.client.particle.SweepAttackParticle;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModParticles {
    public static final SimpleParticleType HIT_MARKER = FabricParticleTypes.simple();
    public static final SimpleParticleType CRIT_ATTACK = FabricParticleTypes.simple();
    public static final SimpleParticleType NORMAL_ATTACK = FabricParticleTypes.simple();
    public static final SimpleParticleType LEFT_NORMAL_ATTACK = FabricParticleTypes.simple();
    public static final SimpleParticleType RING = FabricParticleTypes.simple();

    public static void initialize() {
        Registry.register(Registries.PARTICLE_TYPE, Identifier.of(CombatAmenities.MOD_ID, "hit_marker"), HIT_MARKER);
        Registry.register(Registries.PARTICLE_TYPE, Identifier.of(CombatAmenities.MOD_ID, "crit_attack"), CRIT_ATTACK);
        Registry.register(Registries.PARTICLE_TYPE, Identifier.of(CombatAmenities.MOD_ID, "normal_attack"), NORMAL_ATTACK);
        Registry.register(Registries.PARTICLE_TYPE, Identifier.of(CombatAmenities.MOD_ID, "left_normal_attack"), LEFT_NORMAL_ATTACK);
        Registry.register(Registries.PARTICLE_TYPE, Identifier.of(CombatAmenities.MOD_ID, "ring"), RING);
    }

    public static void initializeClient() {
        // For this example, we will use the end rod particle behaviour.
        ParticleFactoryRegistry.getInstance().register(HIT_MARKER, HitMarkerParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(CRIT_ATTACK, SweepAttackParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(NORMAL_ATTACK, SweepAttackParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(LEFT_NORMAL_ATTACK, SweepAttackParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(RING, RingParticle.Factory::new);
    }
}
