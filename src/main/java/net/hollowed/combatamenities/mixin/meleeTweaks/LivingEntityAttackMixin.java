package net.hollowed.combatamenities.mixin.meleeTweaks;

import net.hollowed.combatamenities.CombatAmenities;
import net.hollowed.combatamenities.util.EntityFreezer;
import net.hollowed.combatamenities.util.TickDelayScheduler;
import net.hollowed.combatamenities.util.WeaponRework;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ProjectileDeflection;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.boss.dragon.EnderDragonPart;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MaceItem;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.EntityTypeTags;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(PlayerEntity.class)
public abstract class LivingEntityAttackMixin extends LivingEntity {

    protected LivingEntityAttackMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Shadow protected abstract float getDamageAgainst(Entity target, float baseDamage, DamageSource damageSource);

    @Shadow public abstract float getAttackCooldownProgress(float baseTime);

    @Shadow public abstract void spawnSweepAttackParticles();

    @Shadow public abstract void addCritParticles(Entity target);

    @Shadow public abstract void addEnchantedHitParticles(Entity target);

    @Shadow public abstract void increaseStat(Identifier stat, int amount);

    @Shadow public abstract void addExhaustion(float exhaustion);

    @Inject(method = "attack", at = @At("HEAD"), cancellable = true)
    private void attackChanges(Entity target, CallbackInfo ci) {
        PlayerEntity player = (PlayerEntity) (Object) this;
        float attackPower = player.getAttackCooldownProgress(0.0f);

        final float[] f = {this.isUsingRiptide() ? this.riptideAttackDamage : (float) this.getAttributeValue(EntityAttributes.ATTACK_DAMAGE)};
        ItemStack itemStack = this.getWeaponStack();
        DamageSource damageSource = Optional.ofNullable(itemStack.getItem().getDamageSource(this)).orElse(this.getDamageSources().playerAttack(player));
        float g = this.getDamageAgainst(target, f[0], damageSource) - f[0];
        float h = this.getAttackCooldownProgress(0.5F);
        f[0] *= 0.2F + h * h * 0.8F;
        g *= h;
        Vec3d vec3d = target.getVelocity();

        boolean bl = h > 0.9F;
        boolean bl3 = bl
                && this.fallDistance > 0.0F
                && !this.isOnGround()
                && !this.isClimbing()
                && !this.isTouchingWater()
                && !this.hasStatusEffect(StatusEffects.BLINDNESS)
                && !this.hasVehicle()
                && target instanceof LivingEntity;

        float finalG = g;
        boolean onGround = this.isOnGround();

        if (attackPower > 0.8 && target instanceof LivingEntity && itemStack.getItem() instanceof WeaponRework access && CombatAmenities.CONFIG.meleeRework) {

            itemStack.postDamageEntity((LivingEntity) target, player);

            int delay = access.combat_Amenities$delay();
            player.getWorld().playSound(
                    null,
                    player.getBlockPos(),
                    (SoundEvent) access.combat_Amenities$sound().get(0),
                    (SoundCategory) access.combat_Amenities$sound().get(1),
                    (float) access.combat_Amenities$sound().get(2),
                    (float) access.combat_Amenities$sound().get(3)
            );

            if (itemStack.streamTags().toList().contains(TagKey.of(RegistryKeys.ITEM, Identifier.of(CombatAmenities.MOD_ID, "weapon_freeze"))) || itemStack.getItem() instanceof MaceItem && this.getVelocity().y < -0.5) {
                if (target instanceof net.hollowed.combatamenities.util.EntityFreezer freezer) {
                    freezer.antiquities$setFrozen(true);
                }
                if (this instanceof EntityFreezer freezer) {
                    freezer.antiquities$setFrozen(true);
                }
            }

            TickDelayScheduler.schedule(delay, () -> {

                if (target instanceof EntityFreezer freezer) {
                    freezer.antiquities$setFrozen(false);
                }
                if (player instanceof EntityFreezer freezer) {
                    freezer.antiquities$setFrozen(false);
                }

                /*
                    Manual attack logic call
                */

                if (target.isAttackable()) {
                    if (!target.handleAttack(this)) {
                        if (target.getType().isIn(EntityTypeTags.REDIRECTABLE_PROJECTILE)
                                && target instanceof ProjectileEntity projectileEntity
                                && projectileEntity.deflect(ProjectileDeflection.REDIRECTED, this, this, true)) {
                            this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_PLAYER_ATTACK_NODAMAGE, this.getSoundCategory());
                            return;
                        }

                        if (f[0] > 0.0F || finalG > 0.0F) {
                            boolean bl2;
                            if (this.isSprinting() && bl) {
                                this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_PLAYER_ATTACK_KNOCKBACK, this.getSoundCategory(), 1.0F, 1.0F);
                                bl2 = true;
                            } else {
                                bl2 = false;
                            }

                            f[0] += itemStack.getItem().getBonusAttackDamage(target, f[0], damageSource);
                            if (bl3) {
                                f[0] *= 1.5F;
                            }

                            float i = f[0] + finalG;
                            boolean bl4 = false;
                            if (bl && !bl3 && !bl2 && onGround) {
                                double d = this.getMovement().horizontalLengthSquared();
                                double e = (double) this.getMovementSpeed() * 2.5;
                                if (d < MathHelper.square(e) && this.getStackInHand(Hand.MAIN_HAND).isIn(ItemTags.SWORDS)) {
                                    bl4 = true;
                                }
                            }

                            float j = 0.0F;
                            if (target instanceof LivingEntity livingEntity) {
                                j = livingEntity.getHealth();
                            }

                            boolean bl5 = target.sidedDamage(damageSource, i);
                            if (bl5) {
                                float k = this.getKnockbackAgainst(target, damageSource) + (bl2 ? 1.0F : 0.0F);
                                if (k > 0.0F) {
                                    if (target instanceof LivingEntity livingEntity2) {
                                        livingEntity2.takeKnockback(
                                                k * 0.5F,
                                                MathHelper.sin(this.getYaw() * (float) (Math.PI / 180.0)),
                                                -MathHelper.cos(this.getYaw() * (float) (Math.PI / 180.0))
                                        );
                                    } else {
                                        target.addVelocity(
                                                -MathHelper.sin(this.getYaw() * (float) (Math.PI / 180.0)) * k * 0.5F,
                                                0.1,
                                                MathHelper.cos(this.getYaw() * (float) (Math.PI / 180.0)) * k * 0.5F
                                        );
                                    }

                                    this.setVelocity(this.getVelocity().multiply(0.6, 1.0, 0.6));
                                    this.setSprinting(false);
                                }

                                if (bl4) {
                                    float l = 1.0F + (float) this.getAttributeValue(EntityAttributes.SWEEPING_DAMAGE_RATIO) * f[0];

                                    for (LivingEntity livingEntity3 : this.getWorld().getNonSpectatingEntities(LivingEntity.class, target.getBoundingBox().expand(1.0, 0.25, 1.0))) {
                                        if (livingEntity3 != this
                                                && livingEntity3 != target
                                                && !this.isTeammate(livingEntity3)
                                                && (!(livingEntity3 instanceof ArmorStandEntity) || !((ArmorStandEntity) livingEntity3).isMarker())
                                                && this.squaredDistanceTo(livingEntity3) < 9.0) {
                                            float m = this.getDamageAgainst(livingEntity3, l, damageSource) * h;
                                            livingEntity3.takeKnockback(
                                                    0.4F, MathHelper.sin(this.getYaw() * (float) (Math.PI / 180.0)), -MathHelper.cos(this.getYaw() * (float) (Math.PI / 180.0))
                                            );
                                            livingEntity3.serverDamage(damageSource, m);
                                            if (this.getWorld() instanceof ServerWorld serverWorld1) {
                                                EnchantmentHelper.onTargetDamaged(serverWorld1, livingEntity3, damageSource);
                                            }
                                        }
                                    }

                                    this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, this.getSoundCategory(), 1.0F, 1.0F);
                                    this.spawnSweepAttackParticles();
                                }

                                if (target instanceof ServerPlayerEntity && target.velocityModified) {
                                    ((ServerPlayerEntity) target).networkHandler.sendPacket(new EntityVelocityUpdateS2CPacket(target));
                                    target.velocityModified = false;
                                    target.setVelocity(vec3d);
                                }

                                if (bl3) {
                                    this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_PLAYER_ATTACK_CRIT, this.getSoundCategory(), 1.0F, 1.0F);
                                    this.addCritParticles(target);
                                }

                                if (!bl3 && !bl4) {
                                    if (bl) {
                                        this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_PLAYER_ATTACK_STRONG, this.getSoundCategory(), 1.0F, 1.0F);
                                    } else {
                                        this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_PLAYER_ATTACK_WEAK, this.getSoundCategory(), 1.0F, 1.0F);
                                    }
                                }

                                if (finalG > 0.0F) {
                                    this.addEnchantedHitParticles(target);
                                }

                                this.onAttacking(target);
                                Entity entity = target;
                                if (target instanceof EnderDragonPart) {
                                    entity = ((EnderDragonPart) target).owner;
                                }

                                if (this.getWorld() instanceof ServerWorld serverWorld2) {
                                    EnchantmentHelper.onTargetDamaged(serverWorld2, target, damageSource);
                                }

                                if (!this.getWorld().isClient && entity instanceof LivingEntity) {
                                    if (itemStack.isEmpty()) {
                                        if (itemStack == this.getMainHandStack()) {
                                            this.setStackInHand(Hand.MAIN_HAND, ItemStack.EMPTY);
                                        } else {
                                            this.setStackInHand(Hand.OFF_HAND, ItemStack.EMPTY);
                                        }
                                    }
                                }

                                if (target instanceof LivingEntity) {
                                    float n = j - ((LivingEntity) target).getHealth();
                                    this.increaseStat(Stats.DAMAGE_DEALT, Math.round(n * 10.0F));
                                    if (this.getWorld() instanceof ServerWorld && n > 2.0F) {
                                        int o = (int) ((double) n * 0.5);
                                        ((ServerWorld) this.getWorld())
                                                .spawnParticles(ParticleTypes.DAMAGE_INDICATOR, target.getX(), target.getBodyY(0.5), target.getZ(), o, 0.1, 0.0, 0.1, 0.2);
                                    }
                                }

                                this.addExhaustion(0.1F);
                            } else {
                                this.getWorld().playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_PLAYER_ATTACK_NODAMAGE, this.getSoundCategory(), 1.0F, 1.0F);
                            }
                        }
                    }
                }
            });
            ci.cancel();
        }
    }
}
