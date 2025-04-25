package net.hollowed.combatamenities.mixin.meleeTweaks;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class EntityFreezer implements net.hollowed.combatamenities.util.interfaces.EntityFreezer {
    @Shadow public abstract void setVelocity(Vec3d velocity);

    @Shadow public boolean velocityModified;
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

    @Inject(method = "tick", at = @At("HEAD"))
    public void tick(CallbackInfo ci) {
        if (this.frozen && this.time > 0) {
            this.setVelocity(Vec3d.ZERO);
            this.velocityModified = true;
            this.time--;
        }
    }
}
