package net.hollowed.combatamenities.mixin;

import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.hit.EntityHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PersistentProjectileEntity.class)
public class ProjectileEntityMixin {
    @Inject(method = "onEntityHit", at = @At("TAIL"))
    public void onEntityHit(EntityHitResult entityHitResult, CallbackInfo ci) {
        ProjectileEntity projectile = (ProjectileEntity) (Object) this;
        if (!projectile.getWorld().isClient && projectile.isAlive()) {
            ServerWorld serverWorld = (ServerWorld) projectile.getWorld();

            // Force the entity to be re-tracked
            serverWorld.getChunkManager().unloadEntity(projectile);
            serverWorld.getChunkManager().loadEntity(projectile);
        }
    }
}
