package net.hollowed.combatamenities.mixin.items;

import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ItemRenderer.class)
public interface ItemRendererAccessor {
    @Invoker("renderBakedItemModel")
    void invokeRenderBakedItemModel(BakedModel model, int[] tints, int light, int overlay, MatrixStack matrices, VertexConsumer vertexConsumer);
}
