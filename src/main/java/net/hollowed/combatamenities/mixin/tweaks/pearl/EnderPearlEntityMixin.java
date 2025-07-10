package net.hollowed.combatamenities.mixin.tweaks.pearl;

import net.hollowed.combatamenities.config.ModConfig;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.thrown.EnderPearlEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EnderPearlEntity.class)
public abstract class EnderPearlEntityMixin {

    @Unique
    private float health = 0;

    @Inject(method = "onCollision", at = @At("HEAD"))
    private void getHealthStart(CallbackInfo ci) {
        if (ModConfig.enderPearlTweaks) {
            EnderPearlEntity self = (EnderPearlEntity) (Object) this;
            Entity owner = self.getOwner();
            if (owner instanceof LivingEntity livingEntity) {
                this.health = livingEntity.getHealth();
            }
        }
    }

    @Inject(method = "onCollision", at = @At("TAIL"))
    private void getHealthEnd(CallbackInfo ci) {
        if (ModConfig.enderPearlTweaks) {
            EnderPearlEntity self = (EnderPearlEntity) (Object) this;
            Entity owner = self.getOwner();
            if (owner instanceof LivingEntity livingEntity) {
                this.health = this.health - livingEntity.getHealth();
                if (this.health > 0) {
                    ((LivingEntity) owner).heal(this.health);
                }
            }
        }
    }
}