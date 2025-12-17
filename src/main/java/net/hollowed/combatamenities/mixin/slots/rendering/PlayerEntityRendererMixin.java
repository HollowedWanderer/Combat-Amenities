package net.hollowed.combatamenities.mixin.slots.rendering;

import net.hollowed.combatamenities.util.interfaces.PlayerEntityRenderStateAccess;
import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.world.entity.Avatar;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AvatarRenderer.class)
public class PlayerEntityRendererMixin {



    @Inject(method = "extractRenderState(Lnet/minecraft/world/entity/Avatar;Lnet/minecraft/client/renderer/entity/state/AvatarRenderState;F)V", at = @At("HEAD"))
    public void updateRenderState(Avatar playerLikeEntity, AvatarRenderState playerEntityRenderState, float f, CallbackInfo ci) {
        if (playerEntityRenderState instanceof PlayerEntityRenderStateAccess access && playerLikeEntity instanceof Player entity) {
            access.combat_Amenities$setPlayerEntity(entity);
        }
    }
}
