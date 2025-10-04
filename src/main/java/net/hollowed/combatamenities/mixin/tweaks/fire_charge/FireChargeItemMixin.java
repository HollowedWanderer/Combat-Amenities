package net.hollowed.combatamenities.mixin.tweaks.fire_charge;

import net.hollowed.combatamenities.config.CAConfig;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.SmallFireballEntity;
import net.minecraft.item.FireChargeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FireChargeItem.class)
public abstract class FireChargeItemMixin extends Item {

    public FireChargeItemMixin(Settings settings) {
        super(settings);
    }

    @Unique
    public ActionResult use(World world, PlayerEntity player, Hand hand) {
        if (!world.isClient()) {
            if (CAConfig.throwableFirecharge) {
                world.syncWorldEvent(null, 1018, player.getBlockPos(), 0);
                ItemStack stack = player.getStackInHand(hand);
                player.swingHand(hand, true);
                SmallFireballEntity fireball = new SmallFireballEntity(player.getEntityWorld(), player, player.getRotationVec(1.0F).normalize().multiply(2));
                fireball.setPosition(fireball.getX(), player.getEyeY(), fireball.getZ());
                world.spawnEntity(fireball);
                stack.decrementUnlessCreative(1, player);
                player.getItemCooldownManager().set(stack, 6);
                return ActionResult.SUCCESS;
            }
        }
        return ActionResult.PASS;
    }

    @Inject(method = "useOnBlock", at = @At("HEAD"), cancellable = true)
    private void useOnBlock(ItemUsageContext context, CallbackInfoReturnable<ActionResult> cir) {
        cir.setReturnValue(ActionResult.PASS);
    }
}
