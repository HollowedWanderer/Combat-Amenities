package net.hollowed.backslot;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class ModKeyBindings {

    //Keybindings
    public static KeyBinding backSlotBinding;

    public static void registerKeyBindings() {
        backSlotBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.backslot.category",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_R,
                "category.backslot.keybinds"
        ));
    }

    public static void initialize() {
        registerKeyBindings();
    }
}
