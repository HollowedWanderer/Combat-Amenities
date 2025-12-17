package net.hollowed.combatamenities.mixin.tweaks.arrow;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.phys.EntityHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractArrow.class)
public class ProjectileEntityMixin {
    @Inject(method = "onHitEntity", at = @At("TAIL"))
    public void onEntityHit(EntityHitResult entityHitResult, CallbackInfo ci) {
        Projectile projectile = (Projectile) (Object) this;
        if (!projectile.level().isClientSide() && projectile.isAlive()) {
            ServerLevel serverWorld = (ServerLevel) projectile.level();

            // Force the entity to be re-tracked
            serverWorld.getChunkSource().removeEntity(projectile);
            serverWorld.getChunkSource().addEntity(projectile);
        }
    }
}
