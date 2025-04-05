package net.hollowed.combatamenities.mixin;

import net.hollowed.combatamenities.util.ModComponents;
import net.hollowed.combatamenities.util.TransformData;
import net.hollowed.combatamenities.util.TransformResourceReloadListener;
import net.minecraft.block.BannerBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.PlayerCapeModel;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import org.joml.Quaternionf;
import org.joml.Vector3f;
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
            TransformData data = TransformResourceReloadListener.getTransform(Registries.ITEM.getId(stack.getItem()), stack.getOrDefault(ModComponents.INTEGER_PROPERTY, -1).toString());

            float bannerMultiplier = 0.4F;
            if (stack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof BannerBlock) {
                bannerMultiplier = 1.0F;
            }

            float backslotMultiplier = 1.0F;
            if (!stack.isEmpty()) {
                backslotMultiplier = data.sway() * bannerMultiplier * 0.9F;
                this.cape.moveOrigin(new Vector3f(0.01F, 0F, -0.25F));
            }

            // Calculate the adjusted cape rotation
            float adjustedXRotation = (6.0F + playerEntityRenderState.field_53537 / 2.0F + playerEntityRenderState.field_53536)
                    * 0.017453292F * backslotMultiplier;
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
