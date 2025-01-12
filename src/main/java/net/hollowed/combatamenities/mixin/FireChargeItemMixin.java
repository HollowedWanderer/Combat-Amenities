package net.hollowed.combatamenities.mixin;

import net.hollowed.combatamenities.CombatAmenities;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.SmallFireballEntity;
import net.minecraft.item.BowItem;
import net.minecraft.item.FireChargeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(FireChargeItem.class)
public abstract class FireChargeItemMixin extends Item {

    public FireChargeItemMixin(Settings settings) {
        super(settings);
    }

    @Unique
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        if (!world.isClient) {

            if (CombatAmenities.CONFIG.throwableFirecharge) {
                // Get the ItemStack in hand
                ItemStack stack = player.getStackInHand(hand);

                SmallFireballEntity fireball = new SmallFireballEntity(player.getWorld(), player.getX(), player.getY() + 1.5, player.getZ(), player.getRotationVec(0));

                // Spawn the fireball entity
                world.spawnEntity(fireball);

                // Consume one fire charge
                stack.decrementUnlessCreative(1, player);
                world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ITEM_FIRECHARGE_USE, SoundCategory.NEUTRAL, 0.5F, 1F);
                player.getItemCooldownManager().set(stack.getItem(), 10);

                // Indicate successful use
                return TypedActionResult.success(stack);
            }
        }
        return TypedActionResult.pass(player.getStackInHand(hand));
    }
}
