package net.hollowed.combatamenities.mixin.slots.registration;

import net.hollowed.combatamenities.util.entities.EntityEquipment;
import net.hollowed.combatamenities.util.interfaces.EquipmentInterface;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
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

    @Inject(method = "writeCustomData", at = @At("HEAD"))
    public void writeNBT(WriteView view, CallbackInfo ci) {
        if (!this.extraEquipment.isEmpty()) {
            view.put("extraEquipment", EntityEquipment.CODEC, this.extraEquipment);
        }
    }

    @Inject(method = "readCustomData", at = @At("HEAD"))
    public void readNBT(ReadView view, CallbackInfo ci) {
        this.extraEquipment.copyFrom(view.read("extraEquipment", EntityEquipment.CODEC).orElseGet(EntityEquipment::new));
    }

    @Override
    public EntityEquipment combat_Amenities$getEquipment() {
        return this.extraEquipment;
    }
}
