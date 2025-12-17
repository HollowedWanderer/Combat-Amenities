package net.hollowed.combatamenities.renderer;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.hollowed.combatamenities.CombatAmenities;
import net.hollowed.combatamenities.config.CAConfig;
import net.hollowed.combatamenities.util.interfaces.PlayerEntityRenderStateAccess;
import net.hollowed.combatamenities.util.items.CAComponents;
import net.hollowed.combatamenities.util.json.BackTransformData;
import net.hollowed.combatamenities.util.json.BackTransformResourceReloadListener;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.player.RemotePlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.BannerBlock;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

@Environment(EnvType.CLIENT)
public class BackSlotFeatureRenderer extends ItemInHandLayer<@NotNull AvatarRenderState, @NotNull PlayerModel> {

	private final ItemInHandRenderer heldItemRenderer;
	private ItemDisplayContext transformationMode = ItemDisplayContext.FIXED;

    public BackSlotFeatureRenderer(RenderLayerParent<@NotNull AvatarRenderState, @NotNull PlayerModel> context, ItemInHandRenderer heldItemRenderer) {
		super(context);
		this.heldItemRenderer = heldItemRenderer;
	}


	@Override
	public void submit(@NotNull PoseStack matrixStack, @NotNull SubmitNodeCollector orderedRenderCommandQueue, int i, AvatarRenderState armedEntityRenderState, float f, float g) {
		if (armedEntityRenderState instanceof PlayerEntityRenderStateAccess access) {
			Player playerEntity = access.combat_Amenities$getPlayerEntity();
			if (playerEntity != null) {
				this.setVelocityFromPos(playerEntity);
				ItemStack backSlotStack = playerEntity.getInventory().getItem(41);
				if (!backSlotStack.isEmpty()) {
					Item item = backSlotStack.getItem();
					Identifier itemId = BuiltInRegistries.ITEM.getKey(item);
					BackTransformData transformData = BackTransformResourceReloadListener.getTransform(itemId, backSlotStack.getOrDefault(CAComponents.INTEGER_PROPERTY, -1).toString());

					BackTransformData.SecondaryTransformData secondaryTransformData = transformData.secondaryTransforms();
					BackTransformData.TertiaryTransformData tertiaryTransformData = transformData.tertiaryTransforms();

					Identifier secondaryModel = secondaryTransformData.item();
					ItemStack secondaryAppleStack = Items.APPLE.getDefaultInstance();
					secondaryAppleStack.set(DataComponents.ITEM_MODEL, secondaryModel);

					Identifier tertiaryModel = tertiaryTransformData.item();
					ItemStack tertiaryAppleStack = Items.APPLE.getDefaultInstance();
					tertiaryAppleStack.set(DataComponents.ITEM_MODEL, tertiaryModel);

					HumanoidArm arm = armedEntityRenderState.mainArm;
					boolean right = arm == HumanoidArm.RIGHT && !CAConfig.flipBackslotDisplay || arm == HumanoidArm.LEFT && CAConfig.flipBackslotDisplay;

					boolean flip = false;

					if (!right) {
						if (!(item instanceof BlockItem) && !transformData.noFlip()) {
							flip = true;
						}
					}

					if (!secondaryModel.equals(Identifier.parse("null"))) {
						matrixStack.pushPose();
						this.getParentModel().body.translateAndRotate(matrixStack);

						// pivot point
						float pivot = 0.0F;
						matrixStack.translate(0, pivot, 0.125);

						matrixStack.mulPose((new Quaternionf())
								.rotateY(-3.1415927F)
								.rotateX(transformData.sway() * -(6.0F + armedEntityRenderState.capeLean / 2.0F + armedEntityRenderState.capeFlap) * 0.017453292F)
								.rotateZ(-(armedEntityRenderState.capeLean2 / 2.0F * 0.017453292F))
								.rotateY((180.0F - armedEntityRenderState.capeLean2 / 2.0F) * 0.017453292F)
						);

						matrixStack.translate(0, 0.35, 0);

						if (playerEntity.getItemBySlot(EquipmentSlot.CHEST) != ItemStack.EMPTY) {
							matrixStack.translate(0, 0, 0.05F);
						}
						if (armedEntityRenderState.showCape && armedEntityRenderState.skin.cape() != null) {
							matrixStack.translate(0, 0, 0.1);
						}

						if (flip) {
							matrixStack.mulPose(Axis.YP.rotationDegrees(180));
						}

						transformationMode = secondaryTransformData.mode();

						// Dynamic movement
						if (playerEntity instanceof RemotePlayer) {
							applyDynamicMovement(matrixStack, playerEntity, item);
						} else if (playerEntity instanceof LocalPlayer) {
							applyDynamicMovement(matrixStack, playerEntity, item);
						}

						List<Float> translation = secondaryTransformData.translation();
						matrixStack.translate(translation.get(0), translation.get(1), flip ? translation.get(2) : -translation.get(2));
						if (right && (item instanceof BlockItem || transformData.noFlip())) {
							matrixStack.translate(translation.getFirst() * -2, 0, 0);
						}

						List<Float> rotation = secondaryTransformData.rotation();
						matrixStack.mulPose(Axis.XP.rotationDegrees(rotation.get(0)));
						if (flip) {
							matrixStack.mulPose(Axis.XP.rotationDegrees(rotation.getFirst() * -2));
						}
						matrixStack.mulPose(Axis.YP.rotationDegrees(rotation.get(1)));
						matrixStack.mulPose(Axis.ZP.rotationDegrees(rotation.get(2)));
						if (right && (item instanceof BlockItem || transformData.noFlip())) {
							matrixStack.mulPose(Axis.ZP.rotationDegrees(rotation.get(2) * -2));
						}

						List<Float> scale = secondaryTransformData.scale();
						matrixStack.scale(scale.get(0), scale.get(1), scale.get(2));

						heldItemRenderer.renderItem(playerEntity, secondaryAppleStack, transformationMode, matrixStack, orderedRenderCommandQueue, i);
						matrixStack.popPose();
					}

					if (!tertiaryModel.equals(Identifier.parse("null"))) {
						matrixStack.pushPose();
						this.getParentModel().body.translateAndRotate(matrixStack);

						if (flip) {
							matrixStack.mulPose(Axis.YP.rotationDegrees(180));
						}

						matrixStack.mulPose(Axis.ZP.rotationDegrees(180));

						List<Float> translation = tertiaryTransformData.translation();
						matrixStack.translate(translation.get(0), translation.get(1), translation.get(2));

						List<Float> rotation = tertiaryTransformData.rotation();
						matrixStack.mulPose(Axis.XP.rotationDegrees(rotation.get(0)));
						matrixStack.mulPose(Axis.YP.rotationDegrees(rotation.get(1)));
						matrixStack.mulPose(Axis.ZP.rotationDegrees(rotation.get(2)));

						List<Float> scale = tertiaryTransformData.scale();
						matrixStack.scale(scale.get(0), scale.get(1), scale.get(2));

						heldItemRenderer.renderItem(playerEntity, tertiaryAppleStack, transformationMode, matrixStack, orderedRenderCommandQueue, i);
						matrixStack.popPose();
					}

					matrixStack.pushPose();
					this.getParentModel().body.translateAndRotate(matrixStack);

					// pivot point
					float pivot = 0.0F;
					matrixStack.translate(0, pivot, 0.125);

					matrixStack.mulPose((new Quaternionf())
							.rotateY(-3.1415927F)
							.rotateX(transformData.sway() * -(6.0F + armedEntityRenderState.capeLean / 2.0F + armedEntityRenderState.capeFlap) * 0.017453292F)
							.rotateZ(-(armedEntityRenderState.capeLean2 / 2.0F * 0.017453292F))
							.rotateY((180.0F - armedEntityRenderState.capeLean2 / 2.0F) * 0.017453292F)
					);

					matrixStack.translate(0, 0.35, 0);

					if (playerEntity.getItemBySlot(EquipmentSlot.CHEST) != ItemStack.EMPTY) {
						matrixStack.translate(0, 0, 0.05F);
					}
					if (armedEntityRenderState.showCape && armedEntityRenderState.skin.cape() != null) {
						matrixStack.translate(0, 0, 0.1);
					}

					if (flip) {
						matrixStack.mulPose(Axis.YP.rotationDegrees(180));
					}

					transformationMode = transformData.mode();

					// Dynamic movement
					if (playerEntity instanceof RemotePlayer) {
						applyDynamicMovement(matrixStack, playerEntity, item);
					} else if (playerEntity instanceof LocalPlayer) {
						applyDynamicMovement(matrixStack, playerEntity, item);
					}

					List<Float> translation = transformData.translation();
					matrixStack.translate(translation.get(0), translation.get(1), flip ? translation.get(2) : -translation.get(2));
					if (right && (item instanceof BlockItem || transformData.noFlip())) {
						matrixStack.translate(translation.getFirst() * -2, 0, 0);
					}

					List<Float> rotation = transformData.rotation();
					matrixStack.mulPose(Axis.XP.rotationDegrees(rotation.get(0)));
					if (flip) {
						matrixStack.mulPose(Axis.XP.rotationDegrees(rotation.getFirst() * -2));
					}
					matrixStack.mulPose(Axis.YP.rotationDegrees(rotation.get(1)));
					matrixStack.mulPose(Axis.ZP.rotationDegrees(rotation.get(2)));
					if (right && (item instanceof BlockItem || transformData.noFlip())) {
						matrixStack.mulPose(Axis.ZP.rotationDegrees(rotation.get(2) * -2));
					}

					List<Float> scale = transformData.scale();
					matrixStack.scale(scale.get(0), scale.get(1), scale.get(2));

					// Render the item
					heldItemRenderer.renderItem(playerEntity, backSlotStack, transformationMode, matrixStack, orderedRenderCommandQueue, i);

					if (backSlotStack.isEnchanted() && Math.random() > ((100 - CAConfig.enchantmentParticleChance) / 100.0F) && CAConfig.backslotParticles && !Minecraft.getInstance().isPaused()) {
						for (int j = 0; j < 5; j++) {
							Vec3 vec3d = CombatAmenities.matrixToVec(matrixStack);
							double offsetX = 0.7 * (Math.random() - 0.5);
							double offsetY = Math.random();
							double offsetZ = 0.7 * (Math.random() - 0.5);
							playerEntity.level().addParticle(
									ParticleTypes.ENCHANT,
									vec3d.x() + offsetX,
									vec3d.y() + offsetY - 0.5,
									vec3d.z() + offsetZ,
									0, 0, 0
							);
						}
					}

					matrixStack.popPose();
				}
			}
		}
	}

