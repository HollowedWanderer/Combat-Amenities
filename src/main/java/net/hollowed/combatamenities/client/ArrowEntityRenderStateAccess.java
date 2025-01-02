package net.hollowed.combatamenities.client;

import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;

public interface ArrowEntityRenderStateAccess {
    void combat_Amenities$setItemStack(ItemStack stack);
    ItemStack combat_Amenities$getItemStack();

    void combat_Amenities$setLook(Vec3d look);
    Vec3d combat_Amenities$getLook();
}
