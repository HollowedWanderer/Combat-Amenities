package net.hollowed.backslot.renderer;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.hollowed.backslot.CombatAmenities;
import net.hollowed.backslot.client.ExtendedPlayerEntityRenderState;
import net.hollowed.backslot.util.TransformData;
import net.hollowed.backslot.util.TransformResourceReloadListener;
import net.minecraft.block.BannerBlock;
import net.minecraft.block.SkullBlock;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.Registries;
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
	private ModelTransformationMode transformationMode = ModelTransformationMode.FIXED;

    public BackSlotFeatureRenderer(FeatureRendererContext<PlayerEntityRenderState, PlayerEntityModel> context, HeldItemRenderer heldItemRenderer) {
		super(context);
		this.heldItemRenderer = heldItemRenderer;
	}

	@Override
	public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int light, PlayerEntityRenderState armedEntityRenderState, float limbSwing, float limbSwingAmount) {
		// Use the correct player entity from the context
		PlayerEntity playerEntity = ((ExtendedPlayerEntityRenderState) armedEntityRenderState).getPlayerEntity();

		if (playerEntity != null) {
			// Retrieve the back slot stack from the correct player's inventory
			ItemStack backSlotStack = playerEntity.getInventory().getStack(41);

			if (backSlotStack.hasEnchantments() && Math.random() > 0.95 && CombatAmenities.CONFIG.backslotParticles) {
				for (int i = 0; i < 5; i++) { // Increase the number for more particles
					double offsetX = (Math.random() - 0.5); // Random value between -1 and 1
					double offsetY = Math.random(); // Random value between 0 and 1.5 for height variation
					double offsetZ = (Math.random() - 0.5); // Random value between -1 and 1
					playerEntity.getWorld().addParticle(
							ParticleTypes.ENCHANT,
							playerEntity.getX() + offsetX,
							playerEntity.getY() + offsetY + 0.75, // Add 1.2 to keep particles near the head
							playerEntity.getZ() + offsetZ,
							0, 0, 0
					);
				}
			}

			if (!backSlotStack.isEmpty()) {
				matrixStack.push();

				// Rotate based on body
				ModelPart bodyPart = this.getContextModel().body;
				bodyPart.rotate(matrixStack);

				// Get the item's transformation data
				Item item = backSlotStack.getItem();
				Identifier itemId = Registries.ITEM.getId(item); // Retrieve the Identifier of the item
				TransformData transformData = TransformResourceReloadListener.getTransform(itemId);

				// Apply the transformations from TransformData using List<Float> format
				List<Float> scale = transformData.scale();
				matrixStack.scale(scale.get(0), scale.get(1), scale.get(2)); // Scale

				List<Float> rotation = transformData.rotation();
				matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(rotation.get(0))); // Rotation X
				matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(rotation.get(1))); // Rotation Y
				matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(rotation.get(2))); // Rotation Z

				List<Float> translation = transformData.translation();
				matrixStack.translate(translation.get(0), translation.get(1), translation.get(2)); // Translation

				// Use transformation mode from the transform data (JSON)
				transformationMode = transformData.mode();

				// Apply dynamic movement and item-specific adjustments
				if (playerEntity instanceof OtherClientPlayerEntity) {
					applyDynamicMovement(matrixStack, playerEntity, item);
				} else if (playerEntity instanceof ClientPlayerEntity) {
					applyDynamicMovement(matrixStack, playerEntity, item);
				}
				setAngles(matrixStack, armedEntityRenderState, backSlotStack.getItem());
				applyItemSpecificAdjustments(matrixStack, armedEntityRenderState, item);

				// Render the item
				heldItemRenderer.renderItem(playerEntity, backSlotStack, transformationMode, false, matrixStack, vertexConsumerProvider, light);

				matrixStack.pop();
			}
		}
	}

	// Helper method for item-specific transformations
	private void applyItemSpecificAdjustments(MatrixStack matrixStack, PlayerEntityRenderState armedEntityRenderState, Item item) {
		if (item instanceof TridentItem) {
			matrixStack.translate(0.0F, 0.0F, -0.15F);
			if (!armedEntityRenderState.equippedChestStack.isEmpty()) {
				matrixStack.translate(0.0F, 0.0F, 0.05F);
			}
			matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180.0F));
			matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(45.0F));
			matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(90.0F));
			transformationMode = ModelTransformationMode.FIRST_PERSON_RIGHT_HAND;
			matrixStack.scale(1.25F, 1.25F, 1.25F);
		} else if (item instanceof FishingRodItem || item instanceof OnAStickItem) {
			matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(180.0F));
			transformationMode = ModelTransformationMode.FIXED;
		} else if (item instanceof BlockItem blockItem) {
			transformationMode = ModelTransformationMode.GROUND;
			matrixStack.scale(2, 2, 2);
			if (!(blockItem.getBlock() instanceof SkullBlock)) {
				matrixStack.translate(0, -0.2, 0);
			} else {
				matrixStack.translate(0, -0.1, 0);
			}

			if (blockItem.getBlock() instanceof BannerBlock) {
				matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(180.0F));
				matrixStack.translate(0, -0.5, 0);
				matrixStack.scale(1.25F, 1.25F, 1.25F);

			}
		} else if (item instanceof ShieldItem) {
			matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0F));
			matrixStack.scale(1.5F, 1.5F, 1.5F);
			matrixStack.translate(0F, 0.1F, 0.05f);
		} else {
			if (armedEntityRenderState.equippedChestStack != null) {
				matrixStack.translate(0.0F, 0.0F, -0.05F);
			}
			transformationMode = ModelTransformationMode.FIXED;
		}
	}

	// Class-level variables for tracking landing state and vertical velocity
	private boolean wasOnGroundLastTick = true;
	private final Queue<Float> verticalVelocityHistory = new LinkedList<>();

	private float jiggleIntensity = 0.0F; // Current jiggle intensity
	private float jiggleDecay = 0.9F; // Decay rate of jiggle intensity
	private float jiggleTimer = 0.0F; // Timer to drive oscillation

	private void setAngles(MatrixStack matrixStack, PlayerEntityRenderState playerEntityRenderState, Item item) {
		TransformData data = TransformResourceReloadListener.getTransform(Registries.ITEM.getId(item));

		// Calculate banner-specific multiplier
		float bannerMultiplier = 0.4F;
		if (item instanceof BlockItem blockItem && blockItem.getBlock() instanceof BannerBlock) {
			bannerMultiplier = 1.0F; // Increase rotation for banners
			matrixStack.translate(0F, 0.075F, -0.05); // Move the banner because it's WRONG
		}

		matrixStack.translate(0F, 0.3F, 0.2);
		float pivotAdjustment = -0.4F; // Pivot point
		matrixStack.translate(0.0F, pivotAdjustment, 0.0F); // Pivot Point ! ! !
		matrixStack.multiply((new Quaternionf())
				.rotateY(-3.1415927F)
				.rotateX(data.sway() * bannerMultiplier * -(6.0F + playerEntityRenderState.field_53537 / 2.0F + playerEntityRenderState.field_53536) * 0.017453292F)
				.rotateZ(-(playerEntityRenderState.field_53538 / 2.0F * 0.017453292F))
				.rotateY((180.0F - playerEntityRenderState.field_53538 / 2.0F) * 0.017453292F)
		);
		matrixStack.translate(0.0F, -pivotAdjustment, 0.0F); // Pivot Point ! ! !
	}

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
			if (detectOtherLanding((ClientPlayerEntity) playerEntity)) {
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
	private boolean detectOtherLanding(ClientPlayerEntity playerEntity) {
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
