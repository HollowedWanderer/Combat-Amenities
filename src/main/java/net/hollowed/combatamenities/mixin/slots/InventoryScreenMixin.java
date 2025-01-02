package net.hollowed.combatamenities.mixin.slots;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InventoryScreen.class)
public class InventoryScreenMixin {

    @Unique
    private static final Identifier SLOT_TEXTURE = Identifier.of("textures/gui/sprites/container/slot.png");

    @Inject(method = "drawForeground", at = @At("HEAD"))
    public void render(DrawContext context, int mouseX, int mouseY, CallbackInfo ci) {
        context.drawTexture(
                RenderLayer::getGuiOpaqueTexturedBackground,
                SLOT_TEXTURE,
                76, 7,
                0, 0, 18, 18, 18, 18 // Texture coordinates and dimensions
        );
        context.drawTexture(
                RenderLayer::getGuiOpaqueTexturedBackground,
                SLOT_TEXTURE,
                76, 25,
                0, 0, 18, 18, 18, 18 // Texture coordinates and dimensions
        );
    }
}
