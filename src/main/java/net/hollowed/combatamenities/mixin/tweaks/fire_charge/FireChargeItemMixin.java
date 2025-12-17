package net.hollowed.combatamenities.mixin.tweaks.fire_charge;

import net.hollowed.combatamenities.config.CAConfig;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.hurtingprojectile.SmallFireball;
import net.minecraft.world.item.FireChargeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FireChargeItem.class)
public abstract class FireChargeItemMixin extends Item {

    public FireChargeItemMixin(Properties settings) {
        super(settings);
    }

    @Unique
    public @NotNull InteractionResult use(Level world, @NotNull Player player, @NotNull InteractionHand hand) {
        if (!world.isClientSide()) {
            if (CAConfig.throwableFirecharge) {
                world.levelEvent(null, 1018, player.blockPosition(), 0);
                ItemStack stack = player.getItemInHand(hand);
                player.swing(hand, true);
                SmallFireball fireball = new SmallFireball(player.level(), player, player.getViewVector(1.0F).normalize().scale(2));
                fireball.setPos(fireball.getX(), player.getEyeY(), fireball.getZ());
                world.addFreshEntity(fireball);
                stack.consume(1, player);
                player.getCooldowns().addCooldown(stack, 6);
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.PASS;
    }

    @Inject(method = "useOn", at = @At("HEAD"), cancellable = true)
    private void useOnBlock(UseOnContext context, CallbackInfoReturnable<InteractionResult> cir) {
        cir.setReturnValue(InteractionResult.PASS);
    }
}
