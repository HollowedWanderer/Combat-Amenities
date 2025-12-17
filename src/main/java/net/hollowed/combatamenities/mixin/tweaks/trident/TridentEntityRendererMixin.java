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
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.projectile.arrow.ThrownTrident;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;
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
    public void updateRenderState(ThrownTrident tridentEntity, ThrownTridentRenderState tridentEntityRenderState, float f, CallbackInfo ci) {
        if (tridentEntityRenderState instanceof TridentEntityRenderStateAccess access) {
            access.combat_Amenities$setLook(tridentEntity.getViewVector(0));
        }
    }

    @Inject(
        method = "submit(Lnet/minecraft/client/renderer/entity/state/ThrownTridentRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/CameraRenderState;)V",
        at = @At("HEAD"),
        cancellable = true
    )
    public void renderWithItem(
            ThrownTridentRenderState tridentEntityRenderState, PoseStack matrixStack, SubmitNodeCollector orderedRenderCommandQueue, CameraRenderState cameraRenderState, CallbackInfo ci
    ) {
        if (tridentEntityRenderState instanceof TridentEntityRenderStateAccess access) {
            matrixStack.pushPose();

            float multiplier = 0.85F;
            matrixStack.translate(access.combat_Amenities$getLook().multiply(multiplier, multiplier, -multiplier));
            matrixStack.mulPose(Axis.YP.rotationDegrees(tridentEntityRenderState.yRot - 90.0F));
            matrixStack.mulPose(Axis.ZP.rotationDegrees(tridentEntityRenderState.xRot - 45.0F));

            matrixStack.scale(2F, 2F, 1F);

            ItemStack trident = Items.TRIDENT.getDefaultInstance();
            if (tridentEntityRenderState.isFoil) {
                trident.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true);
            }

            ItemStackRenderState stackRenderState = new ItemStackRenderState();
            Minecraft.getInstance().getItemModelResolver().appendItemLayers(stackRenderState, trident, ItemDisplayContext.NONE, Minecraft.getInstance().level, null, 1);
            stackRenderState.submit(matrixStack, orderedRenderCommandQueue, tridentEntityRenderState.lightCoords, OverlayTexture.NO_OVERLAY, tridentEntityRenderState.outlineColor);

            matrixStack.popPose();
            ci.cancel();
        }
    }

    @Override
    public boolean shouldRender(ThrownTrident entity, @NotNull Frustum frustum, double x, double y, double z) {
        return true;
    }
}
