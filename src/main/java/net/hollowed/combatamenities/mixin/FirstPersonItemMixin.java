package net.hollowed.combatamenities.mixin;

import net.hollowed.combatamenities.CombatAmenities;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ModelTransformationMode;
import net.minecraft.util.Arm;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HeldItemRenderer.class)
public class FirstPersonItemMixin {

    @Inject(method = "renderItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;Lnet/minecraft/item/ModelTransformationMode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
            at = @At("HEAD"), cancellable = true)
    private void hideOffhandItemWhenUsingRiptide(LivingEntity entity, ItemStack stack, ModelTransformationMode renderMode, boolean leftHanded, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        // Check if the player is in first person, using Riptide, and if this is the offhand
        if (entity instanceof ClientPlayerEntity && entity.isUsingRiptide()) {
            Arm offhandArm = entity.getMainArm() == Arm.RIGHT ? Arm.LEFT : Arm.RIGHT;
            if (leftHanded == (offhandArm == Arm.LEFT) && renderMode == ModelTransformationMode.FIRST_PERSON_LEFT_HAND && CombatAmenities.CONFIG.riptideFix) {
                ci.cancel(); // Prevent the offhand item from rendering
            }
        }

        // Add wobbling effect for bows when pulled back for too long
        if (entity instanceof PlayerEntity player && player.getActiveItem().getItem() instanceof BowItem && CombatAmenities.CONFIG.bowTweaks) {
            int useTime = player.getItemUseTime(); // Time bow has been drawn

            float tickDelta = MinecraftClient.getInstance().getRenderTickCounter().getTickDelta(true);

            // Only apply wobble if bow is drawn for more than 60 ticks
            if (useTime > 60) {
                float wobbleStrength = Math.min((useTime - 60) / 100.0F, 0.5F); // Gradual increase, capped at 0.5F
                float time = player.getWorld().getTime() + tickDelta; // Smooth time using tickDelta
                float wobbleAmount = (float) Math.sin(time) * 0.1F * wobbleStrength; // Oscillating wobble
                matrices.translate(0.0F, wobbleAmount, 0.0F); // Apply up and down movement
            }
        }

    }
}