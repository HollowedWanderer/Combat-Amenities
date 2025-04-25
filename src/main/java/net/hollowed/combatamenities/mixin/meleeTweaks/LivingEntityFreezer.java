package net.hollowed.combatamenities.mixin.meleeTweaks;

import net.hollowed.combatamenities.util.interfaces.EntityFreezer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityFreezer implements EntityFreezer {
    @Unique
    boolean frozen;
    @Unique
    int time;

    @Override
    public void antiquities$setFrozen(boolean frozen, int time) {
        this.frozen = frozen;
        this.time = time;
    }

    @Override
    public boolean antiquities$getFrozen() {
        return this.frozen;
    }

    @Inject(method = "applyMovementInput", at = @At("HEAD"), cancellable = true)
    public void setMovementInput(Vec3d movementInput, float slipperiness, CallbackInfoReturnable<Vec3d> cir) {
        if (this.frozen && this.time > 0) {
            this.time--;
            cir.setReturnValue(Vec3d.ZERO);
        }
    }
}