	private static Vec3 startTickPosition;
	public static Vec3 playerVelocity = new Vec3(0, 0, 0);

	private boolean wasOnGroundLastTick = true;
	private final Queue<Float> verticalVelocityHistory = new LinkedList<>();

	private float jiggleIntensity = 0.0F;
	private float jiggleDecay = 0.9F;
	private float jiggleTimer = 0.0F;

	private void applyDynamicMovement(PoseStack matrixStack, Player playerEntity, Item item) {
		int VELOCITY_HISTORY_SIZE = 5;
		if (verticalVelocityHistory.size() >= VELOCITY_HISTORY_SIZE) {
			verticalVelocityHistory.poll();
			verticalVelocityHistory.poll();
			verticalVelocityHistory.poll();
		}

		if (playerEntity instanceof AbstractClientPlayer abstractClientPlayerEntity) {

			verticalVelocityHistory.offer((float) playerVelocity.y);

			if (detectLanding(abstractClientPlayerEntity)) {
				float landingVelocity = Math.abs(verticalVelocityHistory.peek() != null ? verticalVelocityHistory.peek() : 0.0F);
				jiggleIntensity = Mth.clamp(landingVelocity * 10.0F, 5.0F, 50.0F);
				jiggleDecay = 0.9F + Math.min(0.1F * (Minecraft.getInstance().getFps() / 140.0F - 1), 0.075F);
				jiggleTimer = 0.0F;
			}

			if (jiggleIntensity > 0.1F) {
				jiggleTimer += Math.max(0.4F - 0.25F * (Minecraft.getInstance().getFps() / 140.0F - 1), 0.1F);
				float oscillation = (float) Math.sin(jiggleTimer) * jiggleIntensity;
				if (!(item instanceof BlockItem blockItem && blockItem.getBlock() instanceof BannerBlock)) {
					matrixStack.mulPose(Axis.ZP.rotationDegrees(oscillation));
				}

				jiggleIntensity *= jiggleDecay;
			}

			wasOnGroundLastTick = playerEntity.onGround();
		}
	}

	private boolean detectLanding(AbstractClientPlayer playerEntity) {
		if (playerEntity.onGround() && !wasOnGroundLastTick) {
			for (float velocity : verticalVelocityHistory) {
				if (velocity < 0.0F) {
					return true;
				}
			}
		}
		return false;
	}

	private void setVelocityFromPos(Player player) {
		if (startTickPosition == null) {
			startTickPosition = player.position();
		}

		Vec3 endTickPosition = player.position();
		if (!startTickPosition.equals(endTickPosition)) playerVelocity = endTickPosition.subtract(startTickPosition);
		startTickPosition = endTickPosition;
	}
}
