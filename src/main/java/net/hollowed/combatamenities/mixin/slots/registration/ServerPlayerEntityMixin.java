package net.hollowed.combatamenities.mixin.slots.registration;

import com.mojang.authlib.GameProfile;
import net.hollowed.combatamenities.CombatAmenities;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerEntityMixin extends Player {

    @Shadow public abstract @NotNull ServerLevel level();

    @Shadow public abstract ItemEntity drop(@NotNull ItemStack stack, boolean dropAtSelf, boolean retainOwnership);

    @Unique
    private static final int RESERVED_SLOT_INDEX = 41; // Slot to keep on death
    @Unique
    private static final int OTHER_RESERVED_SLOT_INDEX = 42; // Slot to keep on death

    @Unique
    private SimpleContainer reservedSlotInventory = new SimpleContainer(2);

    public ServerPlayerEntityMixin(Level world, GameProfile profile) {
        super(world, profile);
    }

    @Inject(method = "restoreFrom", at = @At("TAIL"))
    private void copyReservedSlot(ServerPlayer oldPlayer, boolean alive, CallbackInfo ci) {
        if (this.level().getGameRules().get(CombatAmenities.KEEP_BACK_SLOT_ITEM)) {
            ServerPlayerEntityMixin oldMixin = (ServerPlayerEntityMixin) (Object) oldPlayer;
            assert oldMixin != null;
            this.reservedSlotInventory.setItem(0, oldMixin.reservedSlotInventory.getItem(0));
            this.reservedSlotInventory.setItem(1, oldMixin.reservedSlotInventory.getItem(1));
        }
    }

    @Inject(method = "die", at = @At("HEAD"))
    private void storeReservedSlot(DamageSource source, CallbackInfo ci) {
        ItemStack slot41Item = this.getInventory().getItem(RESERVED_SLOT_INDEX);
        if (!slot41Item.isEmpty() && this.level().getGameRules().get(CombatAmenities.KEEP_BACK_SLOT_ITEM)) {
            this.getInventory().setItem(RESERVED_SLOT_INDEX, ItemStack.EMPTY);
            reservedSlotInventory.setItem(0, slot41Item.copy());
        }
        ItemStack slot42Item = this.getInventory().getItem(OTHER_RESERVED_SLOT_INDEX);
        if (!slot42Item.isEmpty() && this.level().getGameRules().get(CombatAmenities.KEEP_BACK_SLOT_ITEM)) {
            this.getInventory().setItem(OTHER_RESERVED_SLOT_INDEX, ItemStack.EMPTY);
            reservedSlotInventory.setItem(1, slot42Item.copy());
        }
    }

    @Inject(method = "initInventoryMenu", at = @At("TAIL"))
    private void restoreReservedSlot(CallbackInfo ci) {
        if (!reservedSlotInventory.getItem(0).isEmpty() && this.level().getGameRules().get(CombatAmenities.KEEP_BACK_SLOT_ITEM)) {
            this.getInventory().setItem(RESERVED_SLOT_INDEX, reservedSlotInventory.getItem(0).copy());
        }
        if (!reservedSlotInventory.getItem(1).isEmpty() && this.level().getGameRules().get(CombatAmenities.KEEP_BACK_SLOT_ITEM)) {
            this.getInventory().setItem(OTHER_RESERVED_SLOT_INDEX, reservedSlotInventory.getItem(1).copy());
        }
        reservedSlotInventory.clearContent();
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void tickItems(CallbackInfo ci) {
        if (!this.getInventory().getItem(41).isEmpty()) {
            this.getInventory().getItem(41).inventoryTick(this.level(), this, null);
        }
        if (!this.getInventory().getItem(42).isEmpty()) {
            this.getInventory().getItem(42).inventoryTick(this.level(), this, null);
        }
    }
}