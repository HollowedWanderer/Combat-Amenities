package net.hollowed.combatamenities.mixin.modMenu;

import com.terraformersmc.modmenu.config.ModMenuConfig;
import com.terraformersmc.modmenu.gui.widget.entries.ModListEntry;
import com.terraformersmc.modmenu.util.mod.Mod;
import net.hollowed.combatamenities.CombatAmenities;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Language;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ModListEntry.class)
public abstract class ModMenuMixin {

    @Shadow @Final public Mod mod;

    @Shadow @Final protected MinecraftClient client;

    @Inject(
            method = "render",
            at = @At("TAIL")
    )
    private void modifyModNameColor(
            DrawContext drawContext,
            int index,
            int y,
            int x,
            int rowWidth,
            int rowHeight,
            int mouseX,
            int mouseY,
            boolean hovered,
            float delta,
            CallbackInfo ci
    ) {
        // Get the mod ID
        String modId = this.mod.getId();
        int iconSize = ModMenuConfig.COMPACT_LIST.getValue() ? 19 : 32;

        // Custom color logic
        int nameColor = 0x5c8790;

        Text name = Text.literal(this.mod.getTranslatedName());
        StringVisitable trimmedName = name;
        int maxNameWidth = rowWidth - iconSize - 3;
        TextRenderer font = this.client.textRenderer;
        if (font.getWidth(name) > maxNameWidth) {
            StringVisitable ellipsis = StringVisitable.plain("...");
            trimmedName = StringVisitable.concat(font.trimToWidth(name, maxNameWidth - font.getWidth(ellipsis)), ellipsis);
        }

        if ("combatamenities".equals(modId)) {
            // Modify the text rendering with a new color
            drawContext.drawText(
                    this.client.textRenderer,
                    Language.getInstance().reorder(trimmedName),
                    x + iconSize + 3,
                    y + 1,
                    nameColor,
                    true
            );

            // Draw small icon
            drawContext.drawTexture(RenderPipelines.GUI_TEXTURED, Identifier.of(CombatAmenities.MOD_ID, "ca_small_icon.png"), x + iconSize + 85, y - 3, 0, 0, 16, 16, 16, 16);

            // Draw colored line below 2 rows of text
            drawContext.drawTexture(RenderPipelines.GUI_TEXTURED, Identifier.of(CombatAmenities.MOD_ID, "ca_line.png"), x + iconSize + 3, y + 31, 0, 0, 76, 1, 76, 1);

            // Draw H signature
            drawContext.drawTexture(RenderPipelines.GUI_TEXTURED, Identifier.of(CombatAmenities.MOD_ID, "h.png"), rowWidth - 2, y, 0, 0, 16, 16, 16, 16);
        }
    }
}
