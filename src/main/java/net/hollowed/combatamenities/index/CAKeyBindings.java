package net.hollowed.combatamenities.index;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.hollowed.combatamenities.CombatAmenities;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class CAKeyBindings {

    public static KeyBinding.Category CA = KeyBinding.Category.create(CombatAmenities.id("category.combatamenities.keybinds"));

    // Keybinding
    public static KeyBinding backSlotBinding;
    public static KeyBinding beltSlotBinding;

    public static void initialize() {
        registerKeyBindings();
    }

    private static void registerKeyBindings() {
        backSlotBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.combatamenities.backslot",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_R,
                CA
        ));
        beltSlotBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.combatamenities.beltslot",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_V,
                CA
        ));
    }
}
