package net.hollowed.combatamenities.mixin.slots.rendering;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractRecipeBookScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.InventoryMenu;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InventoryScreen.class)
public abstract class InventoryScreenMixin extends AbstractRecipeBookScreen<@NotNull InventoryMenu> {

    @Unique
    private static final Identifier SLOT_TEXTURE = Identifier.parse("textures/gui/sprites/container/slot.png");

    public InventoryScreenMixin(InventoryMenu handler, RecipeBookComponent<?> recipeBook, Inventory inventory, Component title) {
        super(handler, recipeBook, inventory, title);
    }

    @Inject(method = "renderBg", at = @At("TAIL"))
    public void render(GuiGraphics context, float delta, int mouseX, int mouseY, CallbackInfo ci) {
        context.blit(
                RenderPipelines.GUI_OPAQUE_TEXTURED_BACKGROUND,
                SLOT_TEXTURE,
                this.leftPos + 76, this.topPos + 7,
                0, 0, 18, 18, 18, 18 // Texture coordinates and dimensions
        );
        context.blit(
                RenderPipelines.GUI_OPAQUE_TEXTURED_BACKGROUND,
                SLOT_TEXTURE,
                this.leftPos + 76, this.topPos + 25,
                0, 0, 18, 18, 18, 18 // Texture coordinates and dimensions
        );
    }
}
