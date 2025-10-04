package net.hollowed.combatamenities.mixin.tweaks.arrow;

import net.hollowed.combatamenities.config.CAConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.feature.StuckArrowsFeatureRenderer;
import net.minecraft.client.render.entity.feature.StuckObjectsFeatureRenderer;
import net.minecraft.client.render.entity.state.ProjectileEntityRenderState;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.Items;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(StuckObjectsFeatureRenderer.class)
public abstract class StuckObjectsFeatureRendererMixin {

    @Shadow @Final private Object stuckObjectState;

    @Inject(
            method = "renderObject",
            at = @At("HEAD"),
            cancellable = true
    )
    public void render(MatrixStack matrixStack, OrderedRenderCommandQueue queue, int light, float f, float directionX, float directionY, int color, CallbackInfo ci) {
        if ((StuckObjectsFeatureRenderer<?, ?>) (Object) this instanceof StuckArrowsFeatureRenderer<?> && CAConfig.itemArrows && this.stuckObjectState instanceof ProjectileEntityRenderState state) {
            // Push the matrix stack for transformations
            matrixStack.push();

            float g = MathHelper.sqrt(f * f + directionY * directionY);
            float h = (float)(Math.atan2(f, directionY) * 57.2957763671875);
            float i = (float)(Math.atan2(directionX, g) * 57.2957763671875);
            matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(h - 90.0F));
            matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(i));

            ItemRenderState stackRenderState = new ItemRenderState();
            MinecraftClient.getInstance().getItemModelManager().update(stackRenderState, Items.ARROW.getDefaultStack(), ItemDisplayContext.NONE, MinecraftClient.getInstance().world, null, 1);
            stackRenderState.render(matrixStack, queue, light, OverlayTexture.DEFAULT_UV, state.outlineColor);

            // Pop the matrix stack
            matrixStack.pop();

            // Cancel the original rendering
            ci.cancel();
        }
    }
}
