package net.hollowed.combatamenities.mixin;

import net.hollowed.combatamenities.CombatAmenities;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.StuckArrowsFeatureRenderer;
import net.minecraft.client.render.entity.feature.StuckObjectsFeatureRenderer;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Items;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(StuckObjectsFeatureRenderer.class)
public abstract class StuckObjectsFeatureRendererMixin<T extends LivingEntity, M extends PlayerEntityModel<T>> extends FeatureRenderer<T, M> {

    @Shadow protected abstract int getObjectCount(T entity);

    @Shadow protected abstract void renderObject(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, Entity entity, float directionX, float directionY, float directionZ, float tickDelta);

    public StuckObjectsFeatureRendererMixin(FeatureRendererContext<T, M> context) {
        super(context);
    }

    @Inject(method = "render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/entity/LivingEntity;FFFFFF)V", at = @At("HEAD"))
    public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int light, T livingEntity, float f, float g, float h, float j, float k, float l, CallbackInfo ci) {
        if ((StuckObjectsFeatureRenderer<?, ?>) (Object) this instanceof StuckArrowsFeatureRenderer<?,?> && CombatAmenities.CONFIG.itemArrows) {
            int m = this.getObjectCount(livingEntity);
            Random random = Random.create(livingEntity.getId());
            if (m > 0) {
                for (int n = 0; n < m; ++n) {
                    matrixStack.push();
                    ModelPart modelPart = (this.getContextModel()).getRandomPart(random);
                    ModelPart.Cuboid cuboid = modelPart.getRandomCuboid(random);
                    modelPart.rotate(matrixStack);
                    float o = random.nextFloat();
                    float p = random.nextFloat();
                    float q = random.nextFloat();
                    float r = MathHelper.lerp(o, cuboid.minX, cuboid.maxX) / 16.0F;
                    float s = MathHelper.lerp(p, cuboid.minY, cuboid.maxY) / 16.0F;
                    float t = MathHelper.lerp(q, cuboid.minZ, cuboid.maxZ) / 16.0F;
                    matrixStack.translate(r, s, t);
                    o = -1.0F * (o * 2.0F - 1.0F);
                    p = -1.0F * (p * 2.0F - 1.0F);
                    q = -1.0F * (q * 2.0F - 1.0F);

                    matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(o));
                    matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(p));
                    matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(q));

                    // Render the item using the ItemRenderer
                    ItemRenderer itemRenderer = MinecraftClient.getInstance().getItemRenderer();
                    itemRenderer.renderItem(
                            Items.ARROW.getDefaultStack(),
                            ModelTransformationMode.NONE,
                            light,
                            OverlayTexture.DEFAULT_UV,
                            matrixStack,
                            vertexConsumerProvider,
                            MinecraftClient.getInstance().world,
                            0
                    );

                    matrixStack.pop();
                }

            }
        }
    }
}
