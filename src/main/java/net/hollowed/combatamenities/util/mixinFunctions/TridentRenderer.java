package net.hollowed.combatamenities.util.mixinFunctions;

import net.hollowed.combatamenities.client.TridentEntityRenderStateAccess;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.state.TridentEntityRenderState;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.RotationAxis;

public class TridentRenderer {

    public static void renderTrident(
            TridentEntityRenderState tridentEntityRenderState,
            MatrixStack matrixStack,
            VertexConsumerProvider vertexConsumerProvider,
            int light,
            TridentEntityRenderStateAccess access
    ) {
        matrixStack.push();

        float multiplier = 0.85F;
        matrixStack.translate(access.combat_Amenities$getLook().multiply(multiplier, multiplier, -multiplier));
        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(tridentEntityRenderState.yaw - 90.0F));
        matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(tridentEntityRenderState.pitch - 45.0F));

        matrixStack.scale(2F, 2F, 1F);

        ItemStack trident = Items.TRIDENT.getDefaultStack();
        if (tridentEntityRenderState.enchanted) {
            trident.set(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true);
        }

        ItemRenderer itemRenderer = MinecraftClient.getInstance().getItemRenderer();
        itemRenderer.renderItem(trident, ItemDisplayContext.NONE, light, OverlayTexture.DEFAULT_UV, matrixStack, vertexConsumerProvider, MinecraftClient.getInstance().world, 0);

        matrixStack.pop();
    }
}
