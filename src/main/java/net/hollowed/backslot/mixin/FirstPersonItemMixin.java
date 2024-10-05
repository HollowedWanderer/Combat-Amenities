package net.hollowed.backslot.mixin;

import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HeldItemRenderer.class)
public class FirstPersonItemMixin {

    @Inject(method = "renderItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/render/model/json/ModelTransformationMode;ZLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
            at = @At("HEAD"), cancellable = true)
    private void hideOffhandItemWhenUsingRiptide(LivingEntity entity, ItemStack stack, ModelTransformationMode renderMode, boolean leftHanded, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        // Check if the player is in first person, using Riptide, and if this is the offhand
        if (entity instanceof ClientPlayerEntity && entity.isUsingRiptide()) {
            Arm offhandArm = entity.getMainArm() == Arm.RIGHT ? Arm.LEFT : Arm.RIGHT;
            if (leftHanded == (offhandArm == Arm.LEFT) && renderMode == ModelTransformationMode.FIRST_PERSON_LEFT_HAND) {
                ci.cancel(); // Prevent the offhand item from rendering
            }
        }
    }
}