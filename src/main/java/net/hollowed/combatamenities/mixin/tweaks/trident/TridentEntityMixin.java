package net.hollowed.combatamenities.mixin.tweaks.trident;

import net.hollowed.combatamenities.config.CAConfig;
import net.hollowed.combatamenities.util.interfaces.TridentOwnerSetter;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.entity.projectile.arrow.ThrownTrident;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ThrownTrident.class)
public abstract class TridentEntityMixin extends AbstractArrow implements TridentOwnerSetter {
    @Shadow @Final private static EntityDataAccessor<Byte> ID_LOYALTY;

    @Shadow public abstract void tick();

    @Unique
    private int originalSlot = -1; // Store the original slot index

    protected TridentEntityMixin(EntityType<? extends AbstractArrow> entityType, Level world) {
        super(entityType, world);
    }

    @Override
    public void combat_Amenities$setInt(int i) {
        this.originalSlot = i;
    }

    // Inject at the beginning of the tick method
    @Inject(method = "tick", at = @At("HEAD"))
    private void injectLoyaltyOverride(CallbackInfo ci) {
        ThrownTrident trident = (ThrownTrident) (Object) this;

        // Get the item stack of the trident
        ItemStack tridentStack = trident.getPickupItemStackOrigin();

        // Check if the trident has any enchantments
        if (EnchantmentHelper.hasAnyEnchantments(tridentStack) && CAConfig.builtInLoyalty) {
            // Access the data tracker and set loyalty to 3
            SynchedEntityData dataTracker = trident.getEntityData();
            EntityDataAccessor<Byte> LOYALTY = TridentEntityMixin.ID_LOYALTY; // Access the LOYALTY data tracker field
            dataTracker.set(LOYALTY, (byte) 3);
        }
    }

    @Inject(method = "tryPickup", at = @At("HEAD"), cancellable = true)
    public void tryPickup(Player player, CallbackInfoReturnable<Boolean> cir) {
        // Check if the player is the owner
        if (this.ownedBy(player)) {

            // Check if the player's inventory is full
            boolean hasSpace = false;
            for (ItemStack stack : player.getInventory().getNonEquipmentItems()) {
                if (stack.isEmpty()) {
                    hasSpace = true;
                    break;
                }
            }

            boolean hasSpaceWithOffhand = hasSpace || player.getOffhandItem().isEmpty();

            // Insert it into the player's inventory
            if (!player.isCreative() && !CAConfig.correctTridentReturn && hasSpace) {
                player.getInventory().add(this.getPickupItem());
                cir.setReturnValue(true);
            }

            // If the trident is in no-clip mode and original slot is valid
            if (this.isNoPhysics() && this.originalSlot != -1) {
                // Place the trident in the original slot if it's empty
                if (player.getInventory().getItem(this.originalSlot).isEmpty()) {
                    player.getInventory().setItem(this.originalSlot, this.getPickupItem());
                    cir.setReturnValue(true);
                }
            }

            if (CAConfig.correctTridentReturn ? !hasSpaceWithOffhand : !hasSpace && !player.isCreative()) {
                cir.setReturnValue(false);
            }
        }
    }
}
