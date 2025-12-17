package net.hollowed.combatamenities.mixin.tweaks.trident;

import net.hollowed.combatamenities.config.CAConfig;
import net.hollowed.combatamenities.util.interfaces.TridentOwnerSetter;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.arrow.ThrownTrident;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TridentItem;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
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
            method = "releaseUsing",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/projectile/Projectile;spawnProjectileFromRotation(Lnet/minecraft/world/entity/projectile/Projectile$ProjectileFactory;Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/LivingEntity;FFF)Lnet/minecraft/world/entity/projectile/Projectile;"
            )
    )
    private Projectile modifyTridentEntity(
            Projectile.ProjectileFactory<@NotNull ThrownTrident> creator, ServerLevel world, ItemStack projectileStack, LivingEntity shooter, float roll, float power, float divergence
    ) {
        Projectile entity = creator.create(world, shooter, projectileStack);
        if (entity instanceof TridentOwnerSetter trident && CAConfig.correctTridentReturn) {
            trident.combat_Amenities$setInt(this.getTheFuckingGodDamnSlot);
        }

        return Projectile.spawnProjectileFromRotation((w, s, u) -> entity, world, projectileStack, shooter, roll, power, divergence);
    }

    @Inject(method = "releaseUsing", at = @At("HEAD"))
    public void stopUsing(ItemStack stack, Level world, LivingEntity user, int remainingUseTicks, CallbackInfoReturnable<Boolean> cir) {
        if (user instanceof Player player) {
            Inventory inventory = player.getInventory();
            for (int i = 0; i < inventory.getContainerSize(); i++) {
                ItemStack itemStack = inventory.getItem(i);
                if (itemStack.equals(stack)) {
                    this.getTheFuckingGodDamnSlot = i;
                }
            }

        }
    }
}

