package net.hollowed.backslot.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Shadow @NotNull public abstract ItemStack getWeaponStack();

    @Inject(method = "damage", at = @At("HEAD"), cancellable = true)
    private void modifyShieldBlocking(ServerWorld world, DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity self = (LivingEntity) (Object) this;

        if (self.blockedByShield(source)) {
            // Halve the blocked damage instead of fully negating
            float reducedDamage = amount * 0.25F;
            self.damageShield(reducedDamage);


            // Apply the reduced damage directly to the entity
            float currentHealth = self.getHealth();

            if (!(source.isIn(DamageTypeTags.IS_PROJECTILE))) {
                for (int i = 0; i < reducedDamage; i++) {
                    if (self.getAbsorptionAmount() > 0) {
                        self.setAbsorptionAmount(self.getAbsorptionAmount() - 1);
                        reducedDamage--;
                    }
                }
                float newHealth = Math.max(0.0F, currentHealth - reducedDamage); // Ensure health doesn't go negative
                if (newHealth > 0) {
                    self.setHealth(newHealth);
                } else {
                    self.setHealth(0.1F);
                }

                // Trigger hurt animations and sound effects
                if (self instanceof ServerPlayerEntity player) {
                    player.increaseStat(Stats.DAMAGE_TAKEN, Math.round(reducedDamage * 10)); // Update stats
                    player.sendAbilitiesUpdate(); // Notify client of health update
                }
                self.timeUntilRegen = 20; // Apply invulnerability ticks to prevent further immediate damage
                self.onDamaged(source); // Trigger damage-related effects
            }

            if (self instanceof PlayerEntity player) {
                player.getWorld().playSound(null, self.getBlockPos(), SoundEvents.ENTITY_PLAYER_HURT, SoundCategory.PLAYERS, 1.0F, 1.0F);
                player.getWorld().playSound(null, self.getBlockPos(), SoundEvents.ITEM_SHIELD_BLOCK, SoundCategory.PLAYERS, 1.0F, 1.0F);
            }

            // Disable the shield temporarily if the damage exceeds 15
            if (amount > 15.0F) {
                if (self instanceof ServerPlayerEntity player) {
                    player.getItemCooldownManager().set(player.getActiveItem(), 60);
                    self.stopUsingItem();
                }
            }

            // Cancel further processing as we've handled the custom shield behavior
            cir.setReturnValue(false);
        }
    }
}

