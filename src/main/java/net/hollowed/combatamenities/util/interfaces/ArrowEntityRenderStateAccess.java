package net.hollowed.combatamenities.util.interfaces;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public interface ArrowEntityRenderStateAccess {
    void combat_Amenities$setItemStack(ItemStack stack);
    ItemStack combat_Amenities$getItemStack();

    void combat_Amenities$setLook(Vec3 look);
    Vec3 combat_Amenities$getLook();
}
