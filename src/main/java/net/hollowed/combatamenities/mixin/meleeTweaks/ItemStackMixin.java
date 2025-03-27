package net.hollowed.combatamenities.mixin.meleeTweaks;

import net.hollowed.combatamenities.CombatAmenities;
import net.hollowed.combatamenities.util.EntityFreezer;
import net.hollowed.combatamenities.util.FrozenEntitySnapshot;
import net.hollowed.combatamenities.util.TickDelayScheduler;
import net.hollowed.combatamenities.util.WeaponRework;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.stream.Stream;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {

    @Shadow public abstract Item getItem();

    @Shadow public abstract Stream<TagKey<Item>> streamTags();

    @Unique
    private boolean ran = false;

    @Inject(method = "postDamageEntity", at = @At("HEAD"), cancellable = true)
    public void postHitModify(LivingEntity target, LivingEntity user, CallbackInfo ci) {
        if (CombatAmenities.CONFIG.meleeRework) {
            if (!ran) {
                ci.cancel();
                ran = true;

                // Get the server instance
                MinecraftServer server = target.getServer();
                if (server == null) {
                    return;
                }

                // Capture frozen snapshots of both entities (for delayed execution)
                FrozenEntitySnapshot frozenUser = new FrozenEntitySnapshot(user);
                FrozenEntitySnapshot frozenTarget = new FrozenEntitySnapshot(target);

                if (this.getItem() instanceof WeaponRework access) {
                    TickDelayScheduler.schedule(access.combat_Amenities$delay(), () -> {

                        // Retrieve the correct ServerWorld
                        ServerWorld world = server.getWorld(user.getWorld().getRegistryKey());
                        if (world == null) return;

                        if (user.isAlive()) {
                            LivingEntity restoredUser = (LivingEntity) world.getEntity(frozenUser.uuid);
                            LivingEntity restoredTarget = (LivingEntity) world.getEntity(frozenTarget.uuid);

                            if (restoredUser != null && restoredTarget != null) {
                                FrozenEntitySnapshot frozenUser1 = new FrozenEntitySnapshot(user);
                                FrozenEntitySnapshot frozenTarget1 = new FrozenEntitySnapshot(target);

                                // Restore frozen entity states (simulate past state for postHit)
                                frozenUser.restoreEntityState(restoredUser);
                                frozenTarget.restoreEntityState(restoredTarget);

                                // Execute postHit with frozen entity states
                                //user.getMainHandStack().postDamageEntity(restoredTarget, restoredUser);
                                user.getMainHandStack().postHit(restoredTarget, restoredUser);

                                LivingEntity restoredUser1 = (LivingEntity) world.getEntity(frozenUser1.uuid);
                                LivingEntity restoredTarget1 = (LivingEntity) world.getEntity(frozenTarget1.uuid);
                                frozenUser1.restoreEntityState(restoredUser1);
                                frozenTarget1.restoreEntityState(restoredTarget1);
                                ci.cancel();
                            }
                        }
                        user.setSprinting(false);
                        user.setSprinting(frozenUser.sprinting);
                        target.setSprinting(false);
                        target.setSprinting(frozenTarget.sprinting);
                    });
                }
            } else {
                ran = false;
            }
        }
    }
}