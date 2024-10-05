package net.hollowed.backslot.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class BackslotHudOverlay {

    // Texture identifiers for your GUI elements
    public static final Identifier WIDGETS_TEXTURE = new Identifier("textures/gui/widgets.png");

    public static void init() {
        HudRenderCallback.EVENT.register((drawContext, tickDelta) -> {
            if (!MinecraftClient.getInstance().options.hudHidden) {
                renderBackSlot(drawContext, tickDelta);
            }
        });
    }

    private static void renderBackSlot(DrawContext drawContext, float tickDelta) {
        PlayerEntity playerEntity = MinecraftClient.getInstance().player;
        if (playerEntity != null) {

            // Get the backslot item
            ItemStack backSlotStack = playerEntity.getInventory().getStack(41);

            if (!backSlotStack.isEmpty()) {
                int x = drawContext.getScaledWindowWidth() / 2 + 97; // X position
                int y = drawContext.getScaledWindowHeight() - 4; // Y position

                // Draw the backslot item
                RenderSystem.enableBlend();
                drawContext.drawTexture(WIDGETS_TEXTURE, x + 1, y - 19, 24, 22, 29, 24); // Texture for the base slot
                renderHotbarItem(drawContext, MinecraftClient.getInstance(), x + 4, y - 15, playerEntity, backSlotStack);
            }
        }
    }

    private static void renderHotbarItem(DrawContext context, MinecraftClient client, int x, int y, PlayerEntity player, ItemStack stack) {
        if (stack.isEmpty()) return;

        context.drawItem(player, stack, x, y, 0);
        context.drawItemInSlot(client.textRenderer, stack, x, y);
    }
}