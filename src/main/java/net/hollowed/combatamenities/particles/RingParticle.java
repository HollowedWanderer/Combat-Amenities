package net.hollowed.combatamenities.particles;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class RingParticle extends SimpleAnimatedParticle {

	RingParticle(ClientLevel world, double x, double y, double z, SpriteSet spriteProvider) {
		super(world, x, y, z, spriteProvider, 0);
		this.lifetime = 7;
		this.quadSize = 1F;
		this.setSpriteFromAge(spriteProvider);
	}

    @Environment(EnvType.CLIENT)
	public static class Factory implements ParticleProvider<@NotNull SimpleParticleType> {
		private final SpriteSet spriteProvider;

		public Factory(SpriteSet spriteProvider) {
			this.spriteProvider = spriteProvider;
		}

		@Override
		public @Nullable Particle createParticle(SimpleParticleType parameters, @NotNull ClientLevel world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, @NotNull RandomSource random) {
			return new RingParticle(world, x, y, z, this.spriteProvider);
		}
	}
}
