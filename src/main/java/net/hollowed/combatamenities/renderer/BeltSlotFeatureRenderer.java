package net.hollowed.combatamenities.renderer;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.hollowed.combatamenities.CombatAmenities;
import net.hollowed.combatamenities.client.PlayerEntityRenderStateAccess;
import net.hollowed.combatamenities.util.*;
import net.minecraft.block.BannerBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.debug.DebugRenderer;
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
import net.minecraft.util.Arm;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import org.joml.Quaternionf;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

@Environment(EnvType.CLIENT)
public class BeltSlotFeatureRenderer extends HeldItemFeatureRenderer<PlayerEntityRenderState, PlayerEntityModel> {

	private final HeldItemRenderer heldItemRenderer;
	private ModelTransformationMode transformationMode = ModelTransformationMode.FIXED;

    public BeltSlotFeatureRenderer(FeatureRendererContext<PlayerEntityRenderState, PlayerEntityModel> context, HeldItemRenderer heldItemRenderer) {
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
                ItemStack backSlotStack = playerEntity.getInventory().getStack(42);

                if (backSlotStack.hasEnchantments() && Math.random() > ((100 - CombatAmenities.CONFIG.enchantmentParticleChance) / 100.0F) && CombatAmenities.CONFIG.backslotParticles && !MinecraftClient.getInstance().isPaused()) {
                    for (int i = 0; i < 5; i++) { // Increase the number for more particles
                        double offsetX = (Math.random() - 0.5); // Random value between -1 and 1
                        double offsetY = Math.random(); // Random value between 0 and 1.5 for height variation
                        double offsetZ = (Math.random() - 0.5); // Random value between -1 and 1
                        playerEntity.getWorld().addParticle(
                                ParticleTypes.ENCHANT,
                                playerEntity.getX() + offsetX,
                                playerEntity.getY() + offsetY, // Add 1.2 to keep particles near the head
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

					setAngles(matrixStack, armedEntityRenderState, backSlotStack, vertexConsumerProvider);
					matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180));
					Arm arm = armedEntityRenderState.mainArm;

                    // Get the item's transformation data
                    Item item = backSlotStack.getItem();
                    Identifier itemId = Registries.ITEM.getId(item); // Retrieve the Identifier of the item
                    BeltTransformData transformData = BeltTransformResourceReloadListener.getTransform(itemId, backSlotStack.getOrDefault(ModComponents.INTEGER_PROPERTY, -1).toString());

                    // Apply the transformations from TransformData using List<Float> format
                    List<Float> scale = transformData.scale();
                    matrixStack.scale(scale.get(0), scale.get(1), scale.get(2)); // Scale

                    List<Float> rotation = transformData.rotation();
                    matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(rotation.get(0))); // Rotation X
                    matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(rotation.get(1))); // Rotation Y
                    matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(rotation.get(2))); // Rotation Z

                    List<Float> translation = transformData.translation();
                    matrixStack.translate(translation.get(0), translation.get(1), translation.get(2)); // Translation

					if (arm == Arm.RIGHT && !CombatAmenities.CONFIG.flipBeltslotDisplay || arm == Arm.LEFT && CombatAmenities.CONFIG.flipBeltslotDisplay) {
						matrixStack.translate(0, 0, -1.1);
					}

                    // Use transformation mode from the transform data (JSON)
                    transformationMode = transformData.mode();

                    // Apply dynamic movement and item-specific adjustments
                    if (playerEntity instanceof OtherClientPlayerEntity) {
                        applyDynamicMovement(matrixStack, playerEntity, item);
                    } else if (playerEntity instanceof ClientPlayerEntity) {
                        applyDynamicMovement(matrixStack, playerEntity, item);
                    }

                    applyItemSpecificAdjustments(matrixStack, armedEntityRenderState.mainArm, item);

                    // Render the item
                    heldItemRenderer.renderItem(playerEntity, backSlotStack, transformationMode, false, matrixStack, vertexConsumerProvider, light);

                    matrixStack.pop();
                }
            }
		}
	}

	// Helper method for item-specific transformations
	private void applyItemSpecificAdjustments(MatrixStack matrixStack, Arm arm, Item stack) {
		if (stack instanceof BlockItem) {
			transformationMode = ModelTransformationMode.FIXED;
			matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(30));
			if (arm == Arm.RIGHT && !CombatAmenities.CONFIG.flipBeltslotDisplay || arm == Arm.LEFT && CombatAmenities.CONFIG.flipBeltslotDisplay) {
				matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-2.5F));
			}
			matrixStack.scale(0.5F, 0.5F, 0.5F);
			matrixStack.translate(0.2, arm == Arm.RIGHT && !CombatAmenities.CONFIG.flipBeltslotDisplay || arm == Arm.LEFT && CombatAmenities.CONFIG.flipBeltslotDisplay ? -0.35 : -0.25, arm == Arm.RIGHT && !CombatAmenities.CONFIG.flipBeltslotDisplay || arm == Arm.LEFT && CombatAmenities.CONFIG.flipBeltslotDisplay ? 1.6 : 0.05);
			if (arm == Arm.RIGHT && !CombatAmenities.CONFIG.flipBeltslotDisplay || arm == Arm.LEFT && CombatAmenities.CONFIG.flipBeltslotDisplay) {
				matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180));
			}
		}
		if (stack instanceof MiningToolItem) {
			matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(-30));
			matrixStack.translate(0.2, 0, 0);
		}
		if (stack instanceof BlockItem item && item.getBlock() instanceof BannerBlock) {
			if (arm == Arm.RIGHT && !CombatAmenities.CONFIG.flipBeltslotDisplay || arm == Arm.LEFT && CombatAmenities.CONFIG.flipBeltslotDisplay) {
				matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(5));
			}
			matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180));
			matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(arm == Arm.RIGHT && !CombatAmenities.CONFIG.flipBeltslotDisplay || arm == Arm.LEFT && CombatAmenities.CONFIG.flipBeltslotDisplay ? -30 : 30));
			matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(12F));
			matrixStack.translate(arm == Arm.RIGHT && !CombatAmenities.CONFIG.flipBeltslotDisplay || arm == Arm.LEFT && CombatAmenities.CONFIG.flipBeltslotDisplay ? 0.135 : -0.07, -0.2, arm == Arm.RIGHT && !CombatAmenities.CONFIG.flipBeltslotDisplay || arm == Arm.LEFT && CombatAmenities.CONFIG.flipBeltslotDisplay ? -1.8 : -0.75);
		}
		if (stack instanceof ShieldItem) {
			matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180));
			matrixStack.translate(-0.4, 0, -0.15);
			matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(arm == Arm.RIGHT && !CombatAmenities.CONFIG.flipBeltslotDisplay || arm == Arm.LEFT && CombatAmenities.CONFIG.flipBeltslotDisplay ? 180.0F : 0));
			transformationMode = ModelTransformationMode.FIXED;
			matrixStack.scale(0.75F, 0.75F, 0.75F);
			matrixStack.translate(arm == Arm.RIGHT && !CombatAmenities.CONFIG.flipBeltslotDisplay || arm == Arm.LEFT && CombatAmenities.CONFIG.flipBeltslotDisplay ? -0.25F : 0.25, 0.0F, arm == Arm.RIGHT && !CombatAmenities.CONFIG.flipBeltslotDisplay || arm == Arm.LEFT && CombatAmenities.CONFIG.flipBeltslotDisplay ? -0.11f : 0.1);
		}
		matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0F));
		if (arm == Arm.RIGHT && !CombatAmenities.CONFIG.flipBeltslotDisplay || arm == Arm.LEFT && CombatAmenities.CONFIG.flipBeltslotDisplay) {
			matrixStack.translate(0, 0, -0.55);
		}
	}

	// Class-level variables for tracking landing state and vertical velocity
	private boolean wasOnGroundLastTick = true;
	private final Queue<Float> verticalVelocityHistory = new LinkedList<>();

	private float jiggleIntensity = 0.0F; // Current jiggle intensity
	private float jiggleDecay = 0.9F; // Decay rate of jiggle intensity
	private float jiggleTimer = 0.0F; // Timer to drive oscillation

	private void setAngles(MatrixStack matrixStack, PlayerEntityRenderState playerEntityRenderState, ItemStack item, VertexConsumerProvider vertexConsumerProvider) {
		BeltTransformData data = BeltTransformResourceReloadListener.getTransform(Registries.ITEM.getId(item.getItem()), item.getOrDefault(ModComponents.INTEGER_PROPERTY, -1).toString());

		// Calculate banner-specific multiplier
		float bannerMultiplier = 0.4F;
		if (item.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof BannerBlock) {
			bannerMultiplier = 1.0F; // Increase rotation for banners
		}

		float pivotAdjustmentY = 0.7F; // Pivot point ! ! !
		float pivotAdjustmentX = playerEntityRenderState.mainArm == Arm.RIGHT && !CombatAmenities.CONFIG.flipBeltslotDisplay || playerEntityRenderState.mainArm == Arm.LEFT && CombatAmenities.CONFIG.flipBeltslotDisplay ? 0.25F : -0.25F; // Pivot point ! ! !

		// Move to the pivot point
		matrixStack.translate(pivotAdjustmentX, pivotAdjustmentY, 0.0F);

		// Render debug point at pivot
		renderDebugPoint(matrixStack, vertexConsumerProvider);

		// Translate further and apply rotations
		matrixStack.multiply((new Quaternionf())
				.rotateY(-3.1415927F)
				.rotateX(data.sway() * bannerMultiplier * -(6.0F + playerEntityRenderState.field_53537 / 2.0F + playerEntityRenderState.field_53536) * 0.017453292F)
				.rotateZ(-(playerEntityRenderState.field_53538 / 2.0F * 0.017453292F))
				.rotateY((180.0F - playerEntityRenderState.field_53538 / 2.0F) * 0.017453292F)
		);

		// Move back to the original position
		matrixStack.translate(-pivotAdjustmentX, -pivotAdjustmentY, 0.0F); // Pivot Point ! ! !

		matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(90));
		matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(playerEntityRenderState.mainArm == Arm.RIGHT && !CombatAmenities.CONFIG.flipBeltslotDisplay || playerEntityRenderState.mainArm == Arm.LEFT && CombatAmenities.CONFIG.flipBeltslotDisplay ? 1F : -1F));

		matrixStack.translate(-0.2, 1, -0.275);
		if (item.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof BannerBlock) {
			matrixStack.scale(1.5F, 1.5F, 1.5F);
			matrixStack.translate(0.0, playerEntityRenderState.mainArm == Arm.RIGHT && !CombatAmenities.CONFIG.flipBeltslotDisplay || playerEntityRenderState.mainArm == Arm.LEFT && CombatAmenities.CONFIG.flipBeltslotDisplay ? 0.0 : 0.075, playerEntityRenderState.mainArm == Arm.RIGHT && !CombatAmenities.CONFIG.flipBeltslotDisplay || playerEntityRenderState.mainArm == Arm.LEFT && CombatAmenities.CONFIG.flipBeltslotDisplay ? 0.07 : -0.25);
			matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(playerEntityRenderState.mainArm == Arm.RIGHT && !CombatAmenities.CONFIG.flipBeltslotDisplay || playerEntityRenderState.mainArm == Arm.LEFT && CombatAmenities.CONFIG.flipBeltslotDisplay ? -5 : 10F));
		}
	}

	private void renderDebugPoint(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider) {
//		DebugRenderer.drawBox(
//				matrixStack,
//				vertexConsumerProvider,
//				-0.02F, -0.02F, -0.02F, // Box min (relative to pivot)
//				0.02F,  0.02F,  0.02F, // Box max (relative to pivot)
//				1.0F, 0.0F, 0.0F, 1.0F  // Red color
//		);
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
