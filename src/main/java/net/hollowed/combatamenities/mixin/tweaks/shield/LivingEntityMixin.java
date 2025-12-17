package net.hollowed.combatamenities.mixin.tweaks.shield;

import net.hollowed.combatamenities.CombatAmenities;
import net.hollowed.combatamenities.config.CAConfig;
import net.hollowed.combatamenities.util.entities.ModDamageTypes;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemCooldowns;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.item.component.BlocksAttacks;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Optional;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {

    @Unique
    private BlocksAttacks shield;

    @Inject(method = "hurtServer", at = @At("HEAD"), cancellable = true)
    private void modifyShieldBlocking(ServerLevel world, DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity self = (LivingEntity) (Object) this;
        BlocksAttacks blocksAttacksComponent = self.getUseItem().get(DataComponents.BLOCKS_ATTACKS);
        boolean bypassShieldTweaks = source.getEntity() instanceof LivingEntity living
                && living.getMainHandItem().getTags().toList().contains(TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(CombatAmenities.MOD_ID, "bypass_shield_tweaks")));

        if (CAConfig.shieldTweaks && !source.is(ModDamageTypes.CLEAVED) && !bypassShieldTweaks) {
            Vec3 attackDirection = source.getSourcePosition() != null ? source.getSourcePosition().subtract(self.position()).normalize() : Vec3.ZERO;
            Vec3 lookDirection = self.getViewVector(1.0F).normalize();
            double angle = attackDirection.dot(lookDirection); // Cosine of the angle between attack and look direction

            // If the shield was raised for 10 ticks or fewer (0.5 seconds at 20 ticks per second)
            if (self.getTicksUsingItem() <= CAConfig.shieldParryTime && self.getTicksUsingItem() > 0 && blocksAttacksComponent != null) {

                // If the attack is in front of the player (within ~90 degrees)
                if (angle > 0.0) {
                    // Negate damage and trigger parry effects
                    if (self instanceof Player player) {
                        ServerLevel serverWorld = (ServerLevel) player.level();
                        serverWorld.playSound(null, player.blockPosition(), SoundEvents.SHIELD_BLOCK.value(), SoundSource.PLAYERS, 1.0F, 1.0F); // Higher-pitched block sound
                        serverWorld.playSound(null, player.blockPosition(), SoundEvents.MACE_SMASH_AIR, SoundSource.PLAYERS, 0.3F, 1.5F); // Parry sound

                        // Grant "Not Today, Thank You!" advancement
                        AdvancementHolder advancement = serverWorld.getServer().getAdvancements().get(Identifier.withDefaultNamespace("story/deflect_arrow"));
                        if (advancement != null && player instanceof ServerPlayer serverPlayer && source.is(DamageTypeTags.IS_PROJECTILE)) {
                            serverPlayer.getAdvancements().award(advancement, "deflected_projectile");
                        }

                        // Knock back nearby entities
                        double knockbackStrength = 1; // Adjust as needed

                        // Apply shield cooldown
                        ItemCooldowns cooldownManager = player.getCooldowns();
                        if (source.getDirectEntity() instanceof LivingEntity attacker) {
                            Vec3 knockbackDirection = attacker.position().subtract(player.position()).normalize();
                            attacker.knockback(knockbackStrength, -knockbackDirection.x, -knockbackDirection.z);
                            // Check if the attacker is using an axe
                            if (attacker.getSecondsToDisableBlocking() > 0) {
                                cooldownManager.addCooldown(self.getUseItem(), 40); // 2 seconds cooldown for axe hit
                                self.releaseUsingItem();
                            } else {
                                cooldownManager.addCooldown(self.getUseItem(), 10); // 0.5 seconds cooldown for regular parry
                                self.releaseUsingItem();
                            }
                        } else if (source.getDirectEntity() instanceof Projectile entity) {
                            entity.setDeltaMovement(entity.getDeltaMovement().scale(10F));
                            entity.hurtMarked = true;
                            entity.needsSync = true;
                        }
                    }

                    // Cancel the damage
                    cir.setReturnValue(false);
                    return;
                }
            }

            if (source.is(DamageTypeTags.IS_PROJECTILE) && self.getTicksUsingItem() > 0 && blocksAttacksComponent != null && angle > 0.0F) {
                if (self instanceof Player player) {
                    ServerLevel serverWorld = (ServerLevel) player.level();
                    serverWorld.playSound(null, player.blockPosition(), SoundEvents.SHIELD_BLOCK.value(), SoundSource.PLAYERS, 1.0F, 1.0F); // Higher-pitched block sound
                    cir.setReturnValue(false);
                }
            }

            if (blocksAttacksComponent != null && self.getUseItem().getItem() instanceof ShieldItem) {
                shield = self.getUseItem().get(DataComponents.BLOCKS_ATTACKS);
                self.getUseItem().set(DataComponents.BLOCKS_ATTACKS, new BlocksAttacks(0.25F, 1.0F, List.of(new BlocksAttacks.DamageReduction(90.0F, Optional.empty(), 0.0F, 0.5F)), new BlocksAttacks.ItemDamageFunction(3.0F, 1.0F, 1.0F), Optional.of(DamageTypeTags.BYPASSES_SHIELD), Optional.of(SoundEvents.SHIELD_BLOCK), Optional.of(SoundEvents.SHIELD_BREAK)));
            }
        }
    }

    @Inject(method = "hurtServer", at = @At("TAIL"))
    private void modifyShieldBlockingEnd(ServerLevel world, DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (shield != null) self.getUseItem().set(DataComponents.BLOCKS_ATTACKS, shield);
        ServerLevel serverWorld = (ServerLevel) self.level();
        if (self instanceof Player) {
            serverWorld.playSound(null, self.blockPosition(), SoundEvents.PLAYER_HURT, SoundSource.PLAYERS, 1.0F, 1.0F);
        }

    }
}

