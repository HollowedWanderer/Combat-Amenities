package net.hollowed.combatamenities.mixin.slots.registration;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.hollowed.combatamenities.util.entities.EntityEquipment;
import net.hollowed.combatamenities.util.interfaces.EquipmentInterface;
import net.hollowed.combatamenities.util.entities.ExtraSlots;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(PlayerInventory.class)
public abstract class PlayerInventoryMixin implements Inventory {

    @Shadow @Final private DefaultedList<ItemStack> main;
    @Shadow @Final public PlayerEntity player;
    @Unique
    private static final Int2ObjectMap<ExtraSlots> EXTRA_SLOTS = new Int2ObjectArrayMap<>(
            Map.of(
                    ExtraSlots.BACKSLOT.getOffsetEntitySlotId(41),
                    ExtraSlots.BACKSLOT,
                    ExtraSlots.BELTSLOT.getOffsetEntitySlotId(41),
                    ExtraSlots.BELTSLOT
            )
    );

    @Inject(method = "removeStack(II)Lnet/minecraft/item/ItemStack;", at = @At("HEAD"), cancellable = true)
    public void removeStack(int slot, int amount, CallbackInfoReturnable<ItemStack> cir) {
        if (this.player instanceof EquipmentInterface access) {
            EntityEquipment equipment = access.combat_Amenities$getEquipment();
            if (slot >= this.main.size()) {
                ExtraSlots extraSlot = EXTRA_SLOTS.get(slot);
                if (extraSlot != null) {
                    ItemStack itemStack = equipment.get(extraSlot);
                    if (!itemStack.isEmpty()) {
                        cir.setReturnValue(itemStack.split(amount));
                    }
                }
            }
        }
    }

    @Inject(method = "removeOne", at = @At("HEAD"))
    public void removeOne(ItemStack stack, CallbackInfo ci) {
        if (this.player instanceof EquipmentInterface access) {
            EntityEquipment equipment = access.combat_Amenities$getEquipment();
            for (ExtraSlots equipmentSlot : EXTRA_SLOTS.values()) {
                ItemStack itemStack = equipment.get(equipmentSlot);
                if (itemStack == stack) {
                    equipment.put(equipmentSlot, ItemStack.EMPTY);
                    return;
                }
            }
        }
    }

    @Inject(method = "removeStack(I)Lnet/minecraft/item/ItemStack;", at = @At("HEAD"), cancellable = true)
    public void removeStack(int slot, CallbackInfoReturnable<ItemStack> cir) {
        if (this.player instanceof EquipmentInterface access) {
            EntityEquipment equipment = access.combat_Amenities$getEquipment();
            if (slot >= this.main.size()) {
                ExtraSlots equipmentSlot = EXTRA_SLOTS.get(slot);
                if (equipmentSlot != null) {
                    cir.setReturnValue(equipment.put(equipmentSlot, ItemStack.EMPTY));
                }
            }
        }
    }

    @Inject(method = "setStack", at = @At("HEAD"))
    public void setStack(int slot, ItemStack stack, CallbackInfo ci) {
        if (this.player instanceof EquipmentInterface access) {
            EntityEquipment equipment = access.combat_Amenities$getEquipment();
            ExtraSlots equipmentSlot = EXTRA_SLOTS.get(slot);
            if (equipmentSlot != null) {
                equipment.put(equipmentSlot, stack);
            }
        }
    }

    @Inject(method = "size", at = @At("RETURN"), cancellable = true)
    public void size(CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(cir.getReturnValue() + EXTRA_SLOTS.size());
    }

    @Inject(method = "isEmpty", at = @At("HEAD"), cancellable = true)
    public void isEmpty(CallbackInfoReturnable<Boolean> cir) {
        if (this.player instanceof EquipmentInterface access) {
            EntityEquipment equipment = access.combat_Amenities$getEquipment();
            for (ExtraSlots equipmentSlot : EXTRA_SLOTS.values()) {
                if (!equipment.get(equipmentSlot).isEmpty()) {
                    cir.setReturnValue(false);
                }
            }
        }
    }

    @Inject(method = "getStack", at = @At("HEAD"), cancellable = true)
    public void getStack(int slot, CallbackInfoReturnable<ItemStack> cir) {
        if (this.player instanceof EquipmentInterface access) {
            EntityEquipment equipment = access.combat_Amenities$getEquipment();
            if (slot >= this.main.size()) {
                ExtraSlots equipmentSlot = EXTRA_SLOTS.get(slot);
                if (equipmentSlot != null) {
                    cir.setReturnValue(equipment.get(equipmentSlot));
                }
            }
        }
    }

    @Inject(method = "dropAll", at = @At("HEAD"))
    public void dropAll(CallbackInfo ci) {
        if (this.player instanceof EquipmentInterface access) {
            EntityEquipment equipment = access.combat_Amenities$getEquipment();
            equipment.dropAll(this.player);
        }
    }

    @Inject(method = "clear", at = @At("HEAD"))
    public void clear(CallbackInfo ci) {
        if (this.player instanceof EquipmentInterface access) {
            EntityEquipment equipment = access.combat_Amenities$getEquipment();
            equipment.clear();
        }
    }
}