package net.hollowed.backslot.renderer;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.*;
import net.minecraft.util.math.RotationAxis;

@Environment(EnvType.CLIENT)
public class BackSlotFeatureRenderer extends HeldItemFeatureRenderer<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> {

	private final HeldItemRenderer heldItemRenderer;

	public BackSlotFeatureRenderer(FeatureRendererContext<AbstractClientPlayerEntity, PlayerEntityModel<AbstractClientPlayerEntity>> context, HeldItemRenderer heldItemRenderer) {
		super(context, heldItemRenderer);
		this.heldItemRenderer = heldItemRenderer;
	}

	@Override
	public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int light, AbstractClientPlayerEntity playerEntity, float limbAngle, float limbDistance, float tickDelta, float age, float headYaw, float headPitch) {
        ItemStack backSlotStack = playerEntity.getInventory().getStack(41);
		if (!backSlotStack.isEmpty()) {
			matrixStack.push();
			ModelPart bodyPart = this.getContextModel().body;
			bodyPart.rotate(matrixStack);
			Item item = backSlotStack.getItem();

			if (item instanceof TridentItem) {
				// Trident transforms separate because severely different
				matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(52.0F));
				matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(40.0F));
				matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(-25.0F));
				matrixStack.translate(-0.26D, 0.0D, 0.0D);
				if (!playerEntity.hasStackEquipped(EquipmentSlot.CHEST)) {
					// Trident armor transforms
					matrixStack.translate(0.05F, 0.0F, 0.0F);
				}
				matrixStack.scale(1.0F, -1.0F, -1.0F);
				heldItemRenderer.renderItem(playerEntity, backSlotStack, ModelTransformationMode.THIRD_PERSON_RIGHT_HAND, false, matrixStack, vertexConsumerProvider, light);
			} else {
				// Default transforms
				matrixStack.translate(0D, 0.3D, 0.15D);
				matrixStack.scale(1F, 1F, 1F);
				matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0F));

				if (item instanceof FishingRodItem || item instanceof OnAStickItem) {
					// Fishing rod transforms
					matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(180.0F));
				}
				if (playerEntity.hasStackEquipped(EquipmentSlot.CHEST)) {
					// Armor transforms
					matrixStack.translate(0.0F, 0.0F, -0.05F);
				}

				if ("item.amarite.amarite_longsword".equals(item.getTranslationKey())) {
					matrixStack.scale(2F, 2F, 1F);
				}

				heldItemRenderer.renderItem(playerEntity, backSlotStack, ModelTransformationMode.FIXED, false, matrixStack, vertexConsumerProvider, light);
			}
			matrixStack.pop();
		}
	}
}
