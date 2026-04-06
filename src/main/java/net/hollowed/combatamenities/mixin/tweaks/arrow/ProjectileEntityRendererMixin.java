package net.hollowed.combatamenities.mixin.tweaks.arrow;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.hollowed.combatamenities.config.CAConfig;
import net.hollowed.combatamenities.util.interfaces.ArrowEntityRenderStateAccess;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.state.ArrowRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.entity.projectile.arrow.Arrow;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import java.util.List;
import java.util.Optional;

@Mixin(ArrowRenderer.class)
@Environment(EnvType.CLIENT)
public abstract class ProjectileEntityRendererMixin<T extends AbstractArrow> {

    @Inject(method = "extractRenderState(Lnet/minecraft/world/entity/projectile/arrow/AbstractArrow;Lnet/minecraft/client/renderer/entity/state/ArrowRenderState;F)V",
            at = @At("HEAD"))
    public void updateRenderState(T persistentProjectileEntity, ArrowRenderState projectileEntityRenderState, float f, CallbackInfo ci) {

        if (CAConfig.itemArrows) {
            if (projectileEntityRenderState instanceof ArrowEntityRenderStateAccess access) {

                ItemStack stack = persistentProjectileEntity.getPickupItemStackOrigin();
                Vec3 look = new Vec3(0, 0, 0);

                if (persistentProjectileEntity instanceof Arrow arrowEntity) {
                    if (arrowEntity.getColor() != -1) {
                        stack = new ItemStack(Items.TIPPED_ARROW);
                        stack.set(DataComponents.POTION_CONTENTS, new PotionContents(Optional.empty(), Optional.of(arrowEntity.getColor()), List.of(), Optional.empty()));
                    }
                    look = arrowEntity.getViewVector(0);
                }

                access.combat_Amenities$setItemStack(stack);
                access.combat_Amenities$setLook(look);
            }
        }
    }

    @Inject(
        method = "submit(Lnet/minecraft/client/renderer/entity/state/ArrowRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/level/CameraRenderState;)V",
        at = @At("HEAD"),
        cancellable = true
    )
    public void renderWithItem(
            ArrowRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, net.minecraft.client.renderer.state.level.CameraRenderState camera, CallbackInfo ci
    ) {
        if (CAConfig.itemArrows) {
            if (state instanceof ArrowEntityRenderStateAccess access) {
                poseStack.pushPose();

                float multiplier = 0.25F;

                poseStack.translate(access.combat_Amenities$getLook().multiply(multiplier, multiplier, -multiplier));

                poseStack.mulPose(Axis.YP.rotationDegrees(state.yRot - 90.0F));
                poseStack.mulPose(Axis.ZP.rotationDegrees(state.xRot - 45.0F));

                ItemStack itemStack = access.combat_Amenities$getItemStack();
                ItemStackRenderState stackRenderState = new ItemStackRenderState();
                Minecraft.getInstance().getItemModelResolver().appendItemLayers(stackRenderState, itemStack, ItemDisplayContext.NONE, Minecraft.getInstance().level, null, 1);
                stackRenderState.submit(poseStack, submitNodeCollector, state.lightCoords, OverlayTexture.NO_OVERLAY, state.outlineColor);

                poseStack.popPose();
                ci.cancel();
            }
        }
    }
}
