package net.hollowed.combatamenities.mixin.slots.rendering;

import com.mojang.blaze3d.opengl.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.hollowed.combatamenities.config.CAConfig;
import net.hollowed.combatamenities.util.items.CAComponents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public abstract class HudRendererMixin {

    @Shadow @Final private MinecraftClient client;
    @Unique
    private static final Identifier WIDGETS_TEXTURE = Identifier.of("textures/gui/sprites/hud/hotbar_offhand_left.png");

    @Unique
    private static ItemStack lastBackSlotStack = ItemStack.EMPTY;
    @Unique
    private static ItemStack lastBeltSlotStack = ItemStack.EMPTY;
    @Unique
    private int animationTicks = 0;

    @Inject(method = "tick()V", at = @At("HEAD"))
    private void tick(CallbackInfo ci) {
        if (animationTicks > 0) {
            animationTicks--;
        }
    }

    @Inject(method = "renderHotbar", at = @At("TAIL"))
    public void renderHotbar(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        renderBackSlot(context, tickCounter);
        renderBeltSlot(context, tickCounter);
    }

    @Unique
    private void renderBeltSlot(DrawContext drawContext, RenderTickCounter tickCounter) {
        PlayerEntity playerEntity = MinecraftClient.getInstance().player;
        if (playerEntity != null) {
            ItemStack beltSlotStack = playerEntity.getInventory().getStack(42);

            if (!ItemStack.areEqual(beltSlotStack, lastBeltSlotStack) && animationTicks == 0) {
                lastBeltSlotStack = beltSlotStack.copy();
                if (beltSlotStack.getOrDefault(CAComponents.STRING_PROPERTY, "").equals("bob5")) {
                    animationTicks = 5;
                }
            }

            if (beltSlotStack.getOrDefault(CAComponents.STRING_PROPERTY, "").equals("bob5")) {
                beltSlotStack.remove(CAComponents.STRING_PROPERTY);
            }

            if (!beltSlotStack.isEmpty()) {
                final int x = getBeltX(drawContext);
                int y = drawContext.getScaledWindowHeight() - CAConfig.backslotY - 4;

                RenderSystem.assertOnRenderThread();
                GlStateManager._enableBlend();

                drawContext.drawTexture(
                        RenderPipelines.GUI_TEXTURED,
                        WIDGETS_TEXTURE,
                        x + 1, y - 19,
                        0, 0, 22, 23, 29, 24
                );

                // Render the back slot item
                renderItem(drawContext, x + 4, y - 15, tickCounter, playerEntity, beltSlotStack);
            }
        }
    }

    @Unique
    private void renderBackSlot(DrawContext drawContext, RenderTickCounter tickCounter) {
        PlayerEntity playerEntity = MinecraftClient.getInstance().player;
        if (playerEntity != null) {
            ItemStack backSlotStack = playerEntity.getInventory().getStack(41);

            if (!ItemStack.areEqual(backSlotStack, lastBackSlotStack) && animationTicks == 0) {
                lastBackSlotStack = backSlotStack.copy();
                if (backSlotStack.getOrDefault(CAComponents.STRING_PROPERTY, "").equals("bob5")) {
                    animationTicks = 5;
                }
            }

            if (backSlotStack.getOrDefault(CAComponents.STRING_PROPERTY, "").equals("bob5")) {
                backSlotStack.remove(CAComponents.STRING_PROPERTY);
            }

            if (!backSlotStack.isEmpty()) {
                final int x = getX(drawContext);
                int y = drawContext.getScaledWindowHeight() - CAConfig.backslotY - 4;

                RenderSystem.assertOnRenderThread();
                GlStateManager._disableBlend();

                drawContext.drawTexture(
                        RenderPipelines.GUI_TEXTURED,
                        WIDGETS_TEXTURE,
                        x + 1, y - 19,
                        0, 0, 22, 23, 29, 24
                );

                // Render the back slot item
                renderItem(drawContext, x + 4, y - 15, tickCounter, playerEntity, backSlotStack);
            }
        }
    }

    @Unique
    private void renderItem(DrawContext context, int x, int y, RenderTickCounter tickCounter, PlayerEntity player, ItemStack stack) {
        if (!stack.isEmpty()) {
            float f = animationTicks - tickCounter.getTickProgress(false);
            if (f > 0.0F) {
                float g = 1.0F + f / 5.0F;
                context.getMatrices().pushMatrix();
                context.getMatrices().translate(x + 8, y + 12);
                context.getMatrices().scale(1.0F / g, (g + 1.0F) / 2.0F);
                context.getMatrices().translate(-(x + 8), -(y + 12));
            }

            context.drawItem(player, stack, x, y, 1);
            if (f > 0.0F) {
                context.getMatrices().popMatrix();
            }

            context.drawStackOverlay(this.client.textRenderer, stack, x, y);
        }
    }

    @Unique
    private static int getX(DrawContext drawContext) {
        boolean isLeftHanded = MinecraftClient.getInstance().options.getMainArm().getValue().equals(Arm.LEFT);

        int x;
        if (isLeftHanded) {
            x = drawContext.getScaledWindowWidth() / 2 - CAConfig.backslotX - 120;
        } else {
            x = drawContext.getScaledWindowWidth() / 2 + CAConfig.backslotX + 97;
        }
        return x;
    }

    @Unique
    private static int getBeltX(DrawContext drawContext) {
        boolean isLeftHanded = MinecraftClient.getInstance().options.getMainArm().getValue().equals(Arm.LEFT);

        int x;
        if (isLeftHanded) {
            x = drawContext.getScaledWindowWidth() / 2 - CAConfig.backslotX - 143;
        } else {
            x = drawContext.getScaledWindowWidth() / 2 + CAConfig.backslotX + 120;
        }
        return x;
    }
}
