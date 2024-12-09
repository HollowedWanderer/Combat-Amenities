package net.hollowed.backslot.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

import java.util.function.Function;

@Environment(EnvType.CLIENT)
public class BackslotHudOverlay {



    // Texture identifiers for your GUI elements
    public static final Identifier WIDGETS_TEXTURE = Identifier.of("textures/gui/sprites/hud/hotbar_offhand_left.png");

    public static void init() {
        HudRenderCallback.EVENT.register((drawContext, tickDelta) -> {
            if (!MinecraftClient.getInstance().options.hudHidden) {
                renderBackSlot(drawContext);
            }
        });
    }

    private static void renderBackSlot(DrawContext drawContext) {
        PlayerEntity playerEntity = MinecraftClient.getInstance().player;
        if (playerEntity != null) {

            // Get the backslot item
            ItemStack backSlotStack = playerEntity.getInventory().getStack(41);

            if (!backSlotStack.isEmpty()) {
                int x = drawContext.getScaledWindowWidth() / 2 + 97; // X position
                int y = drawContext.getScaledWindowHeight() - 4; // Y position

                // Draw the backslot item
                RenderSystem.enableBlend();

                // Render the texture
                drawContext.drawTexture(
                        RenderLayer::getGuiTextured,
                        WIDGETS_TEXTURE,
                        x + 1, y - 19,
                        0, 0, 22, 23, 29, 24 // Texture coordinates and dimensions
                );
                renderHotbarItem(drawContext, MinecraftClient.getInstance(), x + 4, y - 15, playerEntity, backSlotStack);
            }
        }
    }

    private static void renderHotbarItem(DrawContext context, MinecraftClient client, int x, int y, PlayerEntity player, ItemStack stack) {
        if (stack.isEmpty()) return;

        context.drawItem(player, stack, x, y, 0);
        context.drawStackOverlay(client.textRenderer, stack, x, y);
    }
}