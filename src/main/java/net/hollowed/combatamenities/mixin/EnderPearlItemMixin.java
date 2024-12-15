package net.hollowed.combatamenities.mixin;

import net.hollowed.combatamenities.CombatAmenities;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.EnderPearlEntity;
import net.minecraft.item.EnderPearlItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.consume.UseAction;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnderPearlItem.class)
public abstract class EnderPearlItemMixin extends Item {

    public EnderPearlItemMixin(Settings settings) {
        super(settings);
    }

    public UseAction getUseAction(ItemStack stack) {
        if (CombatAmenities.CONFIG.enderPearlTweaks) {
            return UseAction.BOW;
        }
        return UseAction.NONE;
    }

    @Unique
    private static final int MAX_CHARGE_TIME = 20; // Maximum charge time in ticks (1 second)
    @Unique
    private static final float MAX_VELOCITY = 1.5F; // Maximum velocity

    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    private void onUse(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        user.setCurrentHand(hand);
        if (CombatAmenities.CONFIG.enderPearlTweaks) {
            cir.setReturnValue(ActionResult.CONSUME);
        }
    }

    @Override
    public boolean onStoppedUsing(ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        if (CombatAmenities.CONFIG.enderPearlTweaks) {
            if (!(user instanceof PlayerEntity player)) {
                return false;
            }

            int chargeTime = this.getMaxUseTime(stack, user) - remainingUseTicks;
            float chargeRatio = Math.min(chargeTime / (float) MAX_CHARGE_TIME, 1.0F);
            float velocity = chargeRatio * MAX_VELOCITY;

            if (!world.isClient) {
                ServerWorld serverWorld = (ServerWorld) world;
                EnderPearlEntity enderPearlEntity = new EnderPearlEntity(world, user, stack);
                enderPearlEntity.setVelocity(player, player.getPitch(), player.getYaw(), 0.0F, velocity, 1.0F);
                serverWorld.spawnEntity(enderPearlEntity);
            }

            world.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.ENTITY_ENDER_PEARL_THROW, SoundCategory.NEUTRAL, 0.5F, 0.4F / (world.getRandom().nextFloat() * 0.4F + 0.8F));
            player.incrementStat(Stats.USED.getOrCreateStat(this));
            ((PlayerEntity) user).getItemCooldownManager().set(stack, 100);
            stack.decrementUnlessCreative(1, player);
            return false;
        }
        return true;
    }

    @Override
    public int getMaxUseTime(ItemStack stack, LivingEntity user) {
        if (CombatAmenities.CONFIG.enderPearlTweaks) {
            return 72000; // Default max use time for items that can be charged
        }
        return 0;
    }
}