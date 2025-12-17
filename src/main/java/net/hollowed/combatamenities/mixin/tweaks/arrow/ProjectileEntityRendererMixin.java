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
import net.minecraft.client.renderer.state.CameraRenderState;
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

                // Default item stack to what the entity provides
                if (persistentProjectileEntity instanceof Arrow arrowEntity) {
                    if (arrowEntity.getColor() != -1) {
                        stack = new ItemStack(Items.TIPPED_ARROW);
                        stack.set(DataComponents.POTION_CONTENTS, new PotionContents(Optional.empty(), Optional.of(arrowEntity.getColor()), List.of(), Optional.empty()));
                    }
                    look = arrowEntity.getViewVector(0);
                }

                // Pass the dynamically constructed item stack
                access.combat_Amenities$setItemStack(stack);
                access.combat_Amenities$setLook(look);
            }
        }
    }

    @Inject(
        method = "submit(Lnet/minecraft/client/renderer/entity/state/ArrowRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/CameraRenderState;)V",
        at = @At("HEAD"),
        cancellable = true
    )
    public void renderWithItem(
            ArrowRenderState projectileEntityRenderState, PoseStack matrixStack, SubmitNodeCollector orderedRenderCommandQueue, CameraRenderState cameraRenderState, CallbackInfo ci       // Use Object because the generic types are not accessible directly
    ) {
        if (CAConfig.itemArrows) {
            if (projectileEntityRenderState instanceof ArrowEntityRenderStateAccess access) {
                // Push the matrix stack for transformations
                matrixStack.pushPose();

                float multiplier = 0.25F;

                // Apply translation
                matrixStack.translate(access.combat_Amenities$getLook().multiply(multiplier, multiplier, -multiplier));

                // Apply rotations for the projectile's orientation
                matrixStack.mulPose(Axis.YP.rotationDegrees(projectileEntityRenderState.yRot - 90.0F));
                matrixStack.mulPose(Axis.ZP.rotationDegrees(projectileEntityRenderState.xRot - 45.0F));

                ItemStack itemStack = access.combat_Amenities$getItemStack();
                ItemStackRenderState stackRenderState = new ItemStackRenderState();
                Minecraft.getInstance().getItemModelResolver().appendItemLayers(stackRenderState, itemStack, ItemDisplayContext.NONE, Minecraft.getInstance().level, null, 1);
                stackRenderState.submit(matrixStack, orderedRenderCommandQueue, projectileEntityRenderState.lightCoords, OverlayTexture.NO_OVERLAY, projectileEntityRenderState.outlineColor);

                // Pop the matrix stack
                matrixStack.popPose();

                // Cancel the original rendering
                ci.cancel();
            }
        }
    }
}
