package net.hollowed.combatamenities.mixin.slots.rendering;

import net.hollowed.combatamenities.util.items.CAComponents;
import net.hollowed.combatamenities.util.json.BackTransformData;
import net.hollowed.combatamenities.util.json.BackTransformResourceReloadListener;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.PlayerCapeModel;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerCapeModel.class)
public abstract class PlayerCapeModelMixin extends PlayerEntityModel {

    @Shadow @Final private ModelPart cape;

    public PlayerCapeModelMixin(ModelPart root, boolean thinArms) {
        super(root, thinArms);
    }

    @Inject(method = "setAngles(Lnet/minecraft/client/render/entity/state/PlayerEntityRenderState;)V", at = @At("HEAD"), cancellable = true)
    private void injectSetAngles(PlayerEntityRenderState playerEntityRenderState, CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player != null) {
            ItemStack stack = client.player.getInventory().getStack(41);
            BackTransformData transformData = BackTransformResourceReloadListener.getTransform(Registries.ITEM.getId(stack.getItem()), stack.getOrDefault(CAComponents.INTEGER_PROPERTY, -1).toString());
            float sway = 1.0F;
            if (!stack.isEmpty()) {
                sway = transformData.sway();
            }

            if (sway != 1.0F) {
                super.setAngles(playerEntityRenderState);
                this.cape.rotate((new Quaternionf()).rotateY(-(float) Math.PI).rotateX(sway * (6.0F + playerEntityRenderState.field_53537 / 2.0F + playerEntityRenderState.field_53536) * ((float) Math.PI / 180F)).rotateZ(playerEntityRenderState.field_53538 / 2.0F * ((float) Math.PI / 180F)).rotateY((180.0F - playerEntityRenderState.field_53538 / 2.0F) * ((float) Math.PI / 180F)));
                ci.cancel();
            }
        }
    }
}
