package net.hollowed.combatamenities.mixin.tweaks.bow;

import net.hollowed.combatamenities.CombatAmenities;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BowItem;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HeldItemRenderer.class)
public class FirstPersonItemMixin {

    @Inject(method = "renderItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ItemDisplayContext;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
            at = @At("HEAD"), cancellable = true)
    private void hideOffhandItemWhenUsingRiptide(LivingEntity entity, ItemStack stack, ItemDisplayContext renderMode, MatrixStack matrices, VertexConsumerProvider vertexConsumer, int light, CallbackInfo ci) {
        // Check if the player is in first person, using Riptide, and if this is the offhand
        if (entity.isUsingRiptide()) {
            Arm offhandArm = entity.getMainArm() == Arm.RIGHT ? Arm.LEFT : Arm.RIGHT;
            if (entity.getActiveHand() == Hand.MAIN_HAND) {
                if (((offhandArm == Arm.LEFT && renderMode == ItemDisplayContext.FIRST_PERSON_LEFT_HAND)
                        || (offhandArm == Arm.RIGHT && renderMode == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND))
                        && CombatAmenities.CONFIG.riptideFix) {
                    ci.cancel(); // Prevent the offhand item from rendering
                }
            } else {
                if (((offhandArm == Arm.RIGHT && renderMode == ItemDisplayContext.FIRST_PERSON_LEFT_HAND)
                        || (offhandArm == Arm.LEFT && renderMode == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND))
                        && CombatAmenities.CONFIG.riptideFix) {
                    ci.cancel(); // Prevent the offhand item from rendering
                }
            }
        }

        // Add wobbling effect for bows when pulled back for too long
        if (entity instanceof PlayerEntity player && player.getActiveItem().getItem() instanceof BowItem && CombatAmenities.CONFIG.bowTweaks) {
            int useTime = player.getItemUseTime(); // Time bow has been drawn

            float tickDelta = MinecraftClient.getInstance().getRenderTickCounter().getTickProgress(true);

            // Only apply wobble if bow is drawn for more than 120 ticks
            if (useTime > 120) {
                float wobbleStrength = Math.min((useTime - 120) / 25.0F, 0.25F); // Gradual increase, capped at 0.5F
                float wobbleAmount = (float) Math.sin(useTime + tickDelta) * 0.1F * wobbleStrength; // Oscillating wobble
                matrices.translate(0.0F, wobbleAmount, 0.0F); // Apply up and down movement
            }
        }
    }
}