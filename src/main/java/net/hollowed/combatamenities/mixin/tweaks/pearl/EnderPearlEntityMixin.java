package net.hollowed.combatamenities.mixin.tweaks.pearl;

import net.hollowed.combatamenities.config.CAConfig;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrownEnderpearl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ThrownEnderpearl.class)
public abstract class EnderPearlEntityMixin {

    @Unique
    private float health = 0;

    @Inject(method = "onHit", at = @At("HEAD"))
    private void getHealthStart(CallbackInfo ci) {
        if (CAConfig.enderPearlTweaks) {
            ThrownEnderpearl self = (ThrownEnderpearl) (Object) this;
            Entity owner = self.getOwner();
            if (owner instanceof LivingEntity livingEntity) {
                this.health = livingEntity.getHealth();
            }
        }
    }

    @Inject(method = "onHit", at = @At("TAIL"))
    private void getHealthEnd(CallbackInfo ci) {
        if (CAConfig.enderPearlTweaks) {
            ThrownEnderpearl self = (ThrownEnderpearl) (Object) this;
            Entity owner = self.getOwner();
            if (owner instanceof LivingEntity livingEntity) {
                this.health = this.health - livingEntity.getHealth();
                if (this.health > 0) {
                    livingEntity.heal(this.health);
                }
            }
        }
    }
}