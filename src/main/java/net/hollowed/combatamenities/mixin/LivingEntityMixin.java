package net.hollowed.combatamenities.mixin;

import net.hollowed.combatamenities.CombatAmenities;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.ItemCooldownManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ShieldItem;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Inject(method = "damage", at = @At("HEAD"), cancellable = true)
    private void modifyShieldBlocking(ServerWorld world, DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity self = (LivingEntity) (Object) this;

        if (CombatAmenities.CONFIG.shieldTweaks) {
            // If the shield was raised for 10 ticks or fewer (0.5 seconds at 20 ticks per second)
            if (self.getItemUseTime() <= CombatAmenities.CONFIG.shieldParryTime && self.getItemUseTime() > 0 && self.getActiveItem().getItem() instanceof ShieldItem) {
                // Check if the attack is from the front
                Vec3d attackDirection = source.getPosition() != null ? source.getPosition().subtract(self.getPos()).normalize() : Vec3d.ZERO;
                Vec3d lookDirection = self.getRotationVec(1.0F).normalize();
                double angle = attackDirection.dotProduct(lookDirection); // Cosine of the angle between attack and look direction

                // If the attack is in front of the player (within ~90 degrees)
                if (angle > 0.0) {
                    // Negate damage and trigger parry effects
                    if (self instanceof PlayerEntity player) {
                        ServerWorld serverWorld = (ServerWorld) player.getWorld();
                        serverWorld.playSound(null, player.getBlockPos(), SoundEvents.ITEM_SHIELD_BLOCK, SoundCategory.PLAYERS, 1.0F, 1.0F); // Higher-pitched block sound
                        serverWorld.playSound(null, player.getBlockPos(), SoundEvents.ITEM_MACE_SMASH_AIR, SoundCategory.PLAYERS, 0.3F, 1.5F); // Parry sound

                        // Grant "Not Today, Thank You!" advancement
                        AdvancementEntry advancement = serverWorld.getServer().getAdvancementLoader().get(Identifier.ofVanilla("story/deflect_arrow"));
                        if (advancement != null && player instanceof ServerPlayerEntity serverPlayer) {
                            serverPlayer.getAdvancementTracker().grantCriterion(advancement, "deflected_projectile");
                        }

                        // Knock back nearby entities
                        double knockbackStrength = 1; // Adjust as needed

                        // Apply shield cooldown
                        ItemCooldownManager cooldownManager = player.getItemCooldownManager();
                        if (source.getSource() instanceof LivingEntity attacker) {
                            Vec3d knockbackDirection = attacker.getPos().subtract(player.getPos()).normalize();
                            attacker.takeKnockback(knockbackStrength, -knockbackDirection.x, -knockbackDirection.z);
                            // Check if the attacker is using an axe
                            if (attacker.getMainHandStack().getItem() instanceof AxeItem || attacker.getOffHandStack().getItem() instanceof AxeItem) {
                                cooldownManager.set(self.getActiveItem(), 40); // 2 seconds cooldown for axe hit
                                self.stopUsingItem();
                            } else {
                                cooldownManager.set(self.getActiveItem(), 10); // 0.5 seconds cooldown for regular parry
                                self.stopUsingItem();
                            }
                        } else if (source.getSource() instanceof ProjectileEntity entity) {
                            entity.setVelocity(entity.getVelocity().multiply(10F));
                            entity.velocityModified = true;
                            entity.velocityDirty = true;
                        }
                    }

                    // Cancel the damage
                    cir.setReturnValue(false);
                    return;
                }
            }


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
                    if (source.getSource() instanceof LivingEntity attacker) {
                        Vec3d knockbackDirection = attacker.getPos().subtract(player.getPos()).normalize();
                        attacker.takeKnockback(0.25, -knockbackDirection.x, -knockbackDirection.z);
                        attacker.velocityModified = true;
                        attacker.velocityDirty = true;
                    }
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
}

