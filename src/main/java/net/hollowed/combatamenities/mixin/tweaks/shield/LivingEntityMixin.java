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
    private void modifyShieldBlocking(ServerLevel level, DamageSource source, float damage, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity self = (LivingEntity) (Object) this;
        BlocksAttacks blocksAttacksComponent = self.getUseItem().get(DataComponents.BLOCKS_ATTACKS);
        boolean bypassShieldTweaks = source.getEntity() instanceof LivingEntity living
                && living.getMainHandItem().tags().toList().contains(TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(CombatAmenities.MOD_ID, "bypass_shield_tweaks")));

        if (CAConfig.shieldTweaks && !source.is(ModDamageTypes.CLEAVED) && !bypassShieldTweaks) {

            Vec3 sourcePosition = source.getSourcePosition();
            double angle;
            if (sourcePosition != null) {
                Vec3 viewVector = self.calculateViewVector(0.0F, self.getYHeadRot());
                Vec3 vectorTo = sourcePosition.subtract(self.position());
                vectorTo = (new Vec3(vectorTo.x, 0.0F, vectorTo.z)).normalize();
                angle = Math.acos(vectorTo.dot(viewVector));
            } else {
                angle = (float)Math.PI;
            }

            if (self.getTicksUsingItem() <= CAConfig.shieldParryTime && self.getTicksUsingItem() > 0 && blocksAttacksComponent != null) {
                if (angle > 0.0) {
                    if (self instanceof Player player) {
                        ServerLevel serverWorld = (ServerLevel) player.level();
                        serverWorld.playSound(null, player.blockPosition(), SoundEvents.SHIELD_BLOCK.value(), SoundSource.PLAYERS, 1.0F, 1.0F);
                        serverWorld.playSound(null, player.blockPosition(), SoundEvents.MACE_SMASH_AIR, SoundSource.PLAYERS, 0.3F, 1.5F);

                        AdvancementHolder advancement = serverWorld.getServer().getAdvancements().get(Identifier.withDefaultNamespace("story/deflect_arrow"));
                        if (advancement != null && player instanceof ServerPlayer serverPlayer && source.is(DamageTypeTags.IS_PROJECTILE)) {
                            serverPlayer.getAdvancements().award(advancement, "deflected_projectile");
                        }

                        float knockbackStrength = 1F;

                        ItemCooldowns cooldownManager = player.getCooldowns();
                        if (source.getDirectEntity() instanceof LivingEntity attacker) {
                            attacker.push(player.getLookAngle().normalize().multiply(knockbackStrength, 0.25, knockbackStrength).add(0, 0.5, 0));
                            attacker.hurtMarked = true;
                            attacker.needsSync = true;
                            if (attacker.getSecondsToDisableBlocking() > 0) {
                                cooldownManager.addCooldown(self.getUseItem(), 40);
                                self.releaseUsingItem();
                            } else {
                                self.releaseUsingItem();
                            }
                        } else if (source.getDirectEntity() instanceof Projectile entity) {
                            entity.setDeltaMovement(entity.getDeltaMovement().scale(10F));
                            entity.hurtMarked = true;
                            entity.needsSync = true;
                        }
                    }

                    cir.setReturnValue(false);
                    return;
                }
            }

            if (
                    source.is(DamageTypeTags.IS_PROJECTILE)
                            && self.isUsingItem()
                            && blocksAttacksComponent != null
                            && !(angle > (double)(((float)Math.PI / 180F) * blocksAttacksComponent.damageReductions().getFirst().horizontalBlockingAngle()))
            ) {
                    ServerLevel serverWorld = (ServerLevel) self.level();
                    serverWorld.playSound(null, self.blockPosition(), SoundEvents.SHIELD_BLOCK.value(), SoundSource.PLAYERS, 1.0F, 1.0F);
                    cir.setReturnValue(false);
            }

            if (blocksAttacksComponent != null && self.getUseItem().getItem() instanceof ShieldItem) {
                shield = self.getUseItem().get(DataComponents.BLOCKS_ATTACKS);
                self.getUseItem().set(DataComponents.BLOCKS_ATTACKS, new BlocksAttacks(
                        0.25F,
                        1.0F,
                        List.of(new BlocksAttacks.DamageReduction(90.0F, Optional.empty(), 0.0F, 0.5F)),
                        new BlocksAttacks.ItemDamageFunction(3.0F, 1.0F, 1.0F),
                        shield.bypassedBy(),
                        Optional.of(SoundEvents.SHIELD_BLOCK),
                        Optional.of(SoundEvents.SHIELD_BREAK)
                ));
            }
        }
    }

    @Inject(method = "hurtServer", at = @At("TAIL"))
    private void modifyShieldBlockingEnd(ServerLevel level, DamageSource source, float damage, CallbackInfoReturnable<Boolean> cir) {
        LivingEntity self = (LivingEntity) (Object) this;
        if (shield != null) self.getUseItem().set(DataComponents.BLOCKS_ATTACKS, shield);
        ServerLevel serverWorld = (ServerLevel) self.level();
        if (self instanceof Player) {
            serverWorld.playSound(null, self.blockPosition(), SoundEvents.PLAYER_HURT, SoundSource.PLAYERS, 1.0F, 1.0F);
        }

    }
}

