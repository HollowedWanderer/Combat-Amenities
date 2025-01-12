package net.hollowed.combatamenities.mixin;

import net.hollowed.combatamenities.util.TransformData;
import net.hollowed.combatamenities.util.TransformResourceReloadListener;
import net.minecraft.block.BannerBlock;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.CapeFeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.util.SkinTextures;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerModelPart;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import org.joml.Vector3f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CapeFeatureRenderer.class)
public abstract class PlayerCapeModelMixin extends FeatureRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> {

    public PlayerCapeModelMixin(FeatureRendererContext<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> context) {
        super(context);
    }

    @Inject(method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/client/network/AbstractClientPlayerEntity;FFFFFF)V", at = @At("HEAD"), cancellable = true)
    private void injectSetAngles(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, AbstractClientPlayerEntity abstractClientPlayerEntity, float f, float g, float h, float j, float k, float l, CallbackInfo ci) {
        if (!abstractClientPlayerEntity.isInvisible() && abstractClientPlayerEntity.isPartVisible(PlayerModelPart.CAPE)) {
            ItemStack stack = abstractClientPlayerEntity.getInventory().getStack(41);
            TransformData data = TransformResourceReloadListener.getTransform(Registries.ITEM.getId(stack.getItem()));

            float bannerMultiplier = 0.4F;
            if (stack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof BannerBlock) {
                bannerMultiplier = 1.0F;
            }

            float backslotMultiplier = 1.0F;
            if (!stack.isEmpty() && data != null) {
                backslotMultiplier = data.sway() * bannerMultiplier * 0.9F;
            }

            SkinTextures skinTextures = abstractClientPlayerEntity.getSkinTextures();
            if (skinTextures.capeTexture() != null) {
                ItemStack itemStack = abstractClientPlayerEntity.getEquippedStack(EquipmentSlot.CHEST);
                if (!itemStack.isOf(Items.ELYTRA)) {
                    matrixStack.push();
                    matrixStack.translate(0.0F, 0.0F, 0.125F);
                    double d = MathHelper.lerp(h, abstractClientPlayerEntity.prevCapeX, abstractClientPlayerEntity.capeX) - MathHelper.lerp(h, abstractClientPlayerEntity.prevX, abstractClientPlayerEntity.getX());
                    double e = MathHelper.lerp(h, abstractClientPlayerEntity.prevCapeY, abstractClientPlayerEntity.capeY) - MathHelper.lerp(h, abstractClientPlayerEntity.prevY, abstractClientPlayerEntity.getY());
                    double m = MathHelper.lerp(h, abstractClientPlayerEntity.prevCapeZ, abstractClientPlayerEntity.capeZ) - MathHelper.lerp(h, abstractClientPlayerEntity.prevZ, abstractClientPlayerEntity.getZ());
                    float n = MathHelper.lerpAngleDegrees(h, abstractClientPlayerEntity.prevBodyYaw, abstractClientPlayerEntity.bodyYaw);
                    double o = MathHelper.sin(n * 0.017453292F);
                    double p = -MathHelper.cos(n * 0.017453292F);
                    float q = (float)e * 10.0F;
                    q = MathHelper.clamp(q, -6.0F, 32.0F);
                    float r = (float)(d * o + m * p) * 100.0F;
                    r = MathHelper.clamp(r, 0.0F, 150.0F);
                    float s = (float)(d * p - m * o) * 100.0F;
                    s = MathHelper.clamp(s, -20.0F, 20.0F);
                    if (r < 0.0F) {
                        r = 0.0F;
                    }

                    float t = MathHelper.lerp(h, abstractClientPlayerEntity.prevStrideDistance, abstractClientPlayerEntity.strideDistance);
                    q += MathHelper.sin(MathHelper.lerp(h, abstractClientPlayerEntity.prevHorizontalSpeed, abstractClientPlayerEntity.horizontalSpeed) * 6.0F) * 32.0F * t;
                    if (abstractClientPlayerEntity.isInSneakingPose()) {
                        q += 25.0F;
                    }

                    matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees((6.0F + r / 2.0F + q) * backslotMultiplier));
                    matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(s / 2.0F));
                    matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0F - s / 2.0F));
                    VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(RenderLayer.getEntitySolid(skinTextures.capeTexture()));
                    this.getContextModel().renderCape(matrixStack, vertexConsumer, i, OverlayTexture.DEFAULT_UV);
                    matrixStack.pop();
                    ci.cancel();
                }
            }
        }
    }
}
