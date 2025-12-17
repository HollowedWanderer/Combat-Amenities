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
public class HitMarkerParticle extends SingleQuadParticle {
	private final SpriteSet spriteProvider;

	HitMarkerParticle(ClientLevel world, double x, double y, double z, SpriteSet spriteProvider) {
		super(world, x, y, z, 0.0, 0.0, 0.0, spriteProvider.first());
		this.spriteProvider = spriteProvider;
		this.lifetime = 4;
		this.quadSize /= 2.0F;
		this.setSpriteFromAge(spriteProvider);
	}

	@Override
	public int getLightColor(float tint) {
		return 15728880;
	}

	@Override
	public void tick() {
		this.xo = this.x;
		this.yo = this.y;
		this.zo = this.z;
		if (this.age++ >= this.lifetime) {
			this.remove();
		} else {
			this.setSpriteFromAge(this.spriteProvider);
		}
	}

	@Override
	protected @NotNull Layer getLayer() {
		return Layer.OPAQUE;
	}

	@Environment(EnvType.CLIENT)
	public static class Factory implements ParticleProvider<@NotNull SimpleParticleType> {
		private final SpriteSet spriteProvider;

		public Factory(SpriteSet spriteProvider) {
			this.spriteProvider = spriteProvider;
		}

		@Override
		public @Nullable Particle createParticle(SimpleParticleType parameters, @NotNull ClientLevel world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, @NotNull RandomSource random) {
			return new HitMarkerParticle(world, x, y, z, this.spriteProvider);
		}
	}
}
