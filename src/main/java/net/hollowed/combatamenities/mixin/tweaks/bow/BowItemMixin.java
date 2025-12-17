package net.hollowed.combatamenities.mixin.tweaks.bow;

import net.hollowed.combatamenities.config.CAConfig;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.BowItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BowItem.class)
public abstract class BowItemMixin {

    @Unique
    private static final int OVERDRAW_THRESHOLD = 120; // 6 seconds at 20 ticks/second

    @Inject(method = "shootProjectile", at = @At("HEAD"), cancellable = true)
    private void addProjectileInaccuracy(LivingEntity shooter, Projectile projectile, int index, float speed, float divergence, float yaw, LivingEntity target, CallbackInfo ci) {
        if (CAConfig.bowTweaks) {
            int useTicks = shooter.getTicksUsingItem();
            if (useTicks > OVERDRAW_THRESHOLD) {
                float extraInaccuracy = (useTicks - OVERDRAW_THRESHOLD) * 0.15F; // Inaccuracy increases over time
                float modifiedDivergence = divergence + extraInaccuracy;

                // Apply the modified velocity to the projectile
                projectile.shootFromRotation(
                        shooter,
                        shooter.getXRot() + ((float) Math.random() * extraInaccuracy - extraInaccuracy),
                        shooter.getYRot() + yaw + ((float) Math.random() * extraInaccuracy - extraInaccuracy),
                        0.0F,
                        speed,
                        modifiedDivergence
                );

                ci.cancel();
            }
        }
    }
}
