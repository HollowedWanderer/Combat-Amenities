package net.hollowed.combatamenities.particles;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.SimpleParticleType;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;

@Environment(EnvType.CLIENT)
public class HitMarkerParticle extends BillboardParticle {
	private final SpriteProvider spriteProvider;

	HitMarkerParticle(ClientWorld world, double x, double y, double z, SpriteProvider spriteProvider) {
		super(world, x, y, z, 0.0, 0.0, 0.0, spriteProvider.getFirst());
		this.spriteProvider = spriteProvider;
		this.maxAge = 4;
		this.scale /= 2.0F;
		this.updateSprite(spriteProvider);
	}

	@Override
	public int getBrightness(float tint) {
		return 15728880;
	}

	@Override
	public void tick() {
		this.lastX = this.x;
		this.lastY = this.y;
		this.lastZ = this.z;
		if (this.age++ >= this.maxAge) {
			this.markDead();
		} else {
			this.updateSprite(this.spriteProvider);
		}
	}

	@Override
	protected RenderType getRenderType() {
		return RenderType.PARTICLE_ATLAS_OPAQUE;
	}

	@Environment(EnvType.CLIENT)
	public static class Factory implements ParticleFactory<SimpleParticleType> {
		private final SpriteProvider spriteProvider;

		public Factory(SpriteProvider spriteProvider) {
			this.spriteProvider = spriteProvider;
		}

		@Override
		public @Nullable Particle createParticle(SimpleParticleType parameters, ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, Random random) {
			return new HitMarkerParticle(world, x, y, z, this.spriteProvider);
		}
	}
}
