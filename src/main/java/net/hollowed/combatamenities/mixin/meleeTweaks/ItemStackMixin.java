package net.hollowed.combatamenities.mixin.meleeTweaks;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {

    @Shadow public abstract Item getItem();

//    @Unique
//    private boolean ran = false;

    @Inject(method = "postDamageEntity", at = @At("HEAD"))
    public void postHitModify(LivingEntity target, LivingEntity user, CallbackInfo ci) {
//        if (CombatAmenities.CONFIG.meleeRework) {
//            if (!ran) {
//                if (this.getItem() instanceof MaceItem && !(user.getVelocity().y < -1)) {
//                    return;
//                }
//                user.fallDistance += 2;
//                ci.cancel();
//                ran = true;
//
//                // Get the server instance
//                MinecraftServer server = target.getServer();
//                if (server == null) {
//                    return;
//                }
//
//                // Capture frozen snapshots of both entities (for delayed execution)
//                FrozenEntitySnapshot frozenUser = new FrozenEntitySnapshot(user);
//                FrozenEntitySnapshot frozenTarget = new FrozenEntitySnapshot(target);
//
//                if (this.getItem() instanceof WeaponRework access) {
//                    int delay = access.combat_Amenities$delay();
//                    if (EnchantmentListener.hasEnchantment((ItemStack) (Object) this, "minecraft:wind_burst")) {
//                        delay -= 2;
//                    }
//                    TickDelayScheduler.schedule(delay, () -> {
//
//                        // Retrieve the correct ServerWorld
//                        ServerWorld world = server.getWorld(user.getWorld().getRegistryKey());
//                        if (world == null) return;
//
//                        if (user.isAlive()) {
//                            LivingEntity restoredUser = (LivingEntity) world.getEntity(frozenUser.uuid);
//                            LivingEntity restoredTarget = (LivingEntity) world.getEntity(frozenTarget.uuid);
//
//                            if (restoredUser != null && restoredTarget != null) {
//                                FrozenEntitySnapshot frozenUser1 = new FrozenEntitySnapshot(user);
//                                FrozenEntitySnapshot frozenTarget1 = new FrozenEntitySnapshot(target);
//
//                                // Restore frozen entity states (simulate past state for postHit)
//                                frozenUser.restoreEntityState(restoredUser);
//                                frozenTarget.restoreEntityState(restoredTarget);
//
//                                // Execute postHit with frozen entity states
//                                user.getMainHandStack().getItem().postHit(user.getMainHandStack(), restoredTarget, restoredUser);
//                                Objects.requireNonNull(user.getServer()).save(true, false, true);
//
//                                LivingEntity restoredUser1 = (LivingEntity) world.getEntity(frozenUser1.uuid);
//                                LivingEntity restoredTarget1 = (LivingEntity) world.getEntity(frozenTarget1.uuid);
//                                frozenUser1.restoreEntityState(restoredUser1);
//                                frozenTarget1.restoreEntityState(restoredTarget1);
//                                ci.cancel();
//                            }
//                        }
//                        world.getChunkManager().markForUpdate(user.getBlockPos());
//                    });
//                }
//            } else {
//                ran = false;
//                ci.cancel();
//            }
//        }
    }
}