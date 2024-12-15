package net.hollowed.combatamenities.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.hollowed.combatamenities.client.ExtendedArrowEntityRenderState;
import net.minecraft.client.render.entity.ArrowEntityRenderer;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ArrowEntityRenderer.class)
@Environment(EnvType.CLIENT)
public abstract class ArrowEntityRendererMixin<T extends PersistentProjectileEntity> {

    @Inject(method = "createRenderState()Lnet/minecraft/client/render/entity/state/ArrowEntityRenderState;", at = @At("RETURN"), cancellable = true)
    private void setRenderState(CallbackInfoReturnable<ExtendedArrowEntityRenderState> cir) {
        cir.setReturnValue(new ExtendedArrowEntityRenderState());
    }
}
