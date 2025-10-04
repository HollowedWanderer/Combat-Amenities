package net.hollowed.combatamenities.mixin.tweaks.trident;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.hollowed.combatamenities.util.interfaces.TridentEntityRenderStateAccess;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.TridentEntityRenderer;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.client.render.entity.state.TridentEntityRenderState;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.RotationAxis;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TridentEntityRenderer.class)
@Environment(EnvType.CLIENT)
public abstract class TridentEntityRendererMixin extends EntityRenderer<TridentEntity, TridentEntityRenderState> {

    protected TridentEntityRendererMixin(EntityRendererFactory.Context context) {
        super(context);
    }

    @Inject(method = "updateRenderState(Lnet/minecraft/entity/projectile/TridentEntity;Lnet/minecraft/client/render/entity/state/TridentEntityRenderState;F)V", at = @At("HEAD"))
    public void updateRenderState(TridentEntity tridentEntity, TridentEntityRenderState tridentEntityRenderState, float f, CallbackInfo ci) {
        if (tridentEntityRenderState instanceof TridentEntityRenderStateAccess access) {
            access.combat_Amenities$setLook(tridentEntity.getRotationVec(0));
        }
    }

    @Inject(
        method = "render(Lnet/minecraft/client/render/entity/state/TridentEntityRenderState;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/command/OrderedRenderCommandQueue;Lnet/minecraft/client/render/state/CameraRenderState;)V",
        at = @At("HEAD"),
        cancellable = true
    )
    public void renderWithItem(
            TridentEntityRenderState tridentEntityRenderState, MatrixStack matrixStack, OrderedRenderCommandQueue orderedRenderCommandQueue, CameraRenderState cameraRenderState, CallbackInfo ci
    ) {
        if (tridentEntityRenderState instanceof TridentEntityRenderStateAccess access) {
            matrixStack.push();

            float multiplier = 0.85F;
            matrixStack.translate(access.combat_Amenities$getLook().multiply(multiplier, multiplier, -multiplier));
            matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(tridentEntityRenderState.yaw - 90.0F));
            matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(tridentEntityRenderState.pitch - 45.0F));

            matrixStack.scale(2F, 2F, 1F);

            ItemStack trident = Items.TRIDENT.getDefaultStack();
            if (tridentEntityRenderState.enchanted) {
                trident.set(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true);
            }

            ItemRenderState stackRenderState = new ItemRenderState();
            MinecraftClient.getInstance().getItemModelManager().update(stackRenderState, trident, ItemDisplayContext.NONE, MinecraftClient.getInstance().world, null, 1);
            stackRenderState.render(matrixStack, orderedRenderCommandQueue, tridentEntityRenderState.light, OverlayTexture.DEFAULT_UV, tridentEntityRenderState.outlineColor);

            matrixStack.pop();
            ci.cancel();
        }
    }

    @Override
    public boolean shouldRender(TridentEntity entity, Frustum frustum, double x, double y, double z) {
        return true;
    }
}
