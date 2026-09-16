package net.hollowed.combatamenities.index;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.hollowed.combatamenities.CombatAmenities;
import net.minecraft.client.KeyMapping;

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
                InputConstants.KEY_R,
                CA
        ));
        beltSlotBinding = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.combatamenities.beltslot",
                InputConstants.KEY_V,
                CA
        ));
    }
}
