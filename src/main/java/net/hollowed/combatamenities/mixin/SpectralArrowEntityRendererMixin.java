package net.hollowed.combatamenities.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.hollowed.combatamenities.client.ExtendedArrowEntityRenderState;
import net.minecraft.client.render.entity.SpectralArrowEntityRenderer;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SpectralArrowEntityRenderer.class)
@Environment(EnvType.CLIENT)
public abstract class SpectralArrowEntityRendererMixin<T extends PersistentProjectileEntity> {

    @Inject(method = "createRenderState()Lnet/minecraft/client/render/entity/state/EntityRenderState;", at = @At("RETURN"), cancellable = true)
    private void setRenderState(CallbackInfoReturnable<ExtendedArrowEntityRenderState> cir) {
        cir.setReturnValue(new ExtendedArrowEntityRenderState());
    }
}
