package net.hollowed.combatamenities.mixin.trident;

import net.hollowed.combatamenities.CombatAmenities;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TridentEntity.class)
public abstract class TridentEntityMixin extends PersistentProjectileEntity {
    @Shadow @Final private static TrackedData<Byte> LOYALTY;

    @Shadow public abstract void tick();

    @Unique
    private int originalSlot = -1; // Store the original slot index

    protected TridentEntityMixin(EntityType<? extends PersistentProjectileEntity> entityType, World world) {
        super(entityType, world);
    }

    // Capture the slot when the trident is thrown
    @Inject(method = "<init>(Lnet/minecraft/world/World;Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;)V",
            at = @At("TAIL"))
    public void captureOriginalSlot(World world, LivingEntity owner, ItemStack stack, CallbackInfo ci) {
        if (owner instanceof PlayerEntity player) {
            // Find the slot that matches the tridentStack
            for (int i = 0; i < player.getInventory().size(); i++) {
                if (player.getInventory().getStack(i) == stack) {
                    this.originalSlot = i;
                    break;
                }
            }
        }
    }

    // Inject at the beginning of the tick method
    @Inject(method = "tick", at = @At("HEAD"))
    private void injectLoyaltyOverride(CallbackInfo ci) {
        TridentEntity trident = (TridentEntity) (Object) this;

        // Get the item stack of the trident
        ItemStack tridentStack = trident.getItemStack();

        // Check if the trident has any enchantments
        if (EnchantmentHelper.hasEnchantments(tridentStack) && CombatAmenities.CONFIG.builtInLoyalty) {
            // Access the data tracker and set loyalty to 3
            DataTracker dataTracker = trident.getDataTracker();
            TrackedData<Byte> LOYALTY = TridentEntityMixin.LOYALTY; // Access the LOYALTY data tracker field
            dataTracker.set(LOYALTY, (byte) 3);
        }
    }

    @Inject(method = "tryPickup", at = @At("HEAD"), cancellable = true)
    public void tryPickup(PlayerEntity player, CallbackInfoReturnable<Boolean> cir) {
        // Check if the player is the owner
        if (this.isOwner(player)) {
            // Check if the player's inventory is full
            boolean hasSpace = false;
            for (ItemStack stack : player.getInventory().getMainStacks()) {
                if (stack.isEmpty()) {
                    hasSpace = true;
                    break;
                }
            }

            // Insert it into the player's inventory
            if (!player.isCreative() && !CombatAmenities.CONFIG.correctTridentReturn && hasSpace) {
                player.getInventory().insertStack(this.asItemStack());
                cir.setReturnValue(true);
            }
            // If the trident is in no-clip mode and original slot is valid
            if (this.isNoClip() && this.originalSlot != -1) {
                // Place the trident in the original slot if it's empty
                if (player.getInventory().getStack(this.originalSlot).isEmpty()) {
                    player.getInventory().setStack(this.originalSlot, this.asItemStack());
                    cir.setReturnValue(true);
                }
            }

            if (!hasSpace && !player.isCreative()) {
                cir.setReturnValue(false);
            }
        }
    }
}
