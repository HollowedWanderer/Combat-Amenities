package net.hollowed.combatamenities.mixin.tweaks.pearl;

import net.hollowed.combatamenities.config.CAConfig;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrownEnderpearl;
import net.minecraft.world.item.EnderpearlItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnderpearlItem.class)
public abstract class EnderPearlItemMixin extends Item {

    public EnderPearlItemMixin(Properties settings) {
        super(settings);
    }

    public @NotNull ItemUseAnimation getUseAnimation(@NotNull ItemStack stack) {
        if (CAConfig.enderPearlTweaks) {
            return ItemUseAnimation.BOW;
        }
        return ItemUseAnimation.NONE;
    }

    @Unique
    private static final int MAX_CHARGE_TIME = 20; // Maximum charge time in ticks (1 second)
    @Unique
    private static final float MAX_VELOCITY = 1.5F; // Maximum velocity

    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    private void onUse(Level world, Player user, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        user.startUsingItem(hand);
        if (CAConfig.enderPearlTweaks) {
            cir.setReturnValue(InteractionResult.CONSUME);
        }
    }

    @Override
    public boolean releaseUsing(@NotNull ItemStack stack, @NotNull Level world, @NotNull LivingEntity user, int remainingUseTicks) {
        if (CAConfig.enderPearlTweaks) {
            if (!(user instanceof Player player)) {
                return false;
            }

            int chargeTime = this.getUseDuration(stack, user) - remainingUseTicks;
            float chargeRatio = Math.min(chargeTime / (float) MAX_CHARGE_TIME, 1.0F);
            float velocity = chargeRatio * MAX_VELOCITY;

            if (!world.isClientSide()) {
                ServerLevel serverWorld = (ServerLevel) world;
                ThrownEnderpearl enderPearlEntity = new ThrownEnderpearl(world, user, stack);
                enderPearlEntity.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, velocity, 1.0F);
                serverWorld.addFreshEntity(enderPearlEntity);
            }

            world.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.ENDER_PEARL_THROW, SoundSource.NEUTRAL, 0.5F, 0.4F / (world.getRandom().nextFloat() * 0.4F + 0.8F));
            player.awardStat(Stats.ITEM_USED.get(this));
            player.getCooldowns().addCooldown(stack, 100);
            stack.consume(1, player);
            return false;
        }
        return true;
    }

    @Override
    public int getUseDuration(@NotNull ItemStack stack, @NotNull LivingEntity user) {
        if (CAConfig.enderPearlTweaks) {
            return 72000; // Default max use time for items that can be charged
        }
        return 0;
    }
}