package net.hollowed.combatamenities.renderer;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.hollowed.combatamenities.CombatAmenities;
import net.hollowed.combatamenities.config.CAConfig;
import net.hollowed.combatamenities.util.interfaces.PlayerEntityRenderStateAccess;
import net.hollowed.combatamenities.util.items.CAComponents;
import net.hollowed.combatamenities.util.json.BeltTransformData;
import net.hollowed.combatamenities.util.json.BeltTransformResourceReloadListener;
import net.minecraft.block.BannerBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;
import net.minecraft.util.Arm;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import org.joml.Quaternionf;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

@Environment(EnvType.CLIENT)
public class BeltSlotFeatureRenderer extends HeldItemFeatureRenderer<PlayerEntityRenderState, PlayerEntityModel> {

	private final HeldItemRenderer heldItemRenderer;
	private ItemDisplayContext transformationMode = ItemDisplayContext.FIXED;

    public BeltSlotFeatureRenderer(FeatureRendererContext<PlayerEntityRenderState, PlayerEntityModel> context, HeldItemRenderer heldItemRenderer) {
		super(context);
		this.heldItemRenderer = heldItemRenderer;
	}

	@Override
	public void render(MatrixStack matrixStack, OrderedRenderCommandQueue orderedRenderCommandQueue, int i, PlayerEntityRenderState armedEntityRenderState, float f, float g) {
		if (armedEntityRenderState instanceof PlayerEntityRenderStateAccess access) {
			PlayerEntity playerEntity = access.combat_Amenities$getPlayerEntity();
			this.setVelocityFromPos(playerEntity);

			if (playerEntity != null) {
				ItemStack backSlotStack = playerEntity.getInventory().getStack(42);

				Arm arm = armedEntityRenderState.mainArm;
				boolean right = arm == Arm.RIGHT && !CAConfig.flipBeltslotDisplay || arm == Arm.LEFT && CAConfig.flipBeltslotDisplay;

				Item item = backSlotStack.getItem();
				Identifier itemId = Registries.ITEM.getId(item);
				BeltTransformData transformData = BeltTransformResourceReloadListener.getTransform(itemId, backSlotStack.getOrDefault(CAComponents.INTEGER_PROPERTY, -1).toString());

				BeltTransformData.SecondaryTransformData secondaryTransformData = transformData.secondaryTransforms();
				BeltTransformData.TertiaryTransformData tertiaryTransformData = transformData.tertiaryTransforms();

				Identifier secondaryModel = secondaryTransformData.item();
				ItemStack secondaryAppleStack = Items.APPLE.getDefaultStack();
				secondaryAppleStack.set(DataComponentTypes.ITEM_MODEL, secondaryModel);

				Identifier tertiaryModel = tertiaryTransformData.item();
				ItemStack tertiaryAppleStack = Items.APPLE.getDefaultStack();
				tertiaryAppleStack.set(DataComponentTypes.ITEM_MODEL, tertiaryModel);

				if (!backSlotStack.isEmpty()) {
					if (!secondaryModel.equals(Identifier.of("null"))) {
						matrixStack.push();
						this.getContextModel().body.applyTransform(matrixStack);
						float pivot = 0.9F;
						float pivotSide = right ? 0.275F : -0.275F;
						float pivotFront = 0.1F;

						if (playerEntity.getEquippedStack(EquipmentSlot.LEGS) != ItemStack.EMPTY) {
							pivotSide = right ? 0.3F : -0.3F;
						}

						// pivot point
						matrixStack.translate(pivotSide, pivot, pivotFront);

						matrixStack.multiply((new Quaternionf())
								.rotateY(-3.1415927F)
								.rotateX(transformData.sway() * -(6.0F + armedEntityRenderState.field_53537 / 2.0F + armedEntityRenderState.field_53536) * 0.017453292F)
								.rotateZ(-(armedEntityRenderState.field_53538 / 2.0F * 0.017453292F))
								.rotateY((180.0F - armedEntityRenderState.field_53538 / 2.0F) * 0.017453292F)
						);

						matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(90));
						if (right) {
							if (item instanceof BlockItem || transformData.flip()) {
								matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180));
							}
						}

						transformationMode = transformData.mode();

						if (playerEntity instanceof OtherClientPlayerEntity) {
							applyDynamicMovement(matrixStack, playerEntity, item);
						} else if (playerEntity instanceof ClientPlayerEntity) {
							applyDynamicMovement(matrixStack, playerEntity, item);
						}

						List<Float> translation = transformData.translation();
						matrixStack.translate(right && (item instanceof BlockItem || transformData.flip()) ? -translation.get(0) : translation.get(0), translation.get(1), translation.get(2));

						List<Float> rotation = transformData.rotation();
						matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(rotation.get(0)));
						if (right && !(item instanceof BlockItem || transformData.flip())) {
							matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(rotation.getFirst() * -2));
						}
						matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(rotation.get(1)));
						if (right && !(item instanceof BlockItem && !transformData.flip())) {
							matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(rotation.get(1) * -2));
						}
						matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(rotation.get(2)));
						if (right && (item instanceof BlockItem || transformData.flip())) {
							matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(rotation.get(2) * -2));
						}

						List<Float> scale = transformData.scale();
						matrixStack.scale(scale.get(0), scale.get(1), scale.get(2));

						heldItemRenderer.renderItem(playerEntity, secondaryAppleStack, transformationMode, matrixStack, orderedRenderCommandQueue, i);
						matrixStack.pop();
					}
					if (!tertiaryModel.equals(Identifier.of("null"))) {
						matrixStack.push();
						this.getContextModel().body.applyTransform(matrixStack);

						matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(180));

						List<Float> translation = tertiaryTransformData.translation();
						matrixStack.translate(translation.get(0), translation.get(1), translation.get(2));

						List<Float> rotation = tertiaryTransformData.rotation();
						matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(rotation.get(0)));
						matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(rotation.get(1)));
						matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(rotation.get(2)));

						List<Float> scale = tertiaryTransformData.scale();
						matrixStack.scale(scale.get(0), scale.get(1), scale.get(2));

						heldItemRenderer.renderItem(playerEntity, tertiaryAppleStack, transformationMode, matrixStack, orderedRenderCommandQueue, i);
						matrixStack.pop();
					}

					matrixStack.push();

					this.getContextModel().body.applyTransform(matrixStack);
					float pivot = 0.9F;
					float pivotSide = right ? 0.275F : -0.275F;
					float pivotFront = 0.1F;

					if (playerEntity.getEquippedStack(EquipmentSlot.LEGS) != ItemStack.EMPTY) {
						pivotSide = right ? 0.3F : -0.3F;
					}

					// pivot point
					matrixStack.translate(pivotSide, pivot, pivotFront);

					matrixStack.multiply((new Quaternionf())
							.rotateY(-3.1415927F)
							.rotateX(transformData.sway() * -(6.0F + armedEntityRenderState.field_53537 / 2.0F + armedEntityRenderState.field_53536) * 0.017453292F)
							.rotateZ(-(armedEntityRenderState.field_53538 / 2.0F * 0.017453292F))
							.rotateY((180.0F - armedEntityRenderState.field_53538 / 2.0F) * 0.017453292F)
					);

					matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(90));
					if (right) {
						if (item instanceof BlockItem || transformData.flip()) {
							matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180));
						}
					}

					transformationMode = transformData.mode();

					if (playerEntity instanceof OtherClientPlayerEntity) {
						applyDynamicMovement(matrixStack, playerEntity, item);
					} else if (playerEntity instanceof ClientPlayerEntity) {
						applyDynamicMovement(matrixStack, playerEntity, item);
					}

					List<Float> translation = transformData.translation();
					matrixStack.translate(right && (item instanceof BlockItem || transformData.flip()) ? -translation.get(0) : translation.get(0), translation.get(1), translation.get(2));

					List<Float> rotation = transformData.rotation();
					matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(rotation.get(0)));
					if (right && !(item instanceof BlockItem || transformData.flip())) {
						matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(rotation.getFirst() * -2));
					}
					matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(rotation.get(1)));
					if (right && !(item instanceof BlockItem && !transformData.flip())) {
						matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(rotation.get(1) * -2));
					}
					matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(rotation.get(2)));
					if (right && (item instanceof BlockItem || transformData.flip())) {
						matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(rotation.get(2) * -2));
					}

					List<Float> scale = transformData.scale();
					matrixStack.scale(scale.get(0), scale.get(1), scale.get(2));

					heldItemRenderer.renderItem(playerEntity, backSlotStack, transformationMode, matrixStack, orderedRenderCommandQueue, i);

					if (backSlotStack.hasEnchantments() && Math.random() > ((100 - CAConfig.enchantmentParticleChance) / 100.0F) && CAConfig.backslotParticles && !MinecraftClient.getInstance().isPaused()) {
						for (int j = 0; j < 5; j++) {
							Vec3d vec3d = CombatAmenities.matrixToVec(matrixStack);
							double offsetX = 0.7 * (Math.random() - 0.5);
							double offsetY = Math.random();
							double offsetZ = 0.7 * (Math.random() - 0.5);
							playerEntity.getEntityWorld().addParticleClient(
									ParticleTypes.ENCHANT,
									vec3d.getX() + offsetX,
									vec3d.getY() + offsetY - 0.5,
									vec3d.getZ() + offsetZ,
									0, 0, 0
							);
						}
					}

					matrixStack.pop();
				}
			}
		}
	}

	private static Vec3d startTickPosition;
	public static Vec3d playerVelocity = new Vec3d(0, 0, 0);

	private boolean wasOnGroundLastTick = true;
	private final Queue<Float> verticalVelocityHistory = new LinkedList<>();

	private float jiggleIntensity = 0.0F;
	private float jiggleDecay = 0.9F;
	private float jiggleTimer = 0.0F;

	private void applyDynamicMovement(MatrixStack matrixStack, PlayerEntity playerEntity, Item item) {
		int VELOCITY_HISTORY_SIZE = 5;
		if (verticalVelocityHistory.size() >= VELOCITY_HISTORY_SIZE) {
			verticalVelocityHistory.poll();
			verticalVelocityHistory.poll();
			verticalVelocityHistory.poll();
		}

		if (playerEntity instanceof AbstractClientPlayerEntity abstractClientPlayerEntity) {

			verticalVelocityHistory.offer((float) playerVelocity.y);

			if (detectLanding(abstractClientPlayerEntity)) {
				float landingVelocity = Math.abs(verticalVelocityHistory.peek() != null ? verticalVelocityHistory.peek() : 0.0F);
				jiggleIntensity = MathHelper.clamp(landingVelocity * 10.0F, 5.0F, 50.0F);
				jiggleDecay = 0.9F + Math.min(0.1F * (MinecraftClient.getInstance().getCurrentFps() / 140.0F - 1), 0.075F);
				jiggleTimer = 0.0F;
			}

			if (jiggleIntensity > 0.1F) {
				jiggleTimer += Math.max(0.4F - 0.25F * (MinecraftClient.getInstance().getCurrentFps() / 140.0F - 1), 0.1F);
				float oscillation = (float) Math.sin(jiggleTimer) * jiggleIntensity;
				if (!(item instanceof BlockItem blockItem && blockItem.getBlock() instanceof BannerBlock)) {
					matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(oscillation));
				}

				jiggleIntensity *= jiggleDecay;
			}

			wasOnGroundLastTick = playerEntity.isOnGround();
		}
	}

	// Detect landing based on velocity history
	private boolean detectLanding(AbstractClientPlayerEntity playerEntity) {
		if (playerEntity.isOnGround() && !wasOnGroundLastTick) {
			// Check if recent velocities indicate falling
			for (float velocity : verticalVelocityHistory) {
				if (velocity < 0.0F) { // Allow any downward motion to trigger landing
					return true;
				}
			}
		}
		return false;
	}

	private void setVelocityFromPos(PlayerEntity player) {
		if (startTickPosition == null) {
			startTickPosition = player.getEntityPos();
		}

		Vec3d endTickPosition = player.getEntityPos();
		if (!startTickPosition.equals(endTickPosition)) playerVelocity = endTickPosition.subtract(startTickPosition);
		startTickPosition = endTickPosition;
	}
}
