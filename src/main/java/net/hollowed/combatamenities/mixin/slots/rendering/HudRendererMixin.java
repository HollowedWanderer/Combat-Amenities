package net.hollowed.combatamenities.mixin.slots.rendering;

import com.mojang.blaze3d.opengl.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.hollowed.combatamenities.config.CAConfig;
import net.hollowed.combatamenities.util.items.CAComponents;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
public abstract class HudRendererMixin {

    @Shadow @Final private Minecraft minecraft;
    @Unique
    private static final Identifier WIDGETS_TEXTURE = Identifier.parse("textures/gui/sprites/hud/hotbar_offhand_left.png");

    @Unique
    private static ItemStack lastBackSlotStack = ItemStack.EMPTY;
    @Unique
    private static ItemStack lastBeltSlotStack = ItemStack.EMPTY;
    @Unique
    private int backAnimationTicks = 0;
    @Unique
    private int beltAnimationTicks = 0;

    @Inject(method = "tick()V", at = @At("HEAD"))
    private void tick(CallbackInfo ci) {
        if (backAnimationTicks > 0) {
            backAnimationTicks--;
        }
        if (beltAnimationTicks > 0) {
            beltAnimationTicks--;
        }
    }

    @Inject(method = "renderItemHotbar", at = @At("TAIL"))
    public void renderHotbar(GuiGraphics context, DeltaTracker tickCounter, CallbackInfo ci) {
        renderBackSlot(context, tickCounter);
        renderBeltSlot(context, tickCounter);
    }

    @Unique
    private void renderBeltSlot(GuiGraphics drawContext, DeltaTracker tickCounter) {
        Player playerEntity = Minecraft.getInstance().player;
        if (playerEntity != null) {
            ItemStack beltSlotStack = playerEntity.getInventory().getItem(42);

            if (!ItemStack.matches(beltSlotStack, lastBeltSlotStack) && beltAnimationTicks == 0) {
                lastBeltSlotStack = beltSlotStack.copy();
                if (beltSlotStack.getOrDefault(CAComponents.STRING_PROPERTY, "").equals("bob5")) {
                    beltAnimationTicks = 5;
                }
            }

            if (beltSlotStack.getOrDefault(CAComponents.STRING_PROPERTY, "").equals("bob5")) {
                beltSlotStack.remove(CAComponents.STRING_PROPERTY);
            }

            if (!beltSlotStack.isEmpty()) {
                final int x = getBeltX(drawContext);
                int y = drawContext.guiHeight() - CAConfig.backslotY - 4;

                RenderSystem.assertOnRenderThread();
                GlStateManager._enableBlend();

                drawContext.blit(
                        RenderPipelines.GUI_TEXTURED,
                        WIDGETS_TEXTURE,
                        x + 1, y - 19,
                        0, 0, 22, 23, 29, 24
                );

                // Render the back slot item
                renderItem(drawContext, x + 4, y - 15, tickCounter, playerEntity, beltSlotStack, beltAnimationTicks);
            }
        }
    }

    @Unique
    private void renderBackSlot(GuiGraphics drawContext, DeltaTracker tickCounter) {
        Player playerEntity = Minecraft.getInstance().player;
        if (playerEntity != null) {
            ItemStack backSlotStack = playerEntity.getInventory().getItem(41);

            if (!ItemStack.matches(backSlotStack, lastBackSlotStack) && backAnimationTicks == 0) {
                lastBackSlotStack = backSlotStack.copy();
                if (backSlotStack.getOrDefault(CAComponents.STRING_PROPERTY, "").equals("bob5")) {
                    backAnimationTicks = 5;
                }
            }

            if (backSlotStack.getOrDefault(CAComponents.STRING_PROPERTY, "").equals("bob5")) {
                backSlotStack.remove(CAComponents.STRING_PROPERTY);
            }

            if (!backSlotStack.isEmpty()) {
                final int x = getX(drawContext);
                int y = drawContext.guiHeight() - CAConfig.backslotY - 4;

                RenderSystem.assertOnRenderThread();
                GlStateManager._disableBlend();

                drawContext.blit(
                        RenderPipelines.GUI_TEXTURED,
                        WIDGETS_TEXTURE,
                        x + 1, y - 19,
                        0, 0, 22, 23, 29, 24
                );

                // Render the back slot item
                renderItem(drawContext, x + 4, y - 15, tickCounter, playerEntity, backSlotStack, backAnimationTicks);
            }
        }
    }

    @Unique
    private void renderItem(GuiGraphics context, int x, int y, DeltaTracker tickCounter, Player player, ItemStack stack, int animationTicks) {
        if (!stack.isEmpty()) {
            float f = animationTicks - tickCounter.getGameTimeDeltaPartialTick(false);
            if (f > 0.0F) {
                float g = 1.0F + f / 5.0F;
                context.pose().pushMatrix();
                context.pose().translate(x + 8, y + 12);
                context.pose().scale(1.0F / g, (g + 1.0F) / 2.0F);
                context.pose().translate(-(x + 8), -(y + 12));
            }

            context.renderItem(player, stack, x, y, 1);
            if (f > 0.0F) {
                context.pose().popMatrix();
            }

            context.renderItemDecorations(this.minecraft.font, stack, x, y);
        }
    }

    @Unique
    private static int getX(GuiGraphics drawContext) {
        boolean isLeftHanded = Minecraft.getInstance().options.mainHand().get().equals(HumanoidArm.LEFT);

        int x;
        if (isLeftHanded) {
            x = drawContext.guiWidth() / 2 - CAConfig.backslotX - 120;
        } else {
            x = drawContext.guiWidth() / 2 + CAConfig.backslotX + 97;
        }
        return x;
    }

    @Unique
    private static int getBeltX(GuiGraphics drawContext) {
        boolean isLeftHanded = Minecraft.getInstance().options.mainHand().get().equals(HumanoidArm.LEFT);

        int x;
        if (isLeftHanded) {
            x = drawContext.guiWidth() / 2 - CAConfig.backslotX - 143;
        } else {
            x = drawContext.guiWidth() / 2 + CAConfig.backslotX + 120;
        }
        return x;
    }
}
