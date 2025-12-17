package net.hollowed.combatamenities.mixin.slots.rendering;

import net.hollowed.combatamenities.util.items.CAComponents;
import net.hollowed.combatamenities.util.json.BackTransformData;
import net.hollowed.combatamenities.util.json.BackTransformResourceReloadListener;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.player.PlayerCapeModel;
import net.minecraft.client.model.player.PlayerModel;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerCapeModel.class)
public abstract class PlayerCapeModelMixin extends PlayerModel {

    @Shadow @Final private ModelPart cape;

    public PlayerCapeModelMixin(ModelPart root, boolean thinArms) {
        super(root, thinArms);
    }

    @Inject(method = "setupAnim(Lnet/minecraft/client/renderer/entity/state/AvatarRenderState;)V", at = @At("HEAD"), cancellable = true)
    private void injectSetAngles(AvatarRenderState playerEntityRenderState, CallbackInfo ci) {
        Minecraft client = Minecraft.getInstance();
        if (client.player != null) {
            ItemStack stack = client.player.getInventory().getItem(41);
            BackTransformData transformData = BackTransformResourceReloadListener.getTransform(BuiltInRegistries.ITEM.getKey(stack.getItem()), stack.getOrDefault(CAComponents.INTEGER_PROPERTY, -1).toString());
            float sway = 1.0F;
            if (!stack.isEmpty()) {
                sway = transformData.sway();
            }

            if (sway != 1.0F) {
                super.setupAnim(playerEntityRenderState);
                this.cape.rotateBy((new Quaternionf()).rotateY(-(float) Math.PI).rotateX(sway * (6.0F + playerEntityRenderState.capeLean / 2.0F + playerEntityRenderState.capeFlap) * ((float) Math.PI / 180F)).rotateZ(playerEntityRenderState.capeLean2 / 2.0F * ((float) Math.PI / 180F)).rotateY((180.0F - playerEntityRenderState.capeLean2 / 2.0F) * ((float) Math.PI / 180F)));
                ci.cancel();
            }
        }
    }
}
