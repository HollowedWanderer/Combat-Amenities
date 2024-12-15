package net.hollowed.combatamenities.mixin;

import net.hollowed.combatamenities.client.ExtendedPlayerEntityRenderState;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntityRenderer.class)
public class PlayerEntityRendererMixin {

    @Inject(method = "updateRenderState(Lnet/minecraft/client/network/AbstractClientPlayerEntity;Lnet/minecraft/client/render/entity/state/PlayerEntityRenderState;F)V", at = @At("HEAD"))
    public void updateRenderState(AbstractClientPlayerEntity abstractClientPlayerEntity, PlayerEntityRenderState playerEntityRenderState, float f, CallbackInfo ci) {
        ExtendedPlayerEntityRenderState.setPlayerEntity(abstractClientPlayerEntity);
    }

    @Inject(method = "createRenderState()Lnet/minecraft/client/render/entity/state/PlayerEntityRenderState;", at = @At("RETURN"), cancellable = true)
    private void setRenderState(CallbackInfoReturnable<PlayerEntityRenderState> cir) {
        cir.setReturnValue(new ExtendedPlayerEntityRenderState());
    }
}
