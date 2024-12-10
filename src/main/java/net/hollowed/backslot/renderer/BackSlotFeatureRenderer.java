package net.hollowed.backslot.renderer;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.hollowed.backslot.util.TransformData;
import net.hollowed.backslot.util.TransformResourceReloadListener;
import net.minecraft.block.BannerBlock;
import net.minecraft.block.SkullBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.feature.HeldItemFeatureRenderer;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.item.*;
import net.minecraft.registry.Registries;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

@Environment(EnvType.CLIENT)
public class BackSlotFeatureRenderer extends HeldItemFeatureRenderer<PlayerEntityRenderState, PlayerEntityModel> {

	private final HeldItemRenderer heldItemRenderer;
	private ModelTransformationMode transformationMode = ModelTransformationMode.FIXED;

	private float speedEffectVelocity = 0.0F;  		// Velocity for smooth acceleration of speed effect
	private float swayVelocity = 0.0F;					// Velocity for smooth acceleration of sway
    private float verticalRotationVelocity = 0.0F; 	// Velocity for smooth acceleration of vertical rotation

    public BackSlotFeatureRenderer(FeatureRendererContext<PlayerEntityRenderState, PlayerEntityModel> context, HeldItemRenderer heldItemRenderer) {
		super(context);
		this.heldItemRenderer = heldItemRenderer;
	}

	@Override
	public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int light, PlayerEntityRenderState armedEntityRenderState, float limbSwing, float limbSwingAmount) {
		MinecraftClient client = MinecraftClient.getInstance();
		ClientPlayerEntity playerEntity = client.player;

		if (playerEntity != null) {
			// Retrieve the back slot stack
			ItemStack backSlotStack = playerEntity.getInventory().getStack(41);
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
				applyDynamicMovement(matrixStack, playerEntity, item);
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
			matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(52.0F));
			matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(55.0F));
			matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(-25.0F));
			matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(180.0F));
			matrixStack.translate(-0.3D, 0.2D, -0.1D);
			if (!armedEntityRenderState.equippedChestStack.isEmpty()) {
				matrixStack.translate(0.05F, 0.0F, 0.0D);
			}
			transformationMode = ModelTransformationMode.FIRST_PERSON_RIGHT_HAND;
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

	// Apply dynamic movement-based transformations
	private void applyDynamicMovement(MatrixStack matrixStack, ClientPlayerEntity playerEntity, Item item) {
		// Get player movement velocity
		double velocityX = playerEntity.getVelocity().x;
		double velocityZ = playerEntity.getVelocity().z;
		double velocityY = playerEntity.getVelocity().y;

		// Fix translation
		matrixStack.translate(0F, 0.3F, 0.2);

		// Cap horizontal and vertical velocities
		float MAX_HORIZONTAL_VELOCITY = 0.3F;
		float cappedHorizontalVelocity = MathHelper.clamp((float) Math.sqrt(velocityX * velocityX + velocityZ * velocityZ), 0.0F, MAX_HORIZONTAL_VELOCITY);

		float MAX_VERTICAL_VELOCITY = 0.8F;
		float cappedVerticalVelocity = MathHelper.clamp((float) velocityY, -MAX_VERTICAL_VELOCITY, MAX_VERTICAL_VELOCITY);

		// Track vertical velocity history
        // Number of ticks to track vertical velocity
        int VELOCITY_HISTORY_SIZE = 5;
        if (verticalVelocityHistory.size() >= VELOCITY_HISTORY_SIZE) {
			verticalVelocityHistory.poll(); // Remove the oldest velocity
		}
		verticalVelocityHistory.offer((float) velocityY); // Add the current velocity

		// Get tick delta for smoothing
		float tickDelta = MinecraftClient.getInstance().getRenderTickCounter().getTickDelta(true);

		// Calculate sway using a logarithmic function
		float swayScale = 1.0F; // Scale for horizontal velocity before applying the log
		float rawSway = (float) MathHelper.clamp(Math.log(1 + swayScale * cappedHorizontalVelocity), 0F, 0.5F)
				* MathHelper.sin((playerEntity.age + tickDelta) * 0.3F) * 24.0F;

		// Calculate speed effect
		float rawSpeedEffect = MathHelper.clamp((float) Math.pow(cappedHorizontalVelocity * 10.0F, 1.5), 0.0F, 15.0F); // Exponential scaling

		// Smooth sway and speed effect using tickDelta as well as calculate the sway
		float swayAccelerationFactor = 2.0F;
		swayVelocity = MathHelper.lerp(swayAccelerationFactor * tickDelta, swayVelocity, rawSway);

		float speedAccelerationFactor = 0.1F;
		speedEffectVelocity = MathHelper.lerp(speedAccelerationFactor * tickDelta, speedEffectVelocity, rawSpeedEffect);

		// Vertical motion influences rotation
		float verticalRotationFactor = 0.0F;
		if (cappedVerticalVelocity < 0.0F) {
			verticalRotationFactor = MathHelper.clamp(-cappedVerticalVelocity * 2.5F, 0.0F, 50.0F);
		} else if (cappedVerticalVelocity > 0.0F) {
			verticalRotationFactor = MathHelper.clamp(cappedVerticalVelocity * 2.5F, 0.0F, 50.0F);
		}

		// Smooth vertical rotation
		float verticalRotationAccelerationFactor = 0.1F;
		verticalRotationVelocity = MathHelper.lerp(verticalRotationAccelerationFactor * tickDelta, verticalRotationVelocity, verticalRotationFactor);

		// Adjust transformation pivot to the top of the item
		float pivotAdjustment = -0.4F; // Pivot point
		matrixStack.translate(0.0F, pivotAdjustment, 0.0F); // Pivot Point ! ! !

		// Calculate banner-specific multiplier
		float bannerMultiplier = 1.0F;
		if (item instanceof BlockItem blockItem && blockItem.getBlock() instanceof BannerBlock) {
			bannerMultiplier = 4.0F; // Increase rotation for banners
			matrixStack.translate(0F, 0.075F, -0.05); // Move the banner because it's WRONG
		}

		// Combine rotations
		float totalRotation = (speedEffectVelocity * 3.0F + swayVelocity) * bannerMultiplier;

		// Multiply the matrices
		matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(totalRotation));
		matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(verticalRotationVelocity * 6.0F));

		// Revert pivot adjustment or it will freak out and die
		matrixStack.translate(0.0F, -pivotAdjustment, 0.0F); // Restore the original pivot

		// Jiggle oscillation effect for landing
		if (detectOtherLanding(playerEntity)) {
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
			matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(oscillation));

			// Reduce intensity over time (decay effect)
			jiggleIntensity *= jiggleDecay;
		}

		// Update last ground state
		wasOnGroundLastTick = playerEntity.isOnGround();
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
}
