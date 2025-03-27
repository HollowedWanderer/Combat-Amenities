package net.hollowed.combatamenities.util;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.world.World;

public abstract class DataStorageEntity extends LivingEntity {
    protected DataStorageEntity(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }
}
