package net.hollowed.backslot.mixin.slots;

import com.google.common.collect.ImmutableList;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.util.collection.DefaultedList;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Mixin(PlayerInventory.class)
public abstract class PlayerInventoryMixin implements Inventory {
    @Shadow
    @Final
    @Mutable
    private List<DefaultedList<ItemStack>> combinedInventory;

    @Shadow @Final public PlayerEntity player;
    // Unique custom slot, size is 1 for this case
    @Unique
    private DefaultedList<ItemStack> extraSlot;

    // Constructor for mixin
    public PlayerInventoryMixin(PlayerEntity player) {}

    // Initialize the custom slot and update combined inventory
    @Inject(method = "<init>*", at = @At("RETURN"))
    private void initMixin(PlayerEntity playerEntity, CallbackInfo info) {
        this.extraSlot = DefaultedList.ofSize(1, ItemStack.EMPTY);
        this.combinedInventory = new ArrayList<>(combinedInventory);
        this.combinedInventory.add(extraSlot);  // Add the custom slot to combined inventory
        this.combinedInventory = ImmutableList.copyOf(this.combinedInventory);  // Make the list immutable
    }

    // Write custom slot to player's NBT data
    @Inject(method = "writeNbt", at = @At("TAIL"))
    public void writeNbtMixin(NbtList tag, CallbackInfoReturnable<NbtList> info) {
        // Check if the custom slot is not empty before saving
        if (!this.extraSlot.get(0).isEmpty()) {
            NbtCompound compoundTag = new NbtCompound();
            compoundTag.putByte("Slot", (byte) (110));  // Custom slot index
            tag.add(this.extraSlot.get(0).toNbt(this.player.getRegistryManager(), compoundTag));
        }
    }

    // Read the custom slot from player's NBT data
    @Inject(method = "readNbt", at = @At("TAIL"))
    public void readNbtMixin(NbtList tag, CallbackInfo info) {
        this.extraSlot.clear();  // Clear the custom slot
        for (int i = 0; i < tag.size(); ++i) {
            NbtCompound compoundTag = tag.getCompound(i);
            int slot = compoundTag.getByte("Slot") & 255;  // Get slot index
            ItemStack itemStack = ItemStack.fromNbt(this.player.getRegistryManager(), compoundTag).orElse(ItemStack.EMPTY);  // Read item data
            // Load item into the custom slot if it's the correct index
            if (!itemStack.isEmpty()) {
                if (slot == 110) {  // Slot 110 is our custom slot
                    this.extraSlot.set(0, itemStack);
                }
            }
        }
    }

    // Add the size of the custom slot to the total inventory size
    @Inject(method = "size", at = @At("RETURN"), cancellable = true)
    public void sizeMixin(CallbackInfoReturnable<Integer> info) {
        info.setReturnValue(info.getReturnValue() + 1);  // Add 1 for the custom slot
    }

    // Update the empty state to include the custom slot
    @Inject(method = "isEmpty", at = @At("TAIL"), cancellable = true)
    public void isEmptyMixin(CallbackInfoReturnable<Boolean> info) {
        if (!this.extraSlot.get(0).isEmpty()) {
            info.setReturnValue(false);  // If the custom slot has items, it's not empty
        }
    }
}
