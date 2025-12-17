package net.hollowed.combatamenities.mixin.slots.rendering;

import net.hollowed.combatamenities.renderer.BackSlotFeatureRenderer;
import net.hollowed.combatamenities.renderer.BeltSlotFeatureRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AvatarRenderer.class)
public abstract class PlayerFeaturesMixin extends LivingEntityRenderer<@NotNull AbstractClientPlayer, @NotNull AvatarRenderState, @NotNull PlayerModel> {

    public PlayerFeaturesMixin(EntityRendererProvider.Context ctx, PlayerModel model, float shadowRadius) {
        super(ctx, model, shadowRadius);
    }

    @Inject(method = "<init>", at = @At("CTOR_HEAD"))
    private void addCustomFeature(EntityRendererProvider.Context ctx, boolean slim, CallbackInfo ci) {
        ItemInHandRenderer heldItemRenderer = Minecraft.getInstance().getEntityRenderDispatcher().getItemInHandRenderer();
        this.addLayer(new BackSlotFeatureRenderer(this, heldItemRenderer));
        this.addLayer(new BeltSlotFeatureRenderer(this, heldItemRenderer));
    }
}