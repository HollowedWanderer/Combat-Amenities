package net.hollowed.combatamenities.mixin.tweaks.shield;

import net.hollowed.combatamenities.CombatAmenities;
import net.hollowed.combatamenities.config.CAConfig;
import net.hollowed.combatamenities.util.entities.ModDamageTypes;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BlocksAttacksComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.ItemCooldownManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ShieldItem;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
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
    private BlocksAttacksComponent shield;

    @Inject(method = "damage", at = @At("HEAD"), cancellable = true)
    private void modifyShieldBlocking(ServerWorld world, DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity self = (LivingEntity) (Object) this;
        BlocksAttacksComponent blocksAttacksComponent = self.getActiveItem().get(DataComponentTypes.BLOCKS_ATTACKS);
        boolean bypassShieldTweaks = source.getAttacker() instanceof LivingEntity living
                && living.getMainHandStack().streamTags().toList().contains(TagKey.of(RegistryKeys.ITEM, Identifier.of(CombatAmenities.MOD_ID, "bypass_shield_tweaks")));

        if (CAConfig.shieldTweaks && !source.isOf(ModDamageTypes.CLEAVED) && !bypassShieldTweaks) {
            Vec3d attackDirection = source.getPosition() != null ? source.getPosition().subtract(self.getEntityPos()).normalize() : Vec3d.ZERO;
            Vec3d lookDirection = self.getRotationVec(1.0F).normalize();
            double angle = attackDirection.dotProduct(lookDirection); // Cosine of the angle between attack and look direction

            // If the shield was raised for 10 ticks or fewer (0.5 seconds at 20 ticks per second)
            if (self.getItemUseTime() <= CAConfig.shieldParryTime && self.getItemUseTime() > 0 && blocksAttacksComponent != null) {

                // If the attack is in front of the player (within ~90 degrees)
                if (angle > 0.0) {
                    // Negate damage and trigger parry effects
                    if (self instanceof PlayerEntity player) {
                        ServerWorld serverWorld = (ServerWorld) player.getEntityWorld();
                        serverWorld.playSound(null, player.getBlockPos(), SoundEvents.ITEM_SHIELD_BLOCK.value(), SoundCategory.PLAYERS, 1.0F, 1.0F); // Higher-pitched block sound
                        serverWorld.playSound(null, player.getBlockPos(), SoundEvents.ITEM_MACE_SMASH_AIR, SoundCategory.PLAYERS, 0.3F, 1.5F); // Parry sound

                        // Grant "Not Today, Thank You!" advancement
                        AdvancementEntry advancement = serverWorld.getServer().getAdvancementLoader().get(Identifier.ofVanilla("story/deflect_arrow"));
                        if (advancement != null && player instanceof ServerPlayerEntity serverPlayer && source.isIn(DamageTypeTags.IS_PROJECTILE)) {
                            serverPlayer.getAdvancementTracker().grantCriterion(advancement, "deflected_projectile");
                        }

                        // Knock back nearby entities
                        double knockbackStrength = 1; // Adjust as needed

                        // Apply shield cooldown
                        ItemCooldownManager cooldownManager = player.getItemCooldownManager();
                        if (source.getSource() instanceof LivingEntity attacker) {
                            Vec3d knockbackDirection = attacker.getEntityPos().subtract(player.getEntityPos()).normalize();
                            attacker.takeKnockback(knockbackStrength, -knockbackDirection.x, -knockbackDirection.z);
                            // Check if the attacker is using an axe
                            if (attacker.getWeaponDisableBlockingForSeconds() > 0) {
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

            if (source.isIn(DamageTypeTags.IS_PROJECTILE) && self.getItemUseTime() > 0 && blocksAttacksComponent != null && angle > 0.0F) {
                if (self instanceof PlayerEntity player) {
                    ServerWorld serverWorld = (ServerWorld) player.getEntityWorld();
                    serverWorld.playSound(null, player.getBlockPos(), SoundEvents.ITEM_SHIELD_BLOCK.value(), SoundCategory.PLAYERS, 1.0F, 1.0F); // Higher-pitched block sound
                    cir.setReturnValue(false);
                }
            }

            if (blocksAttacksComponent != null && self.getActiveItem().getItem() instanceof ShieldItem) {
                shield = self.getActiveItem().get(DataComponentTypes.BLOCKS_ATTACKS);
                self.getActiveItem().set(DataComponentTypes.BLOCKS_ATTACKS, new BlocksAttacksComponent(0.25F, 1.0F, List.of(new BlocksAttacksComponent.DamageReduction(90.0F, Optional.empty(), 0.0F, 0.5F)), new BlocksAttacksComponent.ItemDamage(3.0F, 1.0F, 1.0F), Optional.of(DamageTypeTags.BYPASSES_SHIELD), Optional.of(SoundEvents.ITEM_SHIELD_BLOCK), Optional.of(SoundEvents.ITEM_SHIELD_BREAK)));
            }
        }
    }

    @Inject(method = "damage", at = @At("TAIL"))
    private void modifyShieldBlockingEnd(ServerWorld world, DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (shield != null) self.getActiveItem().set(DataComponentTypes.BLOCKS_ATTACKS, shield);
        ServerWorld serverWorld = (ServerWorld) self.getEntityWorld();
        if (self instanceof PlayerEntity) {
            serverWorld.playSound(null, self.getBlockPos(), SoundEvents.ENTITY_PLAYER_HURT, SoundCategory.PLAYERS, 1.0F, 1.0F);
        }

    }
}

