package net.hollowed.combatamenities.mixin.tweaks.trident;

import net.hollowed.combatamenities.CombatAmenities;
import net.hollowed.combatamenities.util.interfaces.TridentOwnerSetter;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.TridentItem;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TridentItem.class)
public class TridentItemMixin {

    @Unique
    private int getTheFuckingGodDamnSlot = -1;

    @Redirect(
            method = "onStoppedUsing",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/projectile/ProjectileEntity;spawnWithVelocity(Lnet/minecraft/entity/projectile/ProjectileEntity$ProjectileCreator;Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/LivingEntity;FFF)Lnet/minecraft/entity/projectile/ProjectileEntity;"
            )
    )
    private ProjectileEntity modifyTridentEntity(
            ProjectileEntity.ProjectileCreator<TridentEntity> creator, ServerWorld world, ItemStack projectileStack, LivingEntity shooter, float roll, float power, float divergence
    ) {
        ProjectileEntity entity = creator.create(world, shooter, projectileStack);
        if (entity instanceof TridentOwnerSetter trident && CombatAmenities.CONFIG.correctTridentReturn) {
            trident.combat_Amenities$setInt(this.getTheFuckingGodDamnSlot);
        }

        return ProjectileEntity.spawnWithVelocity((w, s, u) -> entity, world, projectileStack, shooter, roll, power, divergence);
    }

    @Inject(method = "onStoppedUsing", at = @At("HEAD"))
    public void stopUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks, CallbackInfoReturnable<Boolean> cir) {
        if (user instanceof PlayerEntity player) {
            PlayerInventory inventory = player.getInventory();
            for (int i = 0; i < inventory.size(); i++) {
                ItemStack itemStack = inventory.getStack(i);
                if (itemStack.equals(stack)) {
                    this.getTheFuckingGodDamnSlot = i;
                }
            }

        }
    }
}

