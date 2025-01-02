package net.hollowed.combatamenities.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.hollowed.combatamenities.client.TridentEntityRenderStateAccess;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.TridentEntityRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.ModelTransformationMode;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.client.render.entity.state.TridentEntityRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TridentEntityRenderer.class)
@Environment(EnvType.CLIENT)
public class TridentEntityRendererMixin {

    @Inject(method = "updateRenderState(Lnet/minecraft/entity/projectile/TridentEntity;Lnet/minecraft/client/render/entity/state/TridentEntityRenderState;F)V", at = @At("HEAD"))
    public void updateRenderState(TridentEntity tridentEntity, TridentEntityRenderState tridentEntityRenderState, float f, CallbackInfo ci) {
        if (tridentEntityRenderState instanceof TridentEntityRenderStateAccess access) {
            access.combat_Amenities$setLook(tridentEntity.getRotationVec(0));
        }
    }

    @Inject(
        method = "render(Lnet/minecraft/client/render/entity/state/TridentEntityRenderState;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
        at = @At("HEAD"),
        cancellable = true
    )
    public void renderWithItem(
            TridentEntityRenderState tridentEntityRenderState,
            MatrixStack matrixStack,
            VertexConsumerProvider vertexConsumerProvider,
            int light,
            CallbackInfo ci
    ) {
        if (tridentEntityRenderState instanceof TridentEntityRenderStateAccess access) {

            // Push the matrix stack for transformations
            matrixStack.push();

            float multiplier = 0.65F;

            matrixStack.translate(access.combat_Amenities$getLook().multiply(multiplier, multiplier, -multiplier));

            matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(tridentEntityRenderState.yaw - 180.0F));
            matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(tridentEntityRenderState.pitch - 90.0F));

            matrixStack.scale(1.5F, 1.5F, 1.5F);

            ItemStack trident = Items.TRIDENT.getDefaultStack();
            if (tridentEntityRenderState.enchanted) {
                trident.set(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true);
            }

            // Use the ItemRenderer to render the trident item with a FIRST_PERSON_RIGHT_HAND transformation
            ItemRenderer itemRenderer = MinecraftClient.getInstance().getItemRenderer();
            itemRenderer.renderItem(trident, ModelTransformationMode.FIRST_PERSON_RIGHT_HAND, light, OverlayTexture.DEFAULT_UV, matrixStack, vertexConsumerProvider, MinecraftClient.getInstance().world, 0);

            // Pop the matrix stack to clean up transformations
            matrixStack.pop();

            // Cancel the original render call
            ci.cancel();
        }
    }
}
