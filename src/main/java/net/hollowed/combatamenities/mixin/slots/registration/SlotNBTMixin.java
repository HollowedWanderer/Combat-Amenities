package net.hollowed.combatamenities.mixin.slots.registration;

import net.hollowed.combatamenities.util.entities.EntityEquipment;
import net.hollowed.combatamenities.util.interfaces.EquipmentInterface;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class SlotNBTMixin extends Entity implements EquipmentInterface {

    @Unique
    protected final EntityEquipment extraEquipment = new EntityEquipment();

    public SlotNBTMixin(EntityType<?> type, Level world) {
        super(type, world);
    }

    @Inject(method = "addAdditionalSaveData", at = @At("HEAD"))
    public void writeNBT(ValueOutput view, CallbackInfo ci) {
        if (!this.extraEquipment.isEmpty()) {
            view.store("extraEquipment", EntityEquipment.CODEC, this.extraEquipment);
        }
    }

    @Inject(method = "readAdditionalSaveData", at = @At("HEAD"))
    public void readNBT(ValueInput view, CallbackInfo ci) {
        this.extraEquipment.copyFrom(view.read("extraEquipment", EntityEquipment.CODEC).orElseGet(EntityEquipment::new));
    }

    @Override
    public EntityEquipment combat_Amenities$getEquipment() {
        return this.extraEquipment;
    }
}
