package net.hollowed.combatamenities.mixin.slots.registration;

import com.mojang.authlib.GameProfile;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gamerules.GameRules;
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
    private SimpleContainer reservedSlotInventory = new SimpleContainer(2);

    public ServerPlayerEntityMixin(Level world, GameProfile profile) {
        super(world, profile);
    }

    @Inject(method = "restoreFrom", at = @At("TAIL"))
    private void copyReservedSlot(ServerPlayer oldPlayer, boolean restoreAll, CallbackInfo ci) {
        if (this.level().getGameRules().get(GameRules.KEEP_INVENTORY)) {
            ServerPlayerEntityMixin oldMixin = (ServerPlayerEntityMixin) (Object) oldPlayer;
            if (oldMixin != null) {
                this.reservedSlotInventory.setItem(0, oldMixin.reservedSlotInventory.getItem(0));
                this.reservedSlotInventory.setItem(1, oldMixin.reservedSlotInventory.getItem(1));
            }
        }
    }

    @Inject(method = "die", at = @At("HEAD"))
    private void storeReservedSlot(DamageSource source, CallbackInfo ci) {
        ItemStack backItem = getItemBySlot(EquipmentSlot.COMBATAMENITIES_BACKSLOT);
        if (!backItem.isEmpty() && this.level().getGameRules().get(GameRules.KEEP_INVENTORY)) {
            reservedSlotInventory.setItem(0, backItem.copy());
        }
        ItemStack beltItem = getItemBySlot(EquipmentSlot.COMBATAMENITIES_BELTSLOT);
        if (!beltItem.isEmpty() && this.level().getGameRules().get(GameRules.KEEP_INVENTORY)) {
            reservedSlotInventory.setItem(1, beltItem.copy());
        }
    }

    @Inject(method = "initInventoryMenu", at = @At("TAIL"))
    private void restoreReservedSlot(CallbackInfo ci) {
        if (!reservedSlotInventory.getItem(0).isEmpty() && this.level().getGameRules().get(GameRules.KEEP_INVENTORY)) {
            setItemSlot(EquipmentSlot.COMBATAMENITIES_BACKSLOT, reservedSlotInventory.getItem(0).copy());
        }
        if (!reservedSlotInventory.getItem(1).isEmpty() && this.level().getGameRules().get(GameRules.KEEP_INVENTORY)) {
            setItemSlot(EquipmentSlot.COMBATAMENITIES_BELTSLOT, reservedSlotInventory.getItem(1).copy());
        }
        reservedSlotInventory.clearContent();
    }
}