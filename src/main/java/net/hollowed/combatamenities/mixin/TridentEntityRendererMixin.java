package net.hollowed.combatamenities.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.TridentEntityRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TridentEntityRenderer.class)
@Environment(EnvType.CLIENT)
public class TridentEntityRendererMixin {

    @Inject(
        method = "render(Lnet/minecraft/entity/projectile/TridentEntity;FFLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
        at = @At("HEAD"),
        cancellable = true
    )
    public void renderWithItem(
            TridentEntity tridentEntity, float f, float g, MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, CallbackInfo ci
    ) {
        // Push the matrix stack for transformations
        matrixStack.push();

        float multiplier = 0.65F;

        Vec3d look = tridentEntity.getRotationVec(0).multiply(multiplier, multiplier, -multiplier);

        matrixStack.translate(look.x, look.y, look.z);

        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(tridentEntity.getYaw() - 180.0F));
        matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(tridentEntity.getPitch() - 90F));

        matrixStack.scale(1.5F, 1.5F, 1.5F);

        ItemStack trident = Items.TRIDENT.getDefaultStack();
        if (tridentEntity.isEnchanted()) {
            trident.set(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true);
        }

        // Use the ItemRenderer to render the trident item with a FIRST_PERSON_RIGHT_HAND transformation
        ItemRenderer itemRenderer = MinecraftClient.getInstance().getItemRenderer();
        itemRenderer.renderItem(trident, ModelTransformationMode.FIRST_PERSON_RIGHT_HAND, i, OverlayTexture.DEFAULT_UV, matrixStack, vertexConsumerProvider, MinecraftClient.getInstance().world, 0);

        // Pop the matrix stack to clean up transformations
        matrixStack.pop();

        // Cancel the original render call
        ci.cancel();
    }
}
