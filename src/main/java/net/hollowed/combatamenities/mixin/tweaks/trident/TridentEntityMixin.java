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
    private int originalSlot = -1;

    protected TridentEntityMixin(EntityType<? extends AbstractArrow> entityType, Level world) {
        super(entityType, world);
    }

    @Override
    public void combat_Amenities$setInt(int i) {
        this.originalSlot = i;
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void injectLoyaltyOverride(CallbackInfo ci) {
        ThrownTrident trident = (ThrownTrident) (Object) this;
        ItemStack tridentStack = trident.getPickupItemStackOrigin();

        if (EnchantmentHelper.hasAnyEnchantments(tridentStack) && CAConfig.builtInLoyalty) {
            SynchedEntityData dataTracker = trident.getEntityData();
            EntityDataAccessor<Byte> LOYALTY = TridentEntityMixin.ID_LOYALTY;
            dataTracker.set(LOYALTY, (byte) 3);
        }
    }

    @Inject(method = "tryPickup", at = @At("HEAD"), cancellable = true)
    public void tryPickup(Player player, CallbackInfoReturnable<Boolean> cir) {
        if (this.ownedBy(player)) {

            boolean hasSpace = false;
            for (ItemStack stack : player.getInventory().getNonEquipmentItems()) {
                if (stack.isEmpty()) {
                    hasSpace = true;
                    break;
                }
            }

            boolean hasSpaceWithOffhand = hasSpace || player.getOffhandItem().isEmpty();

            if (!player.isCreative() && !CAConfig.correctTridentReturn && hasSpace) {
                player.getInventory().add(this.getPickupItem());
                cir.setReturnValue(true);
            }

            if (this.isNoPhysics() && this.originalSlot != -1) {
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
