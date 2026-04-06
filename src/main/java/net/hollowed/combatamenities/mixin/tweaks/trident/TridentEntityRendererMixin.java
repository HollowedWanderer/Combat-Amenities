package net.hollowed.combatamenities.mixin.tweaks.trident;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.hollowed.combatamenities.util.interfaces.TridentEntityRenderStateAccess;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.ThrownTridentRenderer;
import net.minecraft.client.renderer.entity.state.ThrownTridentRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.projectile.arrow.ThrownTrident;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ThrownTridentRenderer.class)
@Environment(EnvType.CLIENT)
public abstract class TridentEntityRendererMixin extends EntityRenderer<@NotNull ThrownTrident, @NotNull ThrownTridentRenderState> {

    protected TridentEntityRendererMixin(EntityRendererProvider.Context context) {
        super(context);
    }

    @Inject(method = "extractRenderState(Lnet/minecraft/world/entity/projectile/arrow/ThrownTrident;Lnet/minecraft/client/renderer/entity/state/ThrownTridentRenderState;F)V", at = @At("HEAD"))
    public void updateRenderState(ThrownTrident entity, ThrownTridentRenderState state, float partialTicks, CallbackInfo ci) {
        if (state instanceof TridentEntityRenderStateAccess access) {
            access.combat_Amenities$setEntity(entity);
        }
    }

    @Inject(
        method = "submit(Lnet/minecraft/client/renderer/entity/state/ThrownTridentRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/level/CameraRenderState;)V",
        at = @At("HEAD"),
        cancellable = true
    )
    public void renderWithItem(
            ThrownTridentRenderState state, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, net.minecraft.client.renderer.state.level.CameraRenderState camera, CallbackInfo ci
    ) {
        if (state instanceof TridentEntityRenderStateAccess access) {
            poseStack.pushPose();

            float multiplier = 0.5F;
            poseStack.translate(access.combat_Amenities$getEntity().getViewVector(0).multiply(multiplier, multiplier, -multiplier));

            poseStack.translate(-0.3, 0, 0);

            Vec3 pivot = new Vec3(0.3, 0, 0);
            poseStack.translate(pivot);

            poseStack.mulPose(Axis.YP.rotationDegrees(state.yRot - 180.0F));
            poseStack.mulPose(Axis.XP.rotationDegrees(state.xRot - 65F));

            poseStack.translate(pivot.multiply(-1, -1, -1));

            poseStack.scale(1F, 1F, 1F);

            ItemStack trident = Items.TRIDENT.getDefaultInstance();
            if (state.isFoil) {
                trident.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true);
            }

            ItemStackRenderState stackRenderState = new ItemStackRenderState();
            Minecraft.getInstance().getItemModelResolver().appendItemLayers(stackRenderState, trident, ItemDisplayContext.FIRST_PERSON_RIGHT_HAND, Minecraft.getInstance().level, null, 1);
            stackRenderState.submit(poseStack, submitNodeCollector, state.lightCoords, OverlayTexture.NO_OVERLAY, state.outlineColor);

            poseStack.popPose();
            ci.cancel();
        }
    }

    @Override
    public boolean shouldRender(@NonNull ThrownTrident entity, @NotNull Frustum frustum, double x, double y, double z) {
        return true;
    }
}
