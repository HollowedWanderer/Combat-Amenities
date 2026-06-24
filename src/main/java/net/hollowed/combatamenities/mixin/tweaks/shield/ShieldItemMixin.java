package net.hollowed.combatamenities.mixin.tweaks.shield;

import net.hollowed.combatamenities.config.CAConfig;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.NonNull;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ShieldItem.class)
public class ShieldItemMixin extends Item {

    public ShieldItemMixin(Properties properties) {
        super(properties);
    }

    @Override
    public boolean releaseUsing(@NonNull ItemStack itemStack, @NonNull Level level, @NonNull LivingEntity entity, int remainingTime) {
        if (CAConfig.shieldTweaks && entity instanceof ServerPlayer serverPlayer && !serverPlayer.getCooldowns().isOnCooldown(itemStack)) {
            serverPlayer.getCooldowns().addCooldown(itemStack, 10);
        }
        return true;
    }
}
