package net.hollowed.combatamenities.index;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.hollowed.combatamenities.CombatAmenities;
import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;

public class CAKeyBindings {

    public static KeyMapping.Category CA = KeyMapping.Category.register(CombatAmenities.id("all"));

    public static KeyMapping backSlotBinding;
    public static KeyMapping beltSlotBinding;

    public static void initialize() {
        registerKeyBindings();
    }

    private static void registerKeyBindings() {
        backSlotBinding = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.combatamenities.backslot",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_R,
                CA
        ));
        beltSlotBinding = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.combatamenities.beltslot",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_V,
                CA
        ));
    }
}
