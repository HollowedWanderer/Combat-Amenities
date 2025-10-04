package net.hollowed.combatamenities.mixin.tweaks.grass;

import net.hollowed.combatamenities.config.CAConfig;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.ModifyReceiver;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
	@Unique
	private boolean validBlock = false;
	@Unique
	private boolean validEntity = false;

	@ModifyVariable(
			method = "findCrosshairTarget",
			at = @At(value = "INVOKE_ASSIGN", target = "Lnet/minecraft/util/hit/HitResult;getPos()Lnet/minecraft/util/math/Vec3d;"),
			ordinal = 0
	)
	private HitResult getReach(HitResult hitResult, Entity camera, double blockInteractionRange, double entityInteractionRange, float tickDelta) {
		if (hitResult.getType() == HitResult.Type.BLOCK && hitResult instanceof BlockHitResult blockHit && CAConfig.swingThrough) {
			BlockPos blockPos = blockHit.getBlockPos();
            assert MinecraftClient.getInstance().world != null;
            validBlock = MinecraftClient.getInstance().world.getBlockState(blockPos)
					.getCollisionShape(MinecraftClient.getInstance().world, blockPos)
					.isEmpty();
		} else {
			validBlock = false;
		}
		return hitResult;
	}

	@ModifyReceiver(
			method = "findCrosshairTarget",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/util/hit/HitResult;getType()Lnet/minecraft/util/hit/HitResult$Type;")
	)
	private HitResult transparentAsMissed(HitResult instance) {
		return validBlock
				? BlockHitResult.createMissed(instance.getPos(), Direction.EAST, BlockPos.ofFloored(instance.getPos()))
				: instance;
	}

	@ModifyVariable(
			method = "findCrosshairTarget",
			at = @At("RETURN"),
			ordinal = 0
	)
	private EntityHitResult iWonderIfThisEntityIsValid(EntityHitResult hitResult, Entity camera, double blockInteractionRange, double entityInteractionRange, float tickDelta) {
		if (hitResult != null && CAConfig.swingThrough) {
			Entity hitEntity = hitResult.getEntity();
			Vec3d cameraPos = camera.getEntityPos();
			validEntity = hitEntity instanceof LivingEntity &&
					!hitEntity.isSpectator() &&
					hitEntity.isAttackable() &&
					!hitEntity.equals(MinecraftClient.getInstance().player != null ? MinecraftClient.getInstance().player.getVehicle() : null) &&
					cameraPos.squaredDistanceTo(hitEntity.getEntityPos()) < MathHelper.square(entityInteractionRange);
		} else {
			validEntity = false;
		}
		return hitResult;
	}

	@ModifyVariable(
			method = "findCrosshairTarget",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/Vec3d;squaredDistanceTo(Lnet/minecraft/util/math/Vec3d;)D", ordinal = 1),
			ordinal = 4
	)
	private double ignoreBlockHit(double original) {
		return validBlock && validEntity ? Double.MAX_VALUE : original;
	}
}
