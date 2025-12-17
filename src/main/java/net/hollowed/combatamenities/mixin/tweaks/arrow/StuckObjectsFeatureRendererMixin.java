package net.hollowed.combatamenities.mixin.tweaks.arrow;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.hollowed.combatamenities.config.CAConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.layers.ArrowLayer;
import net.minecraft.client.renderer.entity.layers.StuckInBodyLayer;
import net.minecraft.client.renderer.entity.state.ArrowRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.Items;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(StuckInBodyLayer.class)
public abstract class StuckObjectsFeatureRendererMixin {

    @Shadow @Final private Object modelState;

    @Inject(
            method = "submitStuckItem",
            at = @At("HEAD"),
            cancellable = true
    )
    public void render(PoseStack matrixStack, SubmitNodeCollector queue, int light, float f, float directionX, float directionY, int color, CallbackInfo ci) {
        if ((StuckInBodyLayer<?, ?>) (Object) this instanceof ArrowLayer<?> && CAConfig.itemArrows && this.modelState instanceof ArrowRenderState state) {
            // Push the matrix stack for transformations
            matrixStack.pushPose();

            float g = Mth.sqrt(f * f + directionY * directionY);
            float h = (float)(Math.atan2(f, directionY) * 57.2957763671875);
            float i = (float)(Math.atan2(directionX, g) * 57.2957763671875);
            matrixStack.mulPose(Axis.YP.rotationDegrees(h - 90.0F));
            matrixStack.mulPose(Axis.ZP.rotationDegrees(i));

            ItemStackRenderState stackRenderState = new ItemStackRenderState();
            Minecraft.getInstance().getItemModelResolver().appendItemLayers(stackRenderState, Items.ARROW.getDefaultInstance(), ItemDisplayContext.NONE, Minecraft.getInstance().level, null, 1);
            stackRenderState.submit(matrixStack, queue, light, OverlayTexture.NO_OVERLAY, state.outlineColor);

            // Pop the matrix stack
            matrixStack.popPose();

            // Cancel the original rendering
            ci.cancel();
        }
    }
}
