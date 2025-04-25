package net.hollowed.combatamenities.mixin.tweaks.arrow;

import net.hollowed.combatamenities.CombatAmenities;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.StuckArrowsFeatureRenderer;
import net.minecraft.client.render.entity.feature.StuckObjectsFeatureRenderer;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.Items;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(StuckObjectsFeatureRenderer.class)
public abstract class StuckObjectsFeatureRendererMixin {

    @Inject(
            method = "renderObject",
            at = @At("HEAD"),
            cancellable = true
    )
    public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumers, int light, float f, float directionX, float directionY, CallbackInfo ci) {
        if ((StuckObjectsFeatureRenderer<?>) (Object) this instanceof StuckArrowsFeatureRenderer<?> && CombatAmenities.CONFIG.itemArrows) {
            // Push the matrix stack for transformations
            matrixStack.push();

            float g = MathHelper.sqrt(f * f + directionY * directionY);
            float h = (float)(Math.atan2(f, directionY) * 57.2957763671875);
            float i = (float)(Math.atan2(directionX, g) * 57.2957763671875);
            matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(h - 90.0F));
            matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(i));

            // Render the item using the ItemRenderer
            ItemRenderer itemRenderer = MinecraftClient.getInstance().getItemRenderer();
            itemRenderer.renderItem(
                    Items.ARROW.getDefaultStack(),
                    ItemDisplayContext.NONE,
                    light,
                    OverlayTexture.DEFAULT_UV,
                    matrixStack,
                    vertexConsumers,
                    MinecraftClient.getInstance().world,
                    0
            );

            // Pop the matrix stack
            matrixStack.pop();

            // Cancel the original rendering
            ci.cancel();
        }
    }
}
