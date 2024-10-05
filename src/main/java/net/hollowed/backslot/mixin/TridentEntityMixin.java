package net.hollowed.backslot.mixin;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.TridentItem;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TridentEntity.class)
public abstract class TridentEntityMixin extends PersistentProjectileEntity {
    @Shadow
    private ItemStack tridentStack;  // Use existing tridentStack
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

    /**
     * @author Hollowed
     * @reason funny
     */
    @Overwrite
    public boolean tryPickup(PlayerEntity player) {
        // Check if the player is the owner
        if (this.isOwner(player)) {
            // If the trident is in no-clip mode and original slot is valid
            if (this.isNoClip() && this.originalSlot != -1) {
                // Place the trident in the original slot if it's empty
                if (player.getInventory().getStack(this.originalSlot).isEmpty()) {
                    player.getInventory().setStack(this.originalSlot, this.asItemStack());
                    return true;
                }
            }

            // Check if the player's inventory is full
            boolean hasSpace = false;
            for (ItemStack stack : player.getInventory().main) {
                if (stack.isEmpty()) {
                    hasSpace = true;
                    break;
                }
            }

            // If the inventory is full, drop the trident item on the ground
            if (!hasSpace) {
                ItemEntity itemEntity = new ItemEntity(player.getWorld(), this.getX(), this.getY() - 1, this.getZ(), this.asItemStack());
                itemEntity.setVelocity(player.getVelocity().add(0, 0.1, 0)); // Add upward velocity
                player.getWorld().spawnEntity(itemEntity); // Spawn the item entity in the world
                this.remove(RemovalReason.DISCARDED); // Remove the trident entity
                return true;
            }

            // Otherwise, insert it into the player's inventory
            if (!player.isCreative()) {
                player.getInventory().insertStack(this.asItemStack());
            }
            return true;
        }
        return false;
    }
}
