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
    private static final int xOffset = CombatAmenities.CONFIG.backslotX;  // Default position
    private static final int yOffset = CombatAmenities.CONFIG.backslotY;

    public static void init() {
//        loadConfig();
        HudRenderCallback.EVENT.register((drawContext, tickDelta) -> {
            if (!MinecraftClient.getInstance().options.hudHidden) {
                renderBackSlot(drawContext);
            }
        });
    }

//    private static void loadConfig() {
//        try {
//            File configFile = new File(MinecraftClient.getInstance().runDirectory, "config/backslot_position.properties");
//            if (!configFile.exists()) {
//                // Create the file if it does not exist
//                Files.createDirectories(configFile.getParentFile().toPath());
//                configFile.createNewFile();
//                // Set default values
//                saveConfig();
//            }
//
//            Properties properties = new Properties();
//            properties.load(Files.newInputStream(configFile.toPath()));
//
//            // Read the configuration values
//            xOffset = Integer.parseInt(properties.getProperty("xOffset", "0"));
//            yOffset = Integer.parseInt(properties.getProperty("yOffset", "0"));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

//    private static void saveConfig() {
//        try {
//            Properties properties = new Properties();
//            properties.setProperty("xOffset", String.valueOf(xOffset));
//            properties.setProperty("yOffset", String.valueOf(yOffset));
//            properties.store(Files.newOutputStream(Paths.get(MinecraftClient.getInstance().runDirectory + "/config/backslot_position.properties")), "Backslot Position Config");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    private static void renderBackSlot(DrawContext drawContext) {
        PlayerEntity playerEntity = MinecraftClient.getInstance().player;
        if (playerEntity != null) {

            // Get the backslot item
            ItemStack backSlotStack = playerEntity.getInventory().getStack(41);

            if (!backSlotStack.isEmpty()) {
                final int x = getX(drawContext);

                int y = drawContext.getScaledWindowHeight() - yOffset - 4; // Y position remains the same

                // Draw the backslot item
                RenderSystem.enableBlend();

                // Render the texture
                drawContext.drawTexture(
                        RenderLayer::getGuiTexturedOverlay,
                        WIDGETS_TEXTURE,
                        x + 1, y - 19,
                        0, 0, 22, 23, 29, 24 // Texture coordinates and dimensions
                );
                renderHotbarItem(drawContext, MinecraftClient.getInstance(), x + 4, y - 15, playerEntity, backSlotStack);
            }
        }
    }

    private static int getX(DrawContext drawContext) {
        boolean isLeftHanded = MinecraftClient.getInstance().options.getMainArm().getValue().equals(Arm.LEFT);

        // Calculate positions based on hand preference
        int x;
        if (isLeftHanded) {
            x = drawContext.getScaledWindowWidth() / 2 - xOffset - 120; // Position on the left of the hotbar
        } else {
            x = drawContext.getScaledWindowWidth() / 2 + xOffset + 97;  // Position on the right of the hotbar
        }
        return x;
    }

    private static void renderHotbarItem(DrawContext context, MinecraftClient client, int x, int y, PlayerEntity player, ItemStack stack) {
        if (stack.isEmpty()) return;

        context.drawItem(player, stack, x, y, 0);
        context.drawStackOverlay(client.textRenderer, stack, x, y);
    }
}
