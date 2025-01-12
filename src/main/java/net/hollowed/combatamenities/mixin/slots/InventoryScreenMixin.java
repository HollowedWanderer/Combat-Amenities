package net.hollowed.combatamenities.mixin.slots;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.screen.recipebook.RecipeBookProvider;
import net.minecraft.client.gui.screen.recipebook.RecipeBookWidget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InventoryScreen.class)
public abstract class InventoryScreenMixin extends AbstractInventoryScreen<PlayerScreenHandler> implements RecipeBookProvider {

    @Unique
    private static final Identifier SLOT_TEXTURE = Identifier.of("textures/gui/sprites/container/slot.png");

    public InventoryScreenMixin(PlayerScreenHandler screenHandler, PlayerInventory playerInventory, Text text) {
        super(screenHandler, playerInventory, text);
    }

    @Inject(method = "drawBackground", at = @At("TAIL"))
    public void render(DrawContext context, float delta, int mouseX, int mouseY, CallbackInfo ci) {
        context.drawTexture(
                SLOT_TEXTURE,
                this.x + 76, this.y + 7,
                0, 0, 18, 18, 18, 18 // Texture coordinates and dimensions
        );
        context.drawTexture(
                SLOT_TEXTURE,
                this.x + 76, this.y + 25,
                0, 0, 18, 18, 18, 18 // Texture coordinates and dimensions
        );
    }
}
