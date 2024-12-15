package net.hollowed.backslot.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.hollowed.backslot.client.ExtendedArrowEntityRenderState;
import net.hollowed.backslot.client.ExtendedPlayerEntityRenderState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.ArrowEntityRenderer;
import net.minecraft.client.render.entity.ProjectileEntityRenderer;
import net.minecraft.client.render.entity.state.ArrowEntityRenderState;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.render.entity.state.ProjectileEntityRenderState;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.SpectralArrowEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.ModelTransformationMode;
import net.minecraft.util.math.RotationAxis;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ArrowEntityRenderer.class)
@Environment(EnvType.CLIENT)
public abstract class ArrowEntityRendererMixin<T extends PersistentProjectileEntity> {

    @Inject(method = "createRenderState()Lnet/minecraft/client/render/entity/state/ArrowEntityRenderState;", at = @At("RETURN"), cancellable = true)
    private void setRenderState(CallbackInfoReturnable<ExtendedArrowEntityRenderState> cir) {
        cir.setReturnValue(new ExtendedArrowEntityRenderState());
    }
}
