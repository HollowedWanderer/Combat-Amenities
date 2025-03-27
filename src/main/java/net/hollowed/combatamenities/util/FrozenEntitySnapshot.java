package net.hollowed.combatamenities.util;

import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;

import java.util.UUID;

public class FrozenEntitySnapshot {
    public final UUID uuid;
    public final float fallDistance;
    public final boolean sprinting;
    public final NbtCompound entityData;

    public FrozenEntitySnapshot(LivingEntity entity) {
        this.uuid = entity.getUuid();
        this.fallDistance = entity.fallDistance;
        this.sprinting = entity.isSprinting();

        // Save entity's NBT data
        this.entityData = new NbtCompound();
        entity.writeNbt(this.entityData);
    }

    public void restoreEntityState(LivingEntity entity) {
        if (entity != null) {
            entity.readNbt(this.entityData);
        }
    }
}
