package net.hollowed.combatamenities.mixin.modMenu;

import com.terraformersmc.modmenu.config.ModMenuConfig;
import com.terraformersmc.modmenu.gui.widget.entries.ModListEntry;
import com.terraformersmc.modmenu.util.mod.Mod;
import net.hollowed.combatamenities.CombatAmenities;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ModListEntry.class)
public abstract class ModMenuMixin extends ObjectSelectionList.Entry<@NotNull ModListEntry> {

    @Shadow @Final public Mod mod;

    @Shadow @Final protected Minecraft client;

    @Shadow public abstract int getXOffset();

    @Shadow public abstract int getYOffset();

    @Inject(
            method = "renderContent",
            at = @At("TAIL")
    )
    private void modifyModNameColor(
            GuiGraphics drawContext, int mouseX, int mouseY, boolean hovered, float delta, CallbackInfo ci
    ) {
        int x = this.getX() + this.getXOffset();
        int y = this.getContentY() + this.getYOffset();
        int rowWidth = this.getContentWidth();

        // Get the mod ID
        String modId = this.mod.getId();
        int iconSize = ModMenuConfig.COMPACT_LIST.getValue() ? 19 : 32;

        // Custom color logic
        int nameColor = 0xFF33ebcb;

        Component name = Component.literal(this.mod.getTranslatedName());
        FormattedText trimmedName = name;
        int maxNameWidth = rowWidth - iconSize - 3;
        Font font = this.client.font;
        if (font.width(name) > maxNameWidth) {
            FormattedText ellipsis = FormattedText.of("...");
            trimmedName = FormattedText.composite(font.substrByWidth(name, maxNameWidth - font.width(ellipsis)), ellipsis);
        }

        if ("combatamenities".equals(modId)) {
            // Modify the text rendering with a new color
            drawContext.drawString(font, Language.getInstance().getVisualOrder(trimmedName), x + iconSize + 3, y + 1, nameColor);

            // Draw small icon
            drawContext.blit(RenderPipelines.GUI_TEXTURED, Identifier.fromNamespaceAndPath(CombatAmenities.MOD_ID, "ca_small_icon.png"), x + iconSize + 85, y - 3, 0, 0, 16, 16, 16, 16);

            // Draw colored line below 2 rows of text
            drawContext.blit(RenderPipelines.GUI_TEXTURED, Identifier.fromNamespaceAndPath(CombatAmenities.MOD_ID, "ca_line.png"), x + iconSize + 3, y + 31, 0, 0, 76, 1, 76, 1);

            // Draw H signature
            drawContext.blit(RenderPipelines.GUI_TEXTURED, Identifier.fromNamespaceAndPath(CombatAmenities.MOD_ID, "h.png"), rowWidth - 2, y, 0, 0, 16, 16, 16, 16);
        }
    }
}
