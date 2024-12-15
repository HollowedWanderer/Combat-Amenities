package net.hollowed.combatamenities.mixin.slots;

import com.mojang.authlib.GameProfile;
import net.hollowed.combatamenities.CombatAmenities;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity {
    @Unique
    private static final int RESERVED_SLOT_INDEX = 41; // Slot to keep on death

    @Shadow
    public abstract void readCustomDataFromNbt(NbtCompound nbt);

    @Shadow
    public abstract void writeCustomDataToNbt(NbtCompound nbt);

    @Shadow public abstract ServerWorld getServerWorld();

    @Unique
    private SimpleInventory reservedSlotInventory = new SimpleInventory(1);

    public ServerPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile profile) {
        super(world, pos, yaw, profile);
    }

    @Inject(method = "copyFrom", at = @At("TAIL"))
    private void copyReservedSlot(ServerPlayerEntity oldPlayer, boolean alive, CallbackInfo ci) {
        if (this.getServerWorld().getGameRules().getBoolean(CombatAmenities.KEEP_BACK_SLOT_ITEM)) {
            ServerPlayerEntityMixin oldMixin = (ServerPlayerEntityMixin) (Object) oldPlayer;
            assert oldMixin != null;
            this.reservedSlotInventory.setStack(0, oldMixin.reservedSlotInventory.getStack(0));
        }
    }

    @Inject(method = "onDeath", at = @At("HEAD"))
    private void storeReservedSlot(DamageSource source, CallbackInfo ci) {
        ItemStack slot41Item = this.getInventory().getStack(RESERVED_SLOT_INDEX);
        if (!slot41Item.isEmpty() && this.getServerWorld().getGameRules().getBoolean(CombatAmenities.KEEP_BACK_SLOT_ITEM)) {
            this.getInventory().setStack(RESERVED_SLOT_INDEX, ItemStack.EMPTY);
            reservedSlotInventory.setStack(0, slot41Item.copy());
        }
    }

    @Inject(method = "onSpawn", at = @At("TAIL"))
    private void restoreReservedSlot(CallbackInfo ci) {
        if (!reservedSlotInventory.getStack(0).isEmpty() && this.getServerWorld().getGameRules().getBoolean(CombatAmenities.KEEP_BACK_SLOT_ITEM)) {
            this.getInventory().setStack(RESERVED_SLOT_INDEX, reservedSlotInventory.getStack(0).copy());
            reservedSlotInventory.clear();
        }
    }
}