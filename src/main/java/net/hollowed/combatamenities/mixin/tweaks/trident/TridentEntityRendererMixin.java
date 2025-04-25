package net.hollowed.combatamenities.mixin.tweaks.trident;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.hollowed.combatamenities.util.interfaces.TridentEntityRenderStateAccess;
import net.hollowed.combatamenities.util.mixinFunctions.TridentRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.TridentEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.projectile.TridentEntity;
import net.minecraft.client.render.entity.state.TridentEntityRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TridentEntityRenderer.class)
@Environment(EnvType.CLIENT)
public class TridentEntityRendererMixin {

    @Inject(method = "updateRenderState(Lnet/minecraft/entity/projectile/TridentEntity;Lnet/minecraft/client/render/entity/state/TridentEntityRenderState;F)V", at = @At("HEAD"))
    public void updateRenderState(TridentEntity tridentEntity, TridentEntityRenderState tridentEntityRenderState, float f, CallbackInfo ci) {
        if (tridentEntityRenderState instanceof TridentEntityRenderStateAccess access) {
            access.combat_Amenities$setLook(tridentEntity.getRotationVec(0));
        }
    }

    @Inject(
        method = "render(Lnet/minecraft/client/render/entity/state/TridentEntityRenderState;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
        at = @At("HEAD"),
        cancellable = true
    )
    public void renderWithItem(
            TridentEntityRenderState tridentEntityRenderState,
            MatrixStack matrixStack,
            VertexConsumerProvider vertexConsumerProvider,
            int light,
            CallbackInfo ci
    ) {
        if (tridentEntityRenderState instanceof TridentEntityRenderStateAccess access) {

            TridentRenderer.renderTrident(tridentEntityRenderState, matrixStack, vertexConsumerProvider, light, access);

            // Cancel the original render call
            ci.cancel();
        }
    }
}
