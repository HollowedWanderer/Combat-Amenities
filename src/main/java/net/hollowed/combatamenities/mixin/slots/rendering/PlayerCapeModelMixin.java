package net.hollowed.combatamenities.mixin.slots.rendering;

import net.hollowed.combatamenities.util.items.ModComponents;
import net.hollowed.combatamenities.util.json.BackTransformData;
import net.hollowed.combatamenities.util.json.BackTransformResourceReloadListener;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.PlayerCapeModel;
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
public class PlayerCapeModelMixin {

    @Shadow @Final private ModelPart cape;

    @Inject(method = "setAngles(Lnet/minecraft/client/render/entity/state/PlayerEntityRenderState;)V", at = @At("HEAD"), cancellable = true)
    private void injectSetAngles(PlayerEntityRenderState playerEntityRenderState, CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player != null) {
            this.cape.resetTransform();

            ItemStack stack = client.player.getInventory().getStack(41);
            BackTransformData transformData = BackTransformResourceReloadListener.getTransform(Registries.ITEM.getId(stack.getItem()), stack.getOrDefault(ModComponents.INTEGER_PROPERTY, -1).toString());

            // Calculate the adjusted cape rotation
            float adjustedXRotation = transformData.sway() * (6.0F + playerEntityRenderState.field_53537 / 2.0F + playerEntityRenderState.field_53536)
                    * 0.017453292F;
            float zRotation = playerEntityRenderState.field_53538 / 2.0F * 0.017453292F;
            float yRotation = (180.0F - playerEntityRenderState.field_53538 / 2.0F) * 0.017453292F;

            Quaternionf capeRotation = new Quaternionf()
                    .rotateY(-3.1415927F)
                    .rotateX(adjustedXRotation)
                    .rotateZ(zRotation)
                    .rotateY(yRotation);
            this.cape.rotate(capeRotation);

            ci.cancel(); // Cancel further modifications to ensure consistent behavior
        }
    }
}
