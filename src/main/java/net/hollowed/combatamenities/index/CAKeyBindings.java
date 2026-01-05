package net.hollowed.combatamenities.index;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.hollowed.combatamenities.CombatAmenities;
import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;

public class CAKeyBindings {

    public static KeyMapping.Category CA = KeyMapping.Category.register(CombatAmenities.id("all"));

    // Keybinding
    public static KeyMapping backSlotBinding;
    public static KeyMapping beltSlotBinding;

    public static void initialize() {
        registerKeyBindings();
    }

    private static void registerKeyBindings() {
        backSlotBinding = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.combatamenities.backslot",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_R,
                CA
        ));
        beltSlotBinding = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.combatamenities.beltslot",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_V,
                CA
        ));
    }
}
