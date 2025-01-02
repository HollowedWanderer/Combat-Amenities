package net.hollowed.combatamenities.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.hollowed.combatamenities.CombatAmenities;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class BackslotHudOverlay {

    private static final Identifier WIDGETS_TEXTURE = Identifier.of("textures/gui/sprites/hud/hotbar_offhand_left.png");
    private static final int xOffset = CombatAmenities.CONFIG.backslotX; // Default position
    private static final int yOffset = CombatAmenities.CONFIG.backslotY;

    // Track the previous item in the back slot for animation
    private static ItemStack lastBackSlotStack = ItemStack.EMPTY;
    private static ItemStack lastBeltSlotStack = ItemStack.EMPTY;
    private static int animationTicks = 0; // Animation timer
    private static final int ANIMATION_DURATION = 28; // Duration of the stretch animation
    private static int animationTicks1 = 0; // Animation timer

    public static void init() {
        HudRenderCallback.EVENT.register((drawContext, tickDelta) -> {
            if (!MinecraftClient.getInstance().options.hudHidden) {
                renderBackSlot(drawContext);
                renderBeltSlot(drawContext);
            }
        });
    }

    private static void renderBeltSlot(DrawContext drawContext) {
        PlayerEntity playerEntity = MinecraftClient.getInstance().player;
        if (playerEntity != null) {
            // Get the belt slot item
            ItemStack backSlotStack = playerEntity.getInventory().getStack(42);

            // Check if the belt slot item has changed
            if (!ItemStack.areEqual(backSlotStack, lastBeltSlotStack)) {
                lastBeltSlotStack = backSlotStack.copy();
                animationTicks1 = ANIMATION_DURATION; // Reset animation timer
            }

            if (!backSlotStack.isEmpty()) {
                final int x = getBeltX(drawContext);
                int y = drawContext.getScaledWindowHeight() - yOffset - 4;

                RenderSystem.enableBlend();

                // Adjust position and scale based on animation ticks
                float scaleY = 1.0f;
                float scaleX = 1.0f;
                if (animationTicks1 > 0) {
                    float progress = (float) animationTicks1 / ANIMATION_DURATION;
                    scaleX = 0.5f + 0.5f * (1.0f - progress); // Shrink horizontally
                    scaleY = 1.75f - 0.75f * (1.0f - progress); // Stretch vertically
                    animationTicks1--; // Decrease animation timer
                }

                drawContext.drawTexture(
                        RenderLayer::getGuiTextured,
                        WIDGETS_TEXTURE,
                        x + 1, y - 19,
                        0, 0, 22, 23, 29, 24 // Texture coordinates and dimensions
                );

                // Render the back slot item
                renderHotbarItem(drawContext, MinecraftClient.getInstance(), x + 4, y - 15, playerEntity, backSlotStack, scaleX, scaleY);
            }
        }
    }

    private static void renderBackSlot(DrawContext drawContext) {
        PlayerEntity playerEntity = MinecraftClient.getInstance().player;
        if (playerEntity != null) {
            // Get the backslot item
            ItemStack backSlotStack = playerEntity.getInventory().getStack(41);

            // Check if the back slot item has changed
            if (!ItemStack.areEqual(backSlotStack, lastBackSlotStack)) {
                lastBackSlotStack = backSlotStack.copy();
                animationTicks = ANIMATION_DURATION; // Reset animation timer
            }

            if (!backSlotStack.isEmpty()) {
                final int x = getX(drawContext);
                int y = drawContext.getScaledWindowHeight() - yOffset - 4; // Y position remains the same

                RenderSystem.enableBlend();

                // Adjust position and scale based on animation ticks
                float scaleY = 1.0f;
                float scaleX = 1.0f;
                if (animationTicks > 0) {
                    float progress = (float) animationTicks / ANIMATION_DURATION;
                    scaleX = 0.5f + 0.5f * (1.0f - progress); // Shrink horizontally
                    scaleY = 1.75f - 0.75f * (1.0f - progress); // Stretch vertically
                    animationTicks--; // Decrease animation timer
                }

                drawContext.drawTexture(
                        RenderLayer::getGuiTextured,
                        WIDGETS_TEXTURE,
                        x + 1, y - 19,
                        0, 0, 22, 23, 29, 24 // Texture coordinates and dimensions
                );

                // Render the back slot item
                renderHotbarItem(drawContext, MinecraftClient.getInstance(), x + 4, y - 15, playerEntity, backSlotStack, scaleX, scaleY);
            }
        }
    }

    private static void renderHotbarItem(DrawContext context, MinecraftClient client, int x, int y, PlayerEntity player, ItemStack stack, float scaleX, float scaleY) {
        if (stack.isEmpty()) return;

        // Push the current transformation matrix
        context.getMatrices().push();

        // Translate to the center of the item slot
        float scaledX = x + 8; // Center the item
        float scaledY = y + 10;

        // Apply the scaling effect
        context.getMatrices().translate(scaledX, scaledY, 0);
        context.getMatrices().scale(scaleX, scaleY, 1.0f);
        context.getMatrices().translate(-scaledX, -scaledY, 0); // Undo translation after scaling

        // Render the item
        context.drawItem(player, stack, x, y, 0);

        // Render the item overlay (e.g., stack count)
        context.drawStackOverlay(client.textRenderer, stack, x, y);

        // Pop the transformation matrix to reset transformations
        context.getMatrices().pop();
    }


    private static int getX(DrawContext drawContext) {
        boolean isLeftHanded = MinecraftClient.getInstance().options.getMainArm().getValue().equals(Arm.LEFT);

        // Calculate positions based on hand preference
        int x;
        if (isLeftHanded) {
            x = drawContext.getScaledWindowWidth() / 2 - xOffset - 120; // Position on the left of the hotbar
        } else {
            x = drawContext.getScaledWindowWidth() / 2 + xOffset + 97; // Position on the right of the hotbar
        }
        return x;
    }

    private static int getBeltX(DrawContext drawContext) {
        boolean isLeftHanded = MinecraftClient.getInstance().options.getMainArm().getValue().equals(Arm.LEFT);

        // Calculate positions based on hand preference
        int x;
        if (isLeftHanded) {
            x = drawContext.getScaledWindowWidth() / 2 - xOffset - 143; // Position on the left of the hotbar
        } else {
            x = drawContext.getScaledWindowWidth() / 2 + xOffset + 120; // Position on the right of the hotbar
        }
        return x;
    }
}