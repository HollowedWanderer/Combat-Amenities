package net.hollowed.combatamenities.renderer;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.hollowed.combatamenities.CombatAmenities;
import net.hollowed.combatamenities.util.interfaces.PlayerEntityRenderStateAccess;
import net.hollowed.combatamenities.util.items.ModComponents;
import net.hollowed.combatamenities.util.json.BackTransformData;
import net.hollowed.combatamenities.util.json.BackTransformResourceReloadListener;
import net.minecraft.block.BannerBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
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
import org.joml.Quaternionf;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

@Environment(EnvType.CLIENT)
public class BackSlotFeatureRenderer extends HeldItemFeatureRenderer<PlayerEntityRenderState, PlayerEntityModel> {

	private final HeldItemRenderer heldItemRenderer;
	private ItemDisplayContext transformationMode = ItemDisplayContext.FIXED;

    public BackSlotFeatureRenderer(FeatureRendererContext<PlayerEntityRenderState, PlayerEntityModel> context, HeldItemRenderer heldItemRenderer) {
		super(context);
		this.heldItemRenderer = heldItemRenderer;
	}

	@Override
	public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int light, PlayerEntityRenderState armedEntityRenderState, float limbSwing, float limbSwingAmount) {
		if (armedEntityRenderState instanceof PlayerEntityRenderStateAccess access) {
			// Use the correct player entity from the context
			PlayerEntity playerEntity = access.combat_Amenities$getPlayerEntity();

			if (playerEntity != null) {
				// Retrieve the back slot stack from the correct player's inventory
				ItemStack backSlotStack = playerEntity.getInventory().getStack(41);

				if (backSlotStack.hasEnchantments() && Math.random() > ((100 - CombatAmenities.CONFIG.enchantmentParticleChance) / 100.0F) && CombatAmenities.CONFIG.backslotParticles && !MinecraftClient.getInstance().isPaused()) {
					for (int i = 0; i < 5; i++) { // Increase the number for more particles
						double offsetX = (Math.random() - 0.5); // Random value between -1 and 1
						double offsetY = Math.random(); // Random value between 0 and 1.5 for height variation
						double offsetZ = (Math.random() - 0.5); // Random value between -1 and 1
						playerEntity.getWorld().addParticleClient(
								ParticleTypes.ENCHANT,
								playerEntity.getX() + offsetX,
								playerEntity.getY() + offsetY + 0.75, // Add 1.2 to keep particles near the head
								playerEntity.getZ() + offsetZ,
								0, 0, 0
						);
					}
				}

				if (!backSlotStack.isEmpty()) {
					// Get the item's transformation data
					Item item = backSlotStack.getItem();
					Identifier itemId = Registries.ITEM.getId(item); // Retrieve the Identifier of the item
					BackTransformData transformData = BackTransformResourceReloadListener.getTransform(itemId, backSlotStack.getOrDefault(ModComponents.INTEGER_PROPERTY, -1).toString());

					BackTransformData.SecondaryTransformData secondaryTransformData = transformData.secondaryTransforms();
					BackTransformData.TertiaryTransformData tertiaryTransformData = transformData.tertiaryTransforms();

					Identifier secondaryModel = secondaryTransformData.item();
					ItemStack secondaryAppleStack = Items.APPLE.getDefaultStack();
					secondaryAppleStack.set(DataComponentTypes.ITEM_MODEL, secondaryModel);

					Identifier tertiaryModel = tertiaryTransformData.item();
					ItemStack tertiaryAppleStack = Items.APPLE.getDefaultStack();
					tertiaryAppleStack.set(DataComponentTypes.ITEM_MODEL, tertiaryModel);

					Arm arm = armedEntityRenderState.mainArm;
					boolean right = arm == Arm.RIGHT && !CombatAmenities.CONFIG.flipBackslotDisplay || arm == Arm.LEFT && CombatAmenities.CONFIG.flipBackslotDisplay;

					boolean flip = false;

					if (!right) {
						if (!(item instanceof BlockItem) && !transformData.noFlip()) {
							flip = true;
						}
					}

					if (!secondaryModel.equals(Identifier.of("null"))) {
						matrixStack.push();
						this.getContextModel().body.applyTransform(matrixStack);

						float pivot = 0.0F;

						if (playerEntity.getEquippedStack(EquipmentSlot.CHEST) != ItemStack.EMPTY) {
							matrixStack.translate(0, 0, 0.1F);
						}

						matrixStack.translate(0, pivot, 0.125); // pivot point

						matrixStack.multiply((new Quaternionf())
								.rotateY(-3.1415927F)
								.rotateX(transformData.sway() * -(6.0F + armedEntityRenderState.field_53537 / 2.0F + armedEntityRenderState.field_53536) * 0.017453292F)
								.rotateZ(-(armedEntityRenderState.field_53538 / 2.0F * 0.017453292F))
								.rotateY((180.0F - armedEntityRenderState.field_53538 / 2.0F) * 0.017453292F)
						);

						matrixStack.translate(0, 0.35, 0);

						if (flip) {
							matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180));
						}

						// Use transformation mode from the transform data (JSON)
						transformationMode = secondaryTransformData.mode();

						// Apply dynamic movement and item-specific adjustments
						if (playerEntity instanceof OtherClientPlayerEntity) {
							applyDynamicMovement(matrixStack, playerEntity, item);
						} else if (playerEntity instanceof ClientPlayerEntity) {
							applyDynamicMovement(matrixStack, playerEntity, item);
						}

						List<Float> scale = secondaryTransformData.scale();
						matrixStack.scale(scale.get(0), scale.get(1), scale.get(2)); // Scale

						List<Float> translation = secondaryTransformData.translation();
						matrixStack.translate(translation.get(0), translation.get(1), flip ? translation.get(2) : -translation.get(2)); // Translation
						if (right && (item instanceof BlockItem || transformData.noFlip())) {
							matrixStack.translate(translation.getFirst() * -2, 0, 0); // Translation
						}

						List<Float> rotation = secondaryTransformData.rotation();
						matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(rotation.get(0))); // Rotation X
						if (flip) {
							matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(rotation.getFirst() * -2)); // Rotation X
						}
						matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(rotation.get(1))); // Rotation Y
						matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(rotation.get(2))); // Rotation Z
						if (right && (item instanceof BlockItem || transformData.noFlip())) {
							matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(rotation.get(2) * -2)); // Rotation X
						}

						heldItemRenderer.renderItem(playerEntity, secondaryAppleStack, transformationMode, matrixStack, vertexConsumerProvider, light);
						matrixStack.pop();
					}

					if (!tertiaryModel.equals(Identifier.of("null"))) {
						matrixStack.push();
						this.getContextModel().body.applyTransform(matrixStack);

						if (flip) {
							matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180));
						}

						matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(180));

						List<Float> scale = tertiaryTransformData.scale();
						matrixStack.scale(scale.get(0), scale.get(1), scale.get(2)); // Scale

						List<Float> translation = tertiaryTransformData.translation();
						matrixStack.translate(translation.get(0), translation.get(1), translation.get(2)); // Translation

						List<Float> rotation = tertiaryTransformData.rotation();
						matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(rotation.get(0))); // Rotation X
						matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(rotation.get(1))); // Rotation Y
						matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(rotation.get(2))); // Rotation Z

						heldItemRenderer.renderItem(playerEntity, tertiaryAppleStack, transformationMode, matrixStack, vertexConsumerProvider, light);
						matrixStack.pop();
					}

					matrixStack.push();
					this.getContextModel().body.applyTransform(matrixStack);

					float pivot = 0.0F;

					if (playerEntity.getEquippedStack(EquipmentSlot.CHEST) != ItemStack.EMPTY) {
						matrixStack.translate(0, 0, 0.1F);
					}
					if (armedEntityRenderState.capeVisible) {
						matrixStack.translate(0, 0, 0.05F);
					}

					matrixStack.translate(0, pivot, 0.125); // pivot point

					matrixStack.multiply((new Quaternionf())
							.rotateY(-3.1415927F)
							.rotateX(transformData.sway() * -(6.0F + armedEntityRenderState.field_53537 / 2.0F + armedEntityRenderState.field_53536) * 0.017453292F)
							.rotateZ(-(armedEntityRenderState.field_53538 / 2.0F * 0.017453292F))
							.rotateY((180.0F - armedEntityRenderState.field_53538 / 2.0F) * 0.017453292F)
					);

					matrixStack.translate(0, 0.35, 0);

					if (flip) {
						matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180));
					}

					// Use transformation mode from the transform data (JSON)
					transformationMode = transformData.mode();

					// Apply dynamic movement and item-specific adjustments
					if (playerEntity instanceof OtherClientPlayerEntity) {
						applyDynamicMovement(matrixStack, playerEntity, item);
					} else if (playerEntity instanceof ClientPlayerEntity) {
						applyDynamicMovement(matrixStack, playerEntity, item);
					}

					List<Float> scale = transformData.scale();
					matrixStack.scale(scale.get(0), scale.get(1), scale.get(2)); // Scale

					List<Float> translation = transformData.translation();
					matrixStack.translate(translation.get(0), translation.get(1), flip ? translation.get(2) : -translation.get(2)); // Translation
					if (right && (item instanceof BlockItem || transformData.noFlip())) {
						matrixStack.translate(translation.getFirst() * -2, 0, 0); // Translation
					}

					List<Float> rotation = transformData.rotation();
					matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(rotation.get(0))); // Rotation X
					if (flip) {
						matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(rotation.getFirst() * -2)); // Rotation X
					}
					matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(rotation.get(1))); // Rotation Y
					matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(rotation.get(2))); // Rotation Z
					if (right && (item instanceof BlockItem || transformData.noFlip())) {
						matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(rotation.get(2) * -2)); // Rotation X
					}

					// Render the item
					heldItemRenderer.renderItem(playerEntity, backSlotStack, transformationMode, matrixStack, vertexConsumerProvider, light);
					matrixStack.pop();
				}
			}
		}
	}

	// Class-level variables for tracking landing state and vertical velocity
	private boolean wasOnGroundLastTick = true;
	private final Queue<Float> verticalVelocityHistory = new LinkedList<>();

	private float jiggleIntensity = 0.0F; // Current jiggle intensity
	private float jiggleDecay = 0.9F; // Decay rate of jiggle intensity
	private float jiggleTimer = 0.0F; // Timer to drive oscillation

	// Apply dynamic movement-based transformations
	private void applyDynamicMovement(MatrixStack matrixStack, PlayerEntity playerEntity, Item item) {
		if (playerEntity instanceof ClientPlayerEntity) {
			// Get player movement velocity
			double velocityY = playerEntity.getVelocity().y;

			// Track vertical velocity history
			// Number of ticks to track vertical velocity
			int VELOCITY_HISTORY_SIZE = 5;
			if (verticalVelocityHistory.size() >= VELOCITY_HISTORY_SIZE) {
				verticalVelocityHistory.poll(); // Remove the oldest velocity
			}
			verticalVelocityHistory.offer((float) velocityY); // Add the current velocity

			// Jiggle oscillation effect for landing
			if (detectLanding((ClientPlayerEntity) playerEntity)) {
				// Scale jiggle intensity based on fall velocity
				float landingVelocity = Math.abs(verticalVelocityHistory.peek() != null ? verticalVelocityHistory.peek() : 0.0F);
				jiggleIntensity = MathHelper.clamp(landingVelocity * 10.0F, 5.0F, 50.0F); // Scale between 5 and 50 degrees
				jiggleDecay = 0.9F; // Reset decay rate
				jiggleTimer = 0.0F; // Reset jiggle timer
			}

			// If jiggle is active, calculate oscillation
			if (jiggleIntensity > 0.1F) {
				// Increment timer to drive oscillation
				jiggleTimer += 0.4F; // Control speed of oscillation (higher = faster)

				// Oscillate using sine wave for smooth back-and-forth motion
				float oscillation = (float) Math.sin(jiggleTimer) * jiggleIntensity;

				// Apply Z-axis rotation for jiggle
				if (!(item instanceof BlockItem blockItem && blockItem.getBlock() instanceof BannerBlock)) {
					matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(oscillation));
				}

				// Reduce intensity over time (decay effect)
				jiggleIntensity *= jiggleDecay;
			}

			// Update last ground state
			wasOnGroundLastTick = playerEntity.isOnGround();
		} else if (playerEntity instanceof OtherClientPlayerEntity) {
			// Get player movement velocity
			double velocityY = playerEntity.getVelocity().y;

			// Track vertical velocity history
			// Number of ticks to track vertical velocity
			int VELOCITY_HISTORY_SIZE = 5;
			if (verticalVelocityHistory.size() >= VELOCITY_HISTORY_SIZE) {
				verticalVelocityHistory.poll(); // Remove the oldest velocity
			}
			verticalVelocityHistory.offer((float) velocityY); // Add the current velocity

			// Jiggle oscillation effect for landing
			if (detectOtherLanding((OtherClientPlayerEntity) playerEntity)) {
				// Scale jiggle intensity based on fall velocity
				float landingVelocity = Math.abs(verticalVelocityHistory.peek() != null ? verticalVelocityHistory.peek() : 0.0F);
				jiggleIntensity = MathHelper.clamp(landingVelocity * 10.0F, 5.0F, 50.0F); // Scale between 5 and 50 degrees
				jiggleDecay = 0.9F; // Reset decay rate
				jiggleTimer = 0.0F; // Reset jiggle timer
			}

			// If jiggle is active, calculate oscillation
			if (jiggleIntensity > 0.1F) {
				// Increment timer to drive oscillation
				jiggleTimer += 0.4F; // Control speed of oscillation (higher = faster)

				// Oscillate using sine wave for smooth back-and-forth motion
				float oscillation = (float) Math.sin(jiggleTimer) * jiggleIntensity;

				// Apply Z-axis rotation for jiggle
				if (!(item instanceof BlockItem blockItem && blockItem.getBlock() instanceof BannerBlock)) {
					matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(oscillation));
				}

				// Reduce intensity over time (decay effect)
				jiggleIntensity *= jiggleDecay;
			}

			// Update last ground state
			wasOnGroundLastTick = playerEntity.isOnGround();
		}
	}

	// Detect landing based on velocity history
	private boolean detectLanding(ClientPlayerEntity playerEntity) {
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

	// Detect landing based on velocity history
	private boolean detectOtherLanding(OtherClientPlayerEntity playerEntity) {
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
}
