package net.hollowed.combatamenities.mixin.slots.registration;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.hollowed.combatamenities.util.entities.EntityEquipment;
import net.hollowed.combatamenities.util.interfaces.EquipmentInterface;
import net.minecraft.core.NonNullList;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.hollowed.combatamenities.util.entities.ExtraSlots;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(Inventory.class)
public abstract class PlayerInventoryMixin implements Container {

    @Shadow @Final private NonNullList<ItemStack> items;
    @Shadow @Final public Player player;

    @Shadow public abstract void setItem(int slot, @NotNull ItemStack stack);

    @Unique
    private static final Int2ObjectMap<ExtraSlots> EXTRA_SLOTS = new Int2ObjectArrayMap<>(
            Map.of(
                    ExtraSlots.BACKSLOT.getOffsetEntitySlotId(41),
                    ExtraSlots.BACKSLOT,
                    ExtraSlots.BELTSLOT.getOffsetEntitySlotId(41),
                    ExtraSlots.BELTSLOT
            )
    );

    @Inject(method = "removeItem(II)Lnet/minecraft/world/item/ItemStack;", at = @At("HEAD"), cancellable = true)
    public void removeStack(int slot, int amount, CallbackInfoReturnable<ItemStack> cir) {
        if (this.player instanceof EquipmentInterface access) {
            EntityEquipment equipment = access.combat_Amenities$getEquipment();
            if (slot >= this.items.size()) {
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

    @Inject(method = "removeItem(Lnet/minecraft/world/item/ItemStack;)V", at = @At("HEAD"))
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

    @Inject(method = "removeItemNoUpdate(I)Lnet/minecraft/world/item/ItemStack;", at = @At("HEAD"), cancellable = true)
    public void removeStack(int slot, CallbackInfoReturnable<ItemStack> cir) {
        if (this.player instanceof EquipmentInterface access) {
            EntityEquipment equipment = access.combat_Amenities$getEquipment();
            if (slot >= this.items.size()) {
                ExtraSlots equipmentSlot = EXTRA_SLOTS.get(slot);
                if (equipmentSlot != null) {
                    cir.setReturnValue(equipment.put(equipmentSlot, ItemStack.EMPTY));
                }
            }
        }
    }

    @Inject(method = "setItem", at = @At("HEAD"))
    public void setCustomStack(int slot, ItemStack stack, CallbackInfo ci) {
        if (this.player instanceof EquipmentInterface access) {
            EntityEquipment equipment = access.combat_Amenities$getEquipment();
            ExtraSlots equipmentSlot = EXTRA_SLOTS.get(slot);
            if (equipmentSlot != null) {
                equipment.put(equipmentSlot, stack);
            }
        }
    }

    @Inject(method = "getContainerSize", at = @At("RETURN"), cancellable = true)
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

    @Inject(method = "getItem", at = @At("HEAD"), cancellable = true)
    public void getStack(int slot, CallbackInfoReturnable<ItemStack> cir) {
        if (this.player instanceof EquipmentInterface access) {
            EntityEquipment equipment = access.combat_Amenities$getEquipment();
            if (slot >= this.items.size()) {
                ExtraSlots equipmentSlot = EXTRA_SLOTS.get(slot);
                if (equipmentSlot != null) {
                    cir.setReturnValue(equipment.get(equipmentSlot));
                }
            }
        }
    }

    @Inject(method = "clearContent", at = @At("HEAD"))
    public void clear(CallbackInfo ci) {
        if (this.player instanceof EquipmentInterface access) {
            EntityEquipment equipment = access.combat_Amenities$getEquipment();
            equipment.clear();
        }
    }
}