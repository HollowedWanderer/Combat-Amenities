package net.hollowed.combatamenities.mixin.slots.registration;

import net.hollowed.combatamenities.util.entities.EntityEquipment;
import net.hollowed.combatamenities.util.interfaces.EquipmentInterface;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.registry.RegistryOps;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class SlotNBTMixin extends Entity implements EquipmentInterface {

    @Unique
    protected final EntityEquipment extraEquipment = new EntityEquipment();

    public SlotNBTMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method = "writeCustomDataToNbt", at = @At("HEAD"))
    public void writeNBT(NbtCompound nbt, CallbackInfo ci) {
        RegistryOps<NbtElement> registryOps = this.getRegistryManager().getOps(NbtOps.INSTANCE);
        if (!this.extraEquipment.isEmpty()) {
            nbt.put("extraEquipment", EntityEquipment.CODEC, registryOps, this.extraEquipment);
        }
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("HEAD"))
    public void readNBT(NbtCompound nbt, CallbackInfo ci) {
        RegistryOps<NbtElement> registryOps = this.getRegistryManager().getOps(NbtOps.INSTANCE);
        this.extraEquipment.copyFrom(nbt.get("extraEquipment", EntityEquipment.CODEC, registryOps).orElseGet(EntityEquipment::new));
    }

    @Override
    public EntityEquipment combat_Amenities$getEquipment() {
        return this.extraEquipment;
    }
}
